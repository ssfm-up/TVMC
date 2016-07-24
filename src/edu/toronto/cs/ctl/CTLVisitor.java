package edu.toronto.cs.ctl;

import java.util.*;

/**
 ** This is an abstract CTL node.
 **/
public interface CTLVisitor
{
  public Object visit (CTLNode n, Object stateinfo);
  
  public Object visitAbstractNode (CTLAbstractNode n, Object stateinfo);

  public Object visitAFNode (CTLAFNode node, Object stateinfo);
  public Object visitAGNode (CTLAGNode node, Object stateinfo);
  public Object visitAUNode (CTLAUNode node, Object stateinfo);
  public Object visitARNode (CTLARNode node, Object o);
  public Object visitERNode (CTLERNode node, Object o);
  public Object visitAUiNode (CTLAUiNode node, Object stateinfo);
  public Object visitAWNode (CTLAWNode node, Object stateinfo);
  public Object visitAXNode (CTLAXNode node, Object stateinfo);
  public Object visitAndNode (CTLAndNode node, Object stateinfo);
  public Object visitAtomPropNode (CTLAtomPropNode node, Object stateinfo);
  public Object visitEFNode (CTLEFNode node, Object stateinfo);
  public Object visitEGNode (CTLEGNode node, Object stateinfo);
  public Object visitEUNode (CTLEUNode node, Object stateinfo);
  public Object visitEUiNode (CTLEUiNode node, Object stateinfo);
  public Object visitEWNode (CTLEWNode node, Object stateinfo);
  public Object visitEXNode (CTLEXNode node, Object stateinfo);
  public Object visitEqualsNode (CTLEqualsNode node, Object stateinfo);
  public Object visitIffNode (CTLIffNode node, Object stateinfo);
  public Object visitImplNode (CTLImplNode node, Object stateinfo);
  public Object visitConstantNode (CTLConstantNode node, Object stateinfo);
  public Object visitMvSetNode (CTLMvSetNode node, Object stateinfo);
  public Object visitNegNode (CTLNegNode node, Object stateinfo);
  public Object visitNode (CTLNode node, Object stateinfo);
  public Object visitOrNode (CTLOrNode node, Object stateinfo);
  public Object visitOverNode (CTLOverNode node, Object stateinfo);
  public Object visitPreEXNode (CTLPreEXNode node, Object stateinfo);
  public Object visitUnderNode (CTLUnderNode node, Object stateinfo);
  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o);

  
}

