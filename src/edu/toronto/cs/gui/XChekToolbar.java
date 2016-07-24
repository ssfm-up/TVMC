package edu.toronto.cs.gui;


import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;

/**
 * XChekToolbar.java.java
 *
 * A toolbar for the model-checker GUI
 *
 * Based on XCToolbar by Tafliovich Anya
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */

public class XChekToolbar extends JToolBar 
{
  // -- Toolbar buttons
  // -- open the model
  JButton open; 
  // -- load fairness from file
  JButton loadFairness;
  // -- save fairness to file
  JButton saveFairness;
  // -- load CTL history from file
  JButton loadCTL;
  // -- save CTL history to file
  JButton saveCTL;
  // -- show the algebra
  JButton showAlgebra;
  // -- open Preferences Dialog
  JButton openPrefs;
  // -- quit everything
  JButton quit;

  /**
   ** Constructor:
   ** create a new toolbar, associate the actions defined in 'actionMap'
   ** with the buttons
   **/
  public XChekToolbar (ActionMap actionMap)
  {
    super();
    
    open = new JButton ((Action)actionMap.get ("Open a model..."));
    open.setText ("Model");

    // fairnesss
    loadFairness =
      new JButton ((Action)actionMap.get ("Load fairness conditions"));
    loadFairness.setText("fairness");

    saveFairness =
      new JButton ((Action)actionMap.get ("Save fairness conditions"));
    saveFairness.setText("fairness");

    // CTL history buttons
    loadCTL = new JButton ((Action)actionMap.get ("Load CTL history"));
    loadCTL.setText("CTL");

    saveCTL = new JButton ((Action)actionMap.get ("Save CTL history"));
    saveCTL.setText("CTL");
    
    // lattice display
    showAlgebra = new JButton ((Action)actionMap.get ("Algebra info"));
    showAlgebra.setText("Algebra");

    // preferences
    openPrefs = new JButton ((Action)actionMap.get ("Preferences"));
    openPrefs.setText ("Preferences");

    quit = new JButton ((Action)actionMap.get("Quit"));
    quit.setText(null);

    // -- build our tool bar
    add (open);
    addSeparator ();
    add (loadFairness);
    add (saveFairness);
    add (loadCTL);
    add (saveCTL);
    addSeparator ();
    add (showAlgebra);
    addSeparator ();
    add (openPrefs);
    add (quit);

    // turn off dragging
    setFloatable (false);
  }
  
}
