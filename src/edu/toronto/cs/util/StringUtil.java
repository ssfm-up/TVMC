package edu.toronto.cs.util;

import java.util.*; // We all love java.util ...

public class StringUtil
{
  public static Map ordinals = new HashMap ();
  public static Map terminals = new HashMap ();
  static
  {
    ordinals.put (new Integer (1), "first");
    ordinals.put (new Integer (2), "second");
    ordinals.put (new Integer (3), "third");
    ordinals.put (new Integer (4), "fourth");
    ordinals.put (new Integer (5), "fifth");
    ordinals.put (new Integer (6), "sixth");
    ordinals.put (new Integer (7), "seventh");
    ordinals.put (new Integer (8), "eighth");
    ordinals.put (new Integer (9), "ninth");
    ordinals.put (new Integer (10), "tenth");
    terminals.put (new Integer (1), "st");
    terminals.put (new Integer (2), "nd");
    terminals.put (new Integer (3), "rd");
  }
  
  public static final String EmptyString = "";
  
  public static String quote (String s)
  {
    return '"'+s+'"';
  }

  public static String maxLength (String subject, int length, String elipses)
  {
    if (subject.length () <= length)
      return subject;

    StringBuffer sb = new StringBuffer ();
    sb.append (subject.substring (0, length-1));
    sb.append (elipses);
    return sb.toString ();
  }
  
  public static StringBuffer enumerate (String [] s)
  {
    StringBuffer sb = new StringBuffer ();
    for (int i = 0; i < s.length; i++)
      {
	sb.append (s [i]);
	if (i < s.length-1)
	  sb.append ("; ");
      }
    return sb;
  }

  public static StringBuffer blockText (String s, int width, int height)
  {
    StringBuffer sb = new StringBuffer ();
    StringTokenizer st = new StringTokenizer (s);
    ListIterator tokens = getTokenIterator (st);
    for (int i = 0; i < height-1 && tokens.hasNext (); i++)
      {
	sb.append (makeLine (tokens, width));
	sb.append ('\n');
      }
    if (st.hasMoreTokens ())
      sb.append (maxLength (restOfIterator (tokens).toString (), 
			    width, "..."));
    return sb;
  }

  public static ListIterator getTokenIterator (StringTokenizer st)
  {
    List tokens = new ArrayList (st.countTokens ());
    for (; st.hasMoreTokens ();)
      tokens.add (st.nextToken ());
    return tokens.listIterator ();
  }
  
  public static StringBuffer makeLine (ListIterator it, int w)
  {
    StringBuffer sb = new StringBuffer ();
    for (int pos = 0; pos < w && it.hasNext ();)
      {
	String next = (String) it.next ();
	if (next.length () > w)
	  {
	    it.add (next.substring (w));
	    next = next.substring (0, w-1);
	  }
	sb.append (next);
	pos += next.length ();
      }
    return sb;
  }
  
  public static StringBuffer restOfIterator (Iterator it)
  {
    StringBuffer sb = new StringBuffer ();
    for (; it.hasNext ();)
      {
	sb.append ((String)it.next ());
	if (it.hasNext ())
	  sb.append (' ');
      }
    return sb;
  }
  
  public static StringBuffer quote (StringBuffer s)
  {
    s.insert (0, '"');
    s.append ('"');
    return s;
  }

  public static String ordinate (int i, String zeroth, Map ords)
  {
    if (i == 0)
      return zeroth;
    String s = (String) ords.get (new Integer (i));
    if (s != null)
      return s;
    else
      if ((terminals.get (new Integer (i % 10)) != null) && (i >= 14))
	return ""+i+terminals.get (new Integer (i % 10));
      else
	return ""+i+"th";
  }
  
  public static String ordinate 
    (int i, String zeroth)
  {
    return ordinate (i, zeroth, ordinals);
  } 

  public static String doEscapes (String s)
  {
    Map replacements = new HashMap ();
    replacements.put ("\n", "\\n");
    replacements.put ("\t", "\\t");
    replacements.put ("\\", "\\\\");
    return translate (replacements, s);
  }

  public static String translate (Map translation, String string)
  {
    StringBuffer sb = new StringBuffer ();
    int last = -1;
    for (int i = 0;i < string.length (); i++)
      {
	String str = (String) translation.get (""+string.charAt (i));
	if (str != null)
	  {
	    if (last+1<i)
	      sb.append (string.substring (last+1, i));
	    last = i;
	    sb.append (str);
	  }
      }
    sb.append (string.substring (last+1));
    return sb.toString ();
  }

  public static int contains (String val, String subject, boolean cases)
  {
    int i;
    for (i = 0; i <= subject.length () - val.length (); i++)
      if (subject.regionMatches (cases, i, val, 0, val.length ()))
	return i;
    return -1;
  }    
  
  public static String replace (String old, String newS, String subject, boolean caseSensitive)
  {
    StringBuffer sb = new StringBuffer ();
    int i=0;
    while (i <= subject.length () - old.length ())
      {
	if (subject.regionMatches (caseSensitive, i, old, 0, old.length ()))
	  {
	    sb.append (newS);
	    i = i+old.length ();
	  }
	else
	  {
	    sb.append (subject.substring(i,i+1));
	    i++;
	  }
      }
    if (i<subject.length ())
      sb.append (subject.substring (i, subject.length ()-1));
    return sb.toString ();
  }


  /**
   ** Makes sure that 's' is of length 'length' by padding it with 'character'
   */
  public static String pad (int length, String s, char character)
  {
    StringBuffer sb = new StringBuffer ();

    for (int i = 0; i < length - s.length (); i++)
      sb.append (character);
    sb.append (s);
    return sb.toString ();
  }


  public static void main (String args [])
  {
    System.out.println (quote (args[0]));
    //System.out.println (escapeBSlash (args[1]));
    System.out.println ("\\ Hello World! \\Hi");
    System.out.println (doEscapes("\\ Hello World! \\Hi"));
  }

  
}


