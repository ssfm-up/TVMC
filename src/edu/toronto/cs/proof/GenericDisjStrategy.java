package edu.toronto.cs.proof;

import java.util.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;

public class GenericDisjStrategy implements DisjProofStrategy
{
  IAlgebra alg;
  
  public GenericDisjStrategy(IAlgebra _alg)
  {
    alg = _alg;
  }
  
  public BitSet choosePaths(AlgebraValue[] vals, CTLNode[] props)
  {
	  //System.out.println("choosing..." + Arrays.toString(vals));
    // result set
    BitSet paths = new BitSet(props.length);
    
    // turn vals[] into a BitSet of size of the parent algebra
    BitSet bval = new BitSet(alg.size());
    int vl = vals.length;
    for (int i=0; i<vl; i++)
      bval.set(vals[i].getId());

    Set irr = alg.getJoinIrredundant(bval);
    
    // use only the irredundant
    for (int i=0; i<vl; i++) {
      if (irr.contains(vals[i])) {
    	  paths.set(i);
    	  break;
      }
    }
    
   // System.out.println(paths);
    return paths;
  }
  
}
