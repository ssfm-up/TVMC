package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
//import java.net.*;
//import java.beans.*;
//import java.lang.reflect.*;

import javax.swing.*;
//import javax.swing.tree.*;
import javax.swing.event.*;

//import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;
//import edu.toronto.cs.ctl.antlr.CTLNodeParser;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
//import edu.toronto.cs.algebra.*;
//import edu.toronto.cs.tlq.*;

//import edu.toronto.cs.beans.*;
//import edu.toronto.cs.proof.*;

//import edu.toronto.cs.proof2.*;
//import edu.toronto.cs.proof2.CTLProver.*;
//import java.util.prefs.*;
//import javax.swing.filechooser.*;

/**
 ** A simulator taking it's input from user.
 **/
public class DummySimulator extends XCSimulator
{
    /**
     ** Default constructor.
     **/
    DummySimulator (XCTraceTree t, ActionMap map)
    {
	super (t.getAlgebra (), "Dummy simulator");

	// create the panel
	JPanel all = new JPanel ();
	all.setLayout (new BoxLayout (all, BoxLayout.Y_AXIS));
	all.setBorder (StandardFiller.makeWideEmptyBorder ());

	all.add (new JLabel ("The dummy simulator will pick any available state as next state."));
	
	getContentPane ().add (all);
	pack ();
    }
    
    /**
     ** No script here.
     **/
    public void loadScript ()
    {
    }

    /**
     ** Trivial condition.
     **
    public CTLNode step (XCTraceState s)
    {
	return ctltrue;
    }
    /**
     ** Trivial implementation.
     **/
    public void makeStep (XCTraceState s)
    {
	stepcond = ctltrue;
    }
   
    /**
     ** Tells whether the multi-states are supported. And yes it is.
     **/
    public boolean isMultiStateSupported () { return true; }

    /**
     ** Must get a new state for the step.
     **/
    public boolean newStatesOnly () { return true; }
    
}
