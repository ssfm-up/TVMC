package edu.toronto.cs.ctl;

/**
 ** This is an EX CTL node.
 **/
public class CTLEXNode extends CTLUnaryNode
{

  /**
   ** Construct an EX CTL node from another node.
   **/
  protected CTLEXNode (CTLNode phi)
  {
    super (phi, null);
  }

  protected CTLEXNode (CTLNode phi, CTLNode[] fairness)
  {
    super (phi, fairness);
  }
  
  public String toString ()
  {
    return "EX " + getRight ();
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEXNode (this, s);
  }

  
}
