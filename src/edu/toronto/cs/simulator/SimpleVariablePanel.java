package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;

//import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

/**
 ** Simple class to display variables.
 **/
public class SimpleVariablePanel extends VariablePanel 
{
    // info will be stored in a list
    JList vars;

    // model used
    XCTraceTree tree;

    /**
     ** Default constructor.
     **/
    private SimpleVariablePanel () {}
    public SimpleVariablePanel (XCTraceTree t)
    {
	super ();
	tree = t;
	init ();
    }
    
    private void init ()
    {
	// potentially will add some label...
	setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));

	vars = new JList ();
	JScrollPane scroll = new JScrollPane
	    (vars, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	// set some reasonable min size - XXX - CAUTION - MAGIC NUMBERS!!!
	setMinimumSize (new Dimension (400, 200));

	add (scroll);
    }

    /**
     ** Displays the variables from the StateInfo.
     ** @param si - state info containing the variable information to
     ** display.
     **/
    public void show (XCTraceState s)
    {
	if (s.isMultiState ())
	    clear ();
	else
	    vars.setListData (tree.stateToArray (s.getInfo ()));
    }

    /**
     ** Clears the variable panel view.
     **/
    public void clear ()
    {
	vars.setListData (new Vector (0));
    }
}
