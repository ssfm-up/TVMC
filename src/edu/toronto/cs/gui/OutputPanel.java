package edu.toronto.cs.gui;


import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

import edu.toronto.cs.util.gui.*;


/**
 ** The CTL output panel for our Model-Checker.
 **/
public class OutputPanel extends JPanel
{
  // text field

  JTextPane output;

  StyledDocumentPrinter styledPrinter;


  // scrolling capability
  JScrollPane scroll;

  public OutputPanel ()
  {
    this ("Output");
  }
  
  public OutputPanel (String title)
  {
    // text field
//     output = new JTextArea ("Welcome to XChek!\n", 15, 30);
//     output.setEditable (false);
//     output.setLineWrap (true);
//     output.setWrapStyleWord (true);
//     output.setFont (new Font ("Serif", Font.PLAIN, 18));
    output = new JTextPane ();
    output.setEditable (false);
    //output.setWrapStyleWord (true);

    styledPrinter = new StyledDocumentPrinter (output);
  
    // scrolling capability
    scroll = new JScrollPane (output,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setPreferredSize (new Dimension (600, 400));

    setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));

    setBorder (BorderFactory.createTitledBorder
	       (BorderFactory.createCompoundBorder
		(BorderFactory.createEtchedBorder (),
		 StandardFiller.makeEmptyBorder ()), title));

    add (scroll);
  }

  

  /**
   ** Appends the string to the existing output.
   **/
  public void append (String str)
  {
    styledPrinter.print (str);
  }

  public void append (String str, String style)
  {
    styledPrinter.print (str, style);
  }

  public StyledDocumentPrinter getStyledPrinter ()
  {
    return styledPrinter;
  }
  
  
}
