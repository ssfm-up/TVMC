package edu.toronto.cs.util.gui;

import javax.swing.*;

import java.awt.*;

/**
 ** All sorts of useful little things.
 **/
public class GUIUtil
{
  private GUIUtil () {}
  
  /**
   ** Gives all the specified components the same X-alignment.
   **/
  static public void alignAllX (Component [] comp, float alignment)
  {
    for (int i = 0; i < comp.length; i++)
      {
	if (comp [i] instanceof JComponent)
	  ((JComponent) comp [i]).setAlignmentX (alignment);
      }
  }
  /**
   ** Gives all the specified components the same Y-alignment.
   **/
  static public void alignAllY (Component [] comp, float alignment)
  {
    for (int i = 0; i < comp.length; i++)
      {
	if (comp [i] instanceof JComponent)
	  ((JComponent) comp [i]).setAlignmentY (alignment);
      }
  }

  /**
   ** Enables/Disables all components recursively.
   **/
  static public void setEnabled (Component comp, boolean status)
  {
    comp.setEnabled (status);

    if (comp instanceof Container)
      {
	Component [] more;
	more = ((Container) comp).getComponents ();
	for (int i = 0; i < more.length; i++)
	  setEnabled (more [i], status);
      }
  }

}
