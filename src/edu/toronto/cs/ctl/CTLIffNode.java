package edu.toronto.cs.ctl;

/**
 ** This is an Equivalence CTL node.
 **/
public class CTLIffNode extends CTLBinaryNode
{

  /**
   ** Construct an Equivalence CTL node using two other nodes as its children.
   **/
  protected CTLIffNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " <-> " + getRight () + ")";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitIffNode (this, s);
  }


}
