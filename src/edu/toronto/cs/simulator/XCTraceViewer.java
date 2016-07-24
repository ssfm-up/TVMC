package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;
//import java.util.*;
//import java.io.*;
//import java.net.*;
//import java.beans.*;
//import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

import edu.toronto.cs.ctl.*;
//import edu.toronto.cs.ctl.antlr.*;
//import edu.toronto.cs.ctl.antlr.CTLNodeParser.*;
//import edu.toronto.cs.mvset.*;
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
 ** Class used to visualize the trace for XChek.
 **/
public class XCTraceViewer extends JFrame
{
    final static String TRACEVIEWERNAME = "XC Trace Viewer";

    // toolbar
    TraceToolBar toolbar;

    // displays the tree
    TraceTreeBrowser treebrowser;
    // simulation condition panel
    ConditionPanel conditionpanel;
    // displays state info
    StateInfoPanel stateinfo;

    // various pre-loaded simulators
    DummySimulator dummysim;
    UserSimulator usersim;
    FileSimulator scriptsim;
    // current simulator in use
    XCSimulator sim;

    // modelcheker used to look at the model
    MvSetModelChecker mc;
    // various actions that can be performed
    ActionMap actionMap;

    // prompt for condition?
    boolean prompt;
    // useful constants
    CTLNode ctltrue, ctlfalse;


    /**
     ** Construct a traceviewer for a given model.
     **/
    public XCTraceViewer (MvSetModelChecker modelChecker)
    {
	super (TRACEVIEWERNAME);
	mc = modelChecker;
	init ();
    }

    /**
     ** This is where the initialization magic happens.
     **/
    private void init ()
    {
	// store useful constants
	ctlfalse = CTLFactory.createCTLConstantNode
	    (mc.getXKripke ().getAlgebra ().bot ());
	ctltrue = CTLFactory.createCTLConstantNode
	    (mc.getXKripke ().getAlgebra ().top ());

	//BinaryTreeToCTLConverter.setAlgebra (mc.getXKripke ().getAlgebra ());

	// make the layout a bit nicer (more space between everything)
	JPanel all = new JPanel ();
	all.setLayout (new BoxLayout (all, BoxLayout.Y_AXIS));
	/*
	((BorderLayout) all.getLayout ()).setHgap (StandardFiller.getSize ());
	((BorderLayout) all.getLayout ()).setVgap (StandardFiller.getSize ());
	*/

	all.setBorder (StandardFiller.makeWideEmptyBorder ());

	// initialize the actions
	initActionMap ();

	// -- Now add all of the panels that make up the trace viewer.
	// XXX maybe we'll have a toolbar XXX
	//all.add (createToolbar ());

	// this is where the trace tree is gonna live
	all.add (createTreeBrowser ());
	// condition panel
	all.add (createConditionPanel ());
	// -- this is where the state info is gonna live
	all.add (createStateInfoBrowser ());

	// init the simulators
	dummysim = new DummySimulator (treebrowser.getTreeModel (), actionMap);
	usersim = new UserSimulator (treebrowser.getTreeModel (), actionMap);
	scriptsim = new FileSimulator
	    (treebrowser.getTreeModel (), actionMap);
	setSimulator (dummysim);
	

	// align all to the center
	
	GUIUtil.alignAllX (all.getComponents (), Component.CENTER_ALIGNMENT);

	// -- add main panel to our frame -- seems redundant
	getContentPane ().add (all);

	// -- indicate what to do when this frame is closed
	setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

	setEnabled (true);
    }

    /**
     ** Adds the actiones used by the Trace Viewer to the action map.
     **/
    private void initActionMap ()
    {
	actionMap = new ActionMap ();

	Action action;
	String name;

	// -- update the state view
	name = "Update state view";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    updateStateView ();
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);

	// -- use simulation conditions?
	/*
	name = "Get simulation conditions";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    // if they are turned off...
		    if (!conditionpanel.isEnabledCondition ())
			{
			    // set default conditions
			    treebrowser.setDesiredCond (ctltrue);
			    treebrowser.setIgnoreCond (ctlfalse);
			    treebrowser.setBreakCond (ctltrue);
			}
		    else
			{
			    treebrowser.setDesiredCond
				(conditionpanel.getDesiredCond ());
			    treebrowser.setIgnoreCond
				(conditionpanel.getIgnoreCond ());
			    treebrowser.setBreakCond
				(conditionpanel.getBreakCond ());
			}
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);
	*/
	// -- loads the simulation script
	name = "Load sim script";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    if (sim instanceof FileSimulator)
			((FileSimulator) sim).loadScript ();
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);
	// -- saves the simulation script
	name = "Save sim script";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    if (sim instanceof FileSimulator)
			((FileSimulator) sim).saveScript ();
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);

	// -- runs the simulation script
	name = "Run sim script";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    // reset the script before running
		    //sim.reset ();
		    treebrowser.runScript (sim);
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (false);
	actionMap.put (name, action);

	// -- use default dummy sim conditions
	name = "Get states randomly";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    setSimulator (dummysim);
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);
	// -- specify sim conds by hand
	name = "Specify simulation conditions by hand";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    setSimulator (usersim);
		    sim.show ();
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);
	// -- fire up the sim script handler
	name = "Use an xsim script";
	action = new AbstractAction (name, null)
	    {
		public void actionPerformed (ActionEvent e)
		{
		    setSimulator (scriptsim);
		    sim.show ();
		}
	    };
	action.putValue (Action.SHORT_DESCRIPTION, name);
	action.setEnabled (true);
	actionMap.put (name, action);
    }

    /**
     ** Creates a toolbar.
     **/
    private Component createToolbar ()
    {
	toolbar = new TraceToolBar (actionMap);
	
	return toolbar;
    }
    
    /**
     ** Creates the part of the GUI that deals with browsing the trace tree.
     **/
    private Component createTreeBrowser ()
    {
	treebrowser = new TraceTreeBrowser (mc, actionMap);

	return treebrowser;
    }

    /**
     ** Creates a panel for specifying conditions of simulation.
     **/
    private Component createConditionPanel ()
    {
	conditionpanel = new ConditionPanel (actionMap);
	
	return conditionpanel;
    }
    

    /**
     ** Creates the part of the GUI that deals with browsing the trace state.
     **/
    private Component createStateInfoBrowser ()
    {
	stateinfo = new StateInfoPanel (treebrowser.getTreeModel ());

	return stateinfo;
    }

    /**
     ** Updates the selected state view.
     **/
    private void updateStateView ()
    {
	XCTraceState state;			
			
	// get the selected state
	state = treebrowser.getSelectedState ();
	if (state == null)
	    stateinfo.clear ();
	else
	    stateinfo.show (state);
    }
    
    /**
     ** Set a simulator to use for state expansion.
     **/
    private void setSimulator (XCSimulator s)
    {
	// hide the old one
	if (sim != null) sim.hide ();
	// set the new one
	sim = s;
	treebrowser.setSimulator (sim);
	// disable "run" by default
	Action runAction = (Action) actionMap.get ("Run sim script");
	runAction.setEnabled (false);
    }

    /* Static methods */

    /*
    public static void main (String [] args)
    {
	//Create the top-level container and add contents to it.
	XCModel model = new XCModel ();
	XCTraceViewer app = new XCTraceViewer (model);
	JTree tree;
    
	//Finish setting up the frame, and show it.
	app.pack();
	app.setVisible (true);
    }
    */
}
