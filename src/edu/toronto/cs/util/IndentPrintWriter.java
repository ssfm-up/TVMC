package edu.toronto.cs.util;

import java.io.*;
import java.util.Stack;


/**
 * Enhances PrintWriter with ability to indent output
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */
public class IndentPrintWriter extends PrintWriter
{

  /**
   * stack of all previous indentation prefixes
   *
   */
  Stack stack = new Stack ();
  
  /**
   * current indentation prefix
   *
   */
  String prefix = "";

  /**
   * a flag to keep track if a new line has been seen
   *
   */
  boolean newLineSeen = false;
  
  /**
   * Default indentation prefix
   *
   */
  public static final String DEFAULT_PREFIX = "  ";
  
  /**
   * Constructors of the super class
   *
   * @param out an <code>OutputStream</code> value
   */
  public IndentPrintWriter (OutputStream out)
  {
    super (out);
  }
  public IndentPrintWriter (OutputStream out, boolean autoFlush)
  {
    super (out, autoFlush);
  }
  public IndentPrintWriter (Writer out)
  {
    super (out);
  }
  public IndentPrintWriter (Writer out, boolean autoFlush)
  {
    super (out, autoFlush);
  }

  /**
   * Indents an output stream with a default indentPrefix
   *
   */
  public void indent ()
  {
    indent (DEFAULT_PREFIX);
  }
  
  /**
   * indents the output string by <code>indentPrefix</code>
   *
   * @param indentPrefix a <code>String</code> value
   */
  public void indent (String indentPrefix)
  {
    stack.push (prefix);
    prefix = prefix + indentPrefix;
  }

  /**
   * removes one level of indentation
   *
   */
  public void outdent ()
  {
    if (!stack.isEmpty ())
      prefix = (String) stack.pop ();
  }
  
  public void println (String s)
  {
    super.write (prefix);
    super.println (s);
  }

  public void sblock ()
  {
    println ("{");
    indent ();
  }
  public void eblock ()
  {
    outdent ();
    println ("}");
  }
  
  
  

  public static void main (String[] args)
  {
    IndentPrintWriter out = new IndentPrintWriter (System.out, true);
    
    out.indent ("// -- ");
    
    out.println ("int main (void)");
    out.sblock ();

    out.println ("int x;");
    out.println ("int y;");
    out.println ();
    out.println ("x = 0;");
    out.println ("y = 0;");
    out.println ("for (x = 0; x < 10; x++)");
    out.sblock ();
    out.println ("y = y + 1;");
    out.eblock ();

    out.println ("fprintf (stdout, \"x is %d, y is %d\\n\", x, y);");
    out.println ("return 0;");
    out.eblock ();
  }
  
  
  
}
