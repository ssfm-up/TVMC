package edu.toronto.cs.util.gui;

import javax.swing.*;
import javax.swing.text.*;

/***
 *** A simple text styling class
 ***/
public class StyledDocumentPrinter 
{
  JTextPane textPane;
  Document document;
  
  public StyledDocumentPrinter (JTextPane _textPane)
  {
    textPane = _textPane;
    document = textPane.getDocument ();
    initTextPaneStyles (textPane);
  }

  protected static void initTextPaneStyles (JTextPane pane)
  {
    // -- get the default style
    Style defaultStyle = StyleContext.getDefaultStyleContext ().
      getStyle (StyleContext.DEFAULT_STYLE);
    // -- set the default style
    defaultStyle = pane.addStyle ("regular", defaultStyle);
    StyleConstants.setFontFamily (defaultStyle, "SansSerif");
    StyleConstants.setFontSize (defaultStyle, 18);


    // -- italic
    Style style = pane.addStyle ("italic", defaultStyle);
    StyleConstants.setItalic (style, true);
    
    
    // -- bold
    style = pane.addStyle("bold", defaultStyle);
    StyleConstants.setBold (style, true);
    
  }
  
  
  public void print (String s, String style)
  {
    try 
      {
	document.insertString (document.getLength (), s, 
			       textPane.getStyle (style));
	textPane.setCaretPosition (document.getLength ());
      }
    catch (BadLocationException ex)
      {
	throw new RuntimeException (ex);
      }
  }
  public void println ()
  {
    print ("\n", "regular");
  }
  
  public void println (String s, String style)
  {
    print (s + "\n", style);
  }
  public void print (String s)
  {
    print (s, "regular");
  }
  public void println (String s)
  {
    print (s + "\n");
  }
  
  public void bold (String s)
  {
    print (s, "bold");
  }
  public void boldln (String s)
  {
    bold (s + "\n");
  }
  public void italic (String s)
  {
    print (s, "italic");
  }
  
  public void italicln (String s)
  {
    italic (s + "\n");
  }			   
  
}
