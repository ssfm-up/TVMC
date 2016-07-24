package edu.toronto.cs.mdd;

import java.util.*;

import edu.toronto.cs.util.*;



public class MDDLeafNode extends MDDNode
{
  int value;

  BitSet image;

  public MDDLeafNode ()
  {
    this (-1);
  }
  
  public MDDLeafNode (int _value)
  {
    setValue (_value);
  }
  
  public int getValue ()
  {
    return value;
  }
  public BitSet getImage ()
  {
    return image;
  }
  
  public void setValue (int v)
  {
    value = v;

    if (v >= 0)
      {
	image = new BitSet (v + 1);
	image.set (v);
      }
    else
      // XXX Big hack to avoid null pointer exceptions!
      image = new BitSet ();
    
  }
  
  public boolean isConstant ()
  {
    return true;
  }
  public int childrenSize ()
  {
    return 0;
  }
  public MDDNode[] getChildren ()
  {
    assert false : MDDLeafNode.class.getName () + " has no children";
    return null;
  }
  public MDDNode getChild (int i)
  {
    assert false : MDDLeafNode.class.getName () + " has no children";
    return null;
  }
  
  
  public int getVarIndex ()
  {
    return Integer.MAX_VALUE;
  }

  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o.getClass () ==  MDDLeafNode.class)
      return equals ((MDDLeafNode)o);
    return false;
  }
  public boolean equals (MDDLeafNode node)
  {
    return node.getValue () == getValue ();
  }

  public int hashCode ()
  {
    long hash = ((long)getValue ()) * primes [0];
    return (int)(hash);
  }
  
  public int getCubeLastVar ()
  {
    return Integer.MAX_VALUE;
  }

  public String toString ()
  {
    return String.valueOf (value);
  }
  
      
  
}
