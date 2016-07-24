// generate names for the states 

package edu.toronto.cs.util;

import java.util.*;
import java.lang.*;


public class StateName
{
 

  // hashmap to store the names
  Map list = new HashMap ();
  
  // state names are labeled as a0,a1,....
  int count;
  String prefix;
  
  public StateName (String _prefix)
  {
    count = 0;
    prefix = _prefix;
  }
  
  public StateName ()
  {
    this ("a");
  }
  
  // function to generate name and store in the hashmap
  
  public String stateNameGenerator (Object key)
  {
    String name = new String (prefix + (count++));
    list.put (key, name);
    return name;
  }  
    

  // function to get the state name
  
  public String getStateName (Object key)
  {
    if (list.get (key) == null)
      return stateNameGenerator (key);
    else
      return (String) list.get (key);
  }


  static StateName stateNamer = new StateName ();

  public static String stateName (Object key)
  {
    if (stateNamer == null)
      stateNamer = new StateName ();
    return stateNamer.getStateName (key);
  }
  
}

