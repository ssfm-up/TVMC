package edu.toronto.cs.ctl;

/**
 ** This is a universal strong until CTL node.
 **/
public class CTLAUNode extends CTLBinaryNode
{

  /**
   ** Construct a universal strong until CTL node using two other
   ** nodes as its children.
   **/
  protected CTLAUNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi, null);
  }

  protected CTLAUNode (CTLNode phi, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
  }
  
  public String toString ()
  {
    return "A[" + getLeft () + " U " + getRight () + "]";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAUNode (this, s);
  }


}
