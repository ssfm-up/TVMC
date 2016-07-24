package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
//import java.net.*;
import java.beans.*;
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
//import edu.toronto.cs.modelchecker.*;
//import edu.toronto.cs.algebra.*;
//import edu.toronto.cs.tlq.*;

//import edu.toronto.cs.beans.*;
//import edu.toronto.cs.proof.*;

//import edu.toronto.cs.proof2.*;
//import edu.toronto.cs.proof2.CTLProver.*;
//import java.util.prefs.*;
//import javax.swing.filechooser.*;

/**
 ** A simulator reading its input from file.
 **/
public class FileSimulator extends XCSimulator
{
    // the map from properties/variables controlled by the 'user'
    // (i.e. the ones that will not change unless explicitly
    // specified) to their indeces in the spec...
    Vector props;
    // list of steps to simulate
    Vector steps;

    // index of the next step to be processed
    // WARNING: this is not quite the same as currstep + 1
    int currSimStepIndex;
    // current SimStep spec being executed
    SimStep currSimStep;
    // step counter GUI
    JSpinner stepdisplay;
    SpinnerNumberModel stepcounter;

    // the tree model - perhaps will remove this later...
    XCTraceTree tree;
    
    // filechooser used
    JFileChooser filechooser;

    // load, save, apply and run buttons...
    JButton load, save, run, apply;
    Action runAction;

    // test window
    JTextArea script;

    /**
     ** Default constructor.
     **/
    FileSimulator (XCTraceTree t, ActionMap map)
    {
	super (t.getAlgebra (), "Simulator options");

	// XXX - need some reasonable path...
	filechooser = new JFileChooser ();
	tree = t;
	// set up some defualt conditions
	breakcond = new Vector ();
	breakcond.add (ctlfalse); // no breaking condition

	// setup the step counter
	stepcounter = new SpinnerNumberModel (0, 0, 0, 1);
	// connect the stepcounter to 'currstep'
	stepcounter.addChangeListener 
	    (new ChangeListener ()
		{
		    public void stateChanged (ChangeEvent e) 
		    {
			if (stepcounter.getNumber ().intValue () != currstep)
			    {
				currSimStepIndex = 0;
				setStep (stepcounter.getNumber ().intValue ());
				currSimStep = null; // need to reset this
			    }
		    }
		});
	resetScript ();
	stepdisplay = new JSpinner (stepcounter);
	JPanel steppanel = new JPanel ();
	steppanel.setLayout (new BoxLayout (steppanel, BoxLayout.X_AXIS));
	steppanel.add (new JLabel ("Step #: "));
	steppanel.add (stepdisplay);

	// init the buttons
	load = new JButton ((Action) map.get ("Load sim script"));
	save = new JButton ((Action) map.get ("Save sim script"));

	runAction = (Action) map.get ("Run sim script");
	runAction.setEnabled (false);
	run = new JButton (runAction);
	String name = "Apply changes";
	Action applyAction = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    applyScriptChanges ();
		}
	    };
	applyAction.putValue (Action.SHORT_DESCRIPTION, name);
	applyAction.setEnabled (true);
	apply = new JButton (applyAction);
	// create the script area
	script = new JTextArea
	    ("# Empty script\n" +
	     "var:\n" +
	     "# \tlist your controlled variables here\n" +
	     "end var\n\n" +
	     "break:\n" +
	     "# \tthe break conditions (if any)\n" +
	     "# \tshould be specified here\n" +
	     "end break\n\n" +
	     "# Expected format:\n" +
	     "# <stepnum> <command> <var> = <CTL expression>\n" +
	     "# step numbering starts with 0\n" +
	     "# the possible commands are:\n"+
	     "# \tdo - this condition must be met\n" +
	     "# \ttry - meeting this condition is optional\n" +
	     "# \tskipdo and skiptry - same as do and try\n" +
	     "# \t\t but will skip self-loops leading up to them\n");
	script.setEditable (true);
	JScrollPane scriptPane = new JScrollPane
	    (script,
	     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scriptPane.setPreferredSize (new Dimension (450,300));
	// when the script is changed, disable "run"
	script.addKeyListener
	    (new KeyAdapter ()
		{
		    public void keyTyped (KeyEvent e)
		    {
			runAction.setEnabled (false);
		    }
		});

	// add buttons to the panel
	JPanel all = new JPanel ();
	all.setLayout (new BoxLayout (all, BoxLayout.Y_AXIS));
	all.setBorder (StandardFiller.makeWideEmptyBorder ());
	all.add (steppanel);
	all.add (scriptPane);
	all.add (load);
	all.add (save);
	all.add (apply);
	all.add (run);

	getContentPane ().add (all);
	pack ();
    }
    
    /**
     ** Loads a simulation script.
     **/
    public void loadScript ()
    {
	// choose a file
	if (filechooser.showOpenDialog (filechooser) ==
	    JFileChooser.APPROVE_OPTION)
	    {
		BufferedReader f = null;
		try {
		    // create a reader for it and read the script
		    f = new BufferedReader
			(new FileReader (filechooser.getSelectedFile ()));

		    // clear the actual script window
		    script.setText ("");
		    // read the entire script from file
		    String line = f.readLine ();
		    while (line != null)
			{
			    script.append (line + "\n");
			    line = f.readLine ();
			}

		    applyScriptChanges ();
		}
		// deal with exceptions
		catch (FileNotFoundException ex) {
		    showException (filechooser,
				   "File not found",
				   "Simulation script not found",
				   ex);
		}
		catch (IOException ex) {
		    showException (filechooser,
				   "File read failed",
				   "Simulation script load failed.",
				   ex);
		}
		finally {
		    if (f != null) 
			try { f.close (); } catch (IOException ex) {}
		}
	    }
    }

    /**
     ** Saves a simulation script.
     **/
    public void saveScript ()
    {
	// choose a file
	if (filechooser.showSaveDialog (filechooser) ==
	    JFileChooser.APPROVE_OPTION)
	    {
		BufferedWriter f = null;
		try {
		    // create a writer for it and save the script
		    f = new BufferedWriter
			(new FileWriter (filechooser.getSelectedFile ()));
		    String s = script.getText ();
		    f.write (s, 0, s.length ());
		}
		/*
		// deal with exceptions
		catch (FileNotFoundException ex) {
		    showException (filechooser,
				   "File not found",
				   "Simulation script not found",
				   ex);
		}
		*/
		catch (IOException ex) {
		    showException (filechooser,
				   "File write failed",
				   "Simulation script save failed.",
				   ex);
		}
		finally {
		    if (f != null) 
			try { f.close (); } catch (IOException ex) {}
		}
	    }
    }

    /**
     ** Will apply the script changes.
     **/
    private void applyScriptChanges ()
    {
	resetScript ();
	readScript ();
	runAction.setEnabled (true);
    }

    /**
     ** Clears the old script.
     **/
    private void resetScript ()
    {
	props = new Vector ();	
	steps = new Vector ();
	currstep = 0;
	maxstep = 0;
	stepcounter.setValue (new Integer (0));
	stepcounter.setMaximum (new Integer (0));
	currSimStepIndex = 0;
	currSimStep = null;

	// set up some defualt conditions
	stepcond = ctlfalse; // no step allowed
	elsecond = ctlfalse; // no recovery
	breakcond = new Vector ();
	breakcond.add (ctlfalse); // no breaking condition
    }
    

    /**
     ** Resets the current step counter.
     **/
    public void reset ()
    {
	setStep (0);
	currSimStep = null;
    }


    /**
     ** Read and parse the script.
     **/
    private void readScript () // throws IOException
    {
	// what am I reading?
	int meaning = 0;
	CTLNode temp;

	try 
	    {
		BufferedReader r = new BufferedReader
		    (new StringReader (script.getText ()));
		// first read the header with the variables
		readVariables (r);
		// a "break condition" must be present
		readBreak (r);
		// then read the steps to execute
		readSteps (r);
	    }
	catch (Exception e)
	    {
		showException (this, "Script parse error exception",
			       "Script parse error: ", e);
		System.out.println ("Script parse error: " + e);
	    }
    }

    /**
     ** Read the variable declarations.
     **
     ** must start with "var:" and end with "end var"
     **/
    private void readVariables (BufferedReader r) throws Exception
    {
	String line = niceLineRead (r);
	CTLNode temp;

	// skip all the blank and commentted out lines...
	while (line.equals ("")) line = niceLineRead (r);
	if (!line.equals ("var:"))
	    throw new Exception ("'var:' expected instead of '" + line + "'");
	line = niceLineRead (r);
	while (!line.equals ("end var"))
	    {
		// WARNING! perhaps need to be more explicit about this...
		if (line == null) return;

		temp = null;
		if (!line.equals ("")) temp = CTLNodeParser.parse (line);
		if (temp != null && temp instanceof CTLAtomPropNode)
		    props.add (temp);

		line = niceLineRead (r);
	    }
    }

    /**
     ** Reads the break condition.
     **/
    private void readBreak (BufferedReader r) throws Exception
    {
	String line = niceLineRead (r);
	CTLNode temp;

	// skip all the blank and commentted out lines...
	while (line.equals ("")) line = niceLineRead (r);
	if (!line.equals ("break:"))
	    throw new Exception ("'break:' expected instead of '" +line+ "'");
	line = niceLineRead (r);
	breakcond = new Vector ();
	while (!line.equals ("end break"))
	    {
		// WARNING! perhaps need to be more explicit about this...
		if (line == null) return;

		temp = null;
		if (!line.equals ("")) temp = CTLNodeParser.parse (line);
		if (temp != null) breakcond.add (temp);

		line = niceLineRead (r);
	    }

	if (breakcond.size () == 0)
	    breakcond.add (ctlfalse); // no breaking condition
    }

    /**
     ** Reads the steps of the script.
     **/
    private void readSteps (BufferedReader r) throws Exception
    {
	String line = niceLineRead (r);
	String token;
	StringTokenizer st;
	// initialize the step counter, it must be non-decreasing
	int cstep = 0;
	int nstep;
	maxstep = 0;

	while (line != null)
	    {
		// create a tokenizer to read this line
		if (!line.equals (""))
		    {
			st = new StringTokenizer (line);
			// read the step number
			nstep = Integer.parseInt (st.nextToken ());
			if (cstep > nstep)
			    throw new Exception
				("non-decreasing step sequence expected");
			cstep = nstep;
			// read the step
			token = st.nextToken ();
			if (token.equals ("do") ||
			    token.equals ("try") ||
			    token.equals ("skiptry") ||
			    token.equals ("skipdo"))
			    steps.add
				(new SimStep (cstep, token,
					      CTLNodeParser.parse
					      (st.nextToken (""))));
			else throw new Exception ("unrecognized token '" +
						  token + "'");
		    }
		line = niceLineRead (r);
	    }
	if (steps.size () > 0)
	    maxstep = ((SimStep) steps.get (steps.size () - 1)).getNumStep ();
	stepcounter.setMaximum (new Integer (maxstep+1));
    }

    /**
     ** Reads a line from the BufferedReader and trims trailing
     ** whitespace as well as comments.
     **/
    private String niceLineRead (BufferedReader r)
    {
	String s = null;
	int i;

	try 
	    {
		s = r.readLine ();

		if (s != null)
		    {
			// kill the comments
			i = s.indexOf ('#');
			if (i != -1)
			    s = s.substring (0, i);
			// trim the whitespace
			s = s.trim ();
		    }
	    }
	catch (IOException e) { System.out.println ("SimScript error: " + e); }
	return s;
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
     ** Returns the condition to execute the current step fromt he
     ** given state. Then advances the step counter.
     **
    public CTLNode step (XCTraceState s)
    {
	CTLNode cond;

	if (currSimStepIndex >= steps.size ()) reset ();
	if (currstep >= currSimStep.getNumStep ())
	    currSimStep = (SimStep) steps.get (currSimStepIndex++);
	currstep = currSimStep.getNumStep ();

	cond = makeStepCond (currSimStep.getCTL (),
			     tree.stateToArray (s.getInfo ()));

	return cond;
    }

    /**
     ** Makes the appropriate adjustments for one step.
     **/
    public void makeStep (XCTraceState s)
    {
	// if reached the end, make the next step impossible and reset
	if (currstep > maxstep)
	    {
		stepcond = ctlfalse;
		elsecond = ctlfalse;
		reset ();
	    }
	else
	    // set up the appropriate step conditions for the current step.
	    {
		// calculate the step conditions if necessary
		if (currSimStep == null)
		    {
			currSimStep = (SimStep) steps.get (currSimStepIndex);
			// if there was no previous step
			if (stepcond.equals (ctlfalse))
			    {
				stepcond = getUnchangedCTLPart 
				    ((Vector) props.clone (),
				     tree.stateToArray (s.getInfo ()));
				elsecond = ctlfalse;
			    }
		    }
		// if the step should be executed now
		// start making up the step conditions
		if (currSimStep.getNumStep () == currstep)
		    {
			CTLNode doctl = ctltrue;
			CTLNode tryctl = ctltrue;
			CTLNode samectl = ctltrue;
			Vector doV = getDoVector ();
			Vector tryV = getTryVector ();
			Vector changeV = (Vector) doV.clone ();
			changeV.addAll (tryV);
			Vector sameV = getUnchangedVector (changeV);
			// make "do" CTL
			if (doV.size () > 0) doctl = bigAnd (doV);
			// make "try" CTL
			if (tryV.size () > 0) tryctl = bigAnd (tryV);
			// make the common CTL part (what stays the same)
			samectl = getUnchangedCTLPart
			    (sameV, tree.stateToArray (s.getInfo ()));
			stepcond = doctl.and (tryctl).and (samectl);
			if (tryctl == ctltrue)
			    elsecond = ctlfalse; // no "alternative" steps
			else
			    elsecond = doctl.and (samectl);
			// don't forget to advance
			stepcounter.setValue (new Integer (++currstep));
		    }
		else if (currstep > currSimStep.getNumStep ())
		    {
			// advance the SimStep counter according to currstep
			setSimStep (currstep);
			// no need to advance now, will advance in the
			// recursive call
			makeStep (s);
		    }
		else // just advance
		    {
			stepcounter.setValue (new Integer (++currstep));
		    }
	    }
    }
    /**
     ** Helper: finds the appropriate currSimStep.
     **/
    private void findCurrSimStep (XCTraceState s)
    {
	currSimStep = (SimStep) steps.get (currSimStepIndex);
	// if there was no previous step
	if (stepcond.equals (ctlfalse))
	    {
		stepcond = getUnchangedCTLPart 
		    ((Vector) props.clone (),
		     tree.stateToArray (s.getInfo ()));
		elsecond = ctlfalse;
	    }
    }

    /**
     ** Loops can be skipped iff the next command is a "skipdo" or
     ** "skiptry".
     **/
    public boolean canSkipLoops ()
    {
	return (currSimStep != null &&
		currSimStep.getAction ().startsWith ("skip") &&
		currstep < currSimStep.getNumStep ());
    
    }    
    /**
     ** This will skip the unnecessary steps and go to the next
     ** meaningful step in the script if allowed. By default this does
     ** absolutely nothing.
     **/
    public void skip ()
    {
	if (canSkipLoops ())
	    {
		setStep (((SimStep)steps.get (currSimStepIndex))
			 .getNumStep ());
	    }
    }

    /**
     ** Makes a conjunction of all the CTL elements of v.
     **/
    private CTLNode bigAnd (Vector v)
    {
	CTLNode result = ctltrue;
	for (Iterator it = v.iterator (); it.hasNext ();)
	    result = result.and ((CTLNode) it.next ());

	return result;
    }

    /**
     ** Sets the current step to n.
     **/
    public void setStep (int n)
    {
	super.setStep (n);
	//currSimStepIndex = 0;
	setSimStep (n);
	//currSimStep = null;
    }
    /**
     ** Find the SimStep corresponding to the nth step, looking from
     ** the currSimStepIndex and up.
     **/
    private void setSimStep (int n)
    {
	while (currSimStepIndex < steps.size () &&
	       ((SimStep) steps.get (currSimStepIndex)).getNumStep () < n)
	    currSimStepIndex++;

	currSimStep = currSimStepIndex < steps.size () ?
	    (SimStep) steps.get (currSimStepIndex) : null;
    }

    /**
     ** Creates List of CTL formulas corresponding to various
     ** conditions for the current step.
     **/
    private Vector getDoVector ()
    {
	Vector list = new Vector ();
	SimStep stp = (SimStep) steps.get (currSimStepIndex);
	int thisstep = stp.getNumStep ();
	
	// go through all the conditions for the current step
	for (int i = currSimStepIndex; i < steps.size (); i++)
	    {
		stp = (SimStep) steps.get (i);
		if (stp.getNumStep () > thisstep) break;
		// add them to the list if they are "do" or "skipdo"
		else if (stp.getAction ().equals ("do") ||
			 stp.getAction ().equals ("skipdo"))
		    list.add (stp.getCTL ());
	    }

	return list;
    }
    private Vector getTryVector ()
    {
	Vector list = new Vector ();
	SimStep stp = (SimStep) steps.get (currSimStepIndex);
	int thisstep = stp.getNumStep ();
	
	// go through all the conditions for the current step
	for (int i = currSimStepIndex; i < steps.size (); i++)
	    {
		stp = (SimStep) steps.get (i);
		if (stp.getNumStep () > thisstep) break;
		// add them to the list if they are "try" or "skiptry"
		else if (stp.getAction ().equals ("try") ||
			 stp.getAction ().equals ("skiptry"))
		    list.add (stp.getCTL ());
	    }

	return list;
    }

    /**
     ** From the controlled vars list (props) and the change list,
     ** returns the list of unchanged state properties.
     **/
    private Vector getUnchangedVector (Vector change)
    {
	// copy the state vars list
	Vector list = (Vector) props.clone ();
	CTLNode a;

	for (Iterator it = change.iterator (); it.hasNext ();)
	    {
		// get the affected variable name (left side of the '=')
		a = ((CTLNode) it.next ()).getLeft ();
		list.remove (a);
	    }

	return list;
    }

    /**
     ** Creates a CTL formula enforcing the unaffected variables to
     ** stay the same.
     **/
    private CTLNode getUnchangedCTLPart (Vector unchanged,
					 CTLNode [] statevar)
    {
	CTLNode result = ctltrue;
	CTLNode curr;

	for (int i = 0; i < statevar.length; i++)
	    // go through all the controlled properties
	    for (Iterator it = unchanged.iterator (); it.hasNext ();)
		{
		    curr = (CTLNode) it.next ();
		    // copy the current expression for the ones that
		    // didn't change
		    if (curr.equals (statevar [i].getLeft ()))
			result = result.and (statevar [i]);
		}
	return result;
    }
    
    /**
     ** Creates the appropriate condition for the step.
     **/
    private CTLNode makeStepCond (CTLNode cond, CTLNode [] statevar)
    {
	CTLNode result = cond;
	CTLNode same;

	// get the part that stays the same
	same = getUnchangedCTLPart (getUnchangedVector (getDoVector ()),
				    statevar);
	
	return result.and (same);
    }

    /**
     ** If a state that's already in the trace satisfies the step,
     ** keep going.
     **/
    public boolean newStatesOnly () { return true; }
}

/**
 ** Class used to store the simulation steps.
 **/
class SimStep
{
    int numstep;
    String action;
    CTLNode ctl;

    protected SimStep (int n, String a, CTLNode p) throws Exception
    {
	numstep = n;
	action = a;
	if (p != null && p instanceof CTLEqualsNode)
	    ctl = p;
	else throw new Exception ("expected CTL of the form <var> = <expr> ('"
				  + p + "')");
    }

    protected CTLNode getCTL ()
    {
	return ctl;
    }
    protected String getAction ()
    {
	return action;
    }
    protected int getNumStep ()
    {
	return numstep;
    }

    public String toString ()
    {
	return "" + numstep + ": " + action + " " + ctl;
    }
}
