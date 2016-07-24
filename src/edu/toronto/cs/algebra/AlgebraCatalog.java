package edu.toronto.cs.algebra;

import java.util.*;
import java.net.URL;

import edu.toronto.cs.tlq.UpSetAlgebra;
import edu.toronto.cs.tlq.MvSetUpsetAlgebra;

/**
 ** Algebra catalog -- see MvSetCatalog for description
 ** of what a catalog is.
 **/
public class AlgebraCatalog
{
  static Map tagsToAlgebra = new HashMap ();
  static Map algebraToTags = new HashMap ();
  
  public static Iterator iterator ()
  {
    return algebraToTags.keySet ().iterator ();
  }
  
  public static String[] getTags ()
  {
    return (String[])
      tagsToAlgebra.keySet ().toArray (new String [tagsToAlgebra.size ()]);
  }
  
  public static IAlgebra getAlgebra (String tag)
  {
    return (IAlgebra)tagsToAlgebra.get (tag);
  }
  public static String getAlgebraTag (IAlgebra algebra)
  {
    return (String)algebraToTags.get (algebra);
  }
  
  public static void registerAlgebra (String tag, IAlgebra algebra)
  {
    tagsToAlgebra.put (tag, algebra);
    algebraToTags.put (algebra, tag);
  }
  
  static
  {
    registerAlgebra ("2", new TwoValAlgebra ());
    registerAlgebra ("upset", new UpSetAlgebra ());
    registerAlgebra ("mvset-upset", new MvSetUpsetAlgebra ());

    URL algebraURL;
    
    try {
      algebraURL = AlgebraCatalog.class.
	getResource ("/edu/toronto/cs/resources/algebras/3val-logic.xml");
      if (algebraURL != null)
	registerAlgebra ("Kleene", 
			 XMLDeMorganAlgebraParser.parse 
			 (algebraURL.openStream ()));
    }
    catch (Exception ex){
      ex.printStackTrace ();
    }

    try {
      algebraURL = AlgebraCatalog.class.
	getResource ("/edu/toronto/cs/resources/algebras/4val-logic.xml");
      if (algebraURL != null)
	registerAlgebra ("2x2", 
			 XMLDeMorganAlgebraParser.parse 
			 (algebraURL.openStream ()));
    }
    catch (Exception ex){
      ex.printStackTrace ();
    }

    try {
      algebraURL = AlgebraCatalog.class.
	getResource ("/edu/toronto/cs/resources/algebras/9val-logic.xml");
      if (algebraURL != null)
	registerAlgebra ("3x3", 
			 XMLDeMorganAlgebraParser.parse 
			 (algebraURL.openStream ()));
    }
    catch (Exception ex){
      ex.printStackTrace ();
    }
  }
  
  
}
