package edu.toronto.cs.simulator;

import edu.toronto.cs.ctl.*;
import java.util.*;

/**
 ** XCTraceState is the class used to store the state info in the trace.
 **/
public class XCTraceState
{
    // parent
    XCTraceState parent;
    // children - supposed to be a Vector of XCTraceState objects
    Vector children;
    // state info
    XCStateInfo info;
    // flag telling that it is a multi-state
    boolean multistate;
    
    /**
     ** Makes an empty trace state node with no parents or children.
     **/
    public XCTraceState ()
    {
	parent = null;
	children = new Vector ();
	info = new XCStateInfo ();
	multistate = false;
    }

    /**
     ** Makes a trace tree node with the specified parent, children
     ** and state info.
     **
     ** @param par - parent
     ** @param si - state info, if null an empty XCStateInfo will be
     ** used instead.
     **/
    public XCTraceState (XCTraceState par, XCStateInfo si, boolean ms)
    {
	parent = par;
	children = new Vector ();
	info = (si == null ? new XCStateInfo () : si);
	multistate = ms;
    }

    /**
     ** If the object compared to is of type XCTraceState and has
     ** <i>equal</i> state info, returns true; otherwise
     ** returs false.
     **
     ** @param o - Object to compare.
     **
     ** @return true if the state info of XCStateInfo objects is
     ** <i>equal</i> and false otherwise.
     **/
    public boolean equals (Object o)
    {
	if (o instanceof XCTraceState)
	    return info.equals (((XCTraceState) o).info);
	else return false;
    }

    /**
     ** Retrieves the state info.
     **
     ** @return the state info.
     **/
    public XCStateInfo getInfo ()
    {
	return info;
    }

    /**
     ** Sets the state info.
     **
     ** @param newinfo - the new state info, if null an empty
     ** XCStateInfo object will be used instead.
     **/
    public void setInfo (XCStateInfo newinfo, boolean ms)
    {
	info = (newinfo == null ? new XCStateInfo () : newinfo);
	multistate = ms;
    }
    
    /**
     ** Tells the number of the next states available to be explored.
     **
     ** @return the number of the next states available to be explored.
     **/
    public int getNumChildren ()
    {
	return children.size ();
    }

    /**
     ** Gets all the next states that are available.
     **
     ** @return the array of all the next states that are available.
     **/
    public XCTraceState [] getChildren ()
    {
	// need for the return type to be the right one even when
	// vector is empty
	return (XCTraceState []) children.toArray (new XCTraceState [0]);
    }

    /**
     ** Retrieves the next state by its index.
     **
     ** @param index - index of the state to retrieve
     ** @return next state with the specified index or null if not found. 
     **/
    public XCTraceState getChild (int index)
    {
	return (XCTraceState) children.get (index);
    }

    /**
     ** Retrieves the specified next state's index.
     **
     ** @param state - state you want the index of
     ** @return the index of the specified state or -1 if not found.
     **/
    public int getChildIndex (XCTraceState state)
    {
	return children.indexOf (state);
    }
    
    /**
     ** Adds another next state.
     **
     ** @param child - new next state to be added.
     **/
    public void addChild (XCTraceState child)
    {
	if (child != null)
	    {
		children.add (child.remove ());
		child.parent = this;
	    }
    }

    /**
     ** Removes the node (with the entire sub-tree) from it's
     ** parent. Note: removing a root does nothing.
     **
     ** @return state node that was removed.
     **/
    public XCTraceState remove ()
    {
	if (parent != null) 
	    {
		parent.children.remove (this);
		parent = null;
	    }
	
	return this;
    }

    /**
     ** Gets the previous state from the trace.
     **
     ** @return the previous state from the trace.
     **/
    public XCTraceState getParent ()
    {
	return parent;
    }

    /**
     ** Allows to specify the previous state in the trace. This is a
     ** convenience method.
     **
     ** @param newparent - the previous state in the trace. 
     **/
    public void setParent (XCTraceState newparent)
    {
	if (parent != newparent)
	    {
		if (newparent != null) newparent.addChild (this);
		else remove ();
	    }
    }    
    
    /**
     ** Creates a string containg the state name as specified in the
     ** state info (not the parent or children info).
     **
     ** @return state name.
     **/
    public String toString ()
    {
	return info.getLabel ();
    }


    /**
     **
     **/
    public boolean isMultiState ()
    {
	return multistate;
    }
}
