package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;


public class TreeProofStep extends ProofStep
{
  // children[] represents subProofStep arising from a proofstep
  ProofStep[] children;

  // -- rule that was used to expand this proof step
  ProofRule rule;
  
  public TreeProofStep (Formula _formula, ProofStep _parent)
  {
    super (_formula, _parent);
    children = null;
  }
  
  // function to get children
  public ProofStep[] getChildren ()
  {
    return children;
  }

  public Formula[] getChildrenFormula ()
  {
    List f = new ArrayList ();
    for (int i =0; i< children.length ; i++)
      f.add (children [i].getFormula ());
    
    return (Formula []) f.toArray (new Formula [f.size ()]);
    
  }
  
  // function to get ith child
  public ProofStep getChild (int i)
  {
    return children [i];
  }
  
  // function to get the child length
  public int getChildLength ()
  {
    if (children == null)
      return 0;
    else
      return children.length;
  }
  
  // function to set children
  public void setChildren (ProofStep[] _children)
  {
    children = _children;
  }

  public ProofRule getProofRule ()
  {
    return rule;
  }
  public void setProofRule (ProofRule v)
  {
    rule = v;
  }
  
  
  
   
}
