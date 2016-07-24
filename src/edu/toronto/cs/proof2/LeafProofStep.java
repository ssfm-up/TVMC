//represents a class where proofstep has no further child.

package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class LeafProofStep extends ProofStep 
{
  protected LeafProofStep (Formula _formula, ProofStep _parent)
  {
    super ( _formula, _parent);
  }
}

