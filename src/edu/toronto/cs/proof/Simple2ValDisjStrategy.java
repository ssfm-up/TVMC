package edu.toronto.cs.proof;

import java.util.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;

public class Simple2ValDisjStrategy implements DisjProofStrategy
{
  public BitSet choosePaths(AlgebraValue[] vals, CTLNode[] props)
  {
    
    BitSet paths = new BitSet(props.length);
    AlgebraValue top = vals[0].getParentAlgebra().top();
    
    for (int i=0; i<vals.length; i++)
      if (vals[i].equals(top))
	{ paths.set(i);

	
	}
    
    int fst = 0;
    
    // look for first T node (guaranteed at least one,
    //   otherwise this method is never called)
    while (!paths.get(fst))
      fst++;
    

    
    if ((props[fst] instanceof CTLMvSetNode) ||
	(props[fst] instanceof CTLAtomPropNode)
	|| (props[fst] instanceof CTLConstantNode))
      	System.out.println("First disjunct is good\n");
	
    else 
      {
	// otherwise, search for one
	int k = fst+1;
	while (k < props.length)
	  {
	    if (paths.get(k) && (props[k] instanceof CTLMvSetNode || 
				 props[k] instanceof CTLAtomPropNode || 
				 props[k] instanceof CTLConstantNode))
	      {
		for (int j=k+1; j<props.length; j++)
		  paths.clear(j);
		paths.clear(fst);
		return paths;
	      }
	    
	    // not a match, so clear it and keep searching
	    paths.clear(k++);
	    
	  } // endwhile
      } // end else
     
   
    for (int j=fst+1; j<props.length; j++)
      paths.clear(j);
    return paths;
	
  }
  
  
}
