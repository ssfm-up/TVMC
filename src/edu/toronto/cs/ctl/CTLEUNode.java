package edu.toronto.cs.ctl;

/**
 ** This is an existential strong until CTL node.
 **/
public class CTLEUNode extends CTLBinaryNode
{

  /**
   ** Construct an existential strong until CTL node using two other
   ** nodes as its children.
   **/
  protected CTLEUNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi, null);
  }

  protected CTLEUNode (CTLNode phi, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
  }
  

  public String toString ()
  {
    return "E[" + getLeft () + " U " + getRight () + "]";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitEUNode (this, s);
  }

}



