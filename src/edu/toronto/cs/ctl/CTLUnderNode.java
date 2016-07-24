package edu.toronto.cs.ctl;

/**
 ** This is an Under CTL node.
 **/
public class CTLUnderNode extends CTLBinaryNode
{

  /**
   ** Construct an Under CTL node using two other nodes as its children.
   **/
  protected CTLUnderNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " <= " + getRight () + ")";
  }
}
