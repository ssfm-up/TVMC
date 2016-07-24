package edu.toronto.cs.mvset;

import java.util.*;

/***
 *** This some how keeps a list of all MvSet implementation we have
 *** with their names and maybe some help messages/pictures/etc.
 *** For now, the list of MvSet implementation is hard coded but one
 *** day this will have some registration policy
 ***/

public class MvSetCatalog
{
  static Map tagsToClass = new HashMap ();
  static Map classToTags = new HashMap ();
  
  // -- enumerate all implementations in some way
  public static String[] getTags ()
  {
    return (String[])
      tagsToClass.keySet ().toArray (new String [tagsToClass.size ()]);
  }
  

  public static Class getFactoryClass (String name)
  {
    return (Class)tagsToClass.get (name);
  }
  public static String getFactoryTag (Class clazz)
  {
    return (String)classToTags.get (clazz);
  }
  

  public static void registerMvSetFactory (String tag, Class clazz)
  {
    tagsToClass.put (tag, clazz);
    classToTags.put (clazz, tag);
  }
  

  // -- Hard coded list of MvSets
  static
  {
    //registerMvSetFactory ("bdd-vector", MDDMvSetFactory.class);
    registerMvSetFactory ("jadd", JADDMvSetFactory.class);

    registerMvSetFactory ("mdd", MDDMvSetFactory.class);
    registerMvSetFactory ("cudd-add", CUADDMvSetFactory.class);
    registerMvSetFactory ("jcudd", JCUDDMvSetFactory.class);
  }
}
