package edu.toronto.cs.ctl;

import java.util.*;

/**
 ** This is an abstract CTL node.
 **/
public interface CTLNode
{

  /**
   ** Retrieves the left child of the CTLNode.
   **
   ** @return left child.
   **/
  CTLNode getLeft ();

  /**
   ** Retrieves the right child of the CTLNode.
   **
   ** @return right child.
   **/
  CTLNode getRight ();

  /**
   ** Retrieves the fairness of the CTLNode.
   **
   ** @return fairness conditions for this CTL.
   **/
  CTLNode[] getFairness ();
    
  /**
   ** CTL operation - conjunction.
   **/
  CTLAndNode and (CTLNode psi);

  /**
   ** CTL operation - disjunction.
   **/
  CTLOrNode or (CTLNode psi);

  /**
   ** CTL operation - negation.
   **/
  CTLNegNode neg ();

  /**
   ** CTL operation - implication.
   **/
  CTLImplNode implies (CTLNode psi);

  /**
   ** CTL operation - backward implication.
   **/
  CTLImplNode impliedBy (CTLNode psi);

  /**
   ** CTL operation - equivalence.
   **/
  CTLIffNode iff (CTLNode psi);

  /**
   ** CTL operation - EX.
   **/
  CTLEXNode ex ();

  CTLPreEXNode preEX ();

  /**
   ** CTL operation - AX.
   **/
  CTLAXNode ax ();

  /**
   ** CTL operation - EF.
   **/
  CTLEFNode ef ();

  /**
   ** CTL operation - AF.
   **/
  CTLAFNode af ();
  CTLAFNode af (CTLNode[] fairness);

  /**
   ** CTL operation - EU ( E[this U psi] ).
   **/
  CTLEUNode eu (CTLNode psi);

  /**
   ** CTL operation - bounded EU ( E[this Ui psi] ).
   **/
  CTLEUiNode eu (int i, CTLNode psi);

  /**
   ** CTL operation - AU ( A[this U psi] ).
   **/
  CTLAUNode au (CTLNode psi);

  /**
   ** CTL operation - bounded AU ( A[this Ui psi] ).
   **/
  CTLAUiNode au (int i, CTLNode psi);

  /**
   ** CTL Release node A [this R psi ]
   **/
  CTLARNode ar (CTLNode psi);

  CTLERNode er (CTLNode psi);

  CTLAWNode aw (CTLNode psi);
  CTLEWNode ew (CTLNode psi);

  Object accept (CTLVisitor v, Object stateinfo);
  

  /**
   ** CTL operation - EG.
   **/
  CTLEGNode eg ();

  CTLEGNode eg (CTLNode[] fairness);

  /**
   ** CTL operation - AG.
   **/
  CTLAGNode ag ();
  CTLAGNode ag (CTLNode[] fairness);

  /**
   ** CTL operation - equals
   **/
  CTLEqualsNode eq (CTLNode psi);

  /**
   ** CTL operation - under
   **/
  CTLUnderNode under (CTLNode psi);

  /**
   ** CTL operation - over
   **/
  CTLOverNode over (CTLNode psi);

  /**
   ** This is used to determine uniqueness.
   **/
  //String toFairString ();
 
  // -- hashCode as returned by java.lang.Object
  //int objectHashCode ();
  
}


