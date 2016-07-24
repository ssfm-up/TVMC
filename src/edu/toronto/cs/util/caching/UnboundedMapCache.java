package edu.toronto.cs.util.caching;

import java.util.*;

/**
 ** Fairly simplistic unbounded caching.
 **/
public class UnboundedMapCache extends AbstractMapCache
{

  /**
   ** Create a new cache.
   **/
  public UnboundedMapCache ()
  {
    super (new HashMap ());
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
    return cache.put (key, value);
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
    return false;
  }

  public void resize (int size)
  {
  }

}
