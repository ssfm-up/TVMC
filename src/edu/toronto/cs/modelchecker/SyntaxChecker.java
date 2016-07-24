package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;

public class SyntaxChecker extends AbstractCTLVisitor implements CTLReWriter
{
  public CTLNode rewrite (CTLNode node)
  {
    return (CTLNode)node.accept (this, null);
  }

  public Object visitAtomPropNode (CTLAtomPropNode node, Object o)
  {
    if (node.getMvSet () == null)
      throw new SyntaxException ("Could not resolve: " + node.getName ());
    return o;
  }
  

  class SyntaxException extends RuntimeException
  {
    public SyntaxException ()
    {
      super ();
    }
    public SyntaxException (String message)
    {
      super (message);
    }
  }
  
  
}


