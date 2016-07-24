package edu.toronto.cs.beans.editors;

import java.beans.*;

import edu.toronto.cs.algebra.*;


/***
 *** Bean editor for IAlgebra type
 ***/
public class IAlgebraEditor extends PropertyEditorSupport
{
  public String[] getTags ()
  {
    return AlgebraCatalog.getTags ();
  }
  
  public void setAsText (String text) throws IllegalArgumentException
  {
    // -- convert the text value to an algebra and call the setter method
    setValue (AlgebraCatalog.getAlgebra (text));
  }

  public String getAsText ()
  {
    return AlgebraCatalog.getAlgebraTag ((IAlgebra)getValue ());
  }
  

  public Object getValue ()
  {
    Object value = super.getValue ();
    
    if (value == null)
      value = getDefaultValue ();

    return value;
  }


  public String getJavaInitializationString ()
  {
    return AlgebraCatalog.class + ".getAlgebra (\"" + getAsText () + "\")";
  }
  

  IAlgebra getDefaultValue ()
  {
    return AlgebraCatalog.getAlgebra ("2");
  }
  String getDefaultTextValue ()
  {
    return "2";
  }


  
}
