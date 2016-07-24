package edu.toronto.cs.algebra;

import java.util.*;

/**
 * an upset represented by its minimal elements
 **
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */
public class MinUpSet 
{
  // -- store for minimal elements
  Set values;
  

  /**
   * Creates a new <code>MinUpSet</code> instance.
   *
   */
  public MinUpSet ()
  {
    values = new LinkedHashSet ();
  }

  /**
   * Adds all elements of set to this set
   *
   * @param set a <code>MinUpSet</code> value
   */
  public void addAll (MinUpSet set)
  {
    if (values.isEmpty ()) 
      values.addAll (set.getValues ());
    else
      for (Iterator it = set.iterator (); it.hasNext ();)
	add ((AlgebraValue)it.next ());
  }

  /**
   * Adds a single AlgebraValue
   *
   * @param v an <code>AlgebraValue</code> value
   * @return true if v was actually inserted
   */
  public boolean add (AlgebraValue v)
  {
    if (values.contains (v)) return false;
    
    boolean insert = false;
    
    for (Iterator it = iterator (); it.hasNext ();)
      {
	AlgebraValue setValue = (AlgebraValue)it.next ();
	// -- already have this value in our set
	if (!insert && setValue.leq (v).isTop ()) 
	  return false;
	
	// -- if this value is below our lower bound, remove 
	// -- our lower bound and continue since we may
	// -- need to remove more than one point
	else if (v.leq (setValue).isTop ())
	  {
	    // -- found a value that is above v, which means
	    // -- a) v will be inserted
	    // -- b) setValue is now redundant so it should be removed
	    insert = true;
	    it.remove ();
	  }
      }

    
    // -- if we got here than v is incomparable to all elements 
    // -- we currently have in values
    values.add (v);
    return true;
  }
  
  /**
   * true if v is an element of this set, i.e. it is in its cover
   *
   * @param v an <code>AlgebraValue</code> value
   * @return a <code>boolean</code> value
   */
  public boolean isIn (AlgebraValue v)
  {
    if (values.contains (v)) return true;
    
    for (Iterator it = iterator (); it.hasNext ();)
      if (((AlgebraValue)it.next ()).leq (v).isTop ()) return true;

    return false;
  }
  

  /**
   * true if every element of <code>set</code> is an element of this set.
   *
   * @param set a <code>MinUpSet</code> value
   * @return a <code>boolean</code> value
   */
  public boolean isSubSet (MinUpSet set)
  {
    for (Iterator it = set.iterator (); it.hasNext ();)
      if (!isIn ((AlgebraValue)it.next ())) return false;

    return true;
  }
  
  
  /**
   * Returns the set of values
   *
   * @return a <code>Set</code> value
   */
  public Set getValues ()
  {
    return values;
  }
  

  /**
   * Iterator over values of this set.
   *
   * @return an <code>Iterator</code> value
   */
  public Iterator iterator ()
  {
    return values.iterator ();
  }


  /**
   * Describe <code>size</code> method here.
   *
   * @return an <code>int</code> value
   */
  public int size ()
  {
    return values.size ();
  }
  

  /**
   * Equals method
   *
   * @param o an <code>Object</code> value
   * @return a <code>boolean</code> value
   */
  public boolean equals (Object o)
  {
    if (o == null) return false;
    if (o == this) return true;
    if (o.getClass () == MinUpSet.class) 
      return values.equals (((MinUpSet)o).getValues ());
    return false;
  }  

  public int hashCode ()
  {
    return values.hashCode ();
  }
  

  public String toString ()
  {
    return values.toString ();
  }
  
  
}
