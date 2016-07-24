package edu.toronto.cs.simulator;

import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;

/**
 ** XCTraceTree is the trace tree for the simulator.
 **/
public class XCTraceTree implements TreeModel
{

    // the root of the trace tree
    XCTraceState root;
    // last added node
    XCTraceState lastadded;
    
    List listeners;

    // model info and tools for manipulation
    MvSetModelChecker mc;
    MvSetFactory fac;
    StatePresenter pres;
    IAlgebra algebra;
    CTLNode ctlfalse, ctltrue;

    // couple of state namers: for single- and multi-states
    StateName singlename, multiname;

    /**
     ** Builds an empty trace tree. A public constructor needs a root.
     **/
    XCTraceTree () {}

    /**
     ** Builds a trace tree using a modelchecker initialized with a model.
     **/
    public XCTraceTree (MvSetModelChecker modelChecker)//XCTraceState state)
    {
	mc = modelChecker;
	fac = mc.getMvSetFactory ();
	pres = mc.getXKripke ().getStatePresenter ();
	algebra = mc.getXKripke ().getAlgebra ();
	ctlfalse = CTLFactory.createCTLConstantNode (algebra.bot ());
	ctltrue = CTLFactory.createCTLConstantNode (algebra.top ());

	root = getInitialState ();
	lastadded = root;
	listeners = new LinkedList ();
	
	singlename = new StateName ("s");
	multiname = new StateName ("*m");
    }

    /*
     * Needed to satisfy the TreeModel interface
     */

    /**
     ** Adds a listener for the TreeModelEvent posted after the tree changes.
     **/
    public void addTreeModelListener (TreeModelListener l)
    {
	listeners.add (l);
    }
    
    /**
     ** Returns the child of parent at index index in the parent's
     ** child array.
     **/     
    public Object getChild (Object parent, int index)
    {
	if (parent instanceof XCTraceState)
	    return ((XCTraceState) parent).getChild (index);
	else return null;
    }
    
    /**
     ** Returns the number of children of parent.
     **/     
    public int getChildCount (Object parent)
    {
	if (parent instanceof XCTraceState)
	    return ((XCTraceState) parent).getNumChildren ();
	else return -1;
    }
    
    /**
     ** Returns the index of child in parent.
     **/
    public int getIndexOfChild (Object parent, Object child)
    {
	if (parent instanceof XCTraceState &&
	    child instanceof XCTraceState)
	    return ((XCTraceState) parent).getChildIndex 
		(((XCTraceState) child));
	else return -1;
    }
    
    /**
     ** Returns the root of the tree.
     **/
    public Object getRoot ()
    {
	return root;
    }
    
    /**
     ** Returns true if node is a leaf.
     **/
    public boolean isLeaf (Object node)
    {
	if (node instanceof XCTraceState)
	    return ((XCTraceState) node).getNumChildren () == 0;
	else return false;	
    }
    
    /**
     ** Removes a listener previously added with addTreeModelListener.
     **/      
    public void removeTreeModelListener (TreeModelListener l)
    {
	listeners.remove (l);
    }
    
    /**
     ** Messaged when the user has altered the value for the item
     ** identified by path to newValue.
     **/     
    public void valueForPathChanged (TreePath path, Object newValue)
    {
	// The state info (actually just the label) got changed
	((XCTraceState) path.getLastPathComponent ())
	    .getInfo ().setLabel (newValue.toString ());
	fireTreeNodesChanged (new TreeModelEvent (this, path));
    }

    /* TreeModel interface satisfied */

    /**
     ** Gets the algebra used by states in the trace.
     **/
    public IAlgebra getAlgebra ()
    { return algebra; }

    /**
     ** Adds a new child to the specified parent in the tree.
     **
     ** @param parent - parent node
     ** @param child - child node to be added
     **/
    public void addChild (XCTraceState parent, XCTraceState child)
    {
	parent.addChild (child);

	int [] nodei = {parent.getChildIndex (child)};
	Object [] node = {child};
	/*
	System.out.println ("XXX - added: " + child + " index: " + nodei[0]);
	System.out.println ("XXX - now: " + Arrays.asList (parent.getChildren ()));
	*/
	lastadded = child;
	fireTreeNodesInserted 
	    (new TreeModelEvent 
	     (this, getPath (parent), nodei, node));
    }

    /**
     ** Adds a node specified by the path to the tree. All the
     ** necessary ancestor nodes are also added. The first node in the
     ** path must be <i>equal</i> to the root of the trace tree it's
     ** added to.
     **
     ** @return true if added the path sucessfully, false if nothing
     ** was added (wrong TreePath or node already existed).
     **/
    public boolean addPath (TreePath path)
    {
	int i;
	XCTraceState [] arraypath;

	arraypath = (XCTraceState []) path.getPath ();

	if (!arraypath[0].equals (getRoot ())) return false;
	// find the place in the tree where to insert the nodes
	for (i = 1; i < path.getPathCount () &&
	     getIndexOfChild (arraypath[i-1], arraypath[i]) != -1; i++);

	if (i == path.getPathCount ()) return false;
	else
	    {
		// add the rest of the path
		for (; i < path.getPathCount (); i++)
		    // should do something smarter about firing events...
		    addChild (arraypath[i-1], arraypath[i]);
		return true;
	    }
    }

    /**
     ** Removes the trace tree rooted at the state s (state s included
     ** except when s is the root).
     **
     ** @param s - node to be removed.
     ** @return the removed node.
     **/
    public XCTraceState remove (XCTraceState s)
    {
	XCTraceState parent = s.getParent ();
	int [] nodei = {parent.getChildIndex (s)};
	Object [] node = {s};

	s.remove ();
	/*
	System.out.println ("XXX - removed: " + s + " index: " + nodei[0]);
	System.out.println ("XXX - left: " + Arrays.asList (parent.getChildren ()));
	*/
	// lastadded = null;
	fireTreeNodesRemoved
	    (new TreeModelEvent 
	     (this, getPath (parent), nodei, node));

	return s;
    }

    /**
     ** Returns the path from the root of the trace to the specified
     ** state.
     **
     ** @param s - state to get the path of.
     ** @return the path to the specified state.
     **/
    public TreePath getPath (XCTraceState s)
    {
	TreePath path;
	LinkedList list = new LinkedList ();
	XCTraceState curr;

	// traverse the tree from the node s to the root creating a list
	for (curr = s; curr != null; curr = curr.getParent ())
	    list.addFirst (curr);

	path = new TreePath (list.toArray ());

	return path;
    }

    /* event processing methods */

    /**
     ** Invoked after a node (or a set of siblings) has changed in some way.
     **/
    protected void fireTreeNodesChanged (TreeModelEvent e)
    {
	Iterator i;
	
	for (i = listeners.iterator (); i.hasNext ();)
	    ((TreeModelListener)(i.next ())).treeNodesChanged (e);
    }
    
    /**
     ** Invoked after nodes have been inserted into the tree.
     **/
    protected void fireTreeNodesInserted (TreeModelEvent e)
    {
	Iterator i;
	
	for (i = listeners.iterator (); i.hasNext ();)
	    ((TreeModelListener)(i.next ())).treeNodesInserted (e);
    }
    
    /**
     ** Invoked after nodes have been removed from the tree.
     **/      
    protected void fireTreeNodesRemoved (TreeModelEvent e)
    {
	Iterator i;
	
	for (i = listeners.iterator (); i.hasNext ();)
	    ((TreeModelListener)(i.next ())).treeNodesRemoved (e);
    }
    
    /**
     ** Invoked after the tree has drastically changed structure from
     ** a given node down.
     **/      
    protected void fireTreeStructureChanged (TreeModelEvent e)
    {
	Iterator i;
	
	for (i = listeners.iterator (); i.hasNext ();)
	    ((TreeModelListener)(i.next ())).treeStructureChanged (e);
    }

    // public modelchecker stuff

    /**
     ** Given some initial state will add the successor states (as one
     ** multistate) satisfying the condition.
     **/
    public void expand (XCTraceState state, CTLNode condition)
    {
	MvSet parent, children;
	XCStateInfo info;
	XCTraceState child;

	// XXX
	//System.out.println ("Expand: Condition was: " + condition);
	// XXX
		
	parent = state.getInfo ().getVariables ();
	// get the children states satisfying the condition
	children = mc.checkCTL 
	    (condition.and (newStateCondition (state, false)).preEX (),
	     parent).renameArgs (mc.getXKripke ().getUnPrime ());
	
	// if no children are available do nothing
	if (children.equals (fac.bot ())) return;

	// test to see whether you have more than one child in fact
	if (children.getPreImageArray (algebra.top ()).size () > 1)
	    { // add a multi-state
		info = new XCStateInfo
		    (multiname.getStateName (children), children);
		child =  new XCTraceState (null, info, true);
	    }
	else // add a singleton state
	    {
		info = new XCStateInfo
		    (singlename.getStateName (children), children);
		child =  new XCTraceState (null, info, false);
	    }
	
	addChild (state, child);
    }

    /**
     ** Given some initial state will pick one of the successor states
     ** satisfying the condition and add it as a child.
     **/
    public void expandOne (XCTraceState state, CTLNode condition)
    {
	MvSet parent, children, child;
	XCStateInfo info;

	// XXX
	//System.out.println ("ExpandOne: Condition was: " + condition);
	// XXX
		
	parent = state.getInfo ().getVariables ();
	// get the children states satisfying the condition
	children = mc.checkCTL
	    (condition.and (newStateCondition (state, true)).preEX (),
	     parent).renameArgs (mc.getXKripke ().getUnPrime ());

	if (children.equals (fac.bot ())) return;

	// -- extract a single state out of the set of children
	child = extractState (children);
    
	info = new XCStateInfo (singlename.getStateName (child), child);
	addChild (state, new XCTraceState (null, info, false));
    }

    /**
     ** Given a (multi-)state will attempt to split a single state off
     ** of it, modifying the tree accordingly (add 2 siblings in place
     ** of the old state).
     **
     ** NOTE: the root splits differently from other states, it
     ** creates children NOT siblings.
     **
     ** @return the old unsplit state if it was removed from the tree.
     **/
    public XCTraceState split (XCTraceState state, CTLNode condition)
    {
	if (!state.isMultiState ()) return null;
	if (state == root)
	    {
		MvSet init, children, child;
		XCStateInfo info;
		
		init = root.getInfo ().getVariables ();
		
		// get all the children states satisfying the condition
		children = init.and (mc.checkCTL
				     (condition.and (newStateCondition
						     (state, true))));
		
		if (children.equals (fac.bot ())) return null;

		// -- extract a single state out of the set of children
		child = extractState (children);
    
		info = new XCStateInfo (singlename.getStateName (child),
					child);
		addChild (state, new XCTraceState (null, info, false));

		return null;
	    }
	else
	    {
		MvSet init, siblings, sib, lings;
		XCStateInfo info1, info2;

		init = state.getInfo ().getVariables ();
		
		// get the sibling states satisfying the condition
		siblings = init.and (mc.checkCTL
				     (condition.and (newStateCondition
						     (state.getParent (),
						      true))));
		
		// ensure that there were SOME satisfactory siblings found
		if (siblings.equals (fac.bot ())) return null;

		// -- extract a single state out of the set of siblings
		sib = extractState (siblings);
		info1 = new XCStateInfo (singlename.getStateName (sib), sib);
		addChild (state.getParent (),
			  new XCTraceState (null, info1, false));
    
		// -- get whatever is left after removing one sibling
		lings = siblings.and (sib.not ());

		// ensure that there was SOMETHING left
		if (!lings.equals (fac.bot ()))
		    {
			info2 = new XCStateInfo
			    (multiname.getStateName (lings), lings);
			addChild (state.getParent (), new XCTraceState
				  (null, info2, true));
		    }

		return remove (state);
	    }
    }

    /**
     ** Eventually this will allow to merge siblings into one multi-state.
     **
    public void merge (XCTraceState [] siblings)
    {
	
    }
    

    /**
     ** Converts a state description to an array of "variable = value".
     **/
    public CTLNode [] stateToArray (XCStateInfo info)
    {
	AlgebraValue [] state = (AlgebraValue []) info.getVariables
	    ().getPreImageArray (algebra.top ()).iterator ().next ();
	
	return pres.toCTL (state);
    }     
    
    // private modelchecker stuff
    /**
     ** Creates a CTL formula "not the explored children of
     ** this state".
     ** @param onlysingle - if true will consider only the single states.
     **/
    private CTLNode newStateCondition (XCTraceState state, boolean onlysingle)
    {
	CTLNode result;
	MvSet children = fac.bot ();
	int i;
	XCTraceState [] child;

	result = ctlfalse;
	child = state.getChildren ();
	// go through all the children of the specified state and
	// create a disjunction over them
	for (i = 0; i < state.getNumChildren (); i++)
	    {
		// make sure that the state is "single"
		if (!onlysingle || !child [i].isMultiState ())
		    children = children.or (child[i].getInfo ().
					    getVariables ());
		    /*
		    result = CTLFactory.createCTLOrNode 
			(result, 
			 CTLFactory.createCTLMvSetNode 
			 (child [i].getInfo ().getVariables ()));
		    */
	    }
	result = CTLFactory.createCTLMvSetNode (children).neg ();
	return result;
    }

    /**
     ** Returns the initial state of the model.
     **/
    private XCTraceState getInitialState ()
    {
	MvSet state;
	XCStateInfo info;
		
	state = mc.getXKripke ().getInit ();
	
	assert !state.isConstant () : "Initial state is " + state;
	
	info = new XCStateInfo ("Initial state", state);
	return new XCTraceState (null, info, true);
    }
     
    private MvSet extractState (MvSet children)
    {
	return fac.createPoint 
	    (mc.getXKripke ().getSingleState 
	     ((AlgebraValue[]) 
	      children.getPreImageArray(algebra.top ()).iterator ().next ()), 
	     algebra.top ());
    }

    /**
     ** Returns the last node added to the tree. Note: removing any
     ** node resets this to null.
     **/
    public XCTraceState getLastAdded ()
    {
	return lastadded;
    }
}

