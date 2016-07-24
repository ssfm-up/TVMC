package edu.toronto.cs.ctl;

import edu.toronto.cs.util.Primes;
/**
 ** This is an existential strong until CTL node.
 **/
public class CTLEUiNode extends CTLEUNode
{

  protected int bound;

  /**
   ** Construct a bounded existential strong until CTL node using two
   ** other nodes as its children.
   **/
  protected CTLEUiNode (CTLNode phi, int i, CTLNode psi)
  {
    super (phi, psi, null);
    bound = i;
  }
  protected CTLEUiNode (CTLNode phi, int i, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
    bound = i;
  }
  

  /**
   ** Gets the bound on the EUi.
   **/
  public int getI ()
  {
    return bound;
  }

  /**
   ** Sets the bound on the EUi.
   **/
  protected void setI (int i)
  {
    bound = i;
  }

  public String toString ()
  {
    return "E[" + getLeft () + " U" + bound + " " + getRight () + "]";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEUiNode (this, s);
  }


  public boolean equals (Object o)
  {
    // -- assuming lazy && evaluation
    return super.equals (o) && bound == ((CTLEUiNode)o).getI ();
  }

  int storedHashCode = 0;
  public int hashCode ()
  {
    if (storedHashCode == 0)
      {
	long hash = this.getClass ().hashCode () * Primes.primes [0] + 
	  System.identityHashCode (getLeft ()) * Primes.primes [1] + 
	  System.identityHashCode (getRight ()) * Primes.primes [2] + 
	  bound * Primes.primes [3];
	
	for (int i = 0; i < fairness.length; i++)
	  hash += 
	    System.identityHashCode (fairness [i]) * Primes.getPrime (i);
	storedHashCode = (int)hash;
      }
    return storedHashCode;
  }
  


}
