package edu.toronto.cs.gui;


import java.util.*;
import java.awt.Color;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

import att.grappa.*;

import java.util.prefs.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;

import javax.swing.filechooser.*;

import javax.swing.event.*;

import edu.toronto.cs.util.gui.*;




/****
 **** A frame to display and manipulate a grappa graph
 ****/
public class GrappaFrame extends JFrame
{
  
  public static final String DOT_ENGINE_URL 
    = "http://www.research.att.com/~john/cgi-bin/format-graph";

  public static final Preferences PREFS 
    = Preferences.userRoot ().node ("edu/toronto/cs/gui/GrappaFrame");
  
  
  // -- constants for preference names
  public static final String DOT_PATH = "dotPath";

  GrappaPanel gp;
  Graph graph;
  
  JButton layout;
  JButton printer;
  JButton draw;
  JButton quit;
  JPanel panel;
  
  public GrappaFrame (Graph _graph) 
  {
    super ("GrappaFrame");
    graph = _graph;
    
    
    setSize (600, 400);
    setLocation (100, 100);
    
    addWindowListener (new WindowAdapter () 
      {
	public void windowClosing (WindowEvent wev) 
	{
	  quit ();
	}
      });
    
    JScrollPane jsp = new JScrollPane ();
    //jsp.getViewport ().setBackingStoreEnabled (true);
    //jsp.getViewport ().setScrollMode (JViewport.SIMPLE_SCROLL_MODE);
    
    gp = new GrappaPanel (graph);
    gp.addGrappaListener (new GrappaAdapter ());
    gp.setScaleToFit (false);
    
    java.awt.Rectangle bbox = graph.getBoundingBox ().getBounds ();
    
    GridBagLayout gbl = new GridBagLayout ();
    GridBagConstraints gbc = new GridBagConstraints ();
    
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    
    panel = new JPanel ();
    panel.setLayout (gbl);
    
    draw = new JButton ("Draw");
    gbl.setConstraints (draw,gbc);
    panel.add (draw);
    draw.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  drawGraph ();
	}
      });
    
    layout = new JButton ("Layout");
    gbl.setConstraints (layout,gbc);
    panel.add (layout);
    layout.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  doGraphLayout ();
	}
      });
    
    //graph.printGraph (System.out);
    
    printer = new JButton ("Print");
    gbl.setConstraints (printer,gbc);
    panel.add (printer);
    printer.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  graph.printGraph (System.out);
	  System.out.flush ();
	}
      });
    
    quit = new JButton ("Quit");
    gbl.setConstraints (quit, gbc);
    panel.add (quit);
    quit.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  quit ();
	}
      });
    
    getContentPane ().setLayout (new BorderLayout ());
    getContentPane ().add (jsp, BorderLayout.CENTER);
    getContentPane ().add (panel, BorderLayout.WEST);
    
    jsp.setViewportView (gp);
    setVisible (true);
  }

  public void quit ()
  {
    setVisible (false);
    dispose ();
  }
  

  public void doGraphLayout () 
  {
    
    try 
      {
	/* LAYOUT THROUGH AT&T
	URLConnection urlConn = (URLConnection)
	  new URL (DOT_ENGINE_URL).openConnection ();
	urlConn.setDoInput (true);
	urlConn.setDoOutput (true);
	urlConn.setUseCaches (false);
	urlConn.setRequestProperty ("Content-Type",
				    "application/x-www-form-urlencoded");
	if (!GrappaSupport.filterGraph (graph, urlConn)) 
	  System.err.println ("ERROR: somewhere in filterGraph");
	  */




// 	String[] cmd = new String[] { PREFS.get (LAYOUT_PATH, ""), 
// 				      PREFS.get (DOT_PATH, "") };

	String[] cmd = new String[] { PREFS.get (DOT_PATH, "") };

	
	System.out.println ("cmd: " + Arrays.asList (cmd));
	
	//System.out.println (" graph is "+graph.toString ());
	
	//graph.printGraph (System.out);
	
	if (graph.getGraph () == null)
	  System.out.println ("graph is null");
	
	
	Object connector = Runtime.getRuntime ().exec (cmd);
	
	if(!GrappaSupport.filterGraph(graph,connector)) 
	  {
	    System.err.println("ERROR: somewhere in filterGraph");
	  }

	graph.repaint ();
      }
    catch (Exception ex)
      {
	// XXX A very bad way to handle exceptions
	//assert false : ex;
	
	//System.err.println("Exception while setting up Process: " + 
	//	   ex.getMessage() + "\nTrying URLConnection...");
	XChekGUI.showException (GrappaFrame.this, 
				"Error running Grappa", 
				"Make sure preferences are set up and that" + 
				" the graph is not Empty", ex);
	
      }
  }
  
  public void drawGraph ()
  {
    graph.repaint ();
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
    JPanel dotFilePanel;
    JPanel layoutFilePanel;

    JTextField dotInput;
    JTextField layoutInput;
    JFileChooser fcFile;
    JButton dotFcBut;
    JButton layoutFcBut;
    
    FilePanel dotFp;

    public XPreferencesImplGUI ()
    {
      //PrefPanel
      prefPanel = new JPanel ();

      prefPanel.setLayout (new BorderLayout (5,5));

      //for files
      fcFile = new JFileChooser();
      fcFile.setFileSelectionMode(JFileChooser.FILES_ONLY);


      //filepanel code
      dotFp = new FilePanel("Path to DOT Engine", fcFile);

      
      

      insidePanel = new JPanel();
      insidePanel.setLayout (new BoxLayout (insidePanel, BoxLayout.Y_AXIS));

      insidePanel.add (dotFp);
      

      prefPanel.add (insidePanel,BorderLayout.NORTH);

      // populate input fields with our current settings
      updateComponents ();
      
      
      // XXX We are missing URL preference     
      prefPanel.setBorder (BorderFactory.createTitledBorder
			   (BorderFactory.createCompoundBorder
			    (BorderFactory.createEtchedBorder (),
			     StandardFiller.makeEmptyBorder ()), getGroupName()));
      
      
    }
    
    public String toString ()
    {
      return getGroupName ();
    }
    
    public String getGroupName () 
    {
      return "Grappa";
    }
    
    public String getHelp () 
    {
      return " The Preferences fot the Grappa graphing Engine" +
	" and the DOT layout Engine";
      
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
      dotFp.setFileName (PREFS.get (DOT_PATH, ""));
    }    
    
    /*
     * Saves all preferences to Backing Store
     */
    public  void savePrefSettings()
    {
      PREFS.put (DOT_PATH, dotFp.getFileName ());
    }
    
  }
  
}

