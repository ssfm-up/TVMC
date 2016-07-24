package edu.toronto.cs.ctl;

import edu.toronto.cs.util.*;


public abstract class CTLUnaryNode extends CTLLeafNode
{
  CTLNode right;


  CTLNode[] fairness = CTLAbstractNode.EMPTY_ARRAY;
  
  
  public CTLUnaryNode ()
  {
    this (null);
  }
  
  public CTLUnaryNode (CTLNode _right)
  {
    right = _right;
  }

  public CTLUnaryNode (CTLNode _right, CTLNode[] _fairness)
  {
    this (_right);
    if (_fairness != null)
      fairness = _fairness;
  }
  
  public void setFairness(CTLNode[] fairness) {
	  this.fairness = fairness;
  }
  

  public CTLNode getRight ()
  {
    return right;
  }
  public CTLNode getLeft ()
  {
    return null;
  }
  protected void setRight (CTLNode v)
  {
    right = v;
  }
  protected void setLeft (CTLNode v)
  {
    assert false : "Unary nodes only have one child";
  }

  public boolean equals (Object o)
  {
    if (o == null) return false;
    
    if (this == o) return true;
    // -- class must match
    if (o.getClass () != this.getClass ()) return false;
    // -- children must be identical
    if (getRight () != ((CTLUnaryNode)o).getRight ()) return false;
    
    // -- fairness should be of the same size
    if (fairness.length != ((CTLUnaryNode)o).getFairness ().length) 
      return false;

    // -- fairness must be identical up to the order
    CTLNode[] otherFairness = ((CTLUnaryNode)o).getFairness ();
    for (int i = 0; i < fairness.length; i++)
      if (fairness [i] != otherFairness [i]) return false;
    return true;
    
  }

  public int hashCode ()
  {
    long hash = 
      this.getClass ().hashCode () * Primes.getPrime (0) + 
      System.identityHashCode (getRight ()) * Primes.getPrime (1);

    for (int i = 0; i < fairness.length; i++)
      hash += 
	System.identityHashCode (fairness [i]) * Primes.getPrime (i + 2);

    return (int)hash;
  }
  

  public CTLNode[] getFairness ()
  {
    // XXX maybe fairness.clone () ?
    return fairness;
  }
  
  
  
}
