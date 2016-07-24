package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

//import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

/**
 ** This class provides the inerface to the trace tree.
 **/
public class StateInfoPanel extends JPanel
{
    // labels
    JLabel namelabel, translabel, varlabel;
    // text fields
    JTextField name, trans;
    // variables
    VariablePanel vars;
    
    // model used
    XCTraceTree tree;

    /**
     ** The default constructor.
     **/
    private StateInfoPanel ()
    {
    }

    public StateInfoPanel (XCTraceTree t)
    {
	super ();
	tree = t;
	init ();
    }

    private void init ()
    {
	// component panels
	JPanel namepanel, transpanel;

	// init the labels
	namelabel = new JLabel ("State name:");
	translabel = new JLabel ("Transition into state:");
	varlabel = new JLabel ("State variables:");

	// ... and the components
	name = new JTextField ("");
	name.setEditable (false);
	trans = new JTextField ("not implemented");
	trans.setEditable (false);
	vars = new SimpleVariablePanel (tree);

	// deal with sizes
	Dimension d = (Dimension) name.getMinimumSize ().clone ();
	d.setSize (1500, d.getHeight ());
	name.setMaximumSize (d);
	trans.setMaximumSize (d);

	// set the layout and border
	setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));

	// create the sub-panels for the components
	// name
	namepanel = new JPanel ();
	namepanel.setLayout (new BoxLayout (namepanel, BoxLayout.X_AXIS));
	namepanel.add (namelabel);
	namepanel.add (StandardFiller.makeHstrut ());
	namepanel.add (name);
	// transition
	transpanel = new JPanel ();
	transpanel.setLayout (new BoxLayout (transpanel, BoxLayout.X_AXIS));
	transpanel.add (translabel);
	transpanel.add (StandardFiller.makeHstrut ());
	transpanel.add (trans);

	/*
	// variables
	varpanel = new JPanel ();
	varpanel.setLayout (new BoxLayout (varpanel, BoxLayout.Y_AXIS));
	varpanel.add (varlabel);
	varpanel.add (StandardFiller.makeVstrut ());
	varpanel.add (vars);
	GUIUtil.alignAllX (varpanel.getComponents (),
			   Component.LEFT_ALIGNMENT);
	*/

	// add the components
	add (namepanel);
	add (transpanel);
	add (StandardFiller.makeVstrut ());
	add (varlabel);
	add (StandardFiller.makeVstrut ());
	add (vars);

	GUIUtil.alignAllX (getComponents (),
			   Component.LEFT_ALIGNMENT);
	setEnabled (true);
    }

    /**
     ** Displays the information about the given state.
     ** @param state - state to show info for.
     **/
    public void show (XCTraceState state)
    {
	XCStateInfo info = state.getInfo ();
	
	name.setText (info.getLabel ());
	trans.setText (info.getTransition ().toString ());
	vars.show (state);
    }

    /**
     ** Clears the info display.
     **/
    public void clear ()
    {
	name.setText ("");
	trans.setText ("");
	vars.clear ();
    }
}
