package edu.toronto.cs.ctl;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

/**
 ** This is a CTL node representing an MvSet.
 **/
public class CTLMvSetNode extends CTLLeafNode
{

  MvSet mvset;
  String name = null;

  /**
   ** Construct a CTL node associated with a particular MvSet.
   **/
  protected CTLMvSetNode (MvSet set)
  {
    super ();
    mvset = set;
  }

  public void setName (String v)
  {
    if (name == null)
      name = v;
    else
      System.out.println 
	(this.getClass ().getName () + 
	 ".setName called with " + v + " when current name is " + name);
  }

  public String getName ()
  {
    return name;
  }
  
  public String toString ()
  {

    
    if (getName () == null)
      return mvset.toString ();
    return getName ();
  }

  public MvSet getMvSet ()
  {
    return mvset;
  }

  public Object accept (CTLVisitor visitor, Object stateinfo)
  {
    return visitor.visitMvSetNode (this, stateinfo);
  }
  

  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o.getClass () != CTLMvSetNode.class) return false;
    return equals ((CTLMvSetNode)o);
  }
  public boolean equals (CTLMvSetNode node)
  {
    return mvset.equals (node.mvset);
  }

  public int hashCode ()
  {
    return mvset.hashCode ();
  }
  
  
  
}
