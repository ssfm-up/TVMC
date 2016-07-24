package edu.toronto.cs.util;

import java.util.*;

/**
 * <code>ArrayMarkedList</code> is a extention of List that allows 
 * to mark a position in the list and later go back to it. This is
 * done by creating a collection that stores the sizes of the 
 * undelying list after each mark. When restore is called, elements
 * are removed from the list untill it's size is equal to that at 
 * the time of last mark. 
 * 
 * @author <a href="mailto:anton@age.cs">Anton Selyshchev</a>
 * @version 1.0
 */
public class ArrayMarkedList extends ArrayList implements MarkedList
{

  // stores sizes of the undelying list after each mark call.
  List<Integer> listSizes;  

  /**
   * Creates a new <code>ArrayMarkedList</code> instance.
   *
   */
  public ArrayMarkedList()
  {
    super();
    listSizes = new LinkedList();

  }

  /**
   * Creates a new <code>ArrayMarkedList</code> instance given an 
   * initial list.
   *
   */
  public ArrayMarkedList (Collection c)
  {
    super (c);
    listSizes = new LinkedList();

  }

  /**
   * Prints the list plus the number of marks.
   *
   */
  
  public String toString()
  {
	 return super.toString () + " number of marks = " + getNumMarks ();
  }

  /**
   * Returns the number of mark calls up to this point.
   *
   */
  
  public int getNumMarks()
  {
    return listSizes.size();
  }

  /**
   * Records the current number of assertions. Restore can be used 
	* to go back to this number.
   *
   */
  
  public void mark()
  {
    //System.out.println("mark called. List = " + toString());
    listSizes.add(new Integer(super.size()));  
  }
  

  /**
   * removes all the marks done after the last mark. Also removes 
  * the last mark.
   *
   */
  
  public void restore()
  {
    
    // how many elements to pop?  current list size - list size at last mark
    int numberOfElementsToPop = super.size () - 
        listSizes.get (listSizes.size () - 1).intValue ();
    
    // -- remove elements from the list
    for (int i = 0; i < numberOfElementsToPop; i++)
    {
      super.remove(super.size () - 1);
    }
    
    // -- now adjust listSizes
    listSizes.remove (listSizes.size () - 1);    
    
        
  }
  

  /**
   * if markNumber is the last mark, works like restore ()
   * otherwise removes all the asserts done after mark (markNumber +1) 
  * together with the mark number (markNumber + 1)
   *
   * @param level level on which to restore
   */
  
  public void restore(int level)
  {

    // -- if the given level is not correct
    if (getNumMarks () < level || level < 0)
      throw new IllegalArgumentException ("Cannot pop to level " + level);

    // -- if level == 0, clear the list
    if ( level == 0)
    {
      super.clear ();
      listSizes.clear ();
      return;
    }
    
    // 1 should be subtracted from level because level stands for 
    // numberOfAssumptions. So one assumption would make level = 1 
    // and position of that assumption is the list = 0.

    if (listSizes.size() == level) 
      {
        restore();
        return;
  
      }
    
    // if mark is on the last element of the list don't change the list, 
   // only remove the mark
    if (listSizes.get (level) < super.size ()) 
    {
      super.subList (listSizes.get(level - 1) + 1 , super.size ()).clear ();
    }
    listSizes.subList (level, listSizes.size ()).clear ();                    
  }
  


  
  /**
   * Inserts the specified element at the specified position in this list 
   * This operation is not supported
   *
   * @param index position to insert a new member at
   * @param o an <code>Object</code> to be inserted
   */

  public void add (int index, Object o)
  {
    throw new UnsupportedOperationException ();
  }
  
   /**
   *  Inserts all of the elements in the specified collection into 
  *  this list at the specified position (optional operation).
   *  This operation is not supported
   *
   * @param index an <code>int</code> a position where to add
   * @param c a <code>Collection</code> to add
   * @return if the addition was successful.
   */

  public boolean addAll(int index, Collection c)
  {
    throw new UnsupportedOperationException ();    
  }

  
  /**
   * Returns the element at the specified position in this list.
   */
  

  public Object get(int index)
  {
    
    return super.get(index);
  }

  
   
  /**
   * 
   * Removes the element at the specified position in this list.
   * This operation is not supported. Mark and restore should be used instead.
   *
   * @param index an <code>int</code> value
   * @return an <code>Object</code> value
   */
  public Object remove(int index)
  {
    throw new UnsupportedOperationException ();    
  }
  
 
  /**
   *
   * This operation is not supported
   */

  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException ();    
  }
  
  /**
   *
   * This operation is not supported
   */

  public boolean removeAll(Collection c)
  {
    throw new UnsupportedOperationException ();    
  }

  
  /**
   *
   * This operation is not supported
   */

  public boolean retainAll(Collection c)
  {
    throw new UnsupportedOperationException ();        
  }
  
  /**
   *
   * This operation is not supported
   */

  public Object set(int index, Object element)
  {
        throw new UnsupportedOperationException ();      
  }

  // remove me
  public static void main(String[] args)
  {
	 ArrayMarkedList l = new ArrayMarkedList ();
	 l.add("gav");
	 l.add(2);
	 l.add("kria");
	 System.out.println (l.toString ());
  
	 l = new ArrayMarkedList ();
	 System.out.println (l.toString ());

	 l = new ArrayMarkedList ();
	 l.add("gav");
	 System.out.println (l.toString ());
  }
  
}
