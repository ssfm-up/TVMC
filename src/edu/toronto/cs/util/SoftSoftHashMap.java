package edu.toronto.cs.util;

import java.util.*;
import java.lang.ref.*;


public class SoftSoftHashMap extends SoftHashMap 
{
  
  // -- The internal HashMap that will hold the WeakReference.
  //private final SoftHashMap hash = new SoftHashMap ();
  private final ReferenceQueue queue = new ReferenceQueue ();


  public Object get (Object key) 
  {
    Object result = null;

    SoftReference soft_ref = (SoftReference)super.get (key);
    
    if (soft_ref != null) 
      {
	result = soft_ref.get ();
	if (result == null) 
	  {
	    // If the value has been garbage collected, remove the
	    // entry from the HashMap.
	    super.remove(key);
	  } 
      }
    return result;
  }


  /** We define our own subclass of SoftReference which contains
      not only the value but also the key to make it easier to find
      the entry in the HashMap after it's been garbage collected. */

  private static class SoftValue extends SoftReference 
  {
    //private final Object key; 
    
    private SoftValue (Object k, ReferenceQueue q) 
    {
      // XXX we are not using the ReferenceQueue so why 
      // XXX register an object with it?
      super (k);
      //super(k, q);
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
    return super.put(key, new SoftValue(value, queue));
  }
}



