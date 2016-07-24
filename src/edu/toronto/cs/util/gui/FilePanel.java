package edu.toronto.cs.util.gui;

import java.util.*;
import java.io.*;
import javax.swing.*;

import javax.swing.filechooser.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;



/**
 * A utility panel for acquiring a filename from the user.  Presents a
 * labeled text field where the user can enter the file by hand, and a
 * button to bring up file chooser.
 **/
public class FilePanel extends JPanel
{
  JTextField filenameFld;

  String dir;

  JFileChooser filechooser;

  String title;
  

  /**
   * Creates a new <code>FilePanel</code> instance relative to the
   * user's home directory.
   *
   * @param text a <code>String</code> value
   * @param fc a <code>JFileChooser</code> value
   */
  public FilePanel (String text, JFileChooser fc)
  {
    super ();
    init (text, fc, java.lang.System.getProperty("user.dir"));
  }

  /**
   * Creates a new <code>FilePanel</code> instance relative to a given
   * directory.
   *
   * @param text a <code>String</code> value
   * @param fc a <code>JFileChooser</code> value
   * @param _dir a <code>String</code> value
   */
  public FilePanel (String text, JFileChooser fc, String _dir)
  {
    super ();
    init(text, fc, _dir);
    
  }
  
  /**
   * Initializes the panel.
   *
   * @param text string describing what file is required
   * @param fc a <code>JFileChooser</code> to select a file
   * @param _dir an implicit directory relative to which the selection
   * will happen
   */
  private void init (String text, JFileChooser fc, String _dir)
  {

    // -- store file chooser and the directory
    filechooser = fc;
    dir = _dir;
    title = text;


    setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));    
    
    // -- if there is a label to be displayed, create a panel to host it
    // -- and add it to the current component
    if (text.length () != 0)  
      {
	JLabel label = new JLabel (text);
	JPanel labelPanel = new JPanel();
	labelPanel.setLayout (new BoxLayout (labelPanel, BoxLayout.X_AXIS));
	labelPanel.add (label);
	labelPanel.add (Box.createHorizontalGlue());
	add(labelPanel);
      }

    
    filenameFld = new JTextField (30);
    JButton browseBtn = new JButton ("Browse...");

    JPanel browsePanel = new JPanel();
    browsePanel.setLayout (new BoxLayout (browsePanel, BoxLayout.X_AXIS));

    
    // set up the action listener for the button
    browseBtn.addActionListener (getFileGlueListener ());

    filenameFld.getDocument ().addDocumentListener (new DocumentListener ()
      {
	public void changedUpdate(DocumentEvent e) 
	{
	  filenameFld.postActionEvent ();
	}

	public void insertUpdate(DocumentEvent e) 
	{
	  changedUpdate (e);
	}
	
	public void removeUpdate(DocumentEvent e) 
	{
	  changedUpdate (e);
	}
      });
    

    browsePanel.add (filenameFld);
    browsePanel.add (StandardFiller.makeHstrut ());
    browsePanel.add (browseBtn);
    browsePanel.add (Box.createHorizontalGlue());

    
    add(browsePanel);
    
  }

  /**
   * Sets the file selected by this FilePanel.
   *
   * @param a file to be selected
   */
  public void setSelectedFile (File file)
  {
    filenameFld.setText (file.getAbsolutePath ());
  }

  /**
   * Returns the currently selected file or implicit directory.
   *
   * @return currently selected file
   */
  public File getSelectedFile ()
  {
    String fileName = getFileName ();
    
    if (fileName.length () > 0) 
      return new File (fileName);
    else 
      return new File (dir);
  }
  
  
  /**
   * Sets the content of the filename field.
   *
   * @param name a string
   */
  public void setFileName (String name)
  {
    filenameFld.setText (name);
  }

  /**
   * Returns the current content of the filename field
   *
   * @return a <code>String</code> value
   */
  public String getFileName ()
  {
    return filenameFld.getText ();
  }
  
  public void addActionListener (ActionListener l)
  {
    filenameFld.addActionListener (l);
  }

  public void removeActionListener (ActionListener l)
  {
    filenameFld.removeActionListener (l);
  }

  public Document getDocument ()
  {
    return filenameFld.getDocument ();
  }
  
  
  /**
   * Action to be performed when the browse button is pressed. This
   * method can be overriden by subclasses to provide custom file
   * picking mechanism.
   *
   * @return an <code>ActionListener</code> value
   */
  protected ActionListener getFileGlueListener ()
  {
    return new ActionListener ()
       {
	 public void actionPerformed (ActionEvent e)
	   {
	     // -- set filechoser to the currently selected file
	     filechooser.setSelectedFile (getSelectedFile ());
	     
	     // -- set title of the file chooser if one exists
	     if (title != null && title.length () > 0)
	       filechooser.setDialogTitle (title);

	     // -- open the dialog
	     int result = filechooser.showOpenDialog (null);

	     if (result == JFileChooser.APPROVE_OPTION)
	       setSelectedFile (filechooser.getSelectedFile ());
	   }
      };
  }
  
}
