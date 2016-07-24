package edu.toronto.cs.ctl;

import edu.toronto.cs.util.Primes;

/**
 ** This is a universal strong until CTL node.
 **/
public class CTLAUiNode extends CTLAUNode
{

  protected int bound;

  /**
   ** Construct a bounded universal strong until CTL node using two
   ** other nodes as its children.
   **/
  protected CTLAUiNode (CTLNode phi, int i, CTLNode psi)
  {
    super (phi, psi, null);
    bound = i;
  }
  protected CTLAUiNode (CTLNode phi, int i, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
    bound = i;
  }
  

  /**
   ** Gets the bound on the AUi.
   **/
  public int getI ()
  {
    return bound;
  }

  /**
   ** Sets the bound on the AUi.
   **/
  protected void setI (int i)
  {
    bound = i;
  }

  public String toString ()
  {
    return "A[" + getLeft () + " U" + bound + " " + getRight () + "]";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAUiNode (this, s);
  }

  public boolean equals (Object o)
  {
    // -- assuming lazy && evaluation
    return super.equals (o) && bound == ((CTLAUiNode)o).getI ();
  }

  public int hashCode ()
  {
    long hash = this.getClass ().hashCode () * Primes.primes [0] + 
      System.identityHashCode (getLeft ()) * Primes.primes [1] + 
      System.identityHashCode (getRight ()) * Primes.primes [2] + 
      bound;

    for (int i = 0; i < fairness.length; i++)
      hash += System.identityHashCode (fairness [i]) * Primes.getPrime (i);
    
    return (int)hash;
  }



}
