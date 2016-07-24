package edu.toronto.cs.proof2;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;

public class DepthProofRule extends AbstractProofRule 
{
  

  public DepthProofRule(MvSetModelChecker mc)
  {
    super (mc);
  }

  public Formula[] apply (Formula f)
  {

    
    // error checking from CTLNode$AndOrProofRule
    // probably needs to be finer
    if (!(f instanceof BelowFormula || 
	  f instanceof AboveFormula)) return null;
    
    if (f.getConsequent ().getClass () != CTLAndNode.class &&
	f.getConsequent ().getClass () != CTLOrNode.class)
      return null;
    
    MvSet state = f.getState();
    CTLNode left = f.getConsequent ().getLeft ();
    CTLNode right = f.getConsequent ().getRight ();
    
    AlgebraValue lvalue = mc.checkCTL(left, state).getValue();
    AlgebraValue rvalue = mc.checkCTL(right, state).getValue();
    AlgebraValue v = f.getValue();
    if (f instanceof BelowFormula)
      System.err.println("DPR: trying to apply ("+lvalue+" op "+
			 rvalue+") <= "+v+"\n");    
    if (f.getConsequent().getClass() == CTLAndNode.class)
      System.err.println("is an AND node "+left+" && "+right);
    
    // a /\ b <= v holds if *either* a or b is below v,
    //  so if both are then below v, pick the shallowest one
    if ((f instanceof BelowFormula) && (f.getConsequent().getClass() == CTLAndNode.class)
	&& (lvalue.leq(v).isTop()) && (rvalue.leq(v).isTop()))
      {

	System.err.println("Using depth rule");
	DepthVisitor dv = new DepthVisitor();
	int d = dv.ctlDepth(left);
	CTLNode chosen = right;
	
	if (d <= dv.ctlDepth(right))
	  chosen = left;
	
	System.err.println("Applicable: adding "+chosen+" <= "+v);	  
	Formula[] subGoals = new Formula [1];
	subGoals [0] = 
	  Formula.duplicate (f, chosen, 
			     mc.checkCTL (chosen, state).getValue (), state);
	return subGoals;
	
      }

    if ((f instanceof AboveFormula) && (f.getConsequent().getClass() == CTLOrNode.class)
	&& (lvalue.geq(v).isTop()) && (rvalue.geq(v).isTop())) {
	System.err.println("Using depth rule for \\/");
	DepthVisitor dv = new DepthVisitor();
	int d = dv.ctlDepth(left);
	CTLNode chosen = right;
	
	if (d <= dv.ctlDepth(right))
	  chosen = left;
	
	System.err.println("Applicable: adding "+chosen+" >= "+v);	  
	Formula[] subGoals = new Formula [1];
	subGoals [0] = 
	  Formula.duplicate (f, chosen, 
			     mc.checkCTL (chosen, state).getValue (), state);
	return subGoals;
      
      
    }
    
    else
      return null;
    
    
    
  }
  
}

