package edu.toronto.cs.util;

import java.awt.*;

/*
 *  XChek Preferences Interface. For a an example of a 
 *  GUI implementation look at the end of gui/GrappaFrame.java
 */

public interface XPreferences 
{
  /**
   * @return the name of this group of preferences
   * It is assumed that <code>toString</code> returns exactly the 
   * same thing as <code>getGroupname</code>
   */
  String getGroupName ();

  /**
   * @return a help message for the group
   */
  String getHelp (); 
  
  /**
   * @return a visual editor for setting the preferences
   */
  Component getPreferenceEditor ();

  /**
   * update preference editor with data from backing store
   *
   */
  void updateComponents (); 
  
  /**
   * write settings to the backing store
   *
   */
  void savePrefSettings ();  
}
