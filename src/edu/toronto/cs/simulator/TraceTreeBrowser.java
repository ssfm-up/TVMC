package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.util.*;

import edu.toronto.cs.util.*;
//import edu.toronto.cs.util.gui.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;

/**
 ** This class provides the inerface to the trace tree.
 **/
public class TraceTreeBrowser extends JScrollPane
{
    JTree tree;
    XCTraceTree treemodel;
    JPopupMenu popupmenu;
    
    // useful constants
    CTLNode ctlfalse, ctltrue;

    // various actions... should be put into some common action map perhaps
    Action step, split, remove, run;
    Action infochanged;
    // used to get the simulation conditions
    Action load;

    // simulator to get conditions from
    XCSimulator sim;

    MvSetModelChecker mc;
    
    /**
     ** Creates a TraceTreeBrowser for a given model.
     **/    
    public TraceTreeBrowser (MvSetModelChecker mc, ActionMap map)
    {
	super (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	initActions (map);
	init ();
	setMC (mc);
    }

    /**
     ** sets the ActionListener that gets notified when info of a
     ** selected node gets changed.
     **
    public void addSelectedInfoChangedActionListener (ActionListener a)
    {
	selectedinfo_actionlisteners.add (a);
    }
    */

    private void fireSelectedInfoChanged ()
    {
	infochanged.actionPerformed
	    (new ActionEvent (this, ActionEvent.ACTION_PERFORMED,
			      "Selected state info changed"));
    }
    
    /**
     ** Adds a mouse listener to the trace tree.
     ** 
     ** @param m - MouseListener to add.
     **/
    public void addTreeMouseListener (MouseListener m)
    {
	tree.addMouseListener (m);
    }

    // XXX need "remove listener" methods....

    /**
     ** Set the simulator to be used for model exploration.
     **/
    public void setSimulator (XCSimulator s) 
    {
	sim = s;
    }

    /**
     ** Gets the currently selected state.
     **
     ** @return the state that is currently selected.
     **/
    public XCTraceState getSelectedState ()
    {
	TreePath sel = tree.getSelectionPath ();
			
	// get the selected state if any
	if (sel != null)
	    return (XCTraceState) sel.getLastPathComponent ();
	else return null;
    }


    /**
     ** Sets the model from which the traces are shown.
     **/
    private void setMC (MvSetModelChecker modelchecker)
    {
	mc = modelchecker;
	tree.setEditable (mc != null);
	// add the first state to the tree
	if (mc != null)
	    {
		setTreeModel (new XCTraceTree (mc));
		
		ctlfalse = CTLFactory.createCTLConstantNode
		    (mc.getXKripke ().getAlgebra ().bot ());
		ctltrue = CTLFactory.createCTLConstantNode
		    (mc.getXKripke ().getAlgebra ().top ());

		// set the default values for the expansion conditions

		// make sure that when the model gets updated the
		// selected state info listener get's notified
		treemodel.addTreeModelListener (new TreeModelAdapter ()
		    {
			public void treeNodesChanged (TreeModelEvent e)
			{
			    if (e.getTreePath ().equals
				(tree.getSelectionPath ()))
				fireSelectedInfoChanged ();
			}
		    });
	    }
    }

    /**
     ** Creates the tree.
     **/
    private void init ()
    {
	popupmenu = new JPopupMenu ();
	// XXX - actions should be put into some common action map
	popupmenu.add (run);
	popupmenu.add (step);
	popupmenu.add (split);
	popupmenu.add (remove);
	popupmenu.setInvoker (this);

	tree = new JTree ((TreeModel) null);

	tree.setEditable (false);
	tree.setScrollsOnExpand (true);
	setViewportView (tree);

	// make sure that when the selected info listeners get
	// notified appropriately
	tree.addTreeSelectionListener (new TreeSelectionListener ()
	    {
		public void valueChanged (TreeSelectionEvent e)
		{ fireSelectedInfoChanged (); }
	    });
	// add a mouse listener to the trace tree
	tree.addMouseListener 
	    (new MouseAdapter () 
		{
		    public void mousePressed (MouseEvent e)
		    {
			if (e.getButton () == MouseEvent.BUTTON1 &&
			    e.getClickCount () == 2)
			    defaultAction (tree.getPathForLocation 
					   (e.getX (), e.getY()));
			else if (e.getButton () == MouseEvent.BUTTON3)
			    {
				tree.setSelectionPath
				    (tree.getClosestPathForLocation
				     (e.getX (), e.getY()));

				showPopupMenu ((Component) e.getSource (),
					       e.getX (), e.getY ());
			    }
		    }
		}
	     );
	// remove a node when 'del' is pressed
	// perform the "default action" when 'enter' is pressed
	tree.addKeyListener 
	    (new KeyAdapter ()
		{
		    public void keyPressed (KeyEvent e)
		    {
			if (e.getKeyCode () == KeyEvent.VK_DELETE)
			    remove.actionPerformed
				(new ActionEvent (this, 0, "remove"));
			if (e.getKeyCode () == KeyEvent.VK_ENTER)
			    defaultAction (tree.getSelectionPath());
		    }
		}
	     );

	setPreferredSize (new Dimension (500,300));
    }

    /**
     ** Displays the pop-up menu.
     **/
    private void showPopupMenu (Component c, int x, int y)
    {
	XCTraceState s = getSelectedState ();

	split.setEnabled (s.isMultiState () &&
			  sim.isMultiStateSupported ());
	step.setEnabled (!s.isMultiState () ||
			 sim.isMultiStateSupported ());
	
	popupmenu.show (c, x, y);
    }

    /**
     ** Initialize the actions.
     **/
    private void initActions (ActionMap map)
    {
	step = new AbstractAction ("Step")
	    {
		// creates more children if possible
		public void actionPerformed (ActionEvent e) 
		{
		    CTLNode condition;
		    
		    TreePath path = tree.getSelectionPath ();
		    XCTraceState state = (XCTraceState)
			path.getLastPathComponent ();

		    makeOneStep (state);
		}
	    };
	split = new AbstractAction ("Split")
	    {
		// creates more siblings if possible
		public void actionPerformed (ActionEvent e) 
		{
		    CTLNode condition;

		    XCTraceState state = (XCTraceState)
			tree.getLastSelectedPathComponent ();

		    sim.makeStep (state);
		    condition = sim.getStepCond ();
		    treemodel.split (state, condition);
		    // show the tree
		    showNewState (treemodel.getLastAdded ());
		}
	    };
	remove = new AbstractAction ("Remove")
	    {
		public void actionPerformed (ActionEvent e) 
		{
		    treemodel.remove
			((XCTraceState) tree.getSelectionPath ().
			 getLastPathComponent ());
		}
	    };

	map.put ("Step", step);
	map.put ("Remove", remove);
	map.put ("Split", split);
	run = (Action) map.get ("Run sim script");

	// will process selecting a state
	infochanged = (Action) map.get ("Update state view");
    }

    /**
     ** Perform the appropriate default action.
     **/
    //    public void processDblClick (int x, int y)
    public void defaultAction (TreePath path)
    {
	//	TreePath path = tree.getPathForLocation (x, y);
	
	if (path != null)
	    {
		XCTraceState state = (XCTraceState)
		    path.getLastPathComponent ();

		// depending on the type of node either split it or step
		if (state.isMultiState ())
		    split.actionPerformed
			(new ActionEvent (this, 0, "Split"));
		else
		    step.actionPerformed
			(new ActionEvent (this, 0, "Step"));
	    }
    }

    /**
     ** Expands/collapses the tree at the given node.
     **/
    private void toggleTree (int selRow, TreePath selPath)
    {
	if (tree.isExpanded (selRow)) tree.collapseRow (selRow);
	else tree.expandRow (selRow);
    }

    /**
     ** Use this as opposed to simply doing it by hand to avoid
     ** forgetting something.
     **/
    private void setTreeModel (XCTraceTree tm)
    {
	treemodel = tm;
	tree.setModel (treemodel);
    }

    /**
     ** Use this as opposed to simply doing it by hand to avoid
     ** forgetting something.
     **/
    public XCTraceTree getTreeModel ()
    {
	return treemodel;
    }

    /**
     ** Parses a String into CTL without exceptions.
     **/
    public CTLNode parseCTL (String ctlStr)
    {
	try 
	    {
		return CTLNodeParser.parse (ctlStr);
	    }
	catch (Exception ex)
	    {
		System.out.println ("CTL Parsing Error :" + ex);	
	    }
	return null;
    }

    /**
     ** .... will change.... 
     **/
    public void runScript (XCSimulator sim)
    {
	sim.show ();
	CTLNode condition;
	// reached end of script of a break condition
	boolean done = false;
	// break and step conditions
	CTLNode [] breakcond;
	//	MvSetFactory fac = mc.getMvSetFactory ();

	TreePath path = tree.getSelectionPath ();
	XCTraceState state = (XCTraceState)
	    path.getLastPathComponent ();
	XCTraceState child;
	do
	    {
		// make a step ...
		child = makeOneStep (state);
		// get the break condition 
		breakcond = sim.getBreakCond ();

		// we're done if:
		// - no valid children were produced
		if (child == null) done = true;
		// - we've got a break condition
		else if (mustBreak (breakcond, (MvSet)
				    child.getInfo ().getVariables ()))
		    done = true;
		/*
		else if ( !mc.checkCTL (breakcond, (MvSet)
					child.getInfo ().getVariables ())
			  .equals (fac.bot ()) )
		    done = true;
		*/
		// - script ended
		else if (sim.getStep () == 0) done = true;
		// Otherwise we continue
		else state = child;
	    }
	while (!done);
	/*
	do
	    {
		condition = sim.step (state);
		child = null;

		// check whether a child state in the trace can
		// satisfy the step condition
		if (!sim.newStatesOnly ())
		    child = findChild (state, condition);

		if (child == null)
		    { // must have new state - just try to expand
			treemodel.expandOne (state, condition);
			state = treemodel.getLastAdded ();
		    }
		else
		    state = child;
	    }
	while (sim.getStep () > 0); // finish when script ends
	*/
	// show the tree
	tree.expandPath (treemodel.getPath (state));
    }

    /**
     ** Checks the break conditions.
     **
     ** @return true if any of the break conditions were true.
     **/
    private boolean mustBreak (CTLNode [] br, MvSet s)
    {	
	MvSetFactory fac = mc.getMvSetFactory ();

	for (int i = 0; i < br.length; i++)
	    // - we've got a break condition
	    if ( !mc.checkCTL (br[i], s).equals (fac.bot ()) )
		{
		    // show a message
		    String displayMessage =
			"Break condition satisfied:\n" + br[i];
		    JOptionPane.showMessageDialog (this, 
						   displayMessage,
						   "Break",
						   JOptionPane.ERROR_MESSAGE);
		    return true;
		}

	return false;
    }

    /**
     ** Makes just one step.
     **
     ** @return null if the step was unsuccessful.
     **/
    private XCTraceState makeOneStep (XCTraceState state)
    {
	// break and step conditions
	CTLNode stepcond, elsecond;
	XCTraceState result = null;
	XCTraceState child;

	sim.makeStep (state);
	stepcond = sim.getStepCond ();
	elsecond = sim.getElseCond ();

	// make a step
	child = makeOneStep (state, stepcond);
	// if unsuccessful, try the alternative
	if (child == null) 
	    child = makeOneStep (state, elsecond);

	// if we're looping skip to the next meaningful step if allowed
	if (child != null && 
	    state.getInfo ().equals (child.getInfo())
	    && sim.canSkipLoops ())
	    sim.skip ();

	// show the freshly obtained child (if any)
	if (child != null)
	    showNewState (child);

	return child;
    }
    /**
     ** Helper to the above method.
     **/
    private XCTraceState makeOneStep (XCTraceState state, CTLNode condition)
    {
	XCTraceState child = null;

	// check whether a child state in the trace can
	// satisfy the step condition
	if (!sim.newStatesOnly ())
	    child = findChild (state, condition);

	if (child == null)
	    { // must have new state - just try to expand
		XCTraceState old = treemodel.getLastAdded ();
		if (sim.isMultiStateSupported ())
		    treemodel.expand (state, condition);
		else
		    treemodel.expandOne (state, condition);
		// get the state that was just added
		child = treemodel.getLastAdded ();
		// veryfy that something WAS in fact added
		if (child == old) child = null;
	    }

	return child;	
    }

    /**
     ** Makes sure that the specified state is selected and visible.
     **/
    private void showNewState (XCTraceState s)
    {
	TreePath path = treemodel.getPath (s);
	tree.expandPath (path);
	tree.setSelectionPath (path);
	tree.scrollPathToVisible (path);    }

    /**
     ** Finds a child of the specified state satisfying the given
     ** condition.
     **/
    private XCTraceState findChild (XCTraceState state, CTLNode condition)
    {
	XCTraceState child;
	MvSetFactory fac = mc.getMvSetFactory ();

	// go through all the children
	for (Iterator it = Arrays.asList (state.getChildren ()).iterator ();
	     it.hasNext ();)
	    {
		child = (XCTraceState) it.next ();
		// check the condition
		if ( !mc.checkCTL (condition,
				   (MvSet) child.getInfo ().getVariables ())
		     .equals (fac.bot ()) )
		    return child;
	    }
	return null;
    }
}
