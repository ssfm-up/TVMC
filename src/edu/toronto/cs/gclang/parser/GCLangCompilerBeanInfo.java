package edu.toronto.cs.gclang.parser;

import java.beans.*;
import java.util.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.beans.*;
import edu.toronto.cs.beans.editors.*;

public class GCLangCompilerBeanInfo extends SimpleBeanInfo
{
  public GCLangCompilerBeanInfo ()
  {
  }
  
  public BeanDescriptor getBeanDescriptor () 
  {
    BeanDescriptor descriptor = new BeanDescriptor (GCLangCompiler.class);
    descriptor.setDisplayName ("GCLang Compiler");
    return descriptor;
  }
  
  public PropertyDescriptor [] getPropertyDescriptors () 
  {
     
    try {
      List props = new ArrayList ();
      
      PropertyDescriptor prop;
      
      prop = new PropertyDescriptor ("algebra", GCLangCompiler.class);
      prop.setDisplayName ("Model Checking Algebra");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE, 
		     "An algebra used to encode the model");
      
      props.add (prop);
  
      prop = new PropertyDescriptor ("mvSetFactoryClass", 
				     GCLangCompiler.class);
      prop.setDisplayName ("MvSet Implementation");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE,
		     "MvSet implementation to compile the model into");
      prop.setPropertyEditorClass (MvSetFactoryClassEditor.class);
      props.add (prop);

      prop = new PropertyDescriptor ("inputFile", GCLangCompiler.class);
      prop.setDisplayName ("GCLang File");
      prop.setExpert (false);
      prop.setHidden (false);
      prop.setValue (BeanUtil.HELP_ATTRIBUTE, 
		     "A file containing a GCLang model");
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
