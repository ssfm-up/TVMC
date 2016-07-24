package edu.toronto.cs.proof2;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public abstract class AbstractProofRule implements ProofRule
{
  MvSetModelChecker mc;
  IAlgebra algebra;
  MvSetFactory mvSetFactory;
  
  AlgebraValue top;
  AlgebraValue bot;
  
  MvSet topMvSet;
  MvSet botMvSet;
  

  XKripkeStructure xkripke;
  

  public AbstractProofRule (MvSetModelChecker _mc)
  {
    mc = _mc;
    xkripke = mc.getXKripke ();
    algebra = xkripke.getAlgebra ();
    
    top = algebra.top ();
    bot = algebra.bot ();
    
    mvSetFactory = xkripke.getMvSetFactory ();
    topMvSet = mvSetFactory.top ();
    botMvSet = mvSetFactory.bot ();
  }
  
  public abstract Formula[] apply (Formula f);

  public static Formula[] toArray (Formula f)
  {
    if (f == null) return null;
    return new Formula[] { f };
  }
}
