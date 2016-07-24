package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

/** This class is the ancestor of all proof-step objects.
 * By a proof-step we mean a backward application of <i>one</i> proof rule
 * to a consequent that yields a set of antecedents (new, and
 * hopefully simpler, proof obligations): for
 * instance, applying the AND-elimination rule to (<i>p AND q</i>)
 * to get the separate proof obligations <i>p</i> and <i>q</i>;
 * each of which must in turn be expanded until no further expansion
 * is possible.
 *
 * Known subclasses:
 *  @see edu.toronto.cs.proof2.TreeProofStep
 */
public abstract class ProofStep
{
  // formula to represent a proofstep
  Formula formula;
  
  // Every ProofStep has a parent
  ProofStep parent;
    

  protected ProofStep (Formula _formula, ProofStep _parent)
  {
    formula = _formula;
    parent = _parent;
  }
  
  
  // function to return the formula
  public Formula getFormula() 
  {
    return formula;
  }
  
  // function to return the parent
  public ProofStep getParent()
  {
    return parent;
  }


  public String toString ()
  {
    return formula.toString ();
  }
  

  public ProofStep newProofStep (Formula f)
  {
    if (f instanceof ConstantFormula)
      return new LeafProofStep (f, this);
    else
      return new TreeProofStep (f, this);
  }

  public int getChildLength ()
  {
    return 0;
  }
  
}

  
