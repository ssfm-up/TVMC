package edu.toronto.cs.proof;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import java.util.*;

public class ConjProofStep extends ProofStep
{
  protected ConjProofStep(AlgebraValue _v, MvSet _state, String _stateName,
			  CTLNode _consequent)
  {
    super(_v, _state, _stateName, _consequent);
  }

  public boolean unfold ()
  {
    AlgebraValue vl;
    AlgebraValue vr;
    BitSet tbs;

    MvSetModelChecker mc = ProofStepFactory.getMC();

    
    // -- restrict current state-set to a single state
    makeSingleState ();

    // -- get the value of the left-hand operator
    vl = mc.checkCTL (consequent.getLeft (), state).getValue ();

    // -- get the value of the right-hand operator
    vr = mc.checkCTL (consequent.getRight (), state).getValue ();


    // -- expand left-hand side
    antecedents.add (ProofStepFactory.makeProofStep(vl,
						    state,
						    consequent.getLeft()));

    // -- expand right-hand side
    antecedents.add (ProofStepFactory.makeProofStep(vr,
						    state,
						    consequent.getRight()));
    unfolded = true;
    return true;
  }
  
  public Object accept (ProofVisitor pv, Object info)
  {
    return pv.visitAndStep (this,  info);
  }
  
}

