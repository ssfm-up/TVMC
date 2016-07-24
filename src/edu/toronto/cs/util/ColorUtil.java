package edu.toronto.cs.util;

import java.awt.Color;

public class ColorUtil
{
  public static String toHexString (Color color)
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ('#');
    sb.append (StringUtil.pad (2, Integer.toHexString(color.getRed ()), '0'));

    sb.append 
      (StringUtil.pad (2, Integer.toHexString(color.getGreen ()), '0'));
    
    sb.append 
      (StringUtil.pad (2, Integer.toHexString(color.getBlue ()), '0'));
    
    return sb.toString ();
  }
  
}
