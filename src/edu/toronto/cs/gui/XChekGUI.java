package edu.toronto.cs.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.beans.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;
import edu.toronto.cs.ctl.antlr.CTLNodeParser.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.tlq.*;
import edu.toronto.cs.simulator.*;

import edu.toronto.cs.beans.*;
import edu.toronto.cs.proof.*;

import edu.toronto.cs.proof2.*;
import edu.toronto.cs.proof2.CTLProver.*;

// import edu.toronto.cs.cfa.*;
// import edu.toronto.cs.cfa.CFAMvSetFactory.*;

import java.util.prefs.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

//preferences API
import java.util.prefs.*;



/**
 ** The GUI 
 **/
public class XChekGUI extends JFrame
{
  
  //preferences constants
  public static final Preferences PREFS 
    = Preferences.userRoot ().node ("edu/toronto/cs/gui/XChekGUI");
  //preferences constants for Model Picker
  public static final Preferences MODEL_PREFS 
    = Preferences.userRoot ().node ("edu/toronto/cs/gui/ModelPicker");


  
  // -- constants for preference names
  public static final String DEF_DIR = "defDir";
  public static final String MODEL_DEF_DIR = "modelDefDir";
  public static final String CTL_DEF_DIR = "ctldefDir";  
  public static final String CTL_DEF_FILE = "ctldefFile";  
  public static final String CNTR_EX_BL  = "cntrExBl";
  public static final String FAIRNESS_BL  = "fairnessBl";
  public static final String EXP_FEAT_BL  = "expFeatBl";
  public static final String LOOK_AND_FEEL = "lookAndFeel";
  // -- indicates if we are running with a remote X server 
  // -- and should disable double-buffering
  public static final String REMOTE_X = "remoteX";

  public static final String MODELSLIST = "modelsList";



  // name of the application
  final static String GUINAME = "XChek";


  // -- panel that holds CTL expressions
  InputPanel ctlin;
  // -- panel that holds an output of the model checker
  OutputPanel output;
  // -- XXX some options???
  OptionsPanel optionspanel;

  // -- the menu bar
  XChekMenuBar menu;
  // -- the tool bar
  XChekToolbar toolbar;

  // a window to work with traces
  XCTraceViewer traceviewer;

  // -- a window to display model information
  //ModelInfoFrame modelInfo = null;

  // -- a window to display lattice information
  //LatticeDisplay lattice = null;

  // -- the name of the model under analysis
  String modelName = "";
  
   
  // maps the name of the action to the appropriate Action object
  ActionMap actionMap;
  
  // options for the XChek
  Map options = new HashMap ();

  // -- current working directory
  File currentDir = new File (System.getProperty ("user.dir"));

  // -- file chooser dialog parametrised by current directory
  JFileChooser filechooser = new JFileChooser (currentDir);

  // -- XXX user action chains see util package for that

  // -- chain of actions to close the model
  UserActChain closeModelChain;
  // -- chain of actions to quit the model checker
  UserActChain quitActionChain;
  // -- chain of actions to load a model 
  UserActChain loadModelChain;

  // -- version of the GUI -- XXX this should not be here like that
  final static String version = "0.7?!";


  // XXX We should not bundle application logic with its GUI but
  // XXX we do this for now anyways

  ModelCompiler compiler;
  XKripkeStructure xkripke;
  java.util.List ctlReWriters;
  MvSetModelChecker modelChecker;
  PlaceholderReWriter phReWriter = null;
  MvSetPlaceholderReWriter upsetPhReWriter = null;


  // -- public constructor
  public XChekGUI ()
  {
    super (GUINAME);
    init ();
    
    // -- resets the application
    resetApp ();
  }

  private void resetApp ()
  {
        
    compiler = null;
    xkripke = null;
    modelChecker = null;
    ctlReWriters = new LinkedList ();
    setEnabled (false);
  }
  
  private void init ()
  {
    // -- intialize the application -- load known model compilers
    initApp ();

    // -- initialize user chains
    initUserActChains ();

    // -- initialize action map
    initActionMap();
   
    // -- create the menu
    menu = new XChekMenuBar (actionMap);
    // -- create the tool bar
    toolbar = new XChekToolbar (actionMap);

    // init the traceviewer
    traceviewer = null;

    // -- set menu to current frame
    setJMenuBar (menu);

    // make the layout a bit nicer (more space between everything)
    JPanel all = new JPanel ();
    all.setLayout (new BorderLayout ());
    ((BorderLayout) all.getLayout ()).setHgap (StandardFiller.getSize ());
    ((BorderLayout) all.getLayout ()).setVgap (StandardFiller.getSize ());    
    all.setBorder (StandardFiller.makeWideEmptyBorder ());

    // XXX I believe the createXXX functions set global variables as 
    // XXX a side effect -- not good

    // -- Now add all of the panels that make up the model-checker
    all.add (toolbar, BorderLayout.NORTH);
    all.add (createOutputArea (),BorderLayout.CENTER);
    // -- this is where we can specify fairness/macros/etc
    all.add (createOptions (), BorderLayout.EAST);
    // -- this is where the user enters CTL
    all.add (createCTLInteraction (), BorderLayout.SOUTH);



    // -- add main panel to our frame -- seems redundant
    getContentPane ().add (all);

    // -- indicate what to do when this frame is closed
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

    // XXX this does not seem to belong in here
    
    // -- set default initialization options
    options = getDefaultOptions ();

    // load up default CTL History file from Preferences.
    if(PREFS.get (CTL_DEF_FILE, "") != "")
      {
	File ctlQueryFile = new File(PREFS.get (CTL_DEF_FILE, ""));
	if (ctlQueryFile.isFile ())
	  loadCTLHistory (ctlQueryFile);
      }


    
    setEnabled (false);
  }  

  private void initApp ()
  {
    
  }
  

  /**
   ** Initialize the set of actions and add them to actionMap
   **/
  private void initActionMap ()
  {

    actionMap = new ActionMap();
    
    Action action;

    // -- open model using model compiler
    action = loadModelChain;
    action.putValue (Action.SMALL_ICON, new ImageIcon (openBDDIcon));
    action.putValue (Action.SHORT_DESCRIPTION, "Open a model...");
    action.putValue (Action.NAME, "Open a model...");
    actionMap.put ("Open a model...", action);

    // Load fairness conditions action
    action = new AbstractAction ("Load fairness conditions",
				 new ImageIcon (loadFairnessIcon))
                                 { 
                                   public void actionPerformed (ActionEvent e)
				    {
				      loadFairness ();
				    }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Load fairness conditions");
    action.setEnabled(false);
    actionMap.put ("Load fairness conditions", action);
  
    // Save fairness conditions action
    action = new AbstractAction ("Save fairness conditions",
				 new ImageIcon (saveFairnessIcon))
                                 { 
                                   public void actionPerformed (ActionEvent e)
				    {
				      saveFairness ();
				    }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Save fairness conditions");
    action.setEnabled(false);
    actionMap.put ("Save fairness conditions", action);

    // Load CTL history action
    action = new AbstractAction ("Load CTL history",
				 new ImageIcon (loadCTLIcon))
                                 { 
                                   public void actionPerformed (ActionEvent e)
				    {
				      loadCTLHistory ();
				    }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Load CTL history");
    action.setEnabled(true);
    actionMap.put ("Load CTL history", action);
  
    // Save fairness conditions action
    action = new AbstractAction ("Save CTL history",
				 new ImageIcon (saveCTLIcon))
                                 { 
                                   public void actionPerformed (ActionEvent e)
				    {
				      saveCTLHistory ();
				    }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Save CTL history");
    action.setEnabled(true);
    actionMap.put ("Save CTL history", action);

    // Close model action
    action = closeModelChain;
    action.putValue (Action.SHORT_DESCRIPTION, "Close");
    action.putValue (Action.NAME, "Close");
    actionMap.put ("Close model", action);

    // Preferences action
    action = new AbstractAction ("Preferences",
				 new ImageIcon (preferencesIcon))
                                 { 
                                   public void actionPerformed (ActionEvent e)
				    {
				      openPreferences ();
				    }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Preferences");
    action.putValue(Action.NAME, "Preferences");
    actionMap.put ("Preferences", action);

    // Quit action
    action = quitActionChain;
    action.putValue (Action.SMALL_ICON, new ImageIcon (quitIcon));
    action.putValue(Action.SHORT_DESCRIPTION, "Quit");
    action.putValue(Action.NAME, "Quit");
    actionMap.put ("Quit", action);
    
    // Show model info action
    action = new AbstractAction ("Model info", new ImageIcon (modelInfoIcon))
                                 {
				   public void actionPerformed (ActionEvent e)
				   {
				     showModelInfo ();
				   }
                                 };

    // XXX The code to display model info is not written yet, so we
    // disable this action in the menu
    action.setEnabled (false);
    action.putValue(Action.SHORT_DESCRIPTION, "Display model info");
    actionMap.put ("Model info", action);

    // Show lattice info action
    action = new AbstractAction ("Algebra info", new ImageIcon (latticeIcon))
                                 {
				   public void actionPerformed (ActionEvent e)
				   {
				     showAlgebraInfo ();
				   }
                                 };
    action.putValue(Action.SHORT_DESCRIPTION, "Display algebra info");
    action.setEnabled(false);
    actionMap.put ("Algebra info", action);


    // Show variables action
    action = new AbstractAction ("Show variables", new ImageIcon (varIcon))
                                 {
				   public void actionPerformed (ActionEvent e)
				   {
				     showVariables ();
				   }
                                 };
    
    // XXX There is no code to show variables. This action is disabled
    // to avoid confussion.
    action.setEnabled (false);
    actionMap.put ("Show variables", action);

    // Show general help
    action = new AbstractAction ("Help", null)
                                 {
				   public void actionPerformed (ActionEvent e)
				   {
				     showHelp ();
				   }
                                 };
    
    actionMap.put ("Help", action);
    // About
    action = new AbstractAction ("About", null)
                                 {
				   public void actionPerformed (ActionEvent e)
				   {
				     showAbout ();
				   }
                                 };
    
    actionMap.put ("About", action);
  }


  /**
   ** Initalize action chains we have
   **/
  private void initUserActChains ()
  {
    // -- closing a model
    initCloseModel ();
    // -- quiting the application
    initQuitAction ();
    // -- loading a model
    initLoadModelChain ();
        
  }

  private void initLoadModelChain ()
  {
    loadModelChain = new UserActElement ("Open model.", true, true)
      {
	public boolean doConfirm (UserAct stateInfo)
	{
	  return true;
	}
	public boolean doExecute (UserAct stateInfo)
	{
	  
	  ModelCompiler compiler = null;
	  try
	    {
	      compiler = 
		new PickModelCompiler().pickModelCompiler (XChekGUI.this);
		//new LoadModelFromFile ().loadModel (new JFileChooser ());
	      
	    }
	  catch (Exception ex){
	    showException (XChekGUI.this, 
			   "Error loading compiler",
			   "Could not intialize the compiler", ex);
	    return false;
	  }

	  // -- someone canceled loading
	  if (compiler == null) return false;
	  
	  // -- set model compiler
	  setModelCompiler (compiler);
          XChekGUI.this.showText (getXKripke ().getName() + " loaded");
	  XChekGUI.this.setEnabled (true);
	  
	  return true;
	}
      };
  }

  private void initCloseModel ()
  {
    // XXX do better Java :)
    final XChekGUI app = this;

    // First, is there ctl to save?
    closeModelChain = new UserActElement ("Closing model.", true, true) 
      {	
	public boolean doConfirm (UserAct stateinfo)
	{
	  if (ctlin.hasHistory ())
	    {
	      // -- ask the user if should save
	      stateinfo.setExpress ();
	      int answer = JOptionPane.showConfirmDialog 
		(app,
		 "Would you like to save CTL history?",
		 stateinfo.getName (),
		 JOptionPane.YES_NO_CANCEL_OPTION,
		 JOptionPane.QUESTION_MESSAGE);

	      if (answer == JOptionPane.YES_OPTION) 
		// save the CTL history
		return saveCTLHistory ();
	    }
	  return true;
	}
      };
    
    // -- save fairness
    closeModelChain = closeModelChain
      .add (new UserActElement (true, true) 
	{
	  public boolean doConfirm (UserAct stateinfo)
	  {
	    if (optionspanel.getFairness ().length != 0)
	      {
		stateinfo.setExpress ();
		int answer = JOptionPane.showConfirmDialog
		  (app, "Would you like to save fairness conditions?",
		    ((UserAct)stateinfo).getName (), 
		   JOptionPane.YES_NO_CANCEL_OPTION,
		   JOptionPane.QUESTION_MESSAGE);
		if (answer == JOptionPane.YES_OPTION)
		  return app.saveFairness ();
		else if (answer == JOptionPane.CANCEL_OPTION)
		  return false;
	      }
	    return true;
	  } 
	})
      // -- Are you sure you want to close the model?! 
      // -- this should be first, not last
      .add (new FinalUserActElement (false, true) 
	{	  	  
	  public boolean doConfirm (UserAct stateinfo)
	  {
	    if (((UserAct)stateinfo).isExpress ())
	      return true;
	    
	    // XXX If no MCManager, then nothing happened so why ask?!
// 	    if (mcman == null)
// 	      return true;

	    stateinfo.setExpress ();
	    if (JOptionPane.showConfirmDialog 
		(app, "This will close the model. Are you sure?",
		 ((UserAct)stateinfo).getName (), JOptionPane.YES_NO_OPTION,
		 JOptionPane.QUESTION_MESSAGE)
		!= JOptionPane.YES_OPTION)
	      return false;
	    
	    return true;
	  }
	  
	  // -- Execute the chain
	  public boolean doExecute (UserAct stateinfo)
	  {

	    // XXX I think once we got here all confirmations have passed
	    // XXX so we simply reset the model-checker to a consistent
	    // XXX state and garbage collect everything
// 	    if (mcman == null)
// 	      return true;
	    
	    XChekGUI.this.setEnabled (false);
	    System.gc ();
	    output.append ("Model closed. \n");
	    return true;
	  }
	});
  }
  
  private void initQuitAction ()
  {
    final XChekGUI app = this;
    quitActionChain = new UserActElement ("Quitting XCheck.", false, true)  
      {
	
	public boolean doConfirm (UserAct stateinfo)
	{
	  // -- see if we want to close the model first
	  UserAct newAct = new UserAct (stateinfo.getName (), true,
					stateinfo.getActionEvent ());
	  return closeModelChain.confirm (newAct);
	}
	
	public boolean doExecute (UserAct stateinfo)
	{
	  // -- actually close the model if everything is ok
	  return closeModelChain.execute (stateinfo);
	}

      }
    .add (new FinalUserActElement (true, true)
      {
	public boolean doConfirm (UserAct stateinfo)
	{
	  stateinfo.setExpress ();
	  // -- ask if the user is sure :)
	  return 
	    JOptionPane.showConfirmDialog 
	    (app, "This will quit XCheck. Are you sure?",
	     ((UserAct)stateinfo).getName (), JOptionPane.YES_NO_OPTION,
	     JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
	
	public boolean doExecute (UserAct stateinfo)
	{
	  // -- save model checker state before exiting
	  //saveLoaders ();
	  // XXX Really not clean exit! 
	  // XXX Should send event somewhere or something
	  System.exit (0);
	  return true; 
	}
      });
  }
  

  /**
   ** Creates the part of the GUI that deals with inputting a CTL
   ** formula and running the model-checker on it ("run" button).
   **/
  private Component createCTLInteraction ()
  {
    ctlin = new InputPanel ();
    GUIUtil.setEnabled (ctlin, false); // disable CTL input by default
    ctlin.addRunActionListener (new ActionListener ()
				{
				  public void actionPerformed (ActionEvent e)
				    {
				      runOnCTL ();
				    }
				});
    ctlin.addTraceActionListener (new ActionListener ()
				{
				  public void actionPerformed (ActionEvent e)
				    {
				      trace ();
				    }
				});    
    // XXX why CTL?!
    ctlin.addCTLKeyListener (new KeyAdapter ()
			     {
			       public void keyPressed (KeyEvent e)
				 {
				   if (e.getKeyCode () == KeyEvent.VK_ENTER)
				     runOnCTL ();
				 }
			     });
    return ctlin;
  }

  private Component createOutputArea ()
  {
    output = new OutputPanel ();
    return output;
  }

  /**
   ** Creates model-checking options - fairness, macros.
   **/
  private Component createOptions ()
  {
    optionspanel = new OptionsPanel ();
    return optionspanel;
  }


  public void setModelCompiler (ModelCompiler v)
  {
    // -- new model compiler so reset most of the state
    compiler = null;
    xkripke = null;
    ctlReWriters = new LinkedList ();
    modelChecker = null;
    phReWriter = null;

    // -- garbage collect
    System.gc ();

    compiler = v;

    // XXX I'm not proposing this as an example of good programming style
    ctlReWriters.add (getXKripke ());

    // -- add rewriters that get rid of operators we never want to see 
    // -- later on
    ctlReWriters.add (new CTLWeakUntilExpander ());

    // -- rewrite everything using EX, EU and EG so 
    // -- that proof generator works    
    if (getXKripke ().getAlgebra ().getClass () == UpSetAlgebra.class)
      {
	ctlReWriters.add (new NormalFormRewriter ());
	phReWriter = 
	  new PlaceholderReWriter ((UpSetAlgebra)getXKripke ().getAlgebra ());
	ctlReWriters.add (phReWriter);
      }
    else if (getXKripke ().getAlgebra ().getClass () 
	     == MvSetUpsetAlgebra.class)
      {
	ctlReWriters.add (new NormalFormRewriter ());
	upsetPhReWriter = new MvSetPlaceholderReWriter 
	  ((MvSetUpsetAlgebra)getXKripke ().getAlgebra ());
	ctlReWriters.add (upsetPhReWriter);
      }
     else //if (!PREFS.getBoolean (EXP_FEAT_BL, false))
       ctlReWriters.add (new ExistentialRewriter ());
    

    // -- remove EF, AF and ->
    ctlReWriters.add 
      (new CTLUntilExpander (getXKripke ().getMvSetFactory ().top ()));
  }
  public XKripkeStructure getXKripke ()
  {
    if (xkripke == null)
      {
	StopWatch sw = new StopWatch ();
	xkripke = compiler.compile ();
	showText ("Compiled in ", "regular");
	showText (sw + "\n", "italic");
      }
    
    return xkripke;
  }
  public MvSetModelChecker getModelChecker ()
  {
    if (modelChecker == null)
      modelChecker = new MvSetModelChecker (getXKripke ());
    return modelChecker;
  }
  public CTLNode rewriteCTL (CTLNode ctl)
  {
	System.out.println ("Rewriting a formula with fairness: " + 
						Arrays.asList (ctl.getFairness ()));
	
    CTLNode result = ctl;
    for (Iterator it = ctlReWriters.iterator (); it.hasNext ();)
      {
		CTLReWriter rewriter = (CTLReWriter)it.next ();
		System.out.println ("Rewriting using: " + rewriter.getClass ());
		result = rewriter.rewrite (result);	
		System.out.println ("After rewriting fairness is: " + 
							Arrays.asList (result.getFairness ()));
		assert (result != null) : rewriter.getClass () + ": returned null";
      }

    new SyntaxChecker ().rewrite (result);
	System.out.println ("After syntax checking: " + 
						Arrays.asList (result.getFairness ()));
	
    return result;
  }
  
  public CTLNode parseCTL (String ctlStr)
  {
    CTLNode[] fairness = optionspanel.getFairness ();
    System.out.println ("Parsing CTL with " + 
			fairness.length + " fainess cnd");
    System.out.println (Arrays.asList (fairness));
    
    try 
      {
		CTLNode ctl = 
		  CTLNodeParser.parse (ctlStr, fairness);
		System.out.println ("After parsing fairness is: " + 
							Arrays.asList (ctl.getFairness ()));
		return ctl;
      }
    catch (CTLNodeParserException ex)
      {
	showException (this, "CTL Parsing Error", 
		       "parse error", ex);	
      }
    return null;
  }

  // -- prepares a CTL string for model checking
  public CTLNode prepareCTL (String ctlStr)
  {
    CTLNode ctl = parseCTL (ctlStr);
    CTLStyledPrinter.print (ctl, output.getStyledPrinter ());
    output.getStyledPrinter ().println ();
    return rewriteCTL (ctl);
  }
  
  


  void resetForQueryChecking ()
  {
    // -- removes all kinds of caches to avoid collisions
    
    // -- renew the algebra -- this is independent of anything
    if (getXKripke ().getAlgebra ().getClass () == UpSetAlgebra.class)
      ((UpSetAlgebra)getXKripke ().getAlgebra ()).renew ();
    else if (getXKripke ().getAlgebra ().getClass ()
	     == MvSetUpsetAlgebra.class)
      ((MvSetUpsetAlgebra)getXKripke ().getAlgebra ()).renew ();

    
    // -- renew placeholder rewriter -- this uses mvsets so should 
    // -- be cleared before the model checker
    if (phReWriter != null)
      phReWriter.renew ();
    else if (upsetPhReWriter != null)
      upsetPhReWriter.renew ();
    // -- CTLFactory cache uses mvsets so see above
    CTLFactory.renew ();    
    // -- model-checker is our link to the mvset layer so it will call renew 
    // -- there
    getModelChecker ().renew ();

    // -- garbage collect
    System.gc ();
  }
  


  /**
   ** Reads the CTL formula to be used in the current model-checker run.
   **/
  public void runOnCTL ()
  {
    if (getXKripke ().getAlgebra ().getClass () == UpSetAlgebra.class ||
	getXKripke ().getAlgebra ().getClass () == MvSetUpsetAlgebra.class)
      resetForQueryChecking ();

    try {
      // -- get the CTL formula we want to check
      String ctlStr = ctlin.getInputString ();
      
      showText ("Model Checking: " + ctlStr);
      
      // -- parse it and rewrite as required
      CTLNode ctl = prepareCTL (ctlStr);
      
      //output.getStyledPrinter ().print ("After Parsing: ");
      //CTLStyledPrinter.print (ctl, output.getStyledPrinter ());
      //output.getStyledPrinter ().println ();

      // -- got this far, this means the CTL expression was well formed,
      // -- remember it -- Oh what a mess we are making !
      ctlin.addCTLToHistory (ctlStr);

      StopWatch sw = new StopWatch ();
      // -- check
      MvSet result = getModelChecker ().checkCTL (ctl);
      output.getStyledPrinter ().print ("Done in: ");
      output.getStyledPrinter ().italicln (sw.toString ());

//       if (result instanceof CFAMvSet)
// 	{
// 	  System.out.println ("Result mvSet");
// 	  ((CFAMvSet)result).getCFA ().dumpNodes ();
// 	}
      

      // -- restrict the result to the intial state
//       AlgebraValue value = 
// 	result.and (getXKripke ().getInit ()).
// 	existAbstract (getXKripke ().getUnPrimeCube ()).getValue ();
      // -- restrict the resut to the intial state using
      // -- \A{s \in S} Init(s) -> \phi(s)
      System.out.println ("result of type: " + result.getClass ());
      AlgebraValue value = 
	getXKripke ().getInit ().not ().or (result).
	forallAbstract (getXKripke ().getUnPrimeCube ()).getValue ();
      
      showResult (value);

      // XXX Produce a counter-example
      if (ctlin.isCounterExampleEnabled ())
	{
	  if (PREFS.getBoolean (EXP_FEAT_BL, false))
	    showCounterExampleTree2 (ctl, value);
	  else
	    showCounterExampleTree (ctl, value);
	}
      
    } catch (Exception ex) {
      showException (this, "Model Checking Error", 
		     "Failed to model check", ex);
      
    }    
  }

    /**
     ** Starts up the trace viewer...
     **/
    public void trace ()
    {
	traceviewer = new XCTraceViewer (getModelChecker ());

	// Finish setting up the frame, and show it.
	traceviewer.pack();
	traceviewer.setVisible (true);
    }

  void showResult (AlgebraValue value)
  {
    if (!(getXKripke ().getAlgebra () instanceof UpSetAlgebra) &&
	!(getXKripke ().getAlgebra () instanceof MvSetUpsetAlgebra))
      {
	output.getStyledPrinter ().bold ("Result is: ");
	output.getStyledPrinter ().italicln (value.toString ());
      }

    
    CTLNode[] solutions = null;
    if (getXKripke ().getAlgebra () instanceof UpSetAlgebra)
      {
	solutions = 
	  phReWriter.getSolutions (value, 
				   getXKripke ().getStatePresenter ());
      }
    else if (getXKripke ().getAlgebra () instanceof MvSetUpsetAlgebra)
      {
	solutions = 
	  upsetPhReWriter.getSolutions (value, 
					getXKripke ().getStatePresenter ());
	
      }
    else
      return;
	
    for (int i = 0; i < solutions.length; i++)
      {
	output.getStyledPrinter ().boldln ("Solution " + (i + 1) + " is:");
	CTLStyledPrinter.print (solutions [i], 
				output.getStyledPrinter ());
	output.getStyledPrinter ().println ();
      }
    
  }




//   // import edu.toronto.cs.proof2 for the new way of showing proof


  void showCounterExampleTree2 (CTLNode ctl, AlgebraValue value)
  {
    // -- the initial state where the proof starts
    MvSet initState = getXKripke ().getInit ();
    MvSetModelChecker mc = getModelChecker ();
   
     // -- create a formula for the prover
    Formula formula = new EqualFormula (ctl, value, initState);
    // -- create the root node
    edu.toronto.cs.proof2.ProofStep rootStep 
      = new TreeProofStep (formula, null);

    CTLProver prover = new CTLProver (getModelChecker (), rootStep);

 
    // -- initialize the prover with the rules!
    prover.addProofRule (new EqualsProofRule ());
    prover.addProofRule (new CheckingTopBottom (mc));
    prover.addProofRule (new NegationProofRule (mc));
    prover.addProofRule (new AtomicProofRule (mc));
    // new!
    prover.addProofRule (new DepthProofRule(mc));
    
    prover.addProofRule (new AndOrProofRule (mc));
    // new! Do me first!
    //    prover.addProofRule (new VisitedEXProofRule(mc, initState));
    
    // deprecate old EX proofrule
    prover.addProofRule (new EXProofRule (mc));
    prover.addProofRule (new EXCexProofRule (mc));
    prover.addProofRule (new EUProofRule (mc));
    prover.addProofRule (new EUiProofRule (mc));
    prover.addProofRule (new AXProofRule (mc));
    prover.addProofRule (new EGProofRule (mc));
    prover.addProofRule (new AUProofRule (mc));
    prover.addProofRule (new AUiProofRule (mc));
    
    System.out.println("Added proof rules");
    
    // -- run the prover
    prover.expand (rootStep);
    
    DynamicProofDisplay.showProof (prover, rootStep);
    // ProofTreeFrame code temporarily disabled,
    //  to make room for even-more-experimental ProofTreeModel code
  }
  


  //import edu.toronto.cs.proof for running previous way of showing proof  

  void showCounterExampleTree (CTLNode ctl, AlgebraValue value)
  {
    edu.toronto.cs.proof.ProofStepFactory.setMC (getModelChecker ());
    edu.toronto.cs.proof.ProofStepFactory.setSNG (new SimpleNameGenerator ());
    edu.toronto.cs.proof.ProofStepFactory.setStructure (getXKripke ());
    
    // -- create the proof step
    edu.toronto.cs.proof.ProofStep proofStep = 
      edu.toronto.cs.proof.ProofStepFactory.makeProofStep 
      (value, getXKripke ().getInit (), ctl);
    // -- generate the proof
    proofStep.discharge ();
    
    KEGTreeFrame kegFrame = 
      new KEGTreeFrame (proofStep, 
			getXKripke ().getStatePresenter ());
    kegFrame.pack ();
    kegFrame.setVisible (true);
  }
  


  void showText (String text)
  {
    output.append (text);
    output.append ("\n");
  }
  
  void showText (String text, String style)
  {
    output.append (text, style);
  }
  


  /**
   ** Sets the default core options for XCheck.
   **/
  public Map getDefaultOptions ()
  {
    Map opt = new HashMap ();
    opt.put ("kegmode", new Boolean (false));
    opt.put ("verbose", new Integer (0));
    opt.put ("mvset", null);
    opt.put ("initstate", null);
    opt.put ("statenames", new Boolean (false));
    opt.put ("smv", new Boolean (false));
    opt.put ("bdd", new Boolean (false));
    opt.put ("heuristic", null);
    opt.put ("prefix", "KEG");
    opt.put ("output", "out.daVinci");
    //    opt.put ("input", Arrays.asList (inputValues));

    // XXX This is really bad!!! since the user has no way to
    // XXX change this parameter. Valid entries are: 'a' and 'e'
    // XXX a is the only one that works with query-checking
    // XXX e is the only one that works with fairness
    opt.put ("expansion", "e");    
    opt.put ("model name", "");
    return opt;
  }

  

  
  // -- enables various parts of the GUI
  // -- this is called with 'true' when the model was successfully loaded
  // -- and with false once it is closed
  public void setEnabled (boolean enable)
  {
    GUIUtil.setEnabled (ctlin, enable);
    optionspanel.setEnableTabs (enable);
    if (edu.toronto.cs.gui.XChekGUI.PREFS.getBoolean
	(edu.toronto.cs.gui.XChekGUI.FAIRNESS_BL, false))
      {
	optionspanel.enableFairness(true);
      }
    else
      {
	optionspanel.clearFairness ();
      }
    
    optionspanel.clearMacros ();    
  }
  
  


  /**
   ** Confirm the user's intention to quit.
   **/
  private void quit ()
  {
    int response = JOptionPane.showConfirmDialog (null, 
				   "Quit XChek?",
				   "Confirmation",
				   JOptionPane.YES_NO_OPTION);
    if (response == JOptionPane.YES_OPTION)
      {
	System.exit(0);
      }
  }


  private void loadFairness ()
  {
  }
  private boolean saveFairness ()
  {
    return true;
  }
  private boolean saveCTLHistory ()
  {
    if (filechooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION) {
      FileWriter w = null;
      try {
        w = new FileWriter (filechooser.getSelectedFile ());
        BufferedWriter f = new BufferedWriter (w);
        ListIterator h = ctlin.getCTLHistory ().listIterator ();
        while (h.hasNext ()) {
          String line = (String)h.next ();
          f.write (line);
          f.newLine ();
        }
        f.close ();
        w = null;
        return true;
      }
      catch (IOException ex) {
        showException (this,
                       "File write failed",
                       "History file save failed. File may be corrupted.",
                       ex);
      }
      finally {
        if (w != null) try { w.close (); } catch (IOException ex) {}
      }
    }
    return false;
  }
  private void loadCTLHistory ()
  {
    if (filechooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION) {
      BufferedReader f = null;
      LinkedList h = new LinkedList ();
      try {
        f = new BufferedReader (new FileReader (filechooser.getSelectedFile ()));
        String line = f.readLine ();
        while (line != null) {
          h.add (line);
          line = f.readLine ();
        }
        ctlin.setCTLHistory (h);
      }
      catch (FileNotFoundException ex) {
        showException (this,
                       "File not found",
                       "History file not found",
                       ex);
      }
      catch (IOException ex) {
        showException (this,
                       "File read failed",
                       "History file load failed.",
                       ex);
      }
      finally {
        if (f != null) try { f.close (); } catch (IOException ex) {}
      }
    }
  }

  /*
   *Loads a CTL history file passed as a string.
   *Overloaded version of above function
   */
  private void loadCTLHistory (File file)
  {
    BufferedReader f = null;
    LinkedList h = new LinkedList ();
    try {
      f = new BufferedReader (new FileReader (file));
      String line = f.readLine ();
      while (line != null) {
	h.add (line);
	line = f.readLine ();
      }
      ctlin.setCTLHistory (h);
    }
    catch (FileNotFoundException ex) {
      showException (this,
		     "File not found",
		     "History file not found",
		     ex);
    }
    catch (IOException ex) {
      showException (this,
		     "File read failed",
		     "History file load failed.",
		     ex);
    }
    finally {
      if (f != null) try { f.close (); } catch (IOException ex) {}
    } 
  }

  
  /**
   ** Opens up the Preferences Dialog
   **/  
  private void openPreferences ()
  {
    XPreferencesPresenter dialog = new XPreferencesPresenter ();
  }

  /**
   ** Shows some information about the currently loaded model.
   **/
  private void showModelInfo ()
  {
  }

  /**
   ** Show variables used in the currently loaded model.
   **/
  private void showVariables ()
  {
  }

  // display the lattice information window
  private void showAlgebraInfo ()
  {
  }
  
  private void showHelp ()
  {
    JOptionPane.showMessageDialog (this, "No help available just yet. Sorry.",
				   "Help", JOptionPane.ERROR_MESSAGE);
  }
  
  private void showAbout ()
  {
    JOptionPane.showMessageDialog (this, "Multi Valued Model Checker " +
				   version + "\n\n" +
				   "(c) 2002 University of Toronto",
				   "About XChek",
				   JOptionPane.INFORMATION_MESSAGE);
  }


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
  

  // enable given action
  private void enableAction (String actionName, boolean enable)
  {  
    Action action = (Action) actionMap.get (actionName);
    action.setEnabled (enable);
    actionMap.put (actionName, action);
  }
  

  // -- opens a dialog to pick a model compiler 
  // -- and eventually specify a mode
  // MODX debug get rid of this line
  class PickModelCompiler
  {

    class ModelDescription
      /*Small class to hold information about the model */
    {
      String modelClassName; //eg. edu.toronto.cs.smv.Lift
      String modelName; //eg. Lift Compiler

      public void setClassName (String v)
      {
	modelClassName = v;
      }
      public void setModelName (String v)
      {
	modelName = v;
      }

      public String getClassName ()
      {
	return modelClassName;
      }

      public String getModelName ()
      {
	return modelName;
      }

      public String toString()
      {
	return modelName;
      }
    }
    
    //old way of doing things
    // list of models to pick from:
    //     private final  String[] MODELS = {
    //       "edu.toronto.cs.smv.Game",
    //       "edu.toronto.cs.smv.Lift",
    //       "edu.toronto.cs.smv.LiftAbstract",
    //       "edu.toronto.cs.beans.XMLBeanModelCompiler",
    //       "edu.toronto.cs.smv.ConcurExample",
    //       "edu.toronto.cs.smv.parser.FlatSmvCompiler",
    //       "edu.toronto.cs.smv.AxelEx1b",
    //       "edu.toronto.cs.smv.AxelEx2",
    //       "edu.toronto.cs.smv.AxelEx4",
    //       "edu.toronto.cs.smv.FairTester"
    //     };

    
    // list of models to pick from:
    
    //Use this as the default preference string
//     String defModelList = "10"+"edu.toronto.cs.smv.Game" + ":" +
//       "edu.toronto.cs.smv.Lift" + ":" +
//       "edu.toronto.cs.smv.LiftAbstract"+ ":" +
//       "edu.toronto.cs.beans.XMLBeanModelCompiler"+ ":" +
//       "edu.toronto.cs.smv.ConcurExample"+ ":" +
//       "edu.toronto.cs.smv.parser.FlatSmvCompiler"+ ":" +
//       "edu.toronto.cs.smv.AxelEx1b"+ ":" +
//       "edu.toronto.cs.smv.AxelEx2"+ ":" +
//       "edu.toronto.cs.smv.AxelEx4"+ ":" +
//       "edu.toronto.cs.smv.FairTester";
    String defModelList = "03" + 
      edu.toronto.cs.gclang.parser.GCLangCompiler.class.getName () + ":" + 
      edu.toronto.cs.smv.parser.SmvCompiler.class.getName () + ":" + 
      edu.toronto.cs.xkripke.XMLXKripkeModelCompiler.class.getName ();
    
    
    String[] MODELS;
    
    // -- a dialog to pick an input compiler 
    // -- think about this as 'Open File' dialog
    JDialog inputDialog;
    // -- list on the dialog of hard coded model names
    JList inputList;
    DefaultListModel listModel;


    //text box to hold the details of the current Model
    JTextField modelField;
    
    
    // -- this dialog presents compiler's properties and 
    // -- intializes the compiler
    // -- Suggested user interaction is that the user picks
    // -- a model compiler using the inputDialog, then selects
    // -- appropriate options using propertyDialog
    // -- finally, this results in a compiler being created
    // -- that is ready to compile the model
    ModelCompilerPropertyDialog propertyDialog = null;

    public ModelCompiler pickModelCompiler (JFrame owner)
      throws IllegalAccessException,
	     InvocationTargetException
    {

      inputList = new JList ();
      inputList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      inputList.setSelectedIndex (0);
      
      //fill in the list from the preferences
      populateModelArray();
      
      inputDialog = buildInputDialog (owner);
      inputDialog.pack ();
      inputDialog.setVisible (true);
      if (propertyDialog != null)
	return propertyDialog.getModelCompiler ();
      
      return null;
    }

    JDialog buildInputDialog (JFrame owner)
    {
      // -- new modal dialog
      JDialog inputDialog = new JDialog (owner, "Pick a model compiler", 
					 true);

      inputDialog.getContentPane ().setLayout (new BorderLayout ());
      
//       inputDialog.getContentPane ().add (BorderLayout.NORTH, 
// 					 new JLabel ("Specify a model " + 
// 						     "compiler class"));
      
      //listener changes modelField
      inputList.addListSelectionListener (new ListSelectionListener ()
	{
	  public void valueChanged(ListSelectionEvent e)
	  {

	    int i = inputList.getSelectedIndex ();
	    if (i != -1)  //if something is selected
	      {
		//switchSelection (i);
		ModelDescription m = 
		  (ModelDescription) listModel.getElementAt(i);
		modelField.setText(m.getClassName ());
		
	      }
	      
	  }
	});

      JPanel inputListPanel = new JPanel ();
      JScrollPane inputListScroller = new JScrollPane(inputList);

     
      inputDialog.getContentPane ().add (BorderLayout.CENTER, 
					 inputListScroller);
     
      addButtons (owner, inputDialog);
     
      return inputDialog;
    }

    /*
     *gets list from preferences and updates the list using it.
     */
    void populateModelArray ()
    {

      listModel = new DefaultListModel ();
      
      
      String modelsSingle = MODEL_PREFS.get(MODELSLIST,defModelList);

      
      int numModels = Integer.parseInt(modelsSingle.substring(0,2));
      
      //MODELS = new String[numModels];

      modelsSingle= modelsSingle.substring(2);
      MODELS = modelsSingle.split(":");

   
      for (int j = 0; j < MODELS.length; j++)
	{
	  ModelDescription m = new ModelDescription ();
	  m.setClassName (MODELS [j]);
	
	  ModelCompiler bean;
	  BeanDescriptor beanDescriptor;
	  BeanInfo beanInfo;
	  
	  try
	    {
	      bean =(ModelCompiler) Beans.getInstanceOf
		(Beans.instantiate(null, m.modelClassName),
		 ModelCompiler.class);
	      
	      beanInfo = Introspector.getBeanInfo (bean.getClass ());
	      beanDescriptor = beanInfo.getBeanDescriptor ();
	      
	      m.setModelName (beanDescriptor.getDisplayName());
	      
	      listModel.addElement (m);
	      
	    }
	  catch(Exception ex)
	    {
	      XChekGUI.showException (XChekGUI.this, "Error", 
			 "Class not found or IO error", ex);

	      continue;
	    }
	  
	}
      
      inputList.setModel(listModel);
    }

    void toModelArray(String s) //remove later by adding to populate..
    {
      s = s.substring(2);
      MODELS = s.split(":");
    }

    void addToModels()
    {
      if(!modelField.getText().equals("")) //so blanks are not added
	{

	  ModelDescription m = new ModelDescription();
	  m.setClassName (modelField.getText());
	
	  ModelCompiler bean;
	  BeanDescriptor beanDescriptor;
	  BeanInfo beanInfo;
	  
	  try
	    {
	      
	      bean =(ModelCompiler) Beans.getInstanceOf
		(Beans.instantiate(null, m.getClassName ()),
		 ModelCompiler.class);
	      
	      beanInfo = Introspector.getBeanInfo (bean.getClass ());
	      beanDescriptor = beanInfo.getBeanDescriptor ();
	      
	      m.setModelName (beanDescriptor.getDisplayName());
	      
	      
	      listModel.addElement(m);
	      saveList();
	    }
	  catch(Exception ex)
	    {
	      XChekGUI.showException (XChekGUI.this, "Error", 
				      "Class not found or IO error", ex);
	    }
	}
      
    }
   
    void removeFromModels(int index)
    {
      listModel.remove(index);
      saveList();
    } 

    void editModels(int index)
    {

      ModelDescription m = new ModelDescription();
      m.setClassName (modelField.getText());
      
      
      ModelCompiler bean;
      BeanDescriptor beanDescriptor;
      BeanInfo beanInfo;
      
      try
	{
	  bean =(ModelCompiler) Beans.getInstanceOf
	    (Beans.instantiate(null, m.getClassName ()),
	     ModelCompiler.class);
	  
	  beanInfo = Introspector.getBeanInfo (bean.getClass ());
	  beanDescriptor = beanInfo.getBeanDescriptor ();
	  
	  m.setModelName (beanDescriptor.getDisplayName());
	  
	  
	  listModel.setElementAt(m,index);
	  saveList();
	}
      catch(Exception ex)
	{
	  XChekGUI.showException (XChekGUI.this, "Error", 
				  "Class not found or IO error", ex);
	}
      
      
    } 


    /*
     *saves list to preferences
     */
    void saveList()
    {
      int x = listModel.getSize();
      String s = java.lang.String.valueOf (x);
      if (s.length () == 1) s = "0" + s;
      
      for (int k = 0; k < x; k++)
	{
	  ModelDescription m  = 
	    (ModelDescription)(listModel.getElementAt(k));
	  s = s + m.getClassName () + ":";
	}
      
      //remove the : at the end
      s = s.substring(0, s.length() - 1);
      MODEL_PREFS.put(MODELSLIST, s);
      
    }

    
    
    void addButtons (JFrame _owner, JDialog inputDialog)
    {
      final JFrame owner = _owner;
      JButton okButton = new JButton ("Ok");
      
      okButton.addActionListener
	(new ActionListener ()
	  {
	    public void actionPerformed (ActionEvent e)
	    {
	      ModelDescription m = 
		(ModelDescription)(inputList.getSelectedValue ());
	      
	      // -- bail out if nothing is selected
	      // -- and close the dialog
	      if (m == null) 
		{
		  PickModelCompiler.this.inputDialog.setVisible (false);
		  return;
		}
	      
	      
	      String currentInput = m.getClassName ();
	      try {
		propertyDialog = 
		  new ModelCompilerPropertyDialog (owner,
						   currentInput);
		if (propertyDialog.cancel ())
		  propertyDialog = null;
		else
		  PickModelCompiler.this.inputDialog.setVisible (false);
		
	      } catch (Exception ex){
		showException (propertyDialog, "Error during compiler load",
			       "Problems loading the compiler:", ex);
		propertyDialog = null;
	      }
	    }
	  });
      
      JButton cancelButton = new JButton ("Cancel");
      cancelButton.addActionListener
	(new ActionListener ()
	  {
	    public void actionPerformed (ActionEvent e)
	    {
	      PickModelCompiler.this.inputDialog.setVisible (false);
	    }
	  });
      
      //New buttons
      JButton addBut = new JButton ("Add");
      addBut.addActionListener (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent evt)
	  {
	    addToModels();
	  }
	});
      
      JButton removeBut = new JButton ("Remove");
      removeBut.addActionListener (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent evt)
	  {
	  int i = inputList.getSelectedIndex ();
	  if (i != -1)  //if something is selected
	    removeFromModels(i);
	  }
	});
      
      JButton editBut = new JButton ("Edit");
      editBut.addActionListener (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent evt)
	  {
	    int i = inputList.getSelectedIndex ();
	    if (i != -1)  //if something is selected
	      editModels(i);
	  }
	});
      
      JButton importBut = new JButton ("Import");
      importBut.addActionListener (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent evt)
	  {
	    try
	      {
		//doImport();
		String xmlfile;
		
		JFileChooser impfc = new JFileChooser ();
		int returnVal = impfc.showDialog (null, "Import");
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		  {
		    File file = impfc.getSelectedFile ();
		    InputStream is = new
		      BufferedInputStream	 
		      (new FileInputStream (file));
		    Preferences.importPreferences(is);
		    is.close();
		    populateModelArray();
		    
		  }
		
	      }
	    catch (IOException ioex)
	      {
	      }
	    catch (InvalidPreferencesFormatException inex)
	      {
	      }
	    
	    
	  }
	});
      
      
      JButton exportBut = new JButton ("Export");      
      exportBut.addActionListener (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent evt)
	  {
	    //doExport

	  JFileChooser expfc = new JFileChooser();
	  int returnVal = expfc.showDialog(null, "Export");
	  
	  if (returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	      File file = expfc.getSelectedFile();
	      try {
		OutputStream osTree =
		  new BufferedOutputStream(new FileOutputStream
					   (file));
		MODEL_PREFS.exportSubtree(osTree);
		osTree.close();
		
	      } catch(IOException ioEx) {
		System.out.println( ioEx);// ignore
	      } catch(BackingStoreException bsEx) {
		System.out.println( bsEx);// ignore too
	      }
	    }
	  
	}
      });
       
      //Set Preferred sizes
      Dimension d = removeBut.getPreferredSize();

      okButton.setMaximumSize (d);

      cancelButton.setMaximumSize (d);
      addBut.setMaximumSize (d);
      editBut.setMaximumSize (d);
      importBut.setMaximumSize (d);
      exportBut.setMaximumSize (d);


      JPanel modelFieldPanel = new JPanel ();
      modelField = new JTextField(40);
      modelFieldPanel.add(modelField);
      modelFieldPanel.setBorder (BorderFactory.createTitledBorder
			   (BorderFactory.createCompoundBorder
			    (BorderFactory.createEtchedBorder (),
			     StandardFiller.makeEmptyBorder ()),"" ));
      inputDialog.getContentPane ().add (BorderLayout.SOUTH,modelFieldPanel);

      JPanel buttonPanel = new JPanel ();
      buttonPanel.setLayout (new BoxLayout (buttonPanel,BoxLayout.Y_AXIS));
      buttonPanel.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()),
			  ""));

      


      
      buttonPanel.add (okButton);
      buttonPanel.add (cancelButton);
      buttonPanel.add (StandardFiller.makeHstrut ());
      buttonPanel.add (addBut);
      buttonPanel.add (editBut);
      buttonPanel.add (removeBut);
      buttonPanel.add (StandardFiller.makeHstrut ());
      buttonPanel.add (importBut);
      buttonPanel.add (exportBut);

      


      //      okButton.setPreferredSize (cancelButton.getPreferredSize ());
      GUIUtil.alignAllX (buttonPanel.getComponents (), 
			 Component.CENTER_ALIGNMENT);
      inputDialog.getContentPane ().add (BorderLayout.EAST, buttonPanel);
    } 
  }
  

  class LoadModelFromFile 
  {
    public ModelCompiler loadModel (JFileChooser fileChooser) 
    {
      if (fileChooser.showOpenDialog (XChekGUI.this) != 
	  JFileChooser.APPROVE_OPTION)
	return null;
      
      String fileName = fileChooser.getSelectedFile ().getAbsolutePath ();

      showText ("Open " + fileName, "regular");
      
      try 
	{
	  XMLDecoder decoder = new XMLDecoder 
	    (new BufferedInputStream 
	     (new FileInputStream (fileName)));
	  
	  ModelCompiler compiler = (ModelCompiler)decoder.readObject ();
	  decoder.close ();
	  return compiler;
	}
      catch (Exception ex)
	{
	  showException (XChekGUI.this, "Error loading a model", 
			 "Could not load a model from " + fileName, ex);
	}
      return null;
    }
    
  }
  


  

  public final URL openXMLIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Open24.gif");
  public final URL openBDDIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Open24.gif");
  public final URL loadFairnessIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Import24.gif");
  public final URL saveFairnessIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Save24.gif");
  public final URL loadCTLIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Import24.gif");
  public final URL saveCTLIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Save24.gif");
  public final URL quitIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/Stop24.gif");
  public final URL modelInfoIcon = 
    getClass().getResource ("/toolbarButtonGraphics/general/About24.gif");
  public final URL latticeIcon =
    getClass().getResource ("/edu/toronto/cs/resources/icons/lattice.gif");
  public final URL varIcon =
    getClass().getResource ("/toolbarButtonGraphics/general/History24.gif");
  public final URL preferencesIcon =
    getClass().getResource ("/toolbarButtonGraphics/general/History24.gif");


  public static void main (String [] args)
  {
    
    try
      {
	//Set the look and feel using Preferences
	String guiLookAndFeel = 
	  PREFS.get(LOOK_AND_FEEL,
		    "Default");
	
	if (guiLookAndFeel.equals("Kunststoff"))
	  guiLookAndFeel = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
	else if (guiLookAndFeel.equals("Motif"))
	  guiLookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	
	else if (guiLookAndFeel.equals("Windows"))
	  guiLookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	if (!guiLookAndFeel.equals ("Default"))
	  UIManager.setLookAndFeel (guiLookAndFeel);
      }
    catch (Exception e) 
      {
	e.printStackTrace ();
      }
    

    if (PREFS.getBoolean (REMOTE_X, false))
      RepaintManager.currentManager (null).setDoubleBufferingEnabled (false);
    
    //Create the top-level container and add contents to it.
    XChekGUI app = new XChekGUI ();
    
    //Finish setting up the frame, and show it.
    app.pack();
    app.setVisible (true);

  }

 //Preferences  

  static XPreferences xprefs = null;
  public static XPreferences getGUIPreferences ()
  {
    if (xprefs == null)
      xprefs = new XPreferencesImplGUI ();
    return xprefs;
  }
  
  
  /*
   * A GUI implementation of XPreferences
   */
  static class XPreferencesImplGUI implements XPreferences
  {
    public static final int INPUT_WIDTH = 40;

    JPanel prefPanel;
    JPanel insidePanel;
    JPanel cntrExBlChkPanel;
    JPanel lookAndFeelPanel;

    JLabel lookAndFeelLbl;

    JFileChooser fcDir;
    JFileChooser fcFile;
    
    FilePanel defDirFp;
    FilePanel modelDefDirFp;
    FilePanel ctlDefDirFp;
    FilePanel ctlDefFileFp;

    JCheckBox cntrExBlChk;
    JCheckBox fairnessBlChk;
    JCheckBox expFeatBlChk;
    JCheckBox remoteXBlChk;
    
    JComboBox lookAndFeelCmboBx;

    public XPreferencesImplGUI ()
    {
      //PrefPanel
      prefPanel = new JPanel ();
      prefPanel.setLayout (new BorderLayout (5,5));

      insidePanel = new JPanel ();
      
      insidePanel.setLayout (new BoxLayout (insidePanel, BoxLayout.Y_AXIS));

      //the Checkbox Panel XXX maybe should create a checkbox class?
      cntrExBlChkPanel = new JPanel ();
      cntrExBlChkPanel.setLayout (new BoxLayout (cntrExBlChkPanel, 
						 BoxLayout.X_AXIS));
      cntrExBlChk = new JCheckBox ("Startup with Counter-Example?", 
				   PREFS.getBoolean (CNTR_EX_BL, false));

      fairnessBlChk = new JCheckBox ("Enable Fairness?", 
				     PREFS.getBoolean (FAIRNESS_BL, false));

      expFeatBlChk = new JCheckBox ("New (experimental) CexViewer?", 
				    PREFS.getBoolean (EXP_FEAT_BL, false));
      remoteXBlChk = new JCheckBox ("Enable double buffering (Remote X)",
				    PREFS.getBoolean (REMOTE_X, false));

      cntrExBlChkPanel.add (cntrExBlChk);
      cntrExBlChkPanel.add (fairnessBlChk);
      cntrExBlChkPanel.add (expFeatBlChk);
      cntrExBlChkPanel.add (remoteXBlChk);
      cntrExBlChkPanel.add (Box.createHorizontalGlue());


      //the File Choosers
      //for directories
      fcDir = new JFileChooser();
      fcDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      //for files
      fcFile = new JFileChooser();
      fcFile.setFileSelectionMode(JFileChooser.FILES_ONLY);



      defDirFp = new FilePanel("Global Default Directory",
			       fcDir);
      modelDefDirFp = new FilePanel("Model Picker default directory ",
				    fcDir);
      ctlDefDirFp = new FilePanel("CTL-file Picker default directory ", 
				  fcDir);
      ctlDefFileFp = new FilePanel("Default CTL file ", fcFile);

      //Look and Feel ComboBox
      lookAndFeelLbl = new JLabel(" Look and Feel (Requires Restart)");
      String[] lookAndFeelList = { "Default","Kunststoff",
				   "Motif", "Windows"};
      lookAndFeelCmboBx = new JComboBox(lookAndFeelList);

      //      lookAndFeelCmboBx.addActionListener();
      lookAndFeelPanel = new JPanel();
      lookAndFeelPanel.setLayout (new BoxLayout
				  (lookAndFeelPanel, BoxLayout.X_AXIS));
      lookAndFeelPanel.add(lookAndFeelLbl);
      lookAndFeelPanel.add(lookAndFeelCmboBx);
      lookAndFeelPanel.add(Box.createHorizontalGlue());


      insidePanel.add (defDirFp);
      insidePanel.add (modelDefDirFp);
      insidePanel.add (ctlDefDirFp);
      insidePanel.add (ctlDefFileFp);
      insidePanel.add (cntrExBlChkPanel);
      insidePanel.add (lookAndFeelPanel);

      prefPanel.add (insidePanel, BorderLayout.NORTH);
      

      prefPanel.setBorder (BorderFactory.createTitledBorder
			   (BorderFactory.createCompoundBorder
			    (BorderFactory.createEtchedBorder (),
			     StandardFiller.makeEmptyBorder ()), getGroupName()));
      // -- populate input fields with our current settings
      updateComponents ();
      
      
    }

    public String toString ()
    {
      return getGroupName ();
    }
    
    public String getGroupName () 
    {
      return "General";
    }
    
    public String getHelp () 
    {
      return " The XChek Model Checker General Preferences";
    }

    public Component getPreferenceEditor () 
    {
      return prefPanel;
    }
    
    /*
     * Updates Input fields
     */
    public  void updateComponents()
    {
      //set user's home dir as default if not found
      defDirFp.setFileName
	(PREFS.get (DEF_DIR,java.lang.System.getProperty("user.home")));
      modelDefDirFp.setFileName (PREFS.get (MODEL_DEF_DIR, ""));
      ctlDefDirFp.setFileName (PREFS.get (CTL_DEF_DIR, ""));
      ctlDefFileFp.setFileName (PREFS.get (CTL_DEF_FILE, ""));
      cntrExBlChk.setSelected (PREFS.getBoolean (CNTR_EX_BL, false));
      fairnessBlChk.setSelected (PREFS.getBoolean (FAIRNESS_BL, false));
      expFeatBlChk.setSelected (PREFS.getBoolean (EXP_FEAT_BL, false));
      lookAndFeelCmboBx.setSelectedItem(PREFS.get (LOOK_AND_FEEL, "Motif"));
      remoteXBlChk.setSelected (PREFS.getBoolean (REMOTE_X, false));
    }    
    
    /*
     * Saves all preferences to Backing Store
     */
    public  void savePrefSettings()
    {
      PREFS.put (DEF_DIR, defDirFp.getFileName ());
      
      PREFS.put (MODEL_DEF_DIR, 
		 modelDefDirFp.getSelectedFile ().getAbsolutePath ());
      
      PREFS.put (CTL_DEF_DIR, 
		 ctlDefDirFp.getSelectedFile ().getAbsolutePath ());
      
      PREFS.put (CTL_DEF_FILE, 
		 ctlDefFileFp.getSelectedFile ().getAbsolutePath ());
      
      PREFS.putBoolean (CNTR_EX_BL,
			cntrExBlChk.getSelectedObjects () != null);
      PREFS.putBoolean (FAIRNESS_BL,
			fairnessBlChk.getSelectedObjects () != null);
      PREFS.putBoolean (EXP_FEAT_BL,
			expFeatBlChk.getSelectedObjects () != null);
      PREFS.put (LOOK_AND_FEEL,
		 (String) lookAndFeelCmboBx.getSelectedItem ());
      PREFS.putBoolean (REMOTE_X,
			remoteXBlChk.getSelectedObjects () != null);

      }
    
    //Checks status of "Start with Counter-Example" is enabled" checkbox
    public boolean cntrExBlChkStatus ()
    {
      return (cntrExBlChk.getSelectedObjects () != null);
    } 
  }
}
