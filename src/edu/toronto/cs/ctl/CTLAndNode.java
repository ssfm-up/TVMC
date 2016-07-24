package edu.toronto.cs.ctl;

/**
 ** This is an And CTL node.
 **/
public class CTLAndNode extends CTLBinaryNode
{

  /**
   ** Construct an And CTL node using two other nodes as its children.
   **/
  protected CTLAndNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " /\\ " + getRight () + ")";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAndNode (this, s);
  }


}
