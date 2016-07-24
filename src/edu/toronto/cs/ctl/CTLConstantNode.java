package edu.toronto.cs.ctl;

import edu.toronto.cs.algebra.*;

/**
 ** This is a CTL node representing a lattice value.
 **/
public class CTLConstantNode extends CTLLeafNode
{

  AlgebraValue value;

  /**
   ** Construct a lattice node from the index of the lattice value and
   ** the lattice itself.
   **/
  protected CTLConstantNode (AlgebraValue _value)
  {
    super ();
    value = _value;
    //System.out.println("Made constant node: "+value);
    
  }

  public String toString ()
  {
    return value.toString ();
  }

  public AlgebraValue getValue ()
  {
    return value;
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitConstantNode (this, s);
  }

  public boolean equals (Object o)
  {
    if (this == o) return true;
    
    if (o.getClass () != CTLConstantNode.class) return false;
    return equals ((CTLConstantNode)o);
  }
  public boolean equals (CTLConstantNode node)
  {
    return node.value == value;
  }
  
  public int hashCode ()
  {
    return value.hashCode ();
  }
  
  
  
}
