package edu.toronto.cs.util;

import java.util.*;
import java.io.File;

public class BeanMangler implements Filter
{
  //String base;
    
  public BeanMangler ()
  {
    //    base = _base;
  }
    
  public Object process (Object o)
  {
    if (o instanceof File)
      return process ((File)o);
    else if (o instanceof String)
      return process ((String)o);
    else return process (o.toString ());
  } 

  public Object process (File f)
  {
    return new File ((String)process (f.toString ()));
  }
    
  public String dotName (String name)
  {
    if (name.endsWith (".class"))
      name = name.substring (0, name.length () - 6);
    else if (name.endsWith (".java"))
      name = name.substring (0, name.length () - 5);
	
//     if (name.regionMatches (0, base, 0, base.length ()))
//       // base.length ()+1 because of leading / elimination
//       name = name.substring (base.length () + 1, 
// 			     name.length ());

    return StringUtil.replace (System.getProperty ("file.separator"),
			       ".", name, true);
  }

  public String shorten (String name)
  {
    String oldname = name;
    try 
      {
	name = new File (oldname).getCanonicalFile ().toString ();
      }
    catch (java.io.IOException e)
      {
	//System.out.println ("Drat.");
	name = oldname;
      }

    //System.out.println ("Testing: "+name);
    

    String classPath = System.getProperty ("java.class.path");
    //System.out.println ("Classpath: "+classPath);
    
    StringTokenizer st = new StringTokenizer (classPath, ":");
    
    for (int i = 0; i < st.countTokens (); i++)
      {
	String nxtToken = st.nextToken ();
	String path = null;
	try {
	  path = new File (nxtToken).getCanonicalFile ()
	    .toString ();
	}
	catch (java.io.IOException e)
	  {
	    //System.out.println ("Drat.");
	    path = nxtToken;
	  }
	//System.out.println ("Token: "+path);
	if (name.regionMatches (0, path, 0, 
				path.length ()))
	  {
	    // base.length ()+1 because of leading / elimination
	    name = name.substring (path.length () + 1, 
				   name.length ());
	    return name;
	  }
      }
    // In anycase, eliminate leading /
  return name.substring (1, name.length ());
  }
  

  public Object process (String s)
  {
    return dotName (shorten (s));
    
  }
  
	
    
}
