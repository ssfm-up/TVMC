package edu.toronto.cs.beans;

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;



import edu.toronto.cs.util.*;

public class BeanUtil
{
  public static final String HELP_ATTRIBUTE = "help";

  public static PropertyEditor getPropertyEditor (PropertyDescriptor d)
  {
    if (d == null) return null;
    
    PropertyEditor editor = null;

    try
      {
        // -- get the editor class of the given property (if it was specified)
        Class editorClass = d.getPropertyEditorClass ();

	if (editorClass != null)
          // if the property has a special editor go with it
	  editor = (PropertyEditor)editorClass.newInstance ();
	else
          // else get a standard editor
	  editor = PropertyEditorManager.findEditor (d.getPropertyType ());
      }
    catch (Exception e)
      {
	// -- no editor, don't report this as an error
	editor = null;
      }
    return editor;
  }


  /**
   ** Returns an editor Component
   **  a) try the editor itself
   **  b) if enumerated type use list box
   **  c) otherwise just use JText
   **/
  public static Component getEditorComponent (PropertyEditor _editor)
  {
    final PropertyEditor editor = _editor;
    
    if (editor.getCustomEditor () != null) return editor.getCustomEditor ();
    
    if (editor.getTags () != null)
      {
	final JComboBox comboBox = new JComboBox (editor.getTags ());
	if (editor.getValue () != null)
	  comboBox.setSelectedItem (editor.getAsText ());
	comboBox.addActionListener
	  (new ActionListener ()
	    {
	      public void actionPerformed (ActionEvent actEvent)
	      {
		editor.setAsText ((String)comboBox.getSelectedItem ());
	      }
	      
	    });
	return comboBox;
      }
    
    // -- otherwise use JText
    final JTextField textField = new JTextField ();
    if (editor.getValue () != null)
      textField.setText (editor.getAsText ());
    
    textField.getDocument ().addDocumentListener 
      (new DocumentListener ()
	{
	  public void changedUpdate (DocumentEvent evt)
	  {
	    editor.setAsText (textField.getText ());
	  }
	  public void insertUpdate (DocumentEvent evt)
	  {
	    editor.setAsText (textField.getText ());
	  }
	  public void removeUpdate (DocumentEvent evt)
	  {
	    editor.setAsText (textField.getText ());
	  }
	});
    return textField;
  }
  
}
