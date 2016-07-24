package edu.toronto.cs.util.userprompt;

import java.util.*;
import java.io.*;

import edu.toronto.cs.util.*;

/**
 ** This class is a wrapper for Readers with all kinds of useful
 ** methods. It also only throws RuntimeExceptions for convenience.
 **/
public class NiceReader
{

  BufferedReader reader;
  
  /**
   ** Constructs a NiceReader.
   **/
  public NiceReader (Reader in)
  {
    reader = new BufferedReader (in);
  }
  public NiceReader (InputStream in)
  {
    reader = new BufferedReader (new InputStreamReader (in));
  }
  
  /**
   ** Reads a line from the associated source.
   **
   ** @returns -- String representation of the line read.
   **/
  public String readLine ()
  {
    String line;

    try
      {
	line = reader.readLine ();
      }
    catch (IOException e)
      {
	throw new NestedRuntimeException (e);
      }

    return line;
  }
  
  /**
   ** Reads an Integer from the associated source.
   **
   ** @returns -- Integer read.
   **/
  public Integer readInteger ()
  {
    Integer i;
    
    try
      {
	i = Integer.valueOf (reader.readLine ());
      }
    catch (IOException e)
      {
	throw new NestedRuntimeException (e);
      }

    return i;
  }
}






