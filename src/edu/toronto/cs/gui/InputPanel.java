package edu.toronto.cs.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.gui.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.simulator.*;

/**
 ** The CTL input panel for our Model-Checker.
 **/
public class InputPanel extends JPanel
{
  // the label for the test field
  JLabel label;
  // the text field for inputting CTL
  JComboBox inputfield;
  // put the label and the text field together
  //  JPanel ctlin;
  // "Run" button
  JButton run;
  // "counter-example" check box
  JCheckBox keg;

  // "Trace" button
  JButton trace;
  // a window to work with traces
  XCTraceViewer traceviewer;


  // List of variables
  VarList variablelist;

  /**
   ** Creates the part of the GUI that deals with inputting a CTL
   ** formula and running the model-checker on it ("run" button).
   **/
  public InputPanel ()
  {
    // the label for the test field
    label = new JLabel ("CTL :");
    // the text field for inputting CTL
    inputfield = new JComboBox ();
    
    inputfield.setName ("ctlinput");
    inputfield.setMaximumRowCount (5);
    inputfield.setEditable (true);
    // put the label and the text field together
    JPanel ctlin = new JPanel ();
    ctlin.setLayout (new BoxLayout (ctlin, BoxLayout.X_AXIS));
    ctlin.add (label);
    ctlin.add (inputfield);

    // the buttons
    JPanel buttons = new JPanel ();
    buttons.setLayout (new BoxLayout (buttons, BoxLayout.X_AXIS));
    run = new JButton ("Run");
    //    run.setName ("ctlrun");
    trace = new JButton ("Trace");
    keg = new JCheckBox ("Produce counter-example", false);

    // Use Preferences to set the initial status of KEG
    //XXX this is bad, need the constants for Preferences from XChekGui.java
    keg.setSelected(edu.toronto.cs.gui.XChekGUI.PREFS.getBoolean (XChekGUI.CNTR_EX_BL, false));
    
    buttons.add (Box.createGlue ());
    buttons.add (run);
    buttons.add (StandardFiller.makeHstrut ());
    buttons.add (trace);
    buttons.add (StandardFiller.makeHstrut ());
    buttons.add (keg);
    
    // put all of the components together
    setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
    add (buttons);
    add (StandardFiller.makeVstrut ()); // need to add some space
    add (ctlin);

    variablelist = new VarList ();

    // setting alignment -> Lawful Evil
    //    GUIutil.alignAllX (getComponents (), Component.RIGHT_ALIGNMENT);
  }

  /**
   ** Hooks up the listener that deals with the CTL input.
   **/
  public void addCTLKeyListener (KeyListener l)
  {
    for (Iterator it = Arrays.asList 
	   (inputfield.getComponents ()).iterator (); it.hasNext ();)
      {
	Component c = (Component) it.next ();
	// we should add the listener to the text field
	if (c instanceof JTextField)
	  c.addKeyListener (l);
      }
  }

    /**
     ** Adds an action listener to the "Run" button.
     ** @param l - ActionListener to add.
     **/
  public void addRunActionListener (ActionListener l)
  {
    run.addActionListener (l);
    //inputfield.getInputMap ().put (KeyStroke.getKeyStroke ('\r'), "run");
    //inputfield.getActionMap ().put ("run", l);
  }

    /**
     ** Adds an action listener to the "Trace" button.
     ** @param l - ActionListener to add.
     **/
  public void addTraceActionListener (ActionListener l)
  {
    trace.addActionListener (l);
  }

  public String getInputString ()
  {
    // XXX This is an ungly way to get at the JTextField of the JComboBox
    // XXX to get the text that is typed there. There is probably a better
    // XXX way to do it, but that is how it was done before so we just
    // XXX repeat the hack :(
    Component[] components = inputfield.getComponents ();
    for (int i = 0; i < components.length; i++)
      {
	if (components [i] instanceof JTextField)
	  return ((JTextField)components [i]).getText ();
      }
    assert false : "Could not find a JTextField";
    return null;
  }
  


  /**
   ** Add CTL to history.
   **/
  public void addCTLToHistory (String ctl)
  {
    inputfield.setSelectedItem (ctl);
    if (inputfield.getSelectedIndex () < 0)
      inputfield.addItem (ctl);
  }

  /**
   ** Clear the CTL history.
   **/
  public void clearCTLHistory ()
  {
    inputfield.removeAllItems ();
  }

  /**
   ** Set the CTL history.
   **/
  public void setCTLHistory (java.util.List ctlhistory)
  {
    clearCTLHistory ();
    for (Iterator it = ctlhistory.iterator (); it.hasNext ();)
      inputfield.addItem (it.next ().toString ());
  }

  /**
   ** Get the current CTL history.
   **/
  public java.util.List getCTLHistory ()
  {
    java.util.List ctlhistory = new LinkedList ();

    for (int i = 0; i < inputfield.getItemCount (); i++)
      ctlhistory.add (inputfield.getItemAt (i));

    return ctlhistory;
  }

  /**
   ** Tells whether or not there is some CTL history to save.
   **/
  public boolean hasHistory ()
  {
    return (inputfield.getItemCount () > 0);
  }

  /**
   ** Should a counter-example be generated?
   **/
  public boolean isCounterExampleEnabled ()
  {
    return (keg.getSelectedObjects () != null);
  }

  /**
   ** Enables/disables counter-example.
   **/
  public void setCounterExampleEnabled (boolean enabled)
  {
    keg.setSelected (enabled);
  }

  /**
   ** Shows the current list of variables.
   **/
  public void showVariables (String [] v)
  {
    updateVariables (v);
    variablelist.setVisible (true);
    variablelist.setState (JFrame.NORMAL);
  }

  /**
   ** Updates the variable list.
   **/
  public void updateVariables (String [] v)
  {
    if (v == null) v = new String [0];
    variablelist.init (v);
  }

  /**
   ** A class for showing the list of available variables.
   **/
  class VarList extends JFrame
  {
    JPanel pane;
    JLabel text;
    JList varlist; 
    JScrollPane scroll;
    JPanel buttons;
    JButton copy, done;

    VarList ()
    {
      // Eventually, another action map here?
      ActionListener addCTLAction = new ActionListener () 
	 {
	   public void actionPerformed (ActionEvent e)
	     {
	       //System.out.println (e.getModifiers ());
	       if (varlist.getSelectedIndex () < 0) return;
	       JTextField temp = null;
	       // find the text field and add the selected variable to it
	       for (int i = 0; i < inputfield.getComponents ().length; i++)
		 if (inputfield.getComponents () [i] instanceof JTextField)
		   temp = ((JTextField) inputfield.getComponents () [i]);
	       if ((e.getModifiers () & ActionEvent.ALT_MASK) != 0)
	       {
		 temp.setText (temp.getText () + '!' +
			       varlist.getSelectedValue ().toString ());
		 return;
	       }
	       temp.setText (temp.getText () +
			     varlist.getSelectedValue ().toString ());
	     }
	 };
      text = new JLabel ();
      // list of variables
      varlist = new JList ();
      varlist.addMouseListener (new DoubleClickActionAdapter (addCTLAction));
      scroll = new JScrollPane (varlist);
      init (new String [0]);

      // this is only needed here
      final JFrame self = this;
      // Done button
      done = new JButton ("Done");
      done.setMnemonic (KeyEvent.VK_D);
      done.addActionListener 
	(new ActionListener () 
	 {
	   public void actionPerformed (ActionEvent e)
	     { self.setVisible (false); }
	 });
      copy = new JButton ("Paste into CTL");
      copy.setMnemonic (KeyEvent.VK_P);
      copy.addActionListener (addCTLAction);
      buttons = new JPanel ();
      buttons.setLayout (new BoxLayout (buttons, BoxLayout.X_AXIS));
      buttons.setBorder (StandardFiller.makeWideEmptyBorder ());
      buttons.add (copy);
      buttons.add (Box.createGlue ());
      buttons.add (done);

      // set up the list panel
      pane = new JPanel ();
      pane.setLayout (new BoxLayout (pane, BoxLayout.Y_AXIS));
      pane.setBorder (StandardFiller.makeWideEmptyBorder ());
      pane.add (text);
      pane.add (scroll);
      GUIUtil.alignAllX (pane.getComponents (), Component.LEFT_ALIGNMENT);

      // set up the frame
      setTitle ("Variables");
      getContentPane ().add (pane, BorderLayout.CENTER);
      getContentPane ().add (buttons, BorderLayout.SOUTH);
      pack ();
    }    

    void init (String [] v)
    {
      if (v.length == 0)
	text.setText ("No variables.");
      else
	text.setText ("List of variables:");

      // list of variables
      DefaultListModel model = new DefaultListModel ();
      for (int i = 0; i < v.length; i++) model.addElement (v [i]);
      varlist.setModel (model);
    }
  }
}
