package edu.toronto.cs.ctl;

/**
 ** This is a Neg CTL node.
 **/
public class CTLNegNode extends CTLUnaryNode
{

  /**
   ** Construct a Neg CTL node from another node.
   **/
  protected CTLNegNode (CTLNode phi)
  {
    super (phi);
  }

  public String toString ()
  {
    return "!" + toString (getRight (), true);
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitNegNode (this, s);
  }


}





