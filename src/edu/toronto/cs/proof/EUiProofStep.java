package edu.toronto.cs.proof;

import de.upb.agw.util.CTLParser;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import java.util.*;

public class EUiProofStep extends ProofStep
{
  int bound;

  public EUiProofStep(AlgebraValue _v,
			MvSet _state,
			String _stateName,
		      CTLNode _consequent,
		      int _bound)
  {
    super(_v, _state, _stateName, _consequent);
    bound = _bound;
  }
  
  // E [ phi U0 psi ](s) = psi(s)
  // E [ phi Ui psi ](s) = psi(s) \/ phi(s) /\ EX E [phi U(i-1) psi]
  public boolean unfold()
  {
  
    MvSetFactory fac = state.getFactory ();
    IAlgebra alg = state.getAlgebra ();
    MvSetModelChecker mc = ProofStepFactory.getMC ();

    if (bound == 0)
      antecedents.add (ProofStepFactory.makeProofStep(v,
						      state,
						      consequent.getRight()));
    else // bound > 0
      {
	// add phi
	CTLNode exp = 
	  consequent.getRight().
	  or (consequent.getLeft().
	      and(consequent.getLeft().
		  eu(bound-1, consequent.getRight()).ex()));
  
	antecedents.add(ProofStepFactory.makeProofStep(v, state, exp));
      }
    
    unfolded = true;
    return true;
    
  }

  public Object accept(ProofVisitor pv, Object info)
  {
    return pv.visitEUiStep(this,  info);
  }  
}

