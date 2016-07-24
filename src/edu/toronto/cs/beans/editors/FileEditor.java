package edu.toronto.cs.beans.editors;

import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.FilePanel;

/**
 * Filename editor
 */
public class FileEditor extends PropertyEditorSupport
{
  FilePanel filePanel = null;
 
  public FileEditor ()
  {
    super ();
  }

  
  public void setAsText (String s)
  {
    setValue (new File (s));
  }


  public String getAsText ()
  {
    if (getValue () == null) return "";
    
    return ((File)getValue ()).getAbsolutePath ();
  }
  
  public Component getCustomEditor ()
  {
    if (filePanel == null)
      filePanel = initFilePanel ();
    
    return filePanel;
  }

  public FilePanel initFilePanel ()
  {
    final FilePanel result = new FilePanel ("", new JFileChooser ());
    //result.setFileName (getAsText ());

    FilePanelGlue glue = new FilePanelGlue (result, this);
    //this.addPropertyChangeListener (glue);
    result.getDocument ().addDocumentListener (glue);
    return result;
  }

  class FilePanelGlue implements PropertyChangeListener,
				 DocumentListener
  {
    FilePanel p;
    PropertyEditor e;
    
    public FilePanelGlue (FilePanel _p, PropertyEditor _e)
    {
      p = _p;
      e = _e;
    }

    public void insertUpdate(DocumentEvent e)
    {
      changeToDoc ();
    }

    public void removeUpdate(DocumentEvent e)
    {
      changeToDoc ();
    }

    public void changedUpdate(DocumentEvent e)
    {
      changeToDoc ();
    }
    
    public void changeToDoc ()
    {
      try 
	{
	  e.setAsText (p.getFileName ());
	}
      catch (Exception e)
	{
	  // XXX Better error handling
	  e.printStackTrace ();
	  assert false : e;
	}
    }
    
    public void propertyChange (PropertyChangeEvent g)
    {
      try 
	{
	  p.setFileName (e.getAsText ());
	}
      catch (Exception e)
	{
	  // XXX Better error handling
	  e.printStackTrace ();
	  assert false: e;
	}
    }	  
  }  
}

  
