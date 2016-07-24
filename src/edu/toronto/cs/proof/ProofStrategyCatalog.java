package edu.toronto.cs.proof;


import java.util.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;


// strategies are organized by
//  -- algebra: different strategies for different algebras
//  -- proof-step type (so, by CTLNode);
public abstract class ProofStrategyCatalog
{
  static Map byAlgebra = new HashMap();
  static Map byStepType = new HashMap();
  static Map byName = new HashMap();
  
  public static void registerStrategy(String tag,
				      IAlgebra alg,
				      Class clazz)
  {
  }
  
  static 
  {
    try {
      
    registerStrategy ("",
		      AlgebraCatalog.getAlgebra("2"),
		      Class.forName("edu.toronto.cs.ctl.CTLOrNode"));
    }
    catch (ClassNotFoundException cnfe) {
      throw new RuntimeException("Warning: CTL library is not in your path");
    }
    
  }
  

  public static ProofStrategy getStrategy(String tag)
  {
    return (ProofStrategy) byName.get(tag);
    
  }
  
  public static Iterator iterator(CTLNode type) 
  {
    return  ((Set) byStepType.get(type)).iterator();
    
  }
  

}
