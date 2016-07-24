package edu.toronto.cs.beans;

import java.beans.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.beans.editors.*;
import edu.toronto.cs.util.gui.*;


/****
 **** Displays a list of properties for a Model Compiler
 ****/
public class ModelCompilerPropertySheet extends JPanel
{
  

  ModelCompiler bean;
  BeanDescriptor beanDescriptor;
  BeanInfo beanInfo;

  // -- a map from properties to their editors
  Map properties;
  // -- map from property names to their descriptors
  Map propNames;
  

  JTextArea helpArea;
  JPanel propertyEditorPanel;
  
  Component activeEditor = null;

  public ModelCompilerPropertySheet (String className) 
    throws IOException, IntrospectionException, ClassNotFoundException
  {
    // -- load the model compiler. null means use current class loader
    bean = 
      (ModelCompiler) Beans.getInstanceOf (Beans.instantiate 
					   (null, className),
					   ModelCompiler.class);
    initialize ();
  }

  public ModelCompilerPropertySheet (ModelCompiler modelCompiler)
    throws IntrospectionException
  {
    bean = modelCompiler;
    initialize ();
  }
  
  private void initialize () throws IntrospectionException
  {
    // -- extract information from the bean
    beanInfo = Introspector.getBeanInfo (bean.getClass ());
    beanDescriptor = beanInfo.getBeanDescriptor ();
    buildPropertyMap (beanInfo.getPropertyDescriptors ());
    
    // -- build the panel
    initializePanel ();
  }
  

  void buildPropertyMap (PropertyDescriptor[] propDescriptors)
  {
    properties = new LinkedHashMap ();
    propNames = new LinkedHashMap ();
    for (int i = 0; i < propDescriptors.length; i++)
      {
	PropertyDescriptor prop = propDescriptors [i];
	
	// -- skip hidden properties
	if (prop.isHidden ()) 
	  {
	    System.out.println ("Skipping: " + prop.getDisplayName ());
	    continue;
	  }
	
	// -- skip all read-only properties
	if (prop.getWriteMethod () == null)
	  {
	    System.out.println ("Skipping(w): " + prop.getDisplayName ());
	    continue;
	  }


	// -- get the editor for this property
	PropertyEditor editor = BeanUtil.getPropertyEditor (prop);
	
	// -- skip properties that don't have an editor
	if (editor == null)
	  {
	    System.out.println ("Skipping(e): " + prop.getDisplayName ());
	    continue;
	  }

	
	// -- store the result
	properties.put (prop, editor);
	propNames.put (prop.getDisplayName (), prop);
      }
  }
  

  void initializePanel ()
  {
    // -- we are a panel, so must set our own layout
    setLayout (new BoxLayout (this, BoxLayout.X_AXIS));

    // -- our main screen is divided into left and right
    // -- one the left there is a list of properties and some additional 
    // -- options
    JPanel leftPanel = new JPanel ();
    JList propertyList = buildPropertyJList ();
    JPanel optionsPanel = buildOptionsPanel ();
    
    leftPanel.setLayout (new BoxLayout (leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setBorder (BorderFactory.createTitledBorder
		    (BorderFactory.createCompoundBorder
		     (BorderFactory.createEtchedBorder (),
		      StandardFiller.makeEmptyBorder ()), 
		     "Properties"));
    leftPanel.add (new JScrollPane (propertyList));
    leftPanel.add (new JScrollPane (optionsPanel));
    add (leftPanel);
    
    
    // -- on the right there is a help area and the actual property editor
    JPanel rightPanel = new JPanel ();
    propertyEditorPanel =  buildPropertyEditorPanel ();
    helpArea = buildHelpArea ();
    
    rightPanel = new JPanel ();
    rightPanel.setLayout (new BorderLayout ());
    rightPanel.add (BorderLayout.SOUTH, propertyEditorPanel);
    rightPanel.add (BorderLayout.CENTER, new JScrollPane (helpArea));
    rightPanel.setBorder (BorderFactory.createTitledBorder
					(BorderFactory.createCompoundBorder
					 (BorderFactory.createEtchedBorder (),
					  StandardFiller.makeEmptyBorder ()), 
					 "Property Settings"));
    add (rightPanel);
  }

  

  JList buildPropertyJList ()
  {
    // -- get an array of property names
    String[] propNames = new String [properties.size ()];

    int count = 0;

    for (Iterator it = properties.keySet ().iterator (); it.hasNext ();)
      propNames [count++] = 
	((PropertyDescriptor)it.next ()).getDisplayName ();    

    // -- create the list
    JList propertyList = new JList (propNames);
    
    // -- register onChange event handler
    propertyList.addListSelectionListener 
      (new ListSelectionListener ()
	{
	  public void valueChanged (ListSelectionEvent e)
	  {
	    JList list = (JList)e.getSource ();
	    
	    int newIndex = list.getSelectedIndex ();
	    if (newIndex == -1)
	      return;

	    setActiveProperty ((String)list.getSelectedValue ());
	  }
	});

    // -- ready to be used
    return propertyList;
  }
  
  // -- a panel that holds an editor for currently selected property
  JPanel buildPropertyEditorPanel ()
  {
    JPanel editorPanel = new JPanel ();

    editorPanel.setLayout (new BorderLayout ());

    // -- create a border arround it with a title
    editorPanel.setBorder (BorderFactory.createTitledBorder
			   (BorderFactory.createCompoundBorder
			    (BorderFactory.createEtchedBorder (),
			     StandardFiller.makeEmptyBorder ()), 
			    "Property Editor"));	

    
    // -- XXX A very strange way to get correct dimensions. This needs
    // -- XXX looking into at some later point
    String [] t = new String [2];
    t [0] = "Blah";
    t [1] = "Blah! -- yes, this is necessary.";
    Dimension d = new JComboBox (t).getPreferredSize ();

    // -- more border stuff
    Component hbrace = Box.createHorizontalStrut ((int)d.getWidth ());
    Component vbrace = Box.createVerticalStrut ((int)d.getHeight ());
    editorPanel.add (BorderLayout.SOUTH, hbrace);
    editorPanel.add (BorderLayout.WEST, vbrace);

    // -- done, we have the panel -- no events here
    return editorPanel;
  }
  

  // -- a panel that contains extra intra-property information
  JPanel buildOptionsPanel ()
  {
    JPanel optionsPanel = new JPanel ();

    // -- border + title
    optionsPanel.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), 
			  "Options"));
    return optionsPanel;
  }
  
  
  JTextArea buildHelpArea ()
  {
    JTextArea help = new JTextArea ("Help", 7, 40);

    help.setEditable (false);
    help.setWrapStyleWord (true);
    help.setLineWrap (true);
    help.setCaretPosition (0);
    help.setEnabled (false);
    return help;
  }
  


  void showHelp (String help)
  {
    if (help == null)
      {
	helpArea.setText ("");
	helpArea.setCaretPosition (0);
	helpArea.setEnabled (false);
      }
    else
      {
	helpArea.setText (help);
	helpArea.setCaretPosition (0);
	helpArea.setEnabled (true);
      }
  }
  

  // -- repaint the editor panel with the editor
  // -- for this property
  void repaintEditorPanel (PropertyDescriptor prop)
  {
    // -- remove currectly active editor component
    if (activeEditor != null)
      propertyEditorPanel.remove (activeEditor);

    PropertyEditor propEditor = (PropertyEditor)properties.get (prop);
    activeEditor = BeanUtil.getEditorComponent (propEditor);
    
    propertyEditorPanel.add (BorderLayout.CENTER, activeEditor);

    // XXX HACK there appears to be a bug in java's handling of
    // borders necessitating these calls. If these are not made, the
    // editor component will be invisible, even after a repaint (10),
    // until a resize occurs.  Since we're making them, however, they
    // might as well do something useful (change the label.)
    propertyEditorPanel.setBorder (null);
    propertyEditorPanel.setBorder (BorderFactory.createTitledBorder
				   (BorderFactory.createCompoundBorder
				    (BorderFactory.createEtchedBorder (),
				     StandardFiller.makeEmptyBorder ()), 
				     prop.getDisplayName ()));	

    

    // -- update help window
    showHelp ((String)prop.getValue (BeanUtil.HELP_ATTRIBUTE));
  }
  


  void setActiveProperty (String propName)
  {
    PropertyDescriptor prop = (PropertyDescriptor)propNames.get (propName);
    repaintEditorPanel (prop);
  }


  /**
   ** Initializes the model compiler based on user choices
   **/
  public ModelCompiler getModelCompiler () 
    throws IllegalAccessException, InvocationTargetException
  {
    for (Iterator it = properties.entrySet ().iterator (); it.hasNext ();)
      {
	Map.Entry entry = (Map.Entry)it.next ();
	
	PropertyDescriptor prop = (PropertyDescriptor)entry.getKey ();
	PropertyEditor editor = (PropertyEditor)entry.getValue ();
	
	// -- skip properties that don't have a value
	if (editor.getValue () == null) continue;

	Method writeMethod = prop.getWriteMethod ();
	writeMethod.invoke (bean, new Object[] { editor.getValue () });
      }
    
    return bean;
  }  
  

  public static void main (String[] args) throws Exception
  {
    PropertyEditorManager.registerEditor (IAlgebra.class, 
					  IAlgebraEditor.class);

    JFrame test = new JFrame ();
    ModelCompilerPropertySheet sheet = 
      new ModelCompilerPropertySheet (args [0]);

    //test.getContentPane ().add (sheet);
    test.pack ();
    test.show ();

    JDialog dialog = new JDialog (test, "Model Compiler Options", true);
    dialog.getContentPane ().add (sheet);
    dialog.pack ();
    dialog.show ();
    
  }
  
  
}
