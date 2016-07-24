package edu.toronto.cs.ctl;

/**
 ** This is a pre EX CTL node thing.
 **/
public class CTLPreEXNode extends CTLUnaryNode
{

  /**
   ** Construct a pre EX CTL node thing from another node.
   **/
  protected CTLPreEXNode (CTLNode phi)
  {
    super (phi);
  }

  public String toString ()
  {
    return "preEX " + getRight ();
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitPreEXNode (this, s);
  }  
  
}
