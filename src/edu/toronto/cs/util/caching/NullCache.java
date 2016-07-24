package edu.toronto.cs.util.caching;

import java.util.*;

/**
 ** Cache that does absolutely no caching.
 **/
public class NullCache implements Cache
{

  /**
   ** Creates new NullCache.
   **/
  public NullCache ()
  {
  }

  /**
   ** Clears the cache.
   **/
  public void clear ()
  {
  }

  /**
   ** Checks whether the cache contains a value associated with the key.
   **
   ** @param key -- the key to look for.
   **
   ** @return -- true if the key is in the cache; false otherwise.
   **/
  public boolean containsKey (Object key)
  {
    return false;
  }

  /**
   ** Checks whether the cache contains the specified value.
   **
   ** @param value -- the value to look for.
   **
   ** @return -- true if the value is in the cache; false otherwise.
   **/
  public boolean containsValue (Object value)
  {
    return false;
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
    return null;
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
    return null;
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
    return null;
  }

  /**
   ** Returns the number of elements in cache.
   **
   ** @return -- the number of elements in cashe.
   **/
  public int size()
  {
    return 0;
  }

  /**
   ** Determines if the cache is empty.
   **
   ** @return -- true if th cache is empty and false otherwise.
   **/
  public boolean isEmpty ()
  {
    return true;
  }

  /**
   ** Determines if the cache is at its maximum capacity.
   **
   ** @return -- true if maximum capacity has been reached and false
   ** otherwise.
   **/
  public boolean isFull ()
  {
    return true;
  }

  /**
   ** Gets a collection view of the values contained in this map.
   **
   ** @return -- collection view of the values contained in this map.
   **/
  public Collection values ()
  {
    return new HashSet();
  }

  /**
   ** Gets the set of all the keys registered in the cahce.
   **
   ** @return -- the Set of all keys in cache.
   **/
  public Set keySet ()
  {
    return new HashSet ();
  }

  /**
   ** Returns a Set representation of the mapping.
   **
   ** @return -- a Set representing the cache.
   **/
  public Set entrySet ()
  {
    return new HashSet ();
  }

  /**
   ** Sets the maximum cache size. If the cache is currently bigger than
   ** specified size, the excess elements get purged.
   **
   ** @param size -- the maximum cache size.
   **/
  public void resize (int size)
  {
  }

}
