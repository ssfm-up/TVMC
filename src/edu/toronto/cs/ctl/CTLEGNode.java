package edu.toronto.cs.ctl;

/**
 ** This is an EG CTL node.
 **/
public class CTLEGNode extends CTLUnaryNode
{

  /**
   ** Construct an EG CTL node from another node.
   **/
  protected CTLEGNode (CTLNode phi)
  {
    super (phi, null);
  }

  protected CTLEGNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  

  public String toString ()
  {
    return "EG " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEGNode (this, s);
  }


}
