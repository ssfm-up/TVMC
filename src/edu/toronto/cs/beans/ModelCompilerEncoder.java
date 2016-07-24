package edu.toronto.cs.beans;

import java.io.*;
import java.util.*;
import java.beans.*;

import edu.toronto.cs.algebra.*;

public class ModelCompilerEncoder extends XMLEncoder
{
  public ModelCompilerEncoder (OutputStream out)
  {
    super (out);
    PersistenceDelegate delegate =   
      new PersistenceDelegate ()
      {
	protected Expression instantiate (Object oldInstance, 
					  Encoder encoder)
	{
	  IAlgebra algebra = (IAlgebra)oldInstance;
	  String tag = AlgebraCatalog.getAlgebraTag (algebra);
	  return new Expression (oldInstance, AlgebraCatalog.class, 
				 "getAlgebra", 
				  new String[]{tag});
	}
      };
    
    for (Iterator it = AlgebraCatalog.iterator (); it.hasNext (); )
      setPersistenceDelegate (it.next ().getClass (), delegate);
  }
  
}
