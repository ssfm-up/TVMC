package edu.toronto.cs.beans;


import java.beans.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.beans.editors.*;
import edu.toronto.cs.util.gui.*;



public class ModelCompilerPropertyDialog extends JDialog
{

  ModelCompilerPropertySheet propertySheet;
  boolean okPressed = false;
  

  public ModelCompilerPropertyDialog (JFrame owner, String className) 
    throws IOException, IntrospectionException, ClassNotFoundException
  {
    super (owner, className, true);
    propertySheet = new ModelCompilerPropertySheet (className);
    initialize ();
  }
    
  public ModelCompilerPropertyDialog (JFrame owner, 
				      ModelCompiler modelCompiler)
    throws IntrospectionException
  {
    super (owner, modelCompiler.getClass ().getName (), true);
    propertySheet = new ModelCompilerPropertySheet (modelCompiler);
    initialize ();
  }
  
  private void initialize ()
  {

    JButton okButton = new JButton ("Ok");
    okButton.addActionListener 
      (new ActionListener ()
	{
	  public void actionPerformed (ActionEvent e)
	  {
	    okPressed = true;
	    ModelCompilerPropertyDialog.this.setVisible (false);
	  }
	});

    JButton cancelButton = new JButton ("Cancel");
    cancelButton.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  okPressed = false;
	  ModelCompilerPropertyDialog.this.setVisible (false);
	}
      });

    JPanel buttonPanel = new JPanel ();
    buttonPanel.add (cancelButton);
    buttonPanel.add (StandardFiller.makeHstrut ());
    buttonPanel.add (okButton);
    okButton.setPreferredSize (cancelButton.getPreferredSize ());
    GUIUtil.alignAllX (buttonPanel.getComponents (), 
		       Component.CENTER_ALIGNMENT);

      
    getContentPane ().setLayout (new BorderLayout ());
    getContentPane ().add (BorderLayout.CENTER, propertySheet);
    getContentPane ().add (BorderLayout.SOUTH, buttonPanel);

    pack ();
    setVisible (true);
  }
  
  public boolean cancel ()
  {
    return !okPressed;
  }

  public ModelCompiler getModelCompiler ()     
    throws IllegalAccessException, InvocationTargetException
  {
    return propertySheet.getModelCompiler ();
  }

  // XXX This should be done properly later on
  static
  {
    PropertyEditorManager.registerEditor (IAlgebra.class, 
					  IAlgebraEditor.class);
    PropertyEditorManager.registerEditor (java.io.File.class, 
					  FileEditor.class);
  }
  

  public static void main (String[] args) throws Exception
  {
    PropertyEditorManager.registerEditor (IAlgebra.class, 
					  IAlgebraEditor.class);
    PropertyEditorManager.registerEditor (java.io.File.class, 
					  FileEditor.class);


    JFrame test = new JFrame ();


    ModelCompilerPropertyDialog dialog = 
      new ModelCompilerPropertyDialog (test, args [0]);

    if (dialog.cancel ()) 
      System.out.println ("Canceled");
    else
      {
	ModelCompiler compiler = dialog.getModelCompiler ();
	System.out.println ("Got a model compiler!");
	ModelCompilerEncoder encoder = new ModelCompilerEncoder
	  (new BufferedOutputStream 
	   (new FileOutputStream ("ParserHelper.xml")));
	encoder.writeObject (compiler);
	encoder.close ();
      }
  }

}

