package edu.toronto.cs.ctl;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;

// -- a place holder
public class CTLPlaceholderNode extends CTLLeafNode
{
  /**
   ** Funny enough CTLPlaceholderNode is a leaf of the CTL tree, but 
   ** it does have children... it is just neither binary nor unary
   ** so we let it be a leaf node since it has no right and left children :)
   **/

  // -- atomic propositions this placeholder is restricted to
  CTLAtomPropNode[] props;
  // -- terms [i] is the set of all terms for proposition props [i]
  // -- for example if props [0] is a an algebraic proposition a then
  // -- terms [0] = {a, !a}
  // -- if props [0] is an enumerated type proposition 'a;
  // -- with possible values 'foo', 'bar', 'woo'
  // -- then terms [0] = {a=foo, a=bar, a=woo}
  MvSet[][] terms;
  
  String name;

  boolean negated;
  

  public CTLPlaceholderNode ()
  {
    super ();
    props = null;
    terms = null;
  }
  
  public CTLPlaceholderNode (String _name, CTLAtomPropNode[] _props)
  {
    name = _name;
    props = _props;
  }
  
  public boolean isNegated ()
  {
    return negated;
  }
  public void setNegated (boolean v)
  {
    negated = v;
  }
  
  
  public CTLAtomPropNode[] getProps ()
  {
    return props;
  }
  public CTLAtomPropNode getProps (int i)
  {
    return props [i];
  }
  
  public String getName ()
  {
    return name;
  }

  public void setTerms (MvSet[][] v)
  {
    terms = v;
    assert terms.length == props.length;
  }
  public MvSet[][] getTerms ()
  {
    return terms;
  }
  public MvSet[] getTerm (int i)
  {
    return terms [i];
  }
  
  
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ('?');
    sb.append (getName ());
    sb.append ('{');
    for (int i = 0; i < props.length; i++)
      {
	sb.append (props [i].toString ());
	if (i + 1 < props.length)
	  sb.append (", ");
      }
    sb.append ('}');
    
    return sb.toString ();
  }

  public Object accept (CTLVisitor visitor, Object o)
  {
    return visitor.visitPlaceholderNode (this, o);
  }
  

  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o.getClass () != CTLPlaceholderNode.class) return false;
    return equals ((CTLPlaceholderNode)o);
  }
  public boolean equals (CTLPlaceholderNode node)
  {
    return name.equals (node.name);
  }
  
  public int hashCode ()
  {
    return name.hashCode ();
  }
			     
}
