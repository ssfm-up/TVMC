package edu.toronto.cs.util;

import java.util.*;
import junit.framework.TestCase;

/**
 * Most attention is given to mark and restore methods since others are either
 * not supported or are from the super class.
 *
 * @author <a href="mailto:anton@age.cs">Anton Selyshchev</a>
 * @version 1.0
 */
public class ArrayMarkedListTestCase extends TestCase {
  

  public void testMethod1(){
    ArrayMarkedList list = new ArrayMarkedList();
   
    assertTrue (list.isEmpty ());
    list.add ("a");
    list.add ("b");
    list.add ("c");
    list.mark ();
    list.add ("d");
    list.add ("e");
    
    list.restore(1);

    assertEquals(3, list.size());
  }
  

  public void testMethod(){
    ArrayMarkedList list = new ArrayMarkedList();
    // --first list is empty
    assertTrue (list.isEmpty ());
    
    list.mark ();
    list.add ("a");
    list.mark ();
    list.add ("b");
    list.mark ();
    list.add ("c");
    list.add ("d");
    list.add ("e");
  
    // after you restore to n, the numer of marks should become (n-1).
    list.restore (3);
    assertEquals(list.getNumMarks (), 2);
      
    list.restore (2);
 
    list.restore (1);
    assertTrue (list.isEmpty ());

    list.add (7);
    list.add (8);
    list.add (9);
    list.restore (0);  // restore (0) clears the list
    assertTrue (list.isEmpty ());
        
    list.mark ();
    list.add ("b");
    list.add (5);
    
    // -- the last element should be the one we added last
    assertEquals(5, list.get (list.size () - 1));	
    
    
    list.clear ();
    assertTrue (list.isEmpty ());
    list.add ("q");
    list.mark ();
    list.add ("w");
    list.add ("e");
    list.mark ();
    list.add ("r");
    list.restore ();
    assertEquals("e", list.get (list.size () - 1));	
    list.restore ();
    assertEquals("q", list.get (list.size () - 1));	
    list.restore ();
    assertTrue (list.isEmpty ());

    list.add ("q");
    list.mark ();
    list.add ("w");
    list.add ("e");
    list.mark ();
    list.add ("r");
    // -- restore to the first mark. Only q should stay in the list
    
    list.restore(2);
    
    
   
    
    assertEquals("e", list.get (list.size () - 1));	

    // -- add a list
    List l = new ArrayList ();
    list = new ArrayMarkedList ();
    list.mark ();
    l.add ("a"); l.add ("b"); l.add ("c");
    list.addAll (l);
    list.mark ();
    
    assertEquals("c", list.get (list.size () - 1));	
    
    list.restore (1);
   
    assertEquals("c", list.get (list.size () - 1));	
    
    list = new ArrayMarkedList ();
    list.add("t");
    System.out.println("list = " + list);
    list.mark ();
    list.addAll (l);
    System.out.println("list = " + list);
    assertEquals("c", list.get (list.size () - 1));	
    list.restore (1);
    System.out.println("list = " + list);
    assertEquals("t", list.get (list.size () - 1));	

	  
    
       


  }
}
