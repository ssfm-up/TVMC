 package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;

// -- pushes negation to the level of atomic propositions
public class NormalFormRewriter extends AbstractCTLVisitor 
  implements CTLReWriter
{

  public CTLNode rewrite (CTLNode node)
  {
    return rewrite (node, Boolean.FALSE);
  }
    
  CTLNode rewrite (CTLNode node, Object o)
  {
    return (CTLNode)node.accept (this, o);
  }
  

  public Object visitLeafNode (CTLLeafNode node, Object o)
  {
    if (o == Boolean.TRUE) return node.neg ();
    return node;
  }
  
  public Object visitUnaryNode (CTLUnaryNode node, Object o)
  {
    assert false : "No normal form for: " + node;
    return null;
  }
  public Object visitBinaryNode (CTLBinaryNode node, Object o)
  {
    assert false : "No normal form for: " + node;
    return null;
  }
  
  
  public Object visitAFNode (CTLAFNode node, Object o)
  {
    //if (node.getFairness ().length > 0) return node;
    
    // -- ~AF p == EG ~p
    if (o == Boolean.TRUE && node.getFairness ().length == 0)
      return rewrite (node.getRight (), o).eg ();
    else if (o == Boolean.FALSE)
      return rewrite (node.getRight (), o).af (node.getFairness ());
    else
      return node;
  }
  
  public Object visitAGNode (CTLAGNode node, Object o)
  {
    if (node.getFairness ().length > 0) return node;
    // -- ~AG p == EF ~p
    if (o == Boolean.TRUE)
      rewrite (node.getRight (), o).ef ();
    
    return rewrite (node.getRight (), o).ag ();
  }
  
  public Object visitAUNode (CTLAUNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getLeft (), o).er (rewrite (node.getRight (), o));
    
    return rewrite (node.getLeft (), o).au (rewrite (node.getRight (), o));
  }
  
  public Object visitARNode (CTLARNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getLeft (), o).eu (rewrite (node.getRight (), o));
    return rewrite (node.getLeft (), o).ar (rewrite (node.getRight (), o));
  }
  
  public Object visitERNode (CTLERNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getLeft (), o).au (rewrite (node.getRight (), o));
    
    return rewrite (node.getLeft (), o).er (rewrite (node.getRight (), o));
  }
  
  public Object visitAXNode (CTLAXNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getRight (), o).ex ();
    return rewrite(node.getRight (), o).ax ();
  }

  public Object visitAndNode (CTLAndNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getLeft (), o).or (rewrite (node.getRight (), o));
    return rewrite (node.getLeft (), o).and (rewrite (node.getRight (), o));
  }

  public Object visitImplNode (CTLImplNode node, Object o)
  {
    return rewrite (node.getLeft ().neg ().or (node.getRight ()), o);
  }
  
  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    // ~EF p = AG ~p
    if (o == Boolean.TRUE)
      return rewrite (node.getRight (), o).ag ();
    return rewrite (node.getRight (), o).ef ();
  }

  public Object visitEGNode (CTLEGNode node, Object o)
  {
    // -- ~EG p = AF ~p
    if (o == Boolean.TRUE)
      return rewrite (node.getRight (), o).af ();
    return rewrite (node.getRight (), o).eg ();
  }


  public Object visitEXNode (CTLEXNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getRight (), o).ax ();

    return rewrite (node.getRight (), o).ex ();
  }

  public Object visitEUNode (CTLEUNode node, Object o)
  {
    if (o == Boolean.TRUE)
      //return visitBinaryNode (node, o);
      return rewrite (node.getLeft (), o).ar (rewrite (node.getRight (), o));
    
    return rewrite (node.getLeft (), o).eu (rewrite (node.getRight (), o));
  }
  

  public Object visitEqualsNode (CTLEqualsNode node, Object o)
  {
    // -- equals is actually almost a leaf node, or only used 
    // -- as such right now
    if (o == Boolean.TRUE)
      return rewrite (node.getLeft (), Boolean.FALSE).eq 
	(rewrite (node.getRight (), Boolean.FALSE)).neg ();
    
    return rewrite (node.getLeft (), o).eq (rewrite (node.getRight (), o));
  }

  public Object visitNegNode (CTLNegNode node, Object o)
  {
    if (o == Boolean.TRUE)
      return rewrite (node.getRight (), Boolean.FALSE);

    return rewrite (node.getRight (), Boolean.TRUE);
  }  
  

  public Object visitOrNode (CTLOrNode node, Object o)
  {
    return rewrite (node.getLeft (), o).or (rewrite (node.getRight (), o));
  }

  public Object visitOverNode (CTLOverNode node, Object o)
  {
    assert false : "No normal form for Over node";
    return null;
  }

  public Object visitPreEXNode (CTLPreEXNode node, Object o)
  {
    assert false : "no normal form for PreEX";
    return null;
  }

  public Object visitUnderNode (CTLUnderNode node, Object o)
  {
    assert false : "no normal form for UnderNode";
    return null;

  }

  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    if (o == Boolean.TRUE)
      node.setNegated (true);
    return node;
  }
  

  
}
