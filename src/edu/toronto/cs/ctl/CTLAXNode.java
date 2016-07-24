package edu.toronto.cs.ctl;

/**
 ** This is an AX CTL node.
 **/
public class CTLAXNode extends CTLUnaryNode
{

  /**
   ** Construct an AX CTL node from another node.
   **/
  protected CTLAXNode (CTLNode phi)
  {
    super (phi);
  }

  protected CTLAXNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  

  public String toString ()
  {
    return "AX " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAXNode (this, s);
  }

}
