package edu.toronto.cs.ctl;

import edu.toronto.cs.util.*;


public class CTLLeafNode extends CTLAbstractNode
{
  public CTLLeafNode ()
  {
  }
  
  public CTLNode getLeft ()
  {
    assert false : "Leaf nodes have no children";
    return null;
  }

  public CTLNode getRight ()
  {
    assert false : "Leaf nodes have no children";
    return null;
  }

  protected void setRight (CTLNode v)
  {
    assert false : "Leaf nodes have no children";
  }
  protected void setLeft (CTLNode v)
  {
    assert false : "Leaf nodes have no children";
  }

  
}
