package edu.toronto.cs.proof;

import java.util.*;

// only guarantees uniqueness for <260 states
public class SimpleNameGenerator implements StateNameGenerator 
{

  static char chars[] = { 
    'a','b','c','d','e','f','g','h','i','j','k',
    'l','m','n','o','p','q','r','s','t','u','v',
    'w','x','y','z'
  } ;
  
  int currSuffix;
  int currChar;
  Set returnedNames;
  
  
  public SimpleNameGenerator() 
    {
      currSuffix=0;
      currChar=0;
      
      returnedNames = new HashSet();
    }

  public void returnName(String s)
  {
    returnedNames.add(s);
    
  }


  public String getFreshName() 
    {
      String nameToReturn = null;
      
      if (returnedNames.isEmpty())
	{
	  nameToReturn = 
	    new String(chars[currChar]+Integer.toString(currSuffix));

	  if (currSuffix == 9) {
	    currChar++;
	    
	    currSuffix=0;
	    
	  }
	  else 
	    // just advance the digit
	    currSuffix++;
	} 
      else
	// use a returned name
	{
	  Object rn = returnedNames.iterator().next();
	  returnedNames.remove(rn);
	  return (String)rn;
	}
      return nameToReturn;
    } // end getFreshName()
  
  public static void main(String argv[])
  {
    SimpleNameGenerator sng = new SimpleNameGenerator();
    
    for (int i=0; i<260; i++)
      System.out.println("hello"+sng.getFreshName());
    
    
    
  }
  
  

  

  
  
  
}
