package edu.toronto.cs.util;

import java.util.*;
import java.lang.ref.*;


public class WeakWeakHashMap extends WeakHashMap {
  /** The internal HashMap that will hold the WeakReference. */
  private final WeakHashMap hash = new WeakHashMap();

  private final ReferenceQueue queue = new ReferenceQueue();


  public Object get(Object key) {
    Object result = null;
    result = hash.get (key);
    
    if ( result==null ) return result;

    WeakReference weak_ref = (WeakReference)hash.get(key);
    
    if (weak_ref != null) {
      // From the WeakReference we get the value, which can be
      // null if it was not in the map, or it was removed in
      // the processQueue() method defined below
      result = weak_ref.get();
      if (result == null) {
	// If the value has been garbage collected, remove the
	// entry from the HashMap.
	hash.remove(key);
      } 
    }
    return result;
  }


  /** We define our own subclass of WeakReference which contains
      not only the value but also the key to make it easier to find
      the entry in the HashMap after it's been garbage collected. */

  private static class WeakValue extends WeakReference {
    //private final Object key; 

    private WeakValue(Object k, Object key, ReferenceQueue q) {
      super(k, q);
      //this.key = key;
    }
  }

  /** Here we go through the ReferenceQueue and remove garbage
      collected SoftValue objects from the HashMap by looking them
      up using the SoftValue.key data member. */
  // private void processQueue() {
//     WeakValue wv;
//     while ((wv = (WeakValue)queue.poll()) != null) {
//       hash.remove(wv.key); 
//     }
//   }

  /** Here we put the key, value pair into the HashMap using
      a WeakValue object. */
  public Object put(Object key, Object value) {
    //processQueue(); // throw out garbage collected values first
    return hash.put(key, new WeakValue(value, key, queue));
  }

  public Object remove(Object key) {
    //processQueue(); 
    return hash.remove(key);
  }

  public void clear() {
    //processQueue(); 
    hash.clear();
  }

  public int size() {
    //processQueue(); 
    return hash.size();
  }

  public Set entrySet() {
    // no, no, you may NOT do that!!! 
    throw new UnsupportedOperationException();
  }
}



