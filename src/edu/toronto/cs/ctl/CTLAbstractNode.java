package edu.toronto.cs.ctl;

import java.util.*;

/**
 ** This is an abstract CTL node.
 **/
public abstract class CTLAbstractNode implements CTLNode
{

  public static final CTLNode[] EMPTY_ARRAY = new CTLNode [0];
  // -- left and right children
  //protected CTLNode right;
  //protected CTLNode left;
  
  // -- fairness condition
  

  /**
   ** Create an CTLAbstractNode with no children.
   **/
  protected CTLAbstractNode ()
  {
  }

  /**
   ** Create an CTLAbstractNode with one child.
   **/
//   protected CTLAbstractNode (CTLNode onlychild)
//   {
//     left = null;
//     right = onlychild;
//   }

  /**
   ** Create an CTLAbstractNode with two children.
   **/
//   protected CTLAbstractNode (CTLNode l, CTLNode r)
//   {
//     left = l;
//     right = r;
//   }

  /**
   ** Retrieves the fairness of the CTLNode.
   **
   ** @return fairness for this CTL.
   **/
  public CTLNode[] getFairness ()
  {
    return EMPTY_ARRAY;
  }

  /**
   ** Retrieves the left child of the CTLNode.
   **
   ** @return left child.
   **/
  public abstract CTLNode getLeft ();
  /**
   ** Retrieves the right child of the CTLNode.
   **
   ** @return right child.
   **/
  public abstract CTLNode getRight ();

    
  /**
   ** Changes the left child of the CTLNode.
   **/
  protected abstract void setLeft (CTLNode l);

  /**
   ** Changes the right child of the CTLNode.
   **/
  protected abstract void setRight (CTLNode r);

  /**
   ** CTL operation - conjunction.
   **/
  public CTLAndNode and (CTLNode psi)
  {
    return CTLFactory.createCTLAndNode (this, psi);
  }

  /**
   ** CTL operation - disjunction.
   **/
  public CTLOrNode or (CTLNode psi)
  {
    return CTLFactory.createCTLOrNode (this, psi);
  }

  /**
   ** CTL operation - negation.
   **/
  public CTLNegNode neg ()
  {
    return CTLFactory.createCTLNegNode (this);
  }

  /**
   ** CTL operation - implication.
   **/
  public CTLImplNode implies (CTLNode psi)
  {
    return CTLFactory.createCTLImplNode (this, psi);
  }

  /**
   ** CTL operation - backward implication.
   **/
  public CTLImplNode impliedBy (CTLNode psi)
  {
    return CTLFactory.createCTLImplNode (psi, this);
  }

  /**
   ** CTL operation - equivalence.
   **/
  public CTLIffNode iff (CTLNode psi)
  {
    return CTLFactory.createCTLIffNode (this, psi);
  }

  /**
   ** CTL operation - EX.
   **/
  public CTLEXNode ex ()
  {
    return CTLFactory.createCTLEXNode (this);
  }
  
  public CTLEXNode ex (CTLNode[] fairness)
  {
    return CTLFactory.createCTLEXNode (this, fairness);
  }

  public CTLPreEXNode preEX ()
  {
    return CTLFactory.createCTLPreEXNode (this);
  }
  

  /**
   ** CTL operation - AX.
   **/
  public CTLAXNode ax ()
  {
    return CTLFactory.createCTLAXNode (this);
  }

  /**
   ** CTL operation - EF.
   **/
  public CTLEFNode ef ()
  {
    return CTLFactory.createCTLEFNode (this);
  }
  
  public CTLEFNode ef (CTLNode[] fairness)
  {
    return CTLFactory.createCTLEFNode (this, fairness);
  }

  /**
   ** CTL operation - AF.
   **/
  public CTLAFNode af ()
  {
    return CTLFactory.createCTLAFNode (this);
  }
  public CTLAFNode af (CTLNode[] fairness)
  {
    return CTLFactory.createCTLAFNode (this, fairness);
  }
  

  /**
   ** CTL operation - EU ( E[this U psi] ).
   **/
  public CTLEUNode eu (CTLNode psi)
  {
    return CTLFactory.createCTLEUNode (this, psi);
  }
  
  public CTLEUNode eu (CTLNode psi, CTLNode[] fairness)
  {
    return CTLFactory.createCTLEUNode (this, psi, fairness);
  }

  /**
   ** CTL operation - bounded EU ( E[this Ui psi] ).
   **/
  public CTLEUiNode eu (int i, CTLNode psi)
  {
  
    return CTLFactory.createCTLEUiNode (this, i, psi);
  }

  /**
   ** CTL operation - AU ( A[this U psi] ).
   **/
  public CTLAUNode au (CTLNode psi)
  {
    return CTLFactory.createCTLAUNode (this, psi);
  }

  /**
   ** CTL operation - bounded AU ( A[this Ui psi] ).
   **/
  public CTLAUiNode au (int i, CTLNode psi)
  {
    return CTLFactory.createCTLAUiNode (this, i, psi);
  }

  public CTLARNode ar (CTLNode psi)
  {
    return CTLFactory.createCTLARNode (this, psi);
  }
  
  public CTLERNode er (CTLNode psi)
  {
    return CTLFactory.createCTLERNode (this, psi);
  }
  
  public CTLAWNode aw (CTLNode psi)
  {
    return CTLFactory.createCTLAWNode (this, psi);
  }
  public CTLEWNode ew (CTLNode psi)
  {
    return CTLFactory.createCTLEWNode (this, psi);
  }
  

  /**
   ** CTL operation - EG.
   **/
  public CTLEGNode eg ()
  {
    return CTLFactory.createCTLEGNode (this);
  }

  public CTLEGNode eg (CTLNode[] fairness)
  {
    return CTLFactory.createCTLEGNode (this, fairness);
  }
  

  /**
   ** CTL operation - AG.
   **/
  public CTLAGNode ag ()
  {
    return CTLFactory.createCTLAGNode (this);
  }

  public CTLAGNode ag (CTLNode[] fairness)
  {
    return CTLFactory.createCTLAGNode (this, fairness);
  }
  

  /**
   ** CTL operation - equals
   **/
  public CTLEqualsNode eq (CTLNode psi)
  {
    return CTLFactory.createCTLEqualsNode (this, psi);
  }

  /**
   ** CTL operation - under
   **/
  public CTLUnderNode under (CTLNode psi)
  {
    return CTLFactory.createCTLUnderNode (this, psi);
  }

  /**
   ** CTL operation - over
   **/
  public CTLOverNode over (CTLNode psi)
  {
    return CTLFactory.createCTLOverNode (this, psi);
  }

  /**
   ** Overrides the Object's equals method.
   **/
  public boolean equals (Object o)
  {
    return this == o;
  }


  public Object accept (CTLVisitor v, Object stateinfo)
  {
    return v.visitAbstractNode (this, stateinfo);
  }  

  public int objectHashCode ()
  {
    return super.hashCode ();
  }
  

  // -- returns toString representation of node
  // -- adding brackets if the node is not a leaf node
  public static String toString (CTLNode node, boolean optBrackets)
  {
    if (optBrackets && node instanceof CTLUnaryNode)
      return "(" + node + ")";

    return node.toString ();
  }
  
}

