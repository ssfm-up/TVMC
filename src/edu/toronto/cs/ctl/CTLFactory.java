package edu.toronto.cs.ctl;

import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class CTLFactory
{

  static Map uniqueTable = new HashMap ();

  public static CTLEXNode createCTLEXNode (CTLNode right)
  {
    return createCTLEXNode (right, null);
  }

  public static CTLAXNode createCTLAXNode (CTLNode right)
  {
    return createCTLAXNode (right, null);
  }


  public static CTLEFNode createCTLEFNode (CTLNode right)
  {
    return createCTLEFNode (right, null);
  }

  public static CTLAFNode createCTLAFNode (CTLNode right)
  {
    return createCTLAFNode (right, null);
  }

  public static CTLEUNode createCTLEUNode (CTLNode left, CTLNode right)
  {
    return createCTLEUNode (left, right, null);
  }

  public static CTLAUNode createCTLAUNode (CTLNode left, CTLNode right)
  {
    return createCTLAUNode (left, right, null);
  }

  public static CTLEUiNode createCTLEUiNode (CTLNode left, int i,
					     CTLNode right)
  {
    return createCTLEUiNode (left, i, right, null);
  }

  public static CTLAUiNode createCTLAUiNode (CTLNode left, int i,
					     CTLNode right)
  {
    return createCTLAUiNode (left, i, right, null);
  }

  public static CTLEGNode createCTLEGNode (CTLNode right)
  {
    return createCTLEGNode (right, null);
  }

  public static CTLAGNode createCTLAGNode (CTLNode right)
  {
    return createCTLAGNode (right, null);
  }  


  public static CTLAWNode createCTLAWNode (CTLNode left, CTLNode right)
  {
    return createCTLAWNode (left, right, null);
  }

  public static CTLEWNode createCTLEWNode (CTLNode left, CTLNode right)
  {
    return createCTLEWNode (left, right, null);
  }


  public static CTLAndNode createCTLAndNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLAndNode (left, right);
    return (CTLAndNode) makeUnique (ctl);
  }

  public static CTLOrNode createCTLOrNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLOrNode (left, right);
    return (CTLOrNode) makeUnique (ctl);
  }

  public static CTLNegNode createCTLNegNode (CTLNode right)
  {
    CTLAbstractNode ctl = new CTLNegNode (right);
    return (CTLNegNode) makeUnique (ctl);
  }

  public static CTLImplNode createCTLImplNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLImplNode (left, right);
    return (CTLImplNode) makeUnique (ctl);
  }

  public static CTLIffNode createCTLIffNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLIffNode (left, right);
    return (CTLIffNode) makeUnique (ctl);
  }

  public static CTLEXNode createCTLEXNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEXNode (right, fairness);
    return (CTLEXNode) makeUnique (ctl);
  }

  public static CTLAXNode createCTLAXNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLAXNode (right, fairness);
    return (CTLAXNode) makeUnique (ctl);
  }

  public static CTLPreEXNode createCTLPreEXNode (CTLNode right)
  {
    CTLAbstractNode ctl = new CTLPreEXNode (right);
    return (CTLPreEXNode) makeUnique (ctl);
  }

  public static CTLEFNode createCTLEFNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEFNode (right, fairness);
    return (CTLEFNode) makeUnique (ctl);
  }

  public static CTLAFNode createCTLAFNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLAFNode (right, fairness);
    return (CTLAFNode) makeUnique (ctl);
  }

  public static CTLEUNode createCTLEUNode (CTLNode left, CTLNode right,
					   CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEUNode (left, right, fairness);
    return (CTLEUNode) makeUnique (ctl);
  }

  public static CTLAUNode createCTLAUNode (CTLNode left, CTLNode right,
					   CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLAUNode (left, right, fairness);
    return (CTLAUNode) makeUnique (ctl);
  }

  public static CTLEUiNode createCTLEUiNode (CTLNode left, int i,
					     CTLNode right, 
					     CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEUiNode (left, i, right, fairness);
    return (CTLEUiNode) makeUnique (ctl);
  }

  public static CTLAUiNode createCTLAUiNode (CTLNode left, int i,
					     CTLNode right, 
					     CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLAUiNode (left, i, right, fairness);
    return (CTLAUiNode) makeUnique (ctl);
  }

  public static CTLEGNode createCTLEGNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEGNode (right, fairness);
    return (CTLEGNode) makeUnique (ctl);
  }

  public static CTLAGNode createCTLAGNode (CTLNode right, CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLAGNode (right, fairness);
    return (CTLAGNode) makeUnique (ctl);
  }

  public static CTLAtomPropNode createCTLAtomPropNode (String name) 
  {
    CTLAtomPropNode ctl = new CTLAtomPropNode (name);
    return (CTLAtomPropNode) makeUnique (ctl);
  }
  public static CTLPlaceholderNode 
    createCTLPlaceholderNode (String name, CTLAtomPropNode[] props)
  {
    CTLPlaceholderNode node = new CTLPlaceholderNode (name, props);
    return (CTLPlaceholderNode) makeUnique (node);
  }
  

  public static CTLARNode createCTLARNode (CTLNode left, CTLNode right)
  {
    return (CTLARNode) makeUnique (new CTLARNode (left, right));
  }
  public static CTLERNode createCTLERNode (CTLNode left, CTLNode right)
  {
    return (CTLERNode) makeUnique (new CTLERNode (left, right));
  }
	     
  public static CTLConstantNode createCTLConstantNode (AlgebraValue v)
  {
    CTLConstantNode ctl = new CTLConstantNode (v);
    return (CTLConstantNode) makeUnique (ctl);
  }
  
  public static CTLEqualsNode createCTLEqualsNode (CTLNode left,
						   CTLNode right)
  {
    CTLAbstractNode ctl = new CTLEqualsNode (left, right);
    return (CTLEqualsNode) makeUnique (ctl);
  }

  public static CTLUnderNode createCTLUnderNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLUnderNode (left, right);
    return (CTLUnderNode) makeUnique (ctl);
  }

  public static CTLOverNode createCTLOverNode (CTLNode left, CTLNode right)
  {
    CTLAbstractNode ctl = new CTLOverNode (left, right);
    return (CTLOverNode) makeUnique (ctl);
  }

  public static CTLMvSetNode createCTLMvSetNode (MvSet mvset)
  {
    CTLAbstractNode ctl = new CTLMvSetNode (mvset);
    return (CTLMvSetNode) makeUnique (ctl);
  }

  // added
  public static CTLTransitionNode createCTLTransitionNode (MvSet mvset)
  {
    CTLAbstractNode ctl = new CTLTransitionNode(mvset);
    System.out.println("Transition node created: "+mvset);
   return (CTLTransitionNode) makeUnique(ctl);
  }
  
  public static CTLAWNode createCTLAWNode (CTLNode left, CTLNode right,
					   CTLNode[] fairness)
  {
    
    CTLAbstractNode ctl = new CTLAWNode (left, right, fairness);
    return (CTLAWNode) makeUnique (ctl);
  }

  public static CTLEWNode createCTLEWNode (CTLNode left, CTLNode right,
					   CTLNode[] fairness)
  {
    CTLAbstractNode ctl = new CTLEWNode (left, right, fairness);
    return (CTLEWNode) makeUnique (ctl);
  }


  public static void removeCTLNode (CTLNode ctl)
  {
    uniqueTable.remove (ctl);
  }

  /**
   ** Make sure that a CTLNode is unique.
   **/
  private static CTLNode makeUnique (CTLNode ctl)
  {    
    CTLNode answer = (CTLNode)uniqueTable.get (ctl);
    
    if (answer == null)
      {
	//System.out.println("MakeUnique creating node:"+ctl);
	
	answer = ctl;
	uniqueTable.put (answer, answer);
      }
    return answer;
  }
  
  // restart the uniqueTable
  public static void renew ()
  {
    uniqueTable = new HashMap ();
  }
  

  /**
   ** Creates a CTL node that is the same type as the original and
   ** has the specified children.
   **/
  public static CTLNode alterChildren (CTLNode psi, CTLNode left,
				       CTLNode right)
  {
    // make a copy of a node
    CTLAbstractNode phi = (CTLAbstractNode)makeSameNode (psi);
    
    // change the children
    phi.setLeft (left);
    phi.setRight (right);
    return makeUnique (phi);
  }

  
  /**
   ** This is essentially same as cloning, but it is not public, and
   ** only works for certain nodes.
   ** This does not return unique CTL nodes, so makeUnique must
   ** be called on the result!
   **/
  private static CTLNode makeSameNode (CTLNode psi)
  {
    // XXX This is just shallow clone, why not merge it into 
    // XXX appropriate nodes?!
    CTLAbstractNode result;

    // we only ever need to deal with non-leaf nodes
    if (psi instanceof CTLNegNode)
      result = new CTLNegNode (psi.getRight ());
    else if (psi instanceof CTLAndNode)
      result = new CTLAndNode (psi.getLeft (), psi.getRight ());
    else if (psi instanceof CTLOrNode)
      result = new CTLOrNode (psi.getLeft (), psi.getRight ());
    else if (psi instanceof CTLAXNode)
      result = new CTLAXNode (psi.getRight (), psi.getFairness ());
    else if (psi instanceof CTLAUNode)
      result = new CTLAUNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLARNode)
      result = new CTLARNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLAGNode)
      result = new CTLAGNode (psi.getRight (), psi.getFairness ());
    else if (psi instanceof CTLEXNode)
      result = new CTLEXNode (psi.getRight (), psi.getFairness ());
    // NB: The next two must appear in this order, as the former is a
    // subclass of the latter.
    else if (psi instanceof CTLEUiNode)
      result = new CTLEUiNode (psi.getLeft (), 
			       ((CTLEUiNode)psi).getI (), psi.getRight (),
			       psi.getFairness ());
    else if (psi instanceof CTLEUNode)
      result = new CTLEUNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLARNode)
      result = new CTLARNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLERNode)
      result = new CTLERNode (psi.getLeft (), psi.getRight (),
			      psi.getFairness ());
    else if (psi instanceof CTLAWNode)
      result = new CTLAWNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLEWNode)
      result = new CTLEWNode (psi.getLeft (), psi.getRight (), 
			      psi.getFairness ());
    else if (psi instanceof CTLEGNode)
      result = new CTLEGNode (psi.getRight (), psi.getFairness ());
    else if (psi instanceof CTLEqualsNode)
      result = new CTLEqualsNode (psi.getLeft (), psi.getRight ());
    else if (psi instanceof CTLOverNode)
      result = new CTLOverNode (psi.getLeft (), psi.getRight ());
    else if (psi instanceof CTLUnderNode)
      result = new CTLUnderNode (psi.getLeft (), psi.getRight ());
    // apparently it's a leaf node
    else result = (CTLAbstractNode)psi;

    // XXX Note this is a private function so we do not
    // XXX need to call makeUnique
    return result;
  }
 
}

