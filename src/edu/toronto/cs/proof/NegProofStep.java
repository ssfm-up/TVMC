package edu.toronto.cs.proof;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import java.util.*;

// a negation of a *temporal* formula: negated propositional
//  formulae are handled as a PropProofStep
public class NegProofStep extends ProofStep
{
  

  public NegProofStep(AlgebraValue _v,
			MvSet _state,
			String _stateName,
		      CTLNode _consequent)
  {
    super(_v, _state, _stateName, _consequent);
   
  }
  
  public boolean unfold()
  {
    antecedents.add (ProofStepFactory.makeProofStep(v.neg(),
						    state,
						    consequent.getRight()));
    unfolded = true;
    return true;
  }

  

  public Object accept (ProofVisitor pv, Object info)
  {
    return pv.visitNegStep(this, info);
  }
  
}
