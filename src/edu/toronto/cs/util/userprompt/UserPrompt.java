package edu.toronto.cs.util.userprompt;

import java.util.*;
import java.io.*;

import edu.toronto.cs.util.*;

/**
 ** This is a class to be used when the user needs to provide some
 ** input given a number of choices.
 **/
public class UserPrompt
{

  // -- no constructors
  private UserPrompt () {}
  
  /**
   ** Given a Collection of Objects it will display all of them in a
   ** list and then prompt the user to pick one by entering an
   ** appropriate number.
   **
   ** @return -- the Object that was picked.
   **/
  public static Object pickOne (Collection things)
  {
    ArrayList list = new ArrayList (things);
    int i;

    System.out.println ("Pick one of the following :");
    // -- print the indexed list
    for (i = 1; i <= list.size (); i++)
      {
	System.out.println (i + ". " + list.get (i-1));	
      }

    NiceReader reader = new NiceReader (System.in);
    
    do
      {
	System.out.println ("Enter your choice : ");
	try
	  {
	    i = reader.readInteger ().intValue ();
	  }
	catch (NumberFormatException e)
	  {
	    i = 0;
	  }
      }
    while (i > list.size () || i < 1);
    
    return list.get (i-1);
  }

  public static void waitPrompt ()
  {
    try
      {
	NiceReader reader = new NiceReader (System.in);
	System.out.println ("Press the \"any\" key...");
	String s = reader.readLine ();
      }
    catch (Exception e)
      {
      }
    
  }

  // One, none, all.
  public static Set pickSome (Collection things)
  {
    ArrayList list = new ArrayList (things);
    System.out.println ("Pick one of the following:");
    int i=1;
    for (; i <= list.size (); i++)
      {
	System.out.println (i + ". " + list.get (i-1));	
      }
    System.out.println ((i)+". All of the above");
    System.out.println ((i+1)+". None of the above");

    NiceReader reader = new NiceReader (System.in);
    do
      {
	System.out.println ("Enter your choice : ");
	try
	  {
	    i = reader.readInteger ().intValue ();
	  }
	catch (NumberFormatException e)
	  {
	    i = 0;
	  }
      }
    while (i > list.size ()+2 || i < 1);
    
    if (i > list.size ())
      {
      if (i == list.size ()+1)
	return new HashSet (list);
      else if (i == list.size ()+2)
	return new HashSet ();
      }
    else
      {
	Set resultset = new HashSet ();
	resultset.add (list.get (i-1));
	return resultset;
      }
    return null;
  }
    
}






