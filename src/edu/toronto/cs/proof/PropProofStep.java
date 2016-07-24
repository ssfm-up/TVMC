package edu.toronto.cs.proof;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import java.util.*;

// a negation of a *temporal* formula: negated propositional
//  formulae are handled as a PropProofStep
public class PropProofStep extends ProofStep
{
  public PropProofStep (AlgebraValue _v, MvSet _state, String _stateName,
			CTLNode _consequent)
  {
    super(_v, _state, _stateName, _consequent);

    // -- atomic proof steps don't have to be dischared or unfolded
    discharged = true;
    unfolded = true;
  }
  
  public boolean discharge () 
  {
    return true;
  }
  
  public boolean unfold ()
  {
    return true;
  }
  
  public Object accept (ProofVisitor pv, Object info)
  {
    return pv.visitPropStep (this, info);
  }
  
}
