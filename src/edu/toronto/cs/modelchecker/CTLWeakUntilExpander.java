package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;



public class CTLWeakUntilExpander extends CloningRewriter
{

  public CTLWeakUntilExpander ()
  {
    super ();
  }
  
  public Object visitEWNode (CTLEWNode node, Object o)
  {
    // E [ x W y ] == E [y R (x | y)]
    CTLNode x = rewrite (node.getLeft ());
    CTLNode y = rewrite (node.getRight ());
    
    return y.er (x.or (y));    
  }
  

  public Object visitAWNode (CTLAWNode node, Object o)
  {
    // A [ x W y ] == A [y R (x | y)]
    CTLNode x = rewrite (node.getLeft ());
    CTLNode y = rewrite (node.getRight ());
    
    return y.ar (x.or (y));
  }  
}
