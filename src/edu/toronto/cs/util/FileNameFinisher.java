package edu.toronto.cs.util;

import java.util.*;
import java.io.File;

public class FileNameFinisher implements Filter
{
  String remove, add;
    
  public FileNameFinisher (String _remove, String _add)
  {
    remove = _remove;
    add = _add;
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
    
  public Object process (String name)
  {
    
    if (name.endsWith (remove))
      name = name.substring (0, name.length () - remove.length ());
	
    return ""+ name + add;
  }
    
}
