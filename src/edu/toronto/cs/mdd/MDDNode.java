package edu.toronto.cs.mdd;

import java.util.BitSet;

/***
 *** an abstrct mdd node
 ***/

public abstract class MDDNode 
{
  // -- primes from CUDD used for hash functions
  public static long[] primes = {12582917L, 4256249L, 741457L, 1618033999L};
  // -- number of children to use for hashing
  public static int hashChildren = 4;

  

  public abstract int childrenSize ();
  public abstract boolean isConstant ();
  public abstract MDDNode[] getChildren ();
  public abstract int getVarIndex ();
  public abstract int getCubeLastVar ();
  public abstract int getValue ();
  public abstract MDDNode getChild (int i);
  

  // XXX don't like this here
  public abstract BitSet getImage ();
  

  // -- pointer based hash code
  public int objectHashCode ()
  {
    return super.hashCode ();
  }
  
}





