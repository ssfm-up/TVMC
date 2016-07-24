package edu.toronto.cs.ctl;

import edu.toronto.cs.ctl.antlr.CTLNodeParser;

/***
 *** Prints CTL in as much parsable form as possible
 ***/
public class CTLPrettyPrinter extends AbstractCTLVisitor
{
  static CTLPrettyPrinter self = null;

  // -- print a CTLNode to String
  public static String toString (CTLNode node)
  {
    return getPrettyPrinter ().stringValue (node);
  }

  public String stringValue (CTLNode node)
  {
    return (String)visit (node, null);
  }
  
  // -- get an instance of PrettyPrinter, for now we assume 
  // -- that one instance is enough
  public static CTLPrettyPrinter getPrettyPrinter ()
  {
    if (self == null)
      self = new CTLPrettyPrinter ();
    return self;
  }
  

  public Object visitAndNode (CTLAndNode node, Object o)
  {
    // a /\ b
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    if (node.getLeft () instanceof CTLOrNode)
      left = "(" + left + ")";
    if (node.getRight () instanceof CTLOrNode)
      right = "(" + right + ")";
    
    if (isTemporalOperator (node.getLeft ()))
      left = "(" + left + ")";
    if (node.getLeft () instanceof CTLEqualsNode)
      left = "(" + left + ")";
    
    
    return left + " /\\ " + right;
  }
  

  public Object visitOrNode (CTLOrNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    if (node.getLeft () instanceof CTLAndNode)
      left = "(" + left + ")";
    if (node.getRight () instanceof CTLAndNode)
      right = "(" + right + ")";
    
    if (isTemporalOperator (node.getLeft ()))
      left = "(" + left + ")";
    if (node.getLeft () instanceof CTLEqualsNode)
      left = "(" + left + ")";

    
    return left + " \\/ " + right;
  }

  public Object visitAtomPropNode (CTLAtomPropNode node, Object o)
  {
    return node.getName ();
  }
  public Object visitConstantNode (CTLConstantNode node, Object o)
  {
    return node.toString ();
  }
  
  public Object visitMvSetNode (CTLMvSetNode node, Object o)
  {
    return node.toString ();
  }
  
  public Object visitEqualsNode (CTLEqualsNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());

    if (isTemporalOperator (node.getLeft ()))
      left = "(" + left + ")";

    return left + " = " + right;
  }
  
  public Object visitImplNode (CTLImplNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());

    if (isTemporalOperator (node.getLeft ()))
      left = "(" + left + ")";

    return left + " -> " + right;
  }

  public Object visitAFNode (CTLAFNode node, Object o)
  {
    return "AF " + stringValue (node.getRight ());
  }
  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    return "EF " + stringValue (node.getRight ());
  }

  public Object visitAGNode (CTLAGNode node, Object o)
  {
    return "AG " + stringValue (node.getRight ());
  }
  
  public Object visitEGNode (CTLEGNode node, Object o)
  {
    return "EG " + stringValue (node.getRight ());
  }

  public Object visitAXNode (CTLAXNode node, Object o)
  {
    return "AX " + stringValue (node.getRight ());
  }

  public Object visitEXNode (CTLEXNode node, Object o)
  {
    return "EX " + stringValue (node.getRight ());
  }

  public Object visitNegNode (CTLNegNode node, Object o)
  {
    String right = stringValue (node.getRight ());

    if (node.getRight () instanceof CTLUnaryNode)
      right = "(" + right + ")";
      

    return "!" + right;
  }


  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ("?");
    sb.append (node.getName ());
    sb.append ("{");
    
    CTLAtomPropNode[] props = node.getProps ();

    String sep = "";    
    for (int i = 0; i < props.length; i++)
      {
	sb.append (sep);
	sep = ",";
	sb.append (stringValue (props [i]));
      }    
    sb.append ("}");
    return sb.toString ();
  }

  public Object visitEUNode (CTLEUNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    return "E[ " + left + " U " + right + "]";
  }
  public Object visitAUNode (CTLAUNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    return "A[ " + left + " U " + right + "]";
  }

  public Object visitARNode (CTLARNode node, Object o)
  {
    return "A[ " + stringValue (node.getLeft ()) + 
      " R " + stringValue (node.getRight ()) + "]";
  }
  public Object visitERNode (CTLERNode node, Object o)
  {
    return "E[ " + stringValue (node.getLeft ()) + 
      " R " + stringValue (node.getRight ()) + "]";
  }
  
  public Object visitEWNode (CTLEWNode node, Object o)
  {
    return "E[ " + stringValue (node.getLeft ()) + 
      " W " + stringValue (node.getRight ()) + "]";
  }
  public Object visitAWNode (CTLAWNode node, Object o)
  {
    return "A[ " + stringValue (node.getLeft ()) + 
      " W " + stringValue (node.getRight ()) + "]";
  }
  

  public Object visitEUiNode (CTLEUiNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    return "E[ " + left + " U" + node.getI () + " " + right + "]";
  }
  public Object visitAUiNode (CTLAUiNode node, Object o)
  {
    String left = stringValue (node.getLeft ());
    String right = stringValue (node.getRight ());
    
    return "A[ " + left + " U" + node.getI () + " " + right + "]";
  }
  
  

  private boolean isTemporalOperator (CTLNode node)
  {
    
    // -- ignore negation in front of temporal operators
    if (node instanceof CTLNegNode)
      return isTemporalOperator (node.getRight ());

    // -- only negation and temporal operators are unary, and 
    // -- we already deal with negation
    if (node instanceof CTLUnaryNode && !(node instanceof CTLBinaryNode))
      return true;
    
    // -- what is left are binary and leaf nodes
//     if (node instanceof CTLEUNode || node instanceof CTLAUNode)
//       return true;
    return false;
  }
  


  // -- tester method
  public static void main (String[] args) throws Exception
  {
    CTLNode ctl;

    // -- prepare to read from stdin
    java.io.BufferedReader in = 
      new java.io.BufferedReader (new java.io.InputStreamReader (System.in));
    
    // -- read a ctl formula
    System.out.println ("Please enter a CTL property");
    String ctlStr = in.readLine ();

    // -- parse
    System.out.println ("Parsing: " + ctlStr);
    //ctl = BinaryTreeToCTLConverter.convertToCTL (ctlStr);
    ctl = CTLNodeParser.parse (ctlStr);


    System.out.println ("The formula is " + CTLPrettyPrinter.toString (ctl));
  }
  
  
}
