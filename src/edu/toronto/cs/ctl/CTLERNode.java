package edu.toronto.cs.ctl;

/**
 ** This is a universal strong until CTL node.
 **/
public class CTLERNode extends CTLBinaryNode
{

  /**
   ** Construct a universal strong until CTL node using two other
   ** nodes as its children.
   **/
  protected CTLERNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi, null);
  }

  protected CTLERNode (CTLNode phi, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
  }
  
  public String toString ()
  {
    return "E[" + getLeft () + " R " + getRight () + "]";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitERNode (this, s);
  }


}
