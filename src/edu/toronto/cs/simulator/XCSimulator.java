package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
//import java.io.*;
//import java.net.*;
//import java.beans.*;
//import java.lang.reflect.*;

import javax.swing.*;
//import javax.swing.tree.*;
import javax.swing.event.*;

//import edu.toronto.cs.util.*;
//import edu.toronto.cs.util.gui.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;
//import edu.toronto.cs.mvset.*;
//import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
//import edu.toronto.cs.tlq.*;

//import javax.swing.filechooser.*;

/**
 ** An interface for simulators.
 **/
public abstract class XCSimulator extends JFrame
{
    // internal step counter and it's upper bound
    int currstep, maxstep;

    // condition associated with a step and another one in case the step fails
    CTLNode stepcond, elsecond;
    // stopping condition
    Vector breakcond;

    // useful constants
    CTLNode ctltrue, ctlfalse;

    protected XCSimulator (IAlgebra al, String s)
    {
	super (s);

	currstep = 0;
	maxstep = 0;
	
	ctlfalse = CTLFactory.createCTLConstantNode (al.bot ());
	ctltrue = CTLFactory.createCTLConstantNode (al.top ());
	stepcond = ctlfalse; // can't expand
	elsecond = ctlfalse; // no alternatives
	breakcond = new Vector ();
	breakcond.add (ctltrue); // break after each step
    }

    /**
     ** Loads a simulation script.
     **
    public abstract void loadScript ();

    /**
     ** Resets the current step counter.
     **/
    public void reset ()
    {
	setStep (0);
    }
    /**
     ** Gets the number of the current step.
     **/
    public int getStep ()
    {
	return currstep;
    }
    /**
     ** Sets the current step to n.
     **/
    public void setStep (int n)
    {
	currstep = (n > maxstep) ? 0 : n;
    }

    /**
     ** Returns the condition to execute the current step from the
     ** given state. Then advances the step counter.
     **
    public abstract CTLNode step (XCTraceState s);

    /**
     ** Makes the appropriate adjustments for one step.
     **/
    public abstract void makeStep (XCTraceState s);

    /**
     ** Specifies whether steps that result in self-loops can
     ** sometimes be skipped.
     **/
    public boolean canSkipLoops ()
    {
	return false;
    }
    /**
     ** This will skip the unnecessary steps and go to the next
     ** meaningful step in the script if allowed. By default this does
     ** absolutely nothing.
     **/
    public void skip ()
    {}


    /**
     ** Gets the condition for the current step.
     **/
    public CTLNode getStepCond ()
    {
	return stepcond;
    }
    /**
     ** Gets the alternative condition for the current step (in case
     ** the first one fails).
     **/
    public CTLNode getElseCond ()
    {
	return elsecond;
    }
    /**
     ** Gets the break condition(s).
     **/
    public CTLNode [] getBreakCond ()
    {
	return (CTLNode []) breakcond.toArray (new CTLNode [0]);
    }

    /**
     ** Should live somewhere in the utils probably...
     **/
    public static void showException (Component parentComponent, String title,
				      String message, Exception ex)
    {
	ex.printStackTrace ();
	String displayMessage = message + "\n" + ex.getMessage ();
	JOptionPane.showMessageDialog (parentComponent, 
				       displayMessage,
				       title,
				       JOptionPane.ERROR_MESSAGE);
    }

    /**
     ** Tells whether the multi-states are supported. It is 'false' by
     ** default.
     **/
    public boolean isMultiStateSupported () { return false; }

    /**
     ** Tells whether a new state is always expected for the current step
     ** or whether existing states may also be used to satisfy the step.
     **/
    public abstract boolean newStatesOnly ();
}
