package edu.toronto.cs.ctl;

/**
 ** This is an Equals CTL node.
 **/
public class CTLEqualsNode extends CTLBinaryNode
{

  /**
   ** Construct an Equals CTL node using two other nodes as its children.
   **/
  protected CTLEqualsNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi);
  }

  public String toString ()
  {
    return "(" + getLeft () + " = " + getRight () + ")";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEqualsNode (this, s);
  }

}
