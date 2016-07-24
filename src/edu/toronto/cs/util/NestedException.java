package edu.toronto.cs.util;

import java.io.*;

/***
 *** This is a wrapper arround Exception to provide NestedExceptions 
 *** to be used everywhere :) 
 ***/

public class NestedException extends Exception
{
  Exception ex = null;
  
  public NestedException (String message, Exception _ex)
  {
    super (message);
    setException (_ex);
  }
  
  public NestedException (Exception _ex)
  {
    super ();
    setException (_ex);
  }
  
  void setException (Exception _ex)
  {
    //if (_ex == null) throw new IllegalArgumentException ("Null exception");
    ex = _ex;
  }

  // -- returns message of this exception and the exception it contains
  public String getMessage ()
  {
    if (ex != null)
      return super.getMessage () + " -- " + ex.getMessage ();
    return super.getMessage ();
  }
  
  public String toString ()
  {
    if (ex != null)
      return super.toString () + " -- " + ex.toString ();
    return super.toString ();
  }

  public void printStackTrace ()
  {
    printStackTrace (System.err);
  }
  
  public void printStackTrace (PrintStream s)
  {
    super.printStackTrace (s);
    if (ex != null)
      {
	s.println ("-------------------");
	s.println ("NESTED EXCEPTION");
	s.println ("-------------------");
	ex.printStackTrace (s);
      }    
  }
  
  public void printStackTrace (PrintWriter s)
  {
    super.printStackTrace (s);
    if (ex != null)
      {
	s.println ("-------------------");
	s.println ("NESTED EXCEPTION");
	s.println ("-------------------");
	ex.printStackTrace (s);
      }
  }  
}
