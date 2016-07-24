package edu.toronto.cs.gui;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.util.gui.*;


/**
 ** The runtime options panel for our Model-Checker.
 **/
public class OptionsPanel extends JPanel
{
  JTabbedPane tabs;
  JPanel fair;
  JPanel macro;

  // fairness tab
  JCheckBox fair_chk;
  JLabel fair_use_cond;
  JRadioButton fair_all, fair_some;
  ButtonGroup fair_opts;
  JList fair_list;
  JScrollPane fair_scroll;
  JButton fair_add, fair_edit, fair_remove;
  JPanel fair_buttons;
  JPanel fair_select;

  // needed for selecting items
  int [] selection = {};

  // macro tab
  JLabel macro_label1;
  JList macro_list;
  JScrollPane macro_scroll;
  JLabel macro_label2;
  JTextArea macro_exp;
  JScrollPane macro_exp_scroll;
  JButton macro_add, macro_edit, macro_remove;
  JPanel macro_buttons;

  
  /**
   ** Construct the Options Panel.
   **/
  public OptionsPanel ()
  {
    super ();

    tabs = new JTabbedPane ();
    fair = new JPanel ();
    fair.setLayout (new BoxLayout (fair, BoxLayout.Y_AXIS));
    macro = new JPanel ();

    // do we use fairness at all?
    fair_chk = new JCheckBox ("Use fairness", false);

    fair_chk.addItemListener
      (new ItemListener ()
       {
	 public void itemStateChanged (ItemEvent e)
	   {
	     enableFairness (e.getStateChange () == ItemEvent.SELECTED);
	   }
       });

    // if so, which conditions do we use?
    fair_use_cond = new JLabel ("Fairness conditions to be used:");
    fair_all = new JRadioButton ("All", true);
    fair_all.addItemListener
      (new ItemListener ()
       {
	 public void itemStateChanged (ItemEvent e)
	   {
	     if (e.getStateChange () == ItemEvent.SELECTED)
	       selectAllFairness ();
	   }
       });
    fair_some = new JRadioButton ("Selected");
    fair_some.addItemListener
      (new ItemListener ()
       {
	 public void itemStateChanged (ItemEvent e)
	   {
	     if (e.getStateChange () == ItemEvent.SELECTED)
	       selectSomeFairness ();
	   }
       });
    fair_opts = new ButtonGroup ();
    fair_opts.add (fair_all);
    fair_opts.add (fair_some);

    // a scrolling list of fairness conditions
    DefaultListModel listmodel = new DefaultListModel ();
    fair_list = new JList (listmodel);
    fair_list.setSelectionMode 
      (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    // select all of the conditions by default
    selectAllFairness ();
    fair_list.addListSelectionListener
      (new ListSelectionListener ()
       {
	 public void valueChanged (ListSelectionEvent e)
	   { selectedFairnessChanged (); }
       });

    fair_scroll = new JScrollPane
      (fair_list,
       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // a few buttons: add, edit and remove
    fair_add = new JButton ("Add");
    fair_add.addActionListener 
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e) { addFairness (); }
       });
    fair_edit = new JButton ("Edit");
    fair_edit.addActionListener 
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e) { editFairness (); }
       });
    fair_remove = new JButton ("Remove");    
    fair_remove.addActionListener 
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e)
	   { removeSelectedFairness (); }
       });
    fair_buttons = new JPanel ();
    fair_buttons.setLayout (new FlowLayout ());
    fair_buttons.add (fair_add);
    fair_buttons.add (fair_edit);
    fair_buttons.add (fair_remove);

    // a panel that holds all the conditions selection
    fair_select = new JPanel ();
    fair_select.setLayout (new BoxLayout (fair_select, BoxLayout.Y_AXIS));
    fair_select.setBorder (BorderFactory.createCompoundBorder
			   (StandardFiller.makeEmptyBorder (),
			    BorderFactory.createLoweredBevelBorder ()));
    fair_select.add (fair_use_cond);
    fair_select.add (fair_all);
    fair_select.add (fair_some);
    fair_select.add (fair_scroll);
    fair_select.add (fair_buttons);
    // put the fairness tab together
    fair.add (fair_chk);
    fair.add (fair_select);

    // disable fairness for now
    GUIUtil.setEnabled (fair_select, false);

    // now let's make all the components for the macro tab
    macro.setLayout (new BoxLayout (macro, BoxLayout.Y_AXIS));
    // the label for the test field
    macro_label1 = new JLabel ("Macro:");
    // the text field for inputting CTL
    macro_list = new JList (new DefaultListModel ());
    macro_list.setSelectionMode 
      (ListSelectionModel.SINGLE_SELECTION);
    macro_list.addListSelectionListener
      (new ListSelectionListener ()
       {
	 public void valueChanged (ListSelectionEvent e)
	   { showMacroExpansion (); }
       });
    macro_scroll = new JScrollPane
      (macro_list,
       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // the macro expansion area
    macro_label2 = new JLabel ("Expansion:");
    macro_exp = new JTextArea ();
    macro_exp.setLineWrap (true);
    macro_exp.setWrapStyleWord (true);
    macro_exp.setEditable (false);
    // scrolling capability
    macro_exp_scroll = new JScrollPane 
      (macro_exp,
       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    // a few buttons: add, edit and remove
    macro_add = new JButton ("Add");
    macro_add.addActionListener 
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e) { addMacro (); }
       });
    macro_edit = new JButton ("Edit");
    macro_edit.addActionListener 
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e) { editMacro (); }
       });
    macro_remove = new JButton ("Remove");    
    macro_remove.addActionListener
      (new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e)
	   { removeSelectedMacro (); }
       });
    macro_buttons = new JPanel ();
    macro_buttons.setLayout (new FlowLayout ());
    macro_buttons.add (macro_add);
    macro_buttons.add (macro_edit);
    macro_buttons.add (macro_remove);

    // put all of the macro components together
    macro.add (macro_label1);
    macro.add (macro_scroll);
    macro.add (StandardFiller.makeVstrut ()); // need more space here
    macro.add (macro_label2);
    macro.add (macro_exp_scroll);
    macro.add (macro_buttons);
    macro.setBorder (StandardFiller.makeEmptyBorder ());
    showMacroExpansion ();
    
    // last, but not least, put everything together
    setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
    setBorder (BorderFactory.createTitledBorder
	       (BorderFactory.createCompoundBorder
		(BorderFactory.createEtchedBorder (),
		 StandardFiller.makeEmptyBorder ()), "Options"));
    tabs.add ("Fairness", fair);
    tabs.add ("Macros", macro);

    add (tabs);

    // setting alignment -> Lawful Evil
    GUIUtil.alignAllX (fair.getComponents (), Component.LEFT_ALIGNMENT);
    GUIUtil.alignAllX (fair_select.getComponents (), .1f);
    fair_all.setAlignmentX (Component.LEFT_ALIGNMENT);
    fair_some.setAlignmentX (Component.LEFT_ALIGNMENT);
    GUIUtil.alignAllX (macro.getComponents (), Component.LEFT_ALIGNMENT);

    // Use Preferences to set the initial status of KEG
    //XXX this is bad, need the constants for Preferences from XChekGui.java
    fair_chk.setSelected(edu.toronto.cs.gui.XChekGUI.PREFS.getBoolean
			 (edu.toronto.cs.gui.XChekGUI.FAIRNESS_BL, false));
  }

  /**
   ** Set fairness.
   **/
  public void setFairness (java.util.List fair)
  {
    // enable fairness if it was disabled
    if (fair.size () > 0 && !fair_chk.isSelected ())
      {
	fair_chk.setSelected (true);
      }

    DefaultListModel model = new DefaultListModel ();

    for (Iterator i = fair.iterator (); i.hasNext ();)
      model.addElement (i.next ().toString ());
    
    fair_list.setModel (model);
    // You have to explicitly do this, or your gui and your model
    // don't actually coincide on startup.
    selectAllFairness ();
  }
  
  /**
   ** Get all fairness conditions regardless of whether they are
   ** turned on or not.
   ** Note: this is not particularly fast. 
   ** @return fairness.
   **/
  public CTLNode[] getAllFairness ()
  {
    // save the old selection state
    boolean some = fair_some.isSelected ();
    fair_all.setSelected (true);
    CTLNode[] fair = getFairness ();
    fair_some.setSelected (some);

    return fair;
  }

  /**
   ** Get fairness. Note: this is not particularly fast. 
   ** @return fairness.
   **/
  public CTLNode[] getFairness ()
  {
    if (!fair_chk.isSelected ()) return CTLAbstractNode.EMPTY_ARRAY;

    java.util.List fairness = new ArrayList ();
    int[] index = fair_list.getSelectedIndices ();
    DefaultListModel model = (DefaultListModel) fair_list.getModel ();

    try 
      {
	for (int i = 0; i < index.length; i++)
	  fairness.add (CTLNodeParser.parse ((String)model.get (index [i])));
      }
    catch (CTLNodeParser.CTLNodeParserException ex)
      {
	ex.printStackTrace ();
	throw new RuntimeException (ex);
      }
    

    return (CTLNode[])fairness.toArray (new CTLNode [fairness.size ()]);
  }

  /**
   ** Clear the fairness conditions.
   **/
  public void clearFairness ()
  {
    fair_chk.setSelected (false);
    fair_list.setModel (new DefaultListModel ());
  }

  /**
   ** Clear the macros.
   **/
  public void clearMacros ()
  {
    macro_list.setModel (new DefaultListModel ());
    macro_exp.setText ("");
  }

  /**
   ** Set the CTL macro manager.
   **/
//   public void setCTLMacroManager (CTLMacroManager man)
//   {
//     macroManager = man;
//     DefaultListModel model = new DefaultListModel ();

//     for (Iterator i = man.getAllMacroNames ().iterator (); i.hasNext ();)
//       model.addElement (i.next ().toString ());

//     macro_list.setModel (model);


//      // load all the macro names into the combo box
//      Collection c = macroManager.getAllMacroNames ();
//      for (Iterator i = c.iterator (); i.hasNext ();)
//        macro_list.addItem ((i.next ()).toString ());
//   }

  /**
   ** Get the CTL macro manager.
   ** @return the CTL macro manager.
   **/
//   public CTLMacroManager getCTLMacroManager ()
//   {
//     return macroManager;
//   }

  /**
   ** Enables or disables tabs.
   **/
  public void setEnableTabs (boolean enabled)
  {
    GUIUtil.setEnabled (tabs, enabled);
    if (enabled) enableFairness (fair_chk.isSelected ());
  }

  /**
   ** Enable / Disable fairness.
   **/
  public void enableFairness (boolean enable)
  {
    if (enable)
      {
	GUIUtil.setEnabled (fair_select, true);
      }
    else // DESELECTED
      GUIUtil.setEnabled (fair_select, false);
  }

  /**
   ** Select all fairness conditions.
   **/
  private void selectAllFairness ()
  {
    // back up old selection
    selection = fair_list.getSelectedIndices ();
    // select all of the conditions by default
    fair_list.addSelectionInterval (0,
				    fair_list.getModel ().
				    getSize ()-1);
  }

  /**
   ** Select some fairness conditions.
   **/
  private void selectSomeFairness ()
  {
    // clear old selection
    fair_list.clearSelection ();
    // restore old selection
    fair_list.setSelectedIndices (selection);
  }

  /**
   ** Invoked when the selected fairness conditions are changed.
   **/
  private void selectedFairnessChanged ()
  {
    if (fair_all.isSelected ())
      {
	int temp [] = fair_list.getSelectedIndices ();
	if (temp.length < fair_list.getModel ().getSize ())
	  {
	    selection = temp;
	    fair_some.setSelected (true);
	  }
      }
  }

  /**
   ** Show macro expansion.
   **/
  private void showMacroExpansion ()
  {
  }

  /**
   ** Removes selected Fairness conditions.
   **/
  private void removeSelectedFairness ()
  {
    // what needs to be removed
    Object toremove [] = fair_list.getSelectedValues ();
    // get the list model
    DefaultListModel list = (DefaultListModel) fair_list.getModel ();

    for (int i = 0; i < toremove.length; i++)
      list.removeElement (toremove [i]);
  }

  /**
   ** Add a fairness condition.
   **/
  private void addFairness ()
  {
    String newcond = showTextLineDialog ("Add fairness",
					 "New fairness condition:", "");
    if (!newcond.equals ("") &&
	(CTLNodeParser.safeParse (newcond) != null))
      {
	((DefaultListModel) fair_list.getModel ()).addElement (newcond);
	selectedFairnessChanged ();
      }
  }

  /**
   ** Edit a fairness condition.
   **/
  private void editFairness ()
  {
    String newcond = showTextLineDialog
      ("Edit fairness", "New fairness condition:",
       (String) fair_list.getSelectedValue ());

    if (!newcond.equals ("") &&
	(CTLNodeParser.safeParse (newcond) != null))
      {
	int oldspot = fair_list.getSelectedIndex ();
	((DefaultListModel) fair_list.getModel ()).remove (oldspot);
	((DefaultListModel) fair_list.getModel ()).add (oldspot, newcond);
      }
  }

  /**
   ** Add a new macro.
   **/
  private void addMacro ()
  {
    MacroEditPanel panel = new MacroEditPanel
      ("New macro:", "", "Expansion:", "");
    JOptionPane addmacroes = new JOptionPane 
      (panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = addmacroes.createDialog (this, "Add Macro");

    // show the dialog
    dialog.setVisible (true);
    String m = panel.getMacroName ();
    String e = panel.getMacroExpansion ();
    
    if (!m.equals ("") && !e.equals ("") &&
	addmacroes.getValue ().equals (new Integer (JOptionPane.OK_OPTION)))
      {
// 	macroManager.defineMacro
// 	  (BinaryTreeToCTLConverter.convertToCTL (m),
// 	   BinaryTreeToCTLConverter.convertToCTL (e, getFairness ()));
	// update the macro list
	((DefaultListModel) macro_list.getModel ()).addElement (m);
	macro_list.setSelectedValue (m, true);
      }
  }

  /**
   ** Edit a macro.
   **/
  private void editMacro ()
  {
    String oldmacro = (String) macro_list.getSelectedValue ();
    String expansion = 
      CTLNodeParser.safeParse (oldmacro).toString ();
    
    MacroEditPanel panel = new MacroEditPanel
      ("Macro:", oldmacro, "Expansion:", expansion);
    JOptionPane editmacroes = new JOptionPane 
      (panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = editmacroes.createDialog (this, "Edit Macro");

    // show the dialog
    dialog.setVisible (true);
    String m = panel.getMacroName ();
    String e = panel.getMacroExpansion ();
    
    if (!m.equals ("") && !e.equals ("") &&
	editmacroes.getValue ().equals (new Integer (JOptionPane.OK_OPTION)))
      {
	// remove the old macro
	macro_list.setSelectedValue (oldmacro, true);
	removeSelectedMacro ();

	// update the macro list
	((DefaultListModel) macro_list.getModel ()).addElement (m);
	macro_list.setSelectedValue (m, true);
      }

  }

  /**
   ** Removes selected macro.
   **/
  private void removeSelectedMacro ()
  {
    // what needs to be removed
    Object toremove = macro_list.getSelectedValue ();
    // get the list model
    DefaultListModel list = (DefaultListModel) macro_list.getModel ();
    if (list.removeElement (toremove))
      {
	showMacroExpansion ();
      }
  }

  /**
   ** Create an add/edit dialog. This is a convenience method.
   ** @return the string that was entered.
   **/
  private String showTextLineDialog (String title,
				     String message, String text)
  {
    Object result = JOptionPane.showInputDialog
      (this, message, title, JOptionPane.PLAIN_MESSAGE, null, null, text);

    if (result == null)
      return "";
    else return result.toString ();
  }

  class MacroEditPanel extends JPanel
  {
    // fields and their respective labels
    JLabel namelabel, explabel;
    JTextField name, expansion;
    
    MacroEditPanel (String text1, String field1, String text2, String field2)
    {
      // set up the labels for the add/edit panel
      namelabel = new JLabel (text1);
      explabel = new JLabel (text2);
      
      // set up the fields
      name = new JTextField (field1);
      expansion = new JTextField (field2);
      
      name.addActionListener
	(new ActionListener ()
	 {
	   public void actionPerformed (ActionEvent e)
	     {
	       expansion.requestFocus ();
	     }
	 });
      // add all the components to this panel
      this.setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
      this.add (namelabel);
      this.add (name);
      this.add (explabel);
      this.add (expansion);
      GUIUtil.alignAllX (this.getComponents (), Component.LEFT_ALIGNMENT);
    }

    String getMacroName ()
    {
      return CTLNodeParser.safeParse (name.getText ()) != null ? 
	name.getText () : "";
    }

    String getMacroExpansion ()
    {
      return getMacroName ();
    }
  }
}
