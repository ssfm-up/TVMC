package edu.toronto.cs.gui;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.prefs.Preferences;

import edu.toronto.cs.util.XPreferences;
import edu.toronto.cs.util.gui.*;
import edu.toronto.cs.proof.*;
import edu.toronto.cs.modelchecker.*;

import edu.toronto.cs.grappa.*;


public class KEGTreeFrame extends JFrame
{
  ProofStep proofStep;
  StatePresenter statePresenter;
  String layoutPath;

  JTree kegTree;
  JList stateView;

  public KEGTreeFrame (ProofStep _proofStep, StatePresenter _statePresenter)
  {
    super ("KEG Window");
    proofStep = _proofStep;
    statePresenter = _statePresenter;
    
    initFrame ();
  }

  public void setProofStep (ProofStep v)
  {
    proofStep = v;
  }
  public void setStatePresenter (StatePresenter v)
  { statePresenter = v; }


  void initFrame ()
  {
    final ProofStepListModel listModel = 
      new ProofStepListModel (statePresenter);

    listModel.setProofStep (proofStep);

    kegTree = new JTree (new ProofTreeModel (proofStep));
    kegTree.addTreeSelectionListener (new TreeSelectionListener ()
      {
	public void valueChanged (TreeSelectionEvent evt)
	{
	  ProofStep selectedStep = 
	    (ProofStep)kegTree.getLastSelectedPathComponent ();
	  listModel.setProofStep (selectedStep);
	}
      });


    stateView = new JList (listModel);

    JPanel treePanel = new JPanel ();
    treePanel.setLayout (new BoxLayout (treePanel, BoxLayout.X_AXIS));
    treePanel.add (new JScrollPane (kegTree));
    treePanel.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), "Proof"));


    JPanel leftPanel = new JPanel ();
    leftPanel.setLayout (new BoxLayout (leftPanel, BoxLayout.Y_AXIS));

    JPanel stateViewPanel = new JPanel ();
    JScrollPane scroll = new JScrollPane (stateView);
    scroll.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), "Current State"));
    leftPanel.add (scroll);
    leftPanel.add (StandardFiller.makeLongVstrut ());
    
    JButton daVinci = new JButton ("daVinci");
    leftPanel.add (daVinci);

    JButton grappaBut= new JButton ("Grappa");
    leftPanel.add (grappaBut);

    JPanel top = new JPanel ();
    
    top.setLayout (new BoxLayout (top, BoxLayout.X_AXIS));
    top.add (treePanel);
    top.add (leftPanel);
    top.setBorder (StandardFiller.makeWideEmptyBorder ());

    getContentPane ().add (top);


    daVinci.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  try {
      
	    final File tempFile = File.createTempFile ("keg", "status", 
						       new File ("/tmp"));
	    
	    FileWriter out = new FileWriter (tempFile);
	    out.write 
	      (ProofToDaVinci.toDaVinci (proofStep,
					 PREFS.getBoolean (HIDE_PROOF, false)
					 ).toString ());
	    out.close ();
	    
	    Thread t = new Thread 
	      (new Runnable ()
		{
		  public void run ()
		  {
		    try {
		      ProcessBuilder bp = 
			new ProcessBuilder (getDavinciExecName (), 
					    tempFile.getAbsolutePath ());
		      bp.environment ().put ("UDG_HOME", getUDGHomePath ());
		      Process p = bp.start ();
		      
		      p.waitFor ();
		      tempFile.delete ();
		    } 
		    catch (Exception e) 
		      { 
			XChekGUI.showException (KEGTreeFrame.this, 
						"Error running daVinci", 
						"Could not run daVinci", e);
		      }
		  }
		});
	    t.start ();
	  } catch (Exception e) 
	    {  
	      XChekGUI.showException (KEGTreeFrame.this, 
				      "Error running daVinci", 
				      "Could not run daVinci", e);
	    }
	  
	}
      });

    

    grappaBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  try {
	    // -- build a graph from the proof and decorate it a bit
	    GrappaGraph graph = ProofToGrappa.toGrappa (proofStep).
	      orientation ("landscape").
	      center (true).
	      size ("8.5,11").
	      editable (true).
	      errorWriter (System.err);
	    // -- open a frame to show the graph
	    GrappaFrame frame = new GrappaFrame (graph.getGraph ());
	    // -- lay out the graph first
	    frame.doGraphLayout ();
	    // -- show the window
	    frame.setVisible (true);

	  } 
	  catch (Exception e) 
	    {  
	      XChekGUI.showException (KEGTreeFrame.this, 
				      "Error running Grappa", 
				      "Could not run Grappa", e);
	    }
	}
      });
    
    
  }

  private String getUDGHomePath ()
  {
    return PREFS.get (UDG_HOME_PATH, "");
  }
  
  private String getDavinciExecName ()
  {
    return new File (getUDGHomePath (), 
		     "bin/daVinci").getAbsolutePath ();
  }
  
    

  public static final Preferences PREFS
    = Preferences.userRoot ().node ("edu/toronto/cs/gui/KEGTreeFrame");
  public static final String UDG_HOME_PATH = "UDGHomePath";
  public static final String HIDE_PROOF = "hideProof";
  
  static XPreferences xprefs = null;
  public static XPreferences getGUIPreferences ()
  {
    if (xprefs == null)
      xprefs = new XPreferencesImplGUI ();
    return xprefs;
  }

  static class XPreferencesImplGUI implements XPreferences
  {
    JPanel prefPanel;
    FilePanel udgHomePathFp;
    JCheckBox hideProof;
    
    public XPreferencesImplGUI ()
    {
      prefPanel = new JPanel ();
      prefPanel.setLayout (new BorderLayout (5, 5));
      
      JFileChooser fcDir = new JFileChooser ();
      fcDir.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);

      udgHomePathFp = new FilePanel ("Path to UDG home directory", fcDir);
      
      JPanel insidePanel = new JPanel ();
      insidePanel.setLayout (new BoxLayout (insidePanel, BoxLayout.Y_AXIS));
      insidePanel.add (udgHomePathFp);
      
      hideProof = new JCheckBox ("Hide proofs", 
				PREFS.getBoolean (HIDE_PROOF, true));
      insidePanel.add ((Component)hideProof);

      prefPanel.add (insidePanel, BorderLayout.NORTH);

      updateComponents ();

      prefPanel.setBorder (BorderFactory.createTitledBorder
			   (BorderFactory.createCompoundBorder
			    (BorderFactory.createEtchedBorder (),
			     StandardFiller.makeEmptyBorder ()), 
			    getGroupName()));

      
    }
    
    public String toString ()
    {
      return getGroupName ();
    }
    public String getGroupName ()
    {
      return "KEGTree";
    }
    
    public String getHelp ()
    {
      return "Preferences for KEG display";
    }
    
    public Component getPreferenceEditor ()
    {
      return prefPanel;
    }
    
    public void updateComponents ()
    {
      udgHomePathFp.setFileName (PREFS.get (UDG_HOME_PATH, ""));
      hideProof.setSelected (PREFS.getBoolean (HIDE_PROOF, true));
      
    }

    public void savePrefSettings ()
    {
      PREFS.put (UDG_HOME_PATH, 
		 udgHomePathFp.getSelectedFile ().getAbsolutePath ());
      PREFS.putBoolean (HIDE_PROOF, hideProof.getSelectedObjects () != null);
    }
    
    
  }
  
  
}
