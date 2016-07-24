package edu.toronto.cs.util;

import java.util.*;

/**
 ** This is an N-ary Tree class.
 **/
public class NaryTree
{
  
  protected Object data;
  protected Map children;

  /**
   ** Constructs a new NaryTree assigning null to Data and no children.
   **/
  public NaryTree ()
  {
    data = null;
    children = new HashMap ();
  }

  /**
   ** Constructs a new NaryTree contaning the specified Object as its data
   ** and no children.
   **
   ** @param d -- data to be stored in the root node.
   **/
  public NaryTree (Object d)
  {
    data = d;
    children = new HashMap ();
  }

  /**
   ** Constructs a new NaryTree with given data as well as the
   ** children.
   **
   ** @param d -- data to be stored in the root node.
   ** @param c -- a Map contaning children (of type NaryTree).
   **/
  public NaryTree (Object d, Map c)
  {
    data = d;
    children = c;
  }

  /**
   ** Retrieves the data from the NaryTree.
   **
   ** @return -- data contained in the root node.
   **/
  public Object getData ()
  {
    return data;
  }

  /**
   ** Gets the number of children that this tree node has.
   **/
  public int getNumChildren ()
  {
    return children.size ();
  }
  

  /**
   ** Retrieves the Map of children.
   **
   ** @return -- a Map of children.
   **/
  public Map getChildren ()
  {
    return children;
  }

  /**
   ** Gets the child with the specified id.
   **/
  public NaryTree getChild (Object id)
  {
    return (NaryTree) children.get (id);
  }

  /**
   ** Changes the data in the NaryTree.
   **/
  public void setData (Object d)
  {
    data = d;
  }

  /**
   ** Changes the nth child of the NaryTree.
   **/
  public void setChild (Object id, NaryTree child)
  {
    children.put (id, child);
  }

  /**
   ** Assigns a new Map of children.
   **
   ** @param c -- a Map of children.
   **/
  public void setChildren (Map c)
  {
    children = c;
  }

  /**
   ** Checks whether such a child exists.
   **/
  public boolean existChild (Object name)
  {
    return children.containsKey (name);
  }
  
}
