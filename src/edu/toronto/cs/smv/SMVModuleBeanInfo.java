package edu.toronto.cs.smv;

import java.beans.*;
import java.util.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.beans.*;
import edu.toronto.cs.beans.editors.*;

public class SMVModuleBeanInfo extends SimpleBeanInfo
{
  public SMVModuleBeanInfo ()
  {
  }
  
  public BeanDescriptor getBeanDescriptor () 
  {
    BeanDescriptor descriptor = new BeanDescriptor (SMVModule.class);
    descriptor.setDisplayName ("SMV Model Compiler.");
    return descriptor;
  }
  
  public PropertyDescriptor [] getPropertyDescriptors () 
  {
     
    try {
      List props = new ArrayList ();
      
      PropertyDescriptor prop;
      
      prop = new PropertyDescriptor ("algebra", SMVModule.class);
      prop.setDisplayName ("Model Checking Algebra");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE, 
		     "An algebra used to encode the model");
      
      props.add (prop);
  
      prop = new PropertyDescriptor ("mvSetFactoryClass", SMVModule.class);
      prop.setDisplayName ("MvSet Implementation");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE,
		     "MvSet implementation to compile the model into");
      prop.setPropertyEditorClass (MvSetFactoryClassEditor.class);
      props.add (prop);
      // -- more properties
      
      
      return (PropertyDescriptor[])
	props.toArray (new PropertyDescriptor [props.size ()]);
    }
    catch (IntrospectionException e)
      {
	assert false : e;
      }
    
    return null;
  }
}
