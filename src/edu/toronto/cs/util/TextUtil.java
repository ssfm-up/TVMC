package edu.toronto.cs.util;

import java.io.*;
import java.util.regex.*;

public class TextUtil
{
  

  public static Pattern eMailPattern ()
  {
    // (([A-Za-z0-9.-])+@([A-Za-z0-9.-])+\.\p{Alpha}{2,3})

    return Pattern.compile 
      ("([A-Za-z0-9._-])+\\@([A-Za-z0-9._-])+\\.\\p{Alpha}{2,3}");
  }
  
  /**
   ** Converts a random string to a unicode string by stripping all
   ** non-unicode characters out of it
   **/
  public static String toUnicodeString (String s)
  {
    StringBuffer sb = new StringBuffer ();
    char[] data = s.toCharArray ();
    
    for (int i = 0; i < data.length; i++)
      {
	if (Character.isLetterOrDigit (data [i]) || 
	    Character.isSpaceChar (data [i]) ||
	    data [i] == '\n' || 
	    data [i] == '@' ||
	    data [i] == '.' ||
	    data [i] == '-' ||
	    data [i] == '_' || 
	    data [i] == ':' ||
	    data [i] == '"') sb.append (data [i]);
      }
    
    
    return sb.length () > 0 ? sb.toString () : null;
  }
  

  public static void main (String[] args) throws IOException
  {
    BufferedReader in = 
      new BufferedReader (new InputStreamReader (System.in));

    String s;
    int count = 0;
    while ( (s = in.readLine ()) != null)
      {
	s += '\n';
	s = TextUtil.toUnicodeString (s);

	Matcher m = eMailPattern ().matcher (s);
	if (m.find ())
	  {
	    System.out.println ("Got e-mail from: " + s);
	    System.out.println ("e-mail is: " + m.group ());
	  }
	if (count++ > 10) return;
      }
  }
  
}
