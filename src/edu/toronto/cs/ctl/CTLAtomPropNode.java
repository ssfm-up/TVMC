package edu.toronto.cs.ctl;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.Primes;

/**
 ** This is an atomic CTL node.
 **/
public class CTLAtomPropNode extends CTLLeafNode
{

  String name;
  MvSet mvSet;

  protected CTLAtomPropNode ()
  {
    super ();
    name = null;
  }

  /**
   ** Construct an atomic CTL node from a name and value
   **/
  protected CTLAtomPropNode (String _name)
  {
    super ();
    name = _name;
    mvSet = null;
  }
  
  protected CTLAtomPropNode (String _name, MvSet value)
  {
    super ();
    name = _name;
    mvSet = value;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (String d)
  {
    name = d;
  }

  public MvSet getMvSet ()
  {
    return mvSet;
  }

  public void setMvSet (MvSet v)
  {
    mvSet = v;
  }
  

  public String toString ()
  {
    return name;// + "(" + (mvSet != null ? mvSet.toString () : "null") + ")";
  }


  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAtomPropNode (this, s);
  }

  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o.getClass () != CTLAtomPropNode.class) return false;
    return equals ((CTLAtomPropNode)o);
  }
  public boolean equals (CTLAtomPropNode node)
  {
    return name.equals (node.name);
  }
  
  public int hashCode ()
  {
    return name.hashCode ();
  }
  
  

}
