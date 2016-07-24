package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public interface ProofRule 
{

  // takes a formula and returns proofstep when a formula matches a 
  // given proofrule.
  
  public Formula[] apply (Formula formula);
  
}


    
    
    
