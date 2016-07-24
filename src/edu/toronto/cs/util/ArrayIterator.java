package edu.toronto.cs.util;

import java.util.*;


/**** 
 **** A smart array iterator so that we can treat arrays as lists 
 ****/

public class ArrayIterator implements Iterator 
{
  // -- array we iterate over
  Object[] array;

  // -- current position
  int cur;

  // -- number of elements we have already seen
  int seen;

  // -- direction - indicates if we are iterating up or down
  int dir;
  
  // -- total number of elements we want to iterator over
  int total;
  
  public ArrayIterator (Object[] _array, boolean forward)
  {
    this (_array, forward, _array.length);
  }
  
  public ArrayIterator (Object[] _array, boolean forward, int _total)
  {
    array = _array;
    dir = forward ? 1 : -1;
    seen = 0;
    total = _total;
    cur = forward ? 0 : array.length - 1;
  }
  
  
  public boolean hasNext ()
  {
    return seen < total;
  }
  
  public Object next ()
  {
    if (!hasNext ()) throw new NoSuchElementException ("No more elements");
    // -- get an element we want to show
    Object o = array [cur];
    // -- increment 'cur' to point to the next element
    cur += dir;
    // -- increment # of elements we've seen
    seen++;
    // -- done
    return o;
  }
  
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }
  
}
