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
public class UserSimulator extends XCSimulator
{
    // conditon fields
    JTextField desiredcond, ignoredcond;

    /**
     ** Default constructor.
     **/
    UserSimulator (XCTraceTree t, ActionMap map)
    {
	super (t.getAlgebra (), "Freeform simulator options");

	// create the panel
	JPanel all = new JPanel ();
	all.setLayout (new BoxLayout (all, BoxLayout.Y_AXIS));
	all.setBorder (StandardFiller.makeWideEmptyBorder ());

	// make the textfields, etc.
	desiredcond = new JTextField (ctltrue.toString ());
	ignoredcond = new JTextField (ctlfalse.toString ());

	all.add (Box.createHorizontalStrut (150));
	all.add (new LabelAndText (new JLabel ("Desired: "), desiredcond));
	all.add (new LabelAndText (new JLabel ("Ignored: "), ignoredcond));

	// align things properly
	GUIUtil.alignAllX (all.getComponents (), JComponent.LEFT_ALIGNMENT);
	
	getContentPane ().add (all);
	pack ();
    }
    
    /**
     ** In this case the 'script' is whatever the user specified in
     ** the provided fields. So this method does nothing.
     **/
    public void loadScript ()
    {
    }

    /**
     ** Returns the condition to execute the current step from the
     ** given state. Then advances the step counter.
     **/
    private CTLNode step (XCTraceState s)
    {
	CTLNode desired, ignored;
	
	// read the user specified conditions
	desired = CTLNodeParser.safeParse (desiredcond.getText ());
	if (desired == null) desired = ctltrue;
	// read the user specified conditions
	ignored = CTLNodeParser.safeParse (ignoredcond.getText ());
	if (ignored == null) ignored = ctlfalse;

	return desired.and (ignored.neg ());	
    }    
    /**
     ** Trivial implementation.
     **/
    public void makeStep (XCTraceState s)
    {
	stepcond = step (s);
    }

    /**
     ** Tells whether the multi-states are supported. And yes it is.
     **/
    public boolean isMultiStateSupported () { return true; }

    /**
     ** Must get a new state for the step.
     **/
    public boolean newStatesOnly () { return false; }
    
}

class LabelAndText extends JPanel
{
    LabelAndText (JLabel label, JTextField text)
    {
	super ();

	// set horizontal layout	
	setLayout (new BoxLayout (this, BoxLayout.X_AXIS));

	// init the components
	add (label);
	add (StandardFiller.makeHstrut ());
	add (text);
    }
}
