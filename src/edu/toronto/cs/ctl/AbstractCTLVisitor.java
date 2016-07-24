package edu.toronto.cs.ctl;

import edu.toronto.cs.util.*;

/**
 ** This is an abstract CTL node.
 **/
public class AbstractCTLVisitor implements CTLVisitor
{
  public Object visit (CTLNode n, Object o)
  {
    if (n != null)
      return n.accept (this, o);
    return o;
  }
  
  
  public Object visitCTLNode (CTLNode n, Object o)
  {
    return o;
  }
  
  public Object visitAbstractNode (CTLAbstractNode n, Object o)
  {
    return visitCTLNode (n, o);
  }
  
  public Object visitLeafNode (CTLLeafNode n, Object o)
  {
    return visitAbstractNode (n, o);
  }
  public Object visitUnaryNode (CTLUnaryNode n, Object o)
  {
    return visitAbstractNode (n, o);
  }
  public Object visitBinaryNode (CTLBinaryNode n, Object o)
  {
    return visitAbstractNode (n, o);
  }
  
  

  public Object visitAFNode (CTLAFNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }
  
  public Object visitAGNode (CTLAGNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }
  
  public Object visitAUNode (CTLAUNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitARNode (CTLARNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }
  
  public Object visitERNode (CTLERNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitAUiNode (CTLAUiNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitAWNode (CTLAWNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitAXNode (CTLAXNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitAndNode (CTLAndNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitAtomPropNode (CTLAtomPropNode node, Object o)
  {
    return visitLeafNode (node, o);
  }
  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitEGNode (CTLEGNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitEUNode (CTLEUNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitEUiNode (CTLEUiNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitEWNode (CTLEWNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitEXNode (CTLEXNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitEqualsNode (CTLEqualsNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitIffNode (CTLIffNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitImplNode (CTLImplNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitConstantNode (CTLConstantNode node, Object o)
  {
    return visitLeafNode (node, o);
  }

  public Object visitMvSetNode (CTLMvSetNode node, Object o)
  {
    return visitLeafNode (node, o);
  }

  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    return visitLeafNode (node, o);
  }
  
  public Object visitNegNode (CTLNegNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitNode (CTLNode node, Object o)
  {
    assert false : "what the hell?";
    return null;
  }

  public Object visitOrNode (CTLOrNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitOverNode (CTLOverNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }

  public Object visitPreEXNode (CTLPreEXNode node, Object o)
  {
    return visitUnaryNode (node, o);
  }

  public Object visitUnderNode (CTLUnderNode node, Object o)
  {
    return visitBinaryNode (node, o);
  }


  
}

