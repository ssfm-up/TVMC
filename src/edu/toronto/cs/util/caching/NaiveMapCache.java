package edu.toronto.cs.util.caching;

import java.util.*;

/**
 ** Fairly simplistic caching.
 **/
public class NaiveMapCache extends AbstractMapCache
{

  protected List keylist;
  protected int maxsize;

  public final int DEFAULT_SIZE = 10;

  /**
   ** Create a new cache.
   **/
  public NaiveMapCache ()
  {
    super (new HashMap ());
    keylist = new LinkedList ();
    resize (DEFAULT_SIZE);
  }

  /**
   ** Create a new cache with a specified # of items it can store.
   **/
  public NaiveMapCache (int numitems)
  {
    super (new HashMap ());
    keylist = new LinkedList ();
    resize (numitems);
  }

  /**
   ** Stores a value and assotiates a key with it. If the key already
   ** has a value associated with it, the old value is replaced by the
   ** supplied new value and returned.
   **
   ** @param key -- the key associated with the value.
   ** @param value -- value to store.
   **
   ** @return -- the value that has been previously associated with
   ** the key or null if none exists.
   **/
  public Object put (Object key, Object value)
  {
    Object old;

    if (isFull ())
      {
	// kind of kludgey, but does the trick
	resize (size () - 1);
	resize (size () + 1);
      }

    keylist.add (key);
    old = cache.put (key, value);

    return old;
  }

  /**
   ** Retrieves a value with the specified key from the cache.
   **
   ** @param key -- the key associated with the value.
   **
   ** @return -- the value associated with the key or null if not found.
   **/
  public Object get (Object key)
  {
    return cache.get (key);
  }

  /**
   ** Removes a value with the specified key from the cache. If
   ** there's no mapping for the specified key, does nothing.
   **
   ** @param key -- the key associated with the value.
   **
   ** @return -- the value being removed or null if no such key exists.
   **/
  public Object remove (Object key)
  {
    keylist.remove (key);
    return cache.remove (key);
  }

  /**
   ** Determines if the cache is at its maximum capacity.
   **
   ** @return -- true if maximum capacity has been reached and false
   ** otherwise.
   **/
  public boolean isFull ()
  {
    return size () >= maxsize;
  }

  public void resize (int size)
  {
    maxsize = size;
    if (isFull ()) adjustSize ();
  }

  /**
   ** Adjust the cache size to the specified value.
   **/
  protected void adjustSize ()
  {
    ListIterator i;

    i = keylist.listIterator ();
    while (size () > maxsize)
      {
	remove (i.next ());
	i.remove ();
      }
  }

}
