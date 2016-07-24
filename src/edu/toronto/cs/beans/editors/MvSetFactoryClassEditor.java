package edu.toronto.cs.beans.editors;

import java.beans.*;

import edu.toronto.cs.mvset.*;


/***
 *** Bean editor for MvSetFactory.class type
 ***/
public class MvSetFactoryClassEditor extends PropertyEditorSupport
{
  public String[] getTags ()
  {
    return MvSetCatalog.getTags ();
  }
  
  public void setAsText (String text) throws IllegalArgumentException
  {
    // -- convert the text value to an algebra and call the setter method
    setValue (MvSetCatalog.getFactoryClass (text));
  }

  public String getAsText ()
  {
    return MvSetCatalog.getFactoryTag ((Class)getValue ());
  }

  public Object getValue ()
  {
    Object value = super.getValue ();
    
    if (value == null)
      value = getDefaultValue ();

    return value;
  }

  Class getDefaultValue ()
  {
    return MDDMvSetFactory.class;
  }
  String getDefaultTextValue ()
  {
    return MvSetCatalog.getFactoryTag (getDefaultValue ());
  }  
}
