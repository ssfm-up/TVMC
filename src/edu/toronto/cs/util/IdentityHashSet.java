package edu.toronto.cs.util;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Collection;
import java.util.Iterator;


/**
 * This class implements the {@code Set} interface backed up by an
 * {@code IdentityHashMap}. See documentation for {@code Set} for more
 * details.
 */
public class IdentityHashSet<E> extends AbstractSet<E>
{

  IdentityHashMap<E,Boolean> map;

  
  /**
   * Constructs a new, empty set; the backing {@code IdentityHashMap}
   * has default initial maximum size of (21).
   */
  public IdentityHashSet ()
  {
    this (21);
  }
  
  /**
   * Constructs a new set containing the elements of the specified collection.
   *
   * @param c a collection whose elements are to be passed to this set.
   * @throws NullPointerException if the specified collection is null
   */
  public IdentityHashSet (Collection <? extends E> c)
  {
    this ();
    
    addAll (c);
  }

  /**
   * Constructs a new, empty set; the backing {@code IdentityHashMap}
   * has the initial expected maximum size of {@code expectedMaxSize}
   *
   * @param expectedMaxSize the initial expected maximum size of the hash map.
   */
  public IdentityHashSet (int expectedMaxSize)
  {
    map = new IdentityHashMap (expectedMaxSize);
  }
  


  public boolean add (E o)
  {
    return map.put (o, Boolean.TRUE) == null;
  }
  
  public void clear ()
  {
    map.clear ();
  }
  
  public boolean contains (Object o)
  {
    return map.containsKey (o);
  }
  
  
  public Iterator<E> iterator ()
  {
    return map.keySet ().iterator ();
  }
  
  public boolean remove (Object o)
  {
    return map.remove (o) == null;
  }

  public int size ()
  {
    return map.size ();
  }
  
}
