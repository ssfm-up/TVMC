package edu.toronto.cs.ctl.antlr;

import java.io.*;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import edu.toronto.cs.ctl.*;


// -- parses a string (or other input) into a CTLNode

public class CTLNodeParser
{
    /**
     ** Parses a String into CTL without exceptions.
     ** @return either the CTL produced or null.
     **/
    public static CTLNode safeParse (String ctlStr)
    {
	try { return CTLNodeParser.parse (ctlStr); }
	catch (Exception ex)
	    { System.out.println ("CTL Parsing Error :" + ex); }
	return null;
    }
    public static CTLNode safeParse (String ctlStr, CTLNode[] fairness) 
    {
	try { return CTLNodeParser.parse (ctlStr, fairness); }
	catch (Exception ex)
	    { System.out.println ("CTL Parsing Error :" + ex); }
	return null;
    }

  public static CTLNode parse (String ctlStr)
    throws CTLNodeParserException
  {
    return parse (ctlStr, CTLAbstractNode.EMPTY_ARRAY);
  }
  
  public static CTLNode parse (String ctlStr, CTLNode[] fairness) 
    throws CTLNodeParserException
  {
    return parse (new StringReader (ctlStr), fairness);
  }
  

  public static CTLNode parse (Reader in) throws CTLNodeParserException
  {
    return parse (in, CTLAbstractNode.EMPTY_ARRAY);
  }
  
  public static CTLNode parse (Reader in, CTLNode[] fairness) 
    throws CTLNodeParserException
  {
    try 
      {
		CTLLexer lexer = new CTLLexer (in);
		CTLParser parser = new CTLParser (lexer);
		parser.topLevel ();
		return new CTLTreeBuilder (fairness).ctlTree (parser.getAST ());
      }
    catch (RecognitionException ex) 
      {
		throw new CTLNodeParserException (ex);
      }
    catch (TokenStreamException ex)
      {
		throw new CTLNodeParserException (ex);
      }
    
  }
  

  public static class CTLNodeParserException extends Exception 
  {
    public CTLNodeParserException ()
    {
      super ();
    }
    
    public CTLNodeParserException (String message)
    {
      super (message);
    }
    public CTLNodeParserException (Throwable cause)
    {
      super (cause);
    }
    
  }
  

}
