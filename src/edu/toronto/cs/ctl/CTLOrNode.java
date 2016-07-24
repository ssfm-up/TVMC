package edu.toronto.cs.ctl;

/**
 ** This is an Or CTL node.
 **/
public class CTLOrNode extends CTLBinaryNode
{

  /**
   ** Construct an Or CTL node using two other nodes as its children.
   **/
  protected CTLOrNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " \\/ " + getRight () + ")";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitOrNode (this, s);
  }


}
