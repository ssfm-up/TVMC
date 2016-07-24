package edu.toronto.cs.ctl;

/**
 ** This is an AG CTL node.
 **/
public class CTLAGNode extends CTLUnaryNode
{

  /**
   ** Construct an AG CTL node from another node.
   **/
  protected CTLAGNode (CTLNode phi)
  {
    super (phi, null);
  }
  protected CTLAGNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  

  public String toString ()
  {
    return "AG " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAGNode (this, s);
  }

}
