package edu.toronto.cs.gui;

import java.util.*;
import java.awt.*;import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.util.gui.*;

import java.util.prefs.*;
import edu.toronto.cs.util.*;

import java.io.*;
import javax.swing.filechooser.*;

/**
 ** Preferences Dialog for XChek
 **/
public class XPreferencesPresenter extends JDialog
{


  //preferences constants
  public static final Preferences PARENTPREFS 
    = Preferences.userRoot ().node ("edu/toronto/cs");
  
  JList list;
  DefaultListModel listModel;
  JScrollPane listScroller;
  
  JPanel buttonsPanel;
  JPanel listPanel;

  JButton okBut;
  JButton importBut;
  JButton exportBut;
  JButton closeBut;  

  int currentIndex;
  XPreferences currentPreference;

  /**
   ** Construct the Preferences Panel.
   **/
  public XPreferencesPresenter ()
  {
    super ();
    
    //Set Dialog Properties
    setModal (true);    
    // XXX shouldn't this be set prefered dimension instead?!
    setSize (840, 360);
    setTitle ("XChek Preferences");
   
    //CloseWindow Listener
    addWindowListener (new WindowAdapter () 
      {
	public void windowClosing (WindowEvent wev) 
	{
	  quit ();	
	}
      });

    listModel = new DefaultListModel ();
    


    // XXX This should be discussed, we want this preference dialog
    // XXX as a general way to specify preferences in all of our programs
    // XXX no reason to tie it down to xchek
    // XXX This should live in the GUI

    // Register your Preference-using classes here
    addXPreferences (edu.toronto.cs.gui.XChekGUI.getGUIPreferences ());
    addXPreferences (edu.toronto.cs.gui.GrappaFrame.getGUIPreferences ());
    addXPreferences (edu.toronto.cs.gui.KEGTreeFrame.getGUIPreferences ());

    //Link the list to the listmodel 
    list = new JList (listModel);
    list.addListSelectionListener (new ListSelectionListener ()
      {
	public void valueChanged(ListSelectionEvent e)
	{
	  int i = list.getSelectedIndex ();
	  if (i != -1)  //if something is selected
	    switchSelection (i);
	}
      });


    //define the default preferences pane
    setCurrent (0);
    list.setSelectedIndex (0);
    
    //buttons
    okBut = new JButton("Apply");
    importBut = new JButton("Import");
    exportBut = new JButton("Export");
    closeBut = new JButton("Close");    

    getContentPane ().setLayout (new BorderLayout (5,5));

    // XXXX Why is everything split into two somewhat arbitrary?
    // XXXX No comments at all, but try writting comments for this
    // XXXX and you'll see where the problem is
    init ();
    
  }
    
  void quit ()
  {
    setVisible (false);
    dispose ();
  }
  

  void addXPreferences (XPreferences p)
  {
    listModel.addElement (p);
  }
  
  public void setCurrent (int i)
  {
    currentIndex = i;
    setCurrentPreference ((XPreferences)listModel.getElementAt (i));
  }

  public void setCurrentPreference (XPreferences v)
  {
    currentPreference = v;
    v.updateComponents ();
  }
  
    
  public void init ()
  {
    buttonsPanel = new JPanel();
    listPanel = new JPanel();
    
    //Buttons
    buttonsPanel.add (okBut);
    okBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  currentPreference.savePrefSettings();
	  currentPreference.updateComponents();
	}
      });
    
    buttonsPanel.add (importBut);
    importBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  try
	    {
	      doImport();
	    }
	  catch (IOException ioex)
	    {
	    }
	  catch (InvalidPreferencesFormatException inex)
	    {
	    }
	  
	  
	}
      });
    
    
    
    buttonsPanel.add (exportBut);
    exportBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  doExport();
	}
      });
    
    buttonsPanel.add (closeBut);
    closeBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  quit ();
	}
      });

    
    listScroller = new JScrollPane (list);
    listScroller.setPreferredSize(new Dimension(80, 250));
    listPanel.add(listScroller);
    listPanel.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), "Choose"));

    getContentPane ().add (listPanel, BorderLayout.WEST);
    getContentPane ().add (currentPreference.getPreferenceEditor (), 
			   BorderLayout.CENTER);
    getContentPane ().add (buttonsPanel, BorderLayout.SOUTH);



    setVisible(true);
  }

  // -- changes currently displayed preference 
  public void switchSelection (int i)
  {
    // -- hide what is visible right now
    currentPreference.getPreferenceEditor ().setVisible (false);
    // -- remove it from the content pane
    getContentPane ().remove (currentPreference.getPreferenceEditor ());


    setCurrent (i);
    
    
    // -- add new preference editor
    getContentPane ().add (currentPreference.getPreferenceEditor (), 
			   BorderLayout.CENTER);
    // -- make sure it is visible
    currentPreference.getPreferenceEditor ().setVisible (true);
  }
  
  public void doImport() throws IOException, InvalidPreferencesFormatException
  {
    
    JFileChooser impfc = new JFileChooser ();
    //int returnVal = impfc.showOpenDialog (this);
    int returnVal = impfc.showDialog (this, "Import");
    if (returnVal == JFileChooser.APPROVE_OPTION) 
      {
       File file = impfc.getSelectedFile ();
       InputStream is = new
	 BufferedInputStream	 
	 (new FileInputStream (file));
       Preferences.importPreferences(is);
 	is.close();
	currentPreference.updateComponents();
     }
  }
 
 public void doExport()
  {
    
    JFileChooser expfc = new JFileChooser();
//expfc.showOpenDialog(this);
    int returnVal = expfc.showDialog (this, "Export");
    
    if (returnVal == JFileChooser.APPROVE_OPTION) 
      {
	File file = expfc.getSelectedFile();
	try {
	  OutputStream osTree =
            new BufferedOutputStream(new FileOutputStream
				     (file));
	  PARENTPREFS.exportSubtree(osTree);
	  osTree.close();
        
	} catch(IOException ioEx) {
	  System.out.println( ioEx);// ignore
	} catch(BackingStoreException bsEx) {
	  System.out.println( bsEx);// ignore too
	}
      }
  } 
  
}
