package edu.toronto.cs.util;

import java.io.*;

/** 
 * An PrintWriter wrapper for System.out.
 * 
 * @author Kelvin Ku (kelvin@cs.toronto.edu)
 * @version 
 */
public class SystemOutWrapper
{
  public static final PrintWriter writer;
  static
  {
    try
    {
      writer = new PrintWriter (new FileWriter ("tp.log"));
    }
    catch (IOException e)
    {
      throw new RuntimeException (e);
    }
  }
}
