package edu.toronto.cs.util.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 ** The standard fillers for our GUI.
 **/
public class StandardFiller
{
  private StandardFiller () {}

  static int STANDARD_FILLER_SIZE = 5;
  
  static public Component makeVstrut ()
  {
    return Box.createVerticalStrut (STANDARD_FILLER_SIZE);
  }
  static public Component makeLongVstrut ()
  {
    return Box.createVerticalStrut (2*STANDARD_FILLER_SIZE);
  }

  static public Component makeHstrut ()
  {
    return Box.createHorizontalStrut (STANDARD_FILLER_SIZE);
  }
  static public Component makeLongHstrut ()
  {
    return Box.createHorizontalStrut (2*STANDARD_FILLER_SIZE);
  }

  static public Border makeEmptyBorder ()
  {
    return BorderFactory.createEmptyBorder (STANDARD_FILLER_SIZE,
					    STANDARD_FILLER_SIZE,
					    STANDARD_FILLER_SIZE,
					    STANDARD_FILLER_SIZE);
  }
  static public Border makeWideEmptyBorder ()
  {
    return BorderFactory.createEmptyBorder (2*STANDARD_FILLER_SIZE,
					    2*STANDARD_FILLER_SIZE,
					    2*STANDARD_FILLER_SIZE,
					    2*STANDARD_FILLER_SIZE);
  }

  /**
   ** Get the value of STANDARD_FILLER_SIZE.
   ** @return value of STANDARD_FILLER_SIZE.
   **/
  static public int getSize ()
  {
    return STANDARD_FILLER_SIZE;
  }
  
  /**
   ** Set the value of STANDARD_FILLER_SIZE.
   ** @param v  Value to assign to STANDARD_FILLER_SIZE.
   **/
  static public void setSize (int  v)
  {
    STANDARD_FILLER_SIZE = v;
  }
  
}
