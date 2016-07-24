package edu.toronto.cs.proof;

import de.upb.agw.util.CTLParser;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import java.util.*;

public class EUProofStep extends ProofStep
{
  

  public EUProofStep(AlgebraValue _v,
			MvSet _state,
			String _stateName,
		      CTLNode _consequent)
  {
    super(_v, _state, _stateName, _consequent);
  }
  
  // this is unfolded into an EUi proof step..
  // but first we have to find i!
  public boolean unfold()
  {
    MvSetFactory fac = state.getFactory ();
    IAlgebra alg = state.getAlgebra ();
    if(consequent.getFairness().equals(CTLAbstractNode.EMPTY_ARRAY)) {
	    MvSetModelChecker mc = ProofStepFactory.getMC ();
	
	    //AlgebraValue[] stateEnv;
	    AlgebraValue res;
	    
	    //stateEnv = getStateAsArray ();
	    makeSingleState ();
	    
	    int i = -1; // start i=0 and increment until reached
	    do 
	      {
		i++;
		// model-check EUi
		CTLNode exp = consequent.getLeft ().eu(i, consequent.getRight ());
	res = 
		  mc.checkCTL (exp,
			       state).getValue ();
	      }
	    while (!v.equals (res));
	
	    CTLNode exp = consequent.getLeft().
	    eu(i, consequent.getRight());
	    antecedents.add 
	      (ProofStepFactory.makeProofStep(v,
					      state,
					      exp));
    }
    else {
    	CTLNode top = CTLFactory.createCTLConstantNode(alg.top());
		CTLNode exp = consequent.getLeft().eu(consequent.getRight().and(top.eg(consequent.getFairness())));
		antecedents.add(ProofStepFactory.makeProofStep(getValue(), state, exp));
    }
    unfolded = true;
    return true;
  }


  public Object accept(ProofVisitor pv, Object info)
  {
    return pv.visitEUStep(this,  info);
  }
  
}

