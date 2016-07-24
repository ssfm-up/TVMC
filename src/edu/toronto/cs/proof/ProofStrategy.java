package edu.toronto.cs.proof;
import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;

public interface ProofStrategy
{
  public BitSet choosePaths(AlgebraValue[] vals, CTLNode[] props);  
  
}
