package edu.toronto.cs.util;

import java.util.*;
//********//********//********//********//********//********//********//********
/**
 * interface <code>MarkedList</code> is a extention of List that allows to mark
 * a position in the list and to go back to it. Operations like remove (index) 
 * and add (index) that may alter the order in the list are not 
 * supported . Also no remove operations are suported, only restore.
 *
 * @author <a href="mailto:anton@age.cs">Anton Selyshchev</a>
 * @version 1.0
 */
public interface MarkedList extends List
{
  
  /**
   * Marks the current state of the list. This state can be later restored
   * with restore() 
   *
   */
  public void mark ();
  
  /**
   * removes the last mark and all the assertions done after the mark.
   *
   */
  public void restore ();


  /**
   * if markNumber is the last mark, works like restore ()
   * else removes all the asserts done after mark (markNumber +1) together with
   * the mark number (markNumber + 1)
   *
   * @param markNumber  what to restore the list to.
   */
  public void restore (int markNumber);
  
}
