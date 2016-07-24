package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;

public class CloningRewriter extends AbstractCTLVisitor implements CTLReWriter
{
  public CTLNode rewrite (CTLNode ctl)
  {
    return (CTLNode)ctl.accept (this, null);
  }  

  public Object visitLeafNode (CTLLeafNode node, Object o)
  {
    return node;    
  }
    
  public Object visitAFNode (CTLAFNode node, Object o)
  {
    CTLNode[] fairness = (CTLNode[])node.getFairness ().clone ();
    for (int i = 0; i < fairness.length; i++)
      fairness [i] = rewrite (fairness [i]);

    return rewrite (node.getRight ()).af (fairness);
  }
  
  public Object visitAGNode (CTLAGNode node, Object o)
  {

    CTLNode[] fairness = (CTLNode[])node.getFairness ().clone ();
    for (int i = 0; i < fairness.length; i++)
      fairness [i] = rewrite (fairness [i]);

    return rewrite (node.getRight ()).ag (fairness);
  }
  
  public Object visitAUNode (CTLAUNode node, Object o)
  {
    return rewrite (node.getLeft ()).au (rewrite (node.getRight ()));
  }

  public Object visitARNode (CTLARNode node, Object o)
  {
    return rewrite (node.getLeft ()).ar (rewrite (node.getRight ()));
  }
  public Object visitERNode (CTLERNode node, Object o)
  {
    return rewrite (node.getLeft ()).er (rewrite (node.getRight ()));
  }

  public Object visitAWNode (CTLAWNode node, Object o)
  {
    return rewrite (node.getLeft ()).aw (rewrite (node.getRight ()));
  }

  public Object visitEWNode (CTLEWNode node, Object o)
  {
    return rewrite (node.getLeft ()).ew (rewrite (node.getRight ()));
  }
  

  public Object visitAUiNode (CTLAUiNode node, Object o)
  {
    return rewrite (node.getLeft ()).au (node.getI (), 
					  rewrite (node.getRight ()));
  }

  public Object visitAXNode (CTLAXNode node, Object o)
  {
    return rewrite(node.getRight ()).ax ();
  }

  public Object visitAndNode (CTLAndNode node, Object o)
  {
    return rewrite (node.getLeft ()).and (rewrite (node.getRight ()));
  }

  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    return rewrite (node.getRight ()).ef ();
  }

  public Object visitEGNode (CTLEGNode node, Object o)
  {
    CTLNode[] fairness = (CTLNode[])node.getFairness ().clone ();
    for (int i = 0; i < fairness.length; i++)
      fairness [i] = rewrite (fairness [i]);
	
    return rewrite (node.getRight ()).eg (fairness);
  }

  public Object visitEUNode (CTLEUNode node, Object o)
  {
    return rewrite (node.getLeft ()).eu (rewrite (node.getRight ()));
  }

  public Object visitEUiNode (CTLEUiNode node, Object o)
  {
    return rewrite (node.getLeft ()).eu (node.getI (), 
					   rewrite (node.getRight ()));
  }

  public Object visitEXNode (CTLEXNode node, Object o)
  {
    return rewrite (node.getRight ()).ex ();
  }

  public Object visitEqualsNode (CTLEqualsNode node, Object o)
  {
    return rewrite (node.getLeft ()).eq (rewrite (node.getRight ()));
  }

  public Object visitIffNode (CTLIffNode node, Object o)
  {
    return rewrite (node.getLeft ()).iff (rewrite (node.getRight ()));
  }

  public Object visitImplNode (CTLImplNode node, Object o)
  {
    return rewrite (node.getLeft ()).implies (rewrite (node.getRight ()));
  }


  public Object visitNegNode (CTLNegNode node, Object o)
  {
    return rewrite (node.getRight ()).neg ();
  }


  public Object visitOrNode (CTLOrNode node, Object o)
  {
    return rewrite (node.getLeft ()).or (rewrite (node.getRight ()));
  }

  public Object visitOverNode (CTLOverNode node, Object o)
  {
    return rewrite (node.getLeft ()).over (rewrite (node.getRight ()));
  }

  public Object visitPreEXNode (CTLPreEXNode node, Object o)
  {
    return rewrite (node.getRight ()).preEX ();
  }

  public Object visitUnderNode (CTLUnderNode node, Object o)
  {
    return rewrite (node.getLeft ()).under (rewrite (node.getRight ()));

  }

  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    // -- if a palceholder has atomic propsitions, rewrite them as well
    CTLAtomPropNode[] props = node.getProps ();
    if (props == null) return node;
    
    for (int i = 0; i < props.length; i++)
      rewrite (props [i]);

    return node;
  }
  

  
}
