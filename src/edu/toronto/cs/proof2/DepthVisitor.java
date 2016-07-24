package edu.toronto.cs.proof2;


import edu.toronto.cs.ctl.*;


public class DepthVisitor extends CTLVisitorDefaulterAncestor
{
  public int ctlDepth(CTLNode node)
  {
    if (node == null)
      System.out.println ("DV.ctlDepth(): Node is null, bad");
    
    System.out.println("ctlDepth of "+node.toString());
    Integer i = (Integer) visit(node, new Integer(0));
    return i.intValue();
  }
  
  public Object unaryNontemporalVisit(CTLNode node, Object stateinfo)
  {
    return visit(node.getRight(),
			stateinfo);
    
  }
  
  public Object binaryNontemporalVisit(CTLNode node, Object stateinfo)
  {

    Integer lDepth = (Integer)
      visit(node.getLeft(),
		   new Integer(((Integer)stateinfo).intValue()));
    Integer rDepth = (Integer)
      visit(node.getRight(),
		   new Integer(((Integer)stateinfo).intValue()));
    return new Integer(Math.max(lDepth.intValue(),
				rDepth.intValue()));
  }
  
  public Object unaryTemporalVisit(CTLNode node, Object stateinfo)
  {
    
    int i = ((Integer)stateinfo).intValue();
    return visit(node.getRight(),
			new Integer(++i));
  }
  
  public Object binaryTemporalVisit(CTLNode node, Object stateinfo)
  {
    
    int i = ((Integer)stateinfo).intValue();
    
    Integer lDepth = (Integer)
      visit(node.getLeft(),
		   new Integer(++i));
    Integer rDepth = (Integer)
      visit(node.getRight(),
		   new Integer(i));
    return new Integer(Math.max(lDepth.intValue(),
				rDepth.intValue()));
    
  }

  public Object visitAFNode (CTLAFNode node, Object stateinfo)
  {
    return unaryTemporalVisit(node, stateinfo);
    
  }
  
  public Object visitAGNode (CTLAGNode node, Object stateinfo)  
  {
    return unaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitAUNode (CTLAUNode node, Object stateinfo)
  {
    return binaryTemporalVisit(node, stateinfo);
  }
  

  public Object visitARNode (CTLARNode node, Object o)
  {
    return binaryTemporalVisit(node, o);
  }

  public Object visitAUiNode (CTLAUiNode node, Object stateinfo)
  {
    return binaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitAWNode (CTLAWNode node, Object stateinfo)
  {
  return binaryTemporalVisit(node, stateinfo);
  }

  public Object visitAXNode (CTLAXNode node, Object stateinfo)
  {
    return unaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitAndNode (CTLAndNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
    
  }
  
  public Object visitAtomPropNode (CTLAtomPropNode node, Object stateinfo)
  {
    System.out.println("propnode, d="+(Integer)stateinfo);
    
    return stateinfo;
  }

  public Object visitEFNode (CTLEFNode node, Object stateinfo)
  {
    return unaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEGNode (CTLEGNode node, Object stateinfo)
  {
    return unaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEUNode (CTLEUNode node, Object stateinfo) 
  {
    return binaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEUiNode (CTLEUiNode node, Object stateinfo) 
  {
    return binaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEWNode (CTLEWNode node, Object stateinfo)
  {
    return binaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEXNode (CTLEXNode node, Object stateinfo)
  {
    return unaryTemporalVisit(node, stateinfo);
  }
  
  public Object visitEqualsNode (CTLEqualsNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitIffNode (CTLIffNode node, Object stateinfo) 
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitImplNode (CTLImplNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitConstantNode (CTLConstantNode node, Object stateinfo)
  {
      
    return stateinfo;
  }
  
  public Object visitMvSetNode (CTLMvSetNode node, Object stateinfo)
  {
    return stateinfo;
  }
  
  public Object visitNegNode (CTLNegNode node, Object stateinfo)
  {
    return unaryNontemporalVisit(node, stateinfo);
  }
  
  
  public Object visitOrNode (CTLOrNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitOverNode (CTLOverNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitPreEXNode (CTLPreEXNode node, Object stateinfo)
  {
    System.out.println("Negnode: d="+(Integer)stateinfo);
    
    return stateinfo;
  }
  
  public Object visitUnderNode (CTLUnderNode node, Object stateinfo)
  {
    return binaryNontemporalVisit(node, stateinfo);
  }
  
  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
  
    return o;
  }
  

  
}

