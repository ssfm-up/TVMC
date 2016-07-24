package edu.toronto.cs.util;

import java.io.*;

/***
 *** NestedRuntimeException. Just like NestedException except it extends
 *** RuntimeException variant
 ***/

public class NestedRuntimeException extends RuntimeException
{
  Exception ex;
  
  public NestedRuntimeException (String message, Exception _ex)
  {
    super (message);
    setException (_ex);
  }
  
  public NestedRuntimeException (Exception _ex)
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
  

  // -- test
  public static void main (String[] args)
  {
    Exception ex = new Exception ("Hello");
    NestedRuntimeException rex = new NestedRuntimeException 
      ("Nested Exception", ex);
    
    System.out.println ("toString: " + rex.toString ());
    System.out.println ("getMessage: " + rex.getMessage ());
    System.out.println ("Stack trace");
    rex.printStackTrace ();
    System.out.println ("Stack trace 2");
    rex.printStackTrace (System.out);
    System.out.println ("Done");
  }
  
  
}





