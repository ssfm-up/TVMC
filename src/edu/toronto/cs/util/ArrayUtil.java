package edu.toronto.cs.util;

import java.util.*;

// -- array utilitiy functions
public class ArrayUtil
{
  public static String toString (int[] array)
  {
    return toString (array, 0, array.length);
  }
  
  public static String toString (int[] array, int length)
  {
    return toString (array, 0, length);
  }
  
  public static String toString (int[] array, int offset, int length)
  {
    StringBuffer sb = new StringBuffer ();

    int upBound = 
      offset + length > array.length ? array.length : offset + length;
    

    sb.append ('[');
    for (int i = offset; i < upBound; i++)
      {
	sb.append (array [i]);
	if (i < upBound - 1) sb.append (", ");
      }
    
    sb.append (']');
    return sb.toString ();
  }
  

  public static String toString (Object[] array)
  {
    return Arrays.asList (array).toString ();
  }

  public static String[] evenElements(String[] array)
  {
    int numEvens = ((array.length % 2) == 0 ?
		    (array.length /2) : (array.length/2 + 1));
    String[] evens = new String[numEvens];
    for (int i=0; i<numEvens; i++) 
      evens[i] = array[i*2];
    return evens;
    
    
  
  }

  public static String[] oddElements(String[] array)
  {
    String[] odds = new String[array.length/2];
    for (int i=0; i<array.length/2; i++) 
      odds[i] = array[1+i*2];
    return odds;
    
    
  
  }

  public static Object[] evenElements(Object[] array)
  {
    int numEvens = ((array.length & 1) == 0 ?
		    (array.length >> 1) : (array.length >> 1) + 1);

    Object[] evens = new Object [numEvens];

    for (int i = 0; i < numEvens; i++) 
      evens [i] = array[i << 1];
    return evens;
  }

  public static Object[] oddElements(Object[] array)
  {
    Object[] odds = new Object [array.length >> 1];
    for (int i = 0; i < array.length >> 1; i++) 
      odds [i] = array [1 + (i << 1)];
    return odds;  
  }

  
  public static int[] reverse (int[] array)
  {
    for (int i = 0; i < array.length >> 1 ; i++)
      {
	int temp = array [i];
	array [i] = array [array.length - 1 - i];
	array [array.length - 1 - i] = temp;
      }
    
    return array;
  }
  

  // -- computes v \setminus u
  public static Object[] arrayDiff (Object[] v, Object[] u)
  {
    Set set = new HashSet ();
    // -- put v into a set
    for (int i = 0; i < v.length; i++)
      set.add (v [i]);

    for (int i = 0; i < u.length; i++)
      set.remove (u [i]);
    
    return (Object[])set.toArray (new Object [set.size ()]);
  }
  

}
