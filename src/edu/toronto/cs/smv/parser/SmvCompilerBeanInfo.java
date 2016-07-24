package edu.toronto.cs.smv.parser;

import java.beans.*;
import java.util.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.beans.*;
import edu.toronto.cs.beans.editors.*;

public class SmvCompilerBeanInfo extends SimpleBeanInfo
{
  public SmvCompilerBeanInfo ()
  {
  }
  
  public BeanDescriptor getBeanDescriptor () 
  {
    BeanDescriptor descriptor = new BeanDescriptor (SmvCompiler.class);
    descriptor.setDisplayName ("SMV Model Compiler (Flat)");
    return descriptor;
  }
  
  public PropertyDescriptor [] getPropertyDescriptors () 
  {
     
    try {
      List props = new ArrayList ();
      
      PropertyDescriptor prop;
      
      prop = new PropertyDescriptor ("algebra", SmvCompiler.class);
      prop.setDisplayName ("Model Checking Algebra");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE, 
		     "An algebra used to encode the model");
      
      props.add (prop);
  
      prop = new PropertyDescriptor ("mvSetFactoryClass", 
				     SmvCompiler.class);
      prop.setDisplayName ("MvSet Implementation");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE,
		     "MvSet implementation to compile the model into");
      prop.setPropertyEditorClass (MvSetFactoryClassEditor.class);
      props.add (prop);

      prop = new PropertyDescriptor ("inputFile", SmvCompiler.class);
      prop.setDisplayName ("SMV File");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE, 
		     "A file containing an SMV model");
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
