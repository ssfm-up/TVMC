package edu.toronto.cs.ctl;

/**
 ** This is an Implication CTL node.
 **/
public class CTLImplNode extends CTLBinaryNode
{

  /**
   ** Construct an Implication CTL node using two other nodes as its children.
   **/
  protected CTLImplNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " -> " + getRight () + ")";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitImplNode (this, s);
  }


}
