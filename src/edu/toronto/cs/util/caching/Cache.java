package edu.toronto.cs.util.caching;

import java.util.*;

/**
 ** Cache interface.
 **/
public interface Cache
{

  /**
   ** Clears the cache.
   **/
  void clear ();

  /**
   ** Checks whether the cache contains a value associated with the key.
   **
   ** @param key -- the key to look for.
   **
   ** @return -- true if the key is in the cache; false otherwise.
   **/
  boolean containsKey (Object key);

  /**
   ** Checks whether the cache contains the specified value.
   **
   ** @param value -- the value to look for.
   **
   ** @return -- true if the value is in the cache; false otherwise.
   **/
  boolean containsValue (Object value);

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
  Object put (Object key, Object value);

  /**
   ** Retrieves a value with the specified key from the cache.
   **
   ** @param key -- the key associated with the value.
   **
   ** @return -- the value associated with the key or null if not found.
   **/
  Object get (Object key);

  /**
   ** Removes a value with the specified key from the cache. If
   ** there's no mapping for the specified key, does nothing.
   **
   ** @param key -- the key associated with the value.
   **
   ** @return -- the value being removed or null if no such key exists.
   **/
  Object remove (Object key);

  /**
   ** Returns the number of elements in cache.
   **
   ** @return -- the number of elements in cashe.
   **/
  int size();

  /**
   ** Determines if the cache is empty.
   **
   ** @return -- true if th cache is empty and false otherwise.
   **/
  boolean isEmpty ();

  /**
   ** Gets a collection view of the values contained in this map.
   **
   ** @return -- collection view of the values contained in this map.
   **/
  Collection values ();

  /**
   ** Gets the set of all the keys registered in the cahce.
   **
   ** @return -- the Set of all keys in cache.
   **/
  Set keySet ();

  /**
   ** Returns a Set representation of the cache mapping.
   **
   ** @return -- a Set representing the cache.
   **/
  Set entrySet ();

  /**
   ** Sets the maximum cache size. If the cache is currently bigger than
   ** specified size, the excess elements get purged.
   **
   ** @param size -- the maximum cache size.
   **/
  void resize (int size);

}









