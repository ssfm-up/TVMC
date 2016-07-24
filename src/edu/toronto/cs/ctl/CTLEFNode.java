package edu.toronto.cs.ctl;

/**
 ** This is an EF CTL node.
 **/
public class CTLEFNode extends CTLUnaryNode
{

  /**
   ** Construct an EF CTL node from another node.
   **/
  protected CTLEFNode (CTLNode phi)
  {
    super (phi, null);
  }

  protected CTLEFNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  
  public String toString ()
  {
    return "EF " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEFNode (this, s);
  }


}
