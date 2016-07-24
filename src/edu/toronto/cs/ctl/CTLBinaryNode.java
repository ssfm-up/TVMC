package edu.toronto.cs.ctl;

import edu.toronto.cs.util.Primes;


public abstract class CTLBinaryNode extends CTLUnaryNode
{
  CTLNode left;
  
  public CTLBinaryNode ()
  {
    super (null);
    left = null;
  }
  
  public CTLBinaryNode (CTLNode _left, CTLNode _right)
  {
    super (_right);
    left = _left;
  }

  public CTLBinaryNode (CTLNode _left, CTLNode _right, CTLNode[] fairness)
  {
    super (_right, fairness);
    left = _left;
  }
  
  
  public CTLNode getRight ()
  {
    return right;
  }
  public CTLNode getLeft ()
  {
    return left;
  }
  protected void setRight (CTLNode v)
  {
    right = v;
  }
  protected void setLeft (CTLNode v)
  {
    left = v;
  }


  public boolean equals (Object o)
  {
    // -- assuming lazy && evaluation
    // -- fairness is handled in CTLUnaryNode
    return super.equals (o) &&
      ((CTLBinaryNode)o).getRight () == getRight ();
  }

  public int hashCode ()
  {
    long hash = this.getClass ().hashCode () * Primes.primes [0] + 
      System.identityHashCode (getLeft ()) * Primes.primes [1] + 
      System.identityHashCode (getRight ()) * Primes.primes [2];

    // -- fairness 
    for (int i = 0; i < fairness.length; i++)
      hash += 
	System.identityHashCode (fairness [i]) * Primes.getPrime (i + 3);
    
    return (int)hash;
  }

}
