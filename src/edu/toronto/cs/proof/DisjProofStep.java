package edu.toronto.cs.proof;

import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;

public class DisjProofStep extends ProofStep
{
  
  DisjProofStrategy strategy;
  
  public DisjProofStep (AlgebraValue _v, MvSet _state, String stateName,
		       CTLNode _consequent, DisjProofStrategy _strategy)
  {
    super (_v, _state, stateName, _consequent);
    strategy = _strategy;
  }
  
  
  public boolean unfold()
  {
    CTLNode[] props = new CTLNode [2];
    AlgebraValue[] vals = new AlgebraValue [2];
    MvSetModelChecker mc = ProofStepFactory.getMC ();

    BitSet tbs; // temporary bit-set
    IAlgebra alg = v.getParentAlgebra ();
    

    // -- turn current restriction into a single state
    makeSingleState ();

    // -- evaluate left-hand and rigt-hand parts of the disjunction
    int i=0;    
    props [0] = consequent.getLeft();
    vals [0] = mc.checkCTL (props [0], state).getValue ();
    
    props [1] = consequent.getRight();
    vals [1] = mc.checkCTL (props [1], state).getValue ();
    

    // -- make sure that we are not trying to construct a non-existent proof
    if (!alg.bot ().equals (getValue ()))
      {
	tbs = strategy.choosePaths (vals, props);

	for (i = 0; i < tbs.length (); i++)
	  if (tbs.get (i))
	    antecedents.add (ProofStepFactory.makeProofStep (vals [i],
							     state,
							     props [i]));
      }
    
    unfolded = true;
    return true;
  }

  public Object accept(ProofVisitor pv, Object info)
  {
    return pv.visitOrStep(this,  info);
  }
  
  
}

