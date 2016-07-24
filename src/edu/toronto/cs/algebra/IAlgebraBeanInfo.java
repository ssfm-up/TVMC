package edu.toronto.cs.algebra;

import java.beans.*;

public class IAlgebraBeanInfo extends SimpleBeanInfo 
{
  public IAlgebraBeanInfo ()
  {
  }
  
  public BeanDescriptor getBeanDescriptor ()
  {
    BeanDescriptor beanDescriptor = new BeanDescriptor (IAlgebra.class);

    beanDescriptor.setValue 
      ("persistenceDelegate", 
       new PersistenceDelegate ()
       {
	 protected Expression instantiate (Object oldInstance, 
					   Encoder out)
	 {
	   IAlgebra algebra = (IAlgebra)oldInstance;
	   String tag = AlgebraCatalog.getAlgebraTag (algebra);
	   return new Expression (oldInstance, AlgebraCatalog.class, 
				  "getAlgebra", 
				  new String[]{tag});
	 }
       });
    return beanDescriptor;
  }
  
}
