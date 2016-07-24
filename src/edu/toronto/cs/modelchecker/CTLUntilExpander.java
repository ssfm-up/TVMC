package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;


// XXX This should be renamed appropriatelly

public class CTLUntilExpander extends CloningRewriter
{
  CTLNode top;

  public CTLUntilExpander (MvSet _top)
  {
    top = CTLFactory.createCTLMvSetNode (_top);
  }
  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    return top.eu (rewrite (node.getRight ()));
  }

  public Object visitAFNode (CTLAFNode node, Object o)
  {
    // -- avoid fair af
    if (node.getFairness ().length > 0) return node;
    
    return top.au (rewrite (node.getRight ()));
  }

  public Object visitIffNode (CTLIffNode node, Object o)
  {
    assert false : "Not done yet!";
    return null;
  }

  public Object visitImplNode (CTLImplNode node, Object o)
  {
    // a -> b == ~a \/ b
    return rewrite (node.getLeft ()).neg ().or ((rewrite (node.getRight ())));
  }
  
}
