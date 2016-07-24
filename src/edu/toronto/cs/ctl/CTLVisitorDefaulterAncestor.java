package edu.toronto.cs.ctl;

import java.util.*;

/**
 ** This is an abstract CTL node.
 **/
public abstract class CTLVisitorDefaulterAncestor implements CTLVisitor
{
  public Object visit (CTLNode n, Object stateinfo)
  {
    if (n != null)
      return n.accept (this, stateinfo);
    return null;
  }
  
  public Object visitCTLNode (CTLNode node, Object stateinfo)
  {
    // Don't panic.
    return stateinfo;
  }

  public Object visitAbstractNode (CTLAbstractNode node, Object stateinfo)
  {
    return visitCTLNode (node, stateinfo);
  }

  public Object visitAFNode (CTLAFNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAGNode (CTLAGNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAUNode (CTLAUNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAUiNode (CTLAUiNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAWNode (CTLAWNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAXNode (CTLAXNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAndNode (CTLAndNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitAtomPropNode (CTLAtomPropNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEFNode (CTLEFNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitARNode (CTLARNode node, Object stateinfo)
  {
    return visitCTLNode (node, stateinfo);
  }
  public Object visitERNode (CTLERNode node, Object stateinfo)
  {
    return visitCTLNode (node, stateinfo);
  }
  public Object visitEGNode (CTLEGNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEUNode (CTLEUNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEUiNode (CTLEUiNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEWNode (CTLEWNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEXNode (CTLEXNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitEqualsNode (CTLEqualsNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitIffNode (CTLIffNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitImplNode (CTLImplNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitConstantNode (CTLConstantNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitMvSetNode (CTLMvSetNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitNegNode (CTLNegNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitNode (CTLNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitOrNode (CTLOrNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitOverNode (CTLOverNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitPreEXNode (CTLPreEXNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitUnderNode (CTLUnderNode node, Object stateinfo)  
  { return visitCTLNode (node, stateinfo); }
  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    return visitCTLNode (node, o);
    
  }
  
}
