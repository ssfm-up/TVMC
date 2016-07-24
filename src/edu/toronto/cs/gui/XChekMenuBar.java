package edu.toronto.cs.gui;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;


/***
 * A toolbar for the model-checker GUI
 *
 * Based on XCMeny by someone in FMGroup
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */

/**
 ** The menu for our Model-Checker.
 **/
public class XChekMenuBar extends JMenuBar
{
  // various menus
  JMenu file;
  JMenu model;
  JMenu fairness;
  JMenu ctlHistory;
  JMenu help;

//    JMenuItem open;
  JMenuItem openModel;
  JMenuItem editPrefs;
  
  //  JMenuItem fairness;
  JMenuItem loadFairness;
  JMenuItem saveFairness;

  //  JMenuItem ctlhistory;
  JMenuItem loadCTL;
  JMenuItem saveCTL;

  JMenuItem quit;
  JMenuItem modelInfo;
  JMenuItem showVariables;
  JMenuItem helpItem;
  JMenuItem about;

  /**
   ** Constructs a menu for our model-checker.
   **/
  public XChekMenuBar (ActionMap actionMap)
  {    
    super ();

    file = new JMenu ("File");
    file.setMnemonic (KeyEvent.VK_F);

    model = new JMenu ("Model");
    model.setMnemonic (KeyEvent.VK_M);

    fairness = new JMenu ("Fairness");
    fairness.setMnemonic (KeyEvent.VK_A);

    ctlHistory = new JMenu ("CTL history");
    ctlHistory.setMnemonic (KeyEvent.VK_C);

    help = new JMenu ("Help");
    help.setMnemonic (KeyEvent.VK_H);


    // open submenu
    
    openModel = new JMenuItem ((Action)actionMap.get ("Open a model..."));
    openModel.setIcon (null);
    openModel.setAccelerator (KeyStroke.getKeyStroke ("alt G"));

    //edit Preferences submenu
    editPrefs = new JMenuItem ((Action)actionMap.get ("Preferences"));
    editPrefs.setIcon (null);
    editPrefs.setAccelerator (KeyStroke.getKeyStroke ("alt P"));

    // fairness menu
    loadFairness =
      new JMenuItem ((Action)actionMap.get ("Load fairness conditions"));
    loadFairness.setIcon (null);
    loadFairness.setAccelerator (KeyStroke.getKeyStroke ("F3"));

    saveFairness =
      new JMenuItem ((Action)actionMap.get ("Save fairness conditions"));
    saveFairness.setIcon (null);
    saveFairness.setAccelerator (KeyStroke.getKeyStroke ("F4"));

    // CTL history menu
    loadCTL = new JMenuItem ((Action)actionMap.get ("Load CTL history"));
    loadCTL.setIcon (null);
    loadCTL.setAccelerator (KeyStroke.getKeyStroke ("F5"));

    saveCTL = new JMenuItem ((Action)actionMap.get ("Save CTL history"));
    saveCTL.setIcon (null);
    saveCTL.setAccelerator (KeyStroke.getKeyStroke ("F6"));
    
    // quit
    quit = new JMenuItem ((Action)actionMap.get("Quit"));
    quit.setIcon (null);
    quit.setAccelerator (KeyStroke.getKeyStroke ("alt Q"));
    
    // show model info
    modelInfo = new JMenuItem ((Action)actionMap.get ("Model info"));
    modelInfo.setIcon (null);
    modelInfo.setAccelerator (KeyStroke.getKeyStroke ("alt I"));
    
    // show variables
    showVariables = new JMenuItem ((Action)actionMap.get ("Show variables"));
    showVariables.setIcon (null);
    showVariables.setAccelerator (KeyStroke.getKeyStroke ("alt V"));

    // help, about...
    helpItem = new JMenuItem ((Action)actionMap.get ("Help"));
    helpItem.setIcon (null);
    helpItem.setAccelerator (KeyStroke.getKeyStroke ("F1"));

    about = new JMenuItem ((Action)actionMap.get ("About"));
    about.setIcon (null);
    

    // -- File menu
    file.add (openModel);
    file.add (editPrefs);
    file.add (new JSeparator ());
    file.add (quit);

    // -- Fairness menu
    fairness.add (loadFairness);
    fairness.add (saveFairness);

    // -- CTL History menu
    ctlHistory.add (loadCTL);
    ctlHistory.add (saveCTL);

    // -- Model menu
    model.add (modelInfo);
    model.add (showVariables);

    // -- Help menu
    help.add (helpItem);
    help.add (about);
    
    // -- finally build the menu bar
    add (file);
    add (model);
    add (fairness);
    add (ctlHistory);
    add (help);
  }

}






