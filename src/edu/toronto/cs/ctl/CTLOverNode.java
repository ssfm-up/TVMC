package edu.toronto.cs.ctl;

/**
 ** This is an Over CTL node.
 **/
public class CTLOverNode extends CTLBinaryNode
{

  /**
   ** Construct an Over CTL node using two other nodes as its children.
   **/
  protected CTLOverNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " >= " + getRight () + ")";
  }

  public Object accept (CTLVisitor v, Object o)
  {
    return v.visitOverNode (this, o);
  }
  
}
