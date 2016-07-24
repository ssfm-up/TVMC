package edu.toronto.cs.smv;

import java.awt.*;
import java.beans.*;
import java.util.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.beans.editors.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;

/***
 *** A bean tester
 ***/
public class GameBeanTest
{
  public static void main (String[] args) throws Exception
  {

    PropertyEditorManager.registerEditor (IAlgebra.class, 
					  IAlgebraEditor.class);
    
    // -- instantiate the bean and cast it to appropriate type
    ModelCompiler bean = 
      (ModelCompiler) Beans.getInstanceOf (Beans.instantiate 
					   (null, "edu.toronto.cs.smv.Game"), 
					   ModelCompiler.class);

    BeanInfo beanInfo = Introspector.getBeanInfo (bean.getClass ());
    System.out.println ("BeanInfo");
    System.out.println (beanInfo);
    
    BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor ();
    System.out.println ("BeanDescriptor");
    System.out.println (beanDescriptor);
    
    PropertyDescriptor [] propDescriptor = beanInfo.getPropertyDescriptors ();
    System.out.println ("PropertyDescriptor");
    System.out.println (propDescriptor);
	
    System.out.println ("Bean's name is " + beanDescriptor.getName ());

    System.out.println 
      ("Bean has " + propDescriptor.length + " property(ies).");

    for (int i = 0; i < propDescriptor.length; i++)
      {
	System.out.println ("The " + 
			    StringUtil.ordinate (i + 1, "?") + 
			    " property's name is: " +
			    propDescriptor [i].getName ());

	System.out.println ("The " + 
			    StringUtil.ordinate (i + 1, "?") + 
			    " property's name describes itself as "+ 
			    propDescriptor [i].getDisplayName ());
	System.out.println ("It's type is: " + 
			    propDescriptor [i].getPropertyType ());
	

	PropertyEditor propEditor = PropertyEditorManager.
	  findEditor (propDescriptor [i].getPropertyType ());
	System.out.println ("It's property editor is: " + propEditor);

	if (propDescriptor [i].getName ().equals ("algebra"))
	{
	  System.out.println ("Actual bean value: " + 
			      ((Game)bean).getAlgebra ());

	  System.out.println ("current value: " + propEditor.getAsText ());
	  System.out.println ("setting value");
	  propEditor.setAsText ("2");
	  System.out.println ("New value: " + propEditor.getValue ());
	  
	  System.out.println ("Actual bean value: " + 
			      ((Game)bean).getAlgebra ());
	}
	    

// 	e.setValue ("hi");
// 	System.out.println ("   The value of this property is now: "
// 			    + e.getValue ());
      }
	
    
  }
}
