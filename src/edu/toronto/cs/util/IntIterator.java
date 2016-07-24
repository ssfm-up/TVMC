package edu.toronto.cs.util;

import java.util.*;

/***
 *** Defines an Iterator like interface for integers
 ***/


public interface IntIterator
{
  int nextInt ();
  boolean hasNext ();


  public static final IntIterator EMPTY_INT_ITERATOR
    = new IntIterator ()
      {
	public boolean hasNext () { return false; }
	public int nextInt () 
	{ throw new NoSuchElementException (); }
      };
  


  /***
   *** Iterates over a range
   ***/
  // XXX This should be extended to support arbitrary steps and directions
  public static class RangeIterator  implements IntIterator
  {
    int start;
    int stop;
    
    int cur;
    
    public RangeIterator (int _start, int _stop)
    {
      start = _start;
      stop = _stop;
      cur = start;
    }
    
    
    public boolean hasNext ()
    {
      return cur < stop;
    }
    
    public int nextInt ()
    {
      if (!hasNext ()) throw new NoSuchElementException ();
      return cur++;
    }
    
  }
  
}
