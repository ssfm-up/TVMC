package edu.toronto.cs.ctl;

/**
 ** This is an AF CTL node.
 **/
public class CTLAFNode extends CTLUnaryNode
{

  /**
   ** Construct an AF CTL node from another node.
   **/
  protected CTLAFNode (CTLNode phi)
  {
    super (phi, null);
  }
  protected CTLAFNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  

  public String toString ()
  {
    return "AF " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAFNode (this, s);
  }  

}
