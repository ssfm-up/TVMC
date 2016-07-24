package edu.toronto.cs.proof;


import java.util.*;

import de.upb.agw.util.CTLParser;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;

public class EGProofStep extends ProofStep 
{
  protected EGProofStep(AlgebraValue _v,
			MvSet _state,
			String _stateName,
			CTLNode _consequent)
  {
    super(_v, _state, _stateName, _consequent);
  }

  public boolean unfold()
  {
    CTLNode ctlr = consequent.getRight ();
    CTLMvSetNode s = CTLFactory.createCTLMvSetNode (state);
    s.setName (ProofStepFactory.makeStateName (state));
    //CTLMvSetNode nots = CTLFactory.createCTLMvSetNode (state.not());
    CTLNode nots = s.neg ();
    CTLNode[] fairness = consequent.getFairness();
    CTLNode exp;
    if(fairness.equals(CTLAbstractNode.EMPTY_ARRAY)) {
    	exp = ctlr.and (ctlr.eu (s).ex().or (ctlr.and (nots).eg ().ex ()));
    }
    else {
    	/*CTLNode right = ctlr.and(nots).eg(fairness).ex();
    	CTLNode left = ctlr.eu(s).ex();
    	for(CTLNode constraint : fairness) {
    		left = ctlr.eu(constraint.and(left)).ex();
    	}
    	exp = ctlr.and (left.or(right));*/
    	CTLNode right = ctlr.and(ctlr.and(nots).eg(fairness).ex());
    	CTLNode leftinner = ctlr.eu(ctlr.and(s)).ex();
    	for(CTLNode constraint : fairness) {
    		leftinner = ctlr.eu(ctlr.and(constraint).and(leftinner)).ex();
    	}
    	CTLNode left = ctlr.and(leftinner);
    	exp = left.or(right);
    }   
    
    
    antecedents.add(ProofStepFactory.makeProofStep(v, state, exp));
    
    unfolded = true;
    return unfolded;
  }
  
  
  public Object accept (ProofVisitor pv, Object info)
  {
    return pv.visitEGStep(this,  info);
  }


}
