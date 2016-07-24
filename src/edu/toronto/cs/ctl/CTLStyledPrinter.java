package edu.toronto.cs.ctl;

import edu.toronto.cs.util.gui.StyledDocumentPrinter;

/***
 *** Prints CTL in as much parsable form as possible
 ***/
public class CTLStyledPrinter extends AbstractCTLVisitor
{
  static CTLStyledPrinter self = null;

  // -- print a CTLNode to String
  public static void print (CTLNode node, 
			    StyledDocumentPrinter styledPrinter)
  {
    getStyledPrinter ().printNode (node, styledPrinter);
  }

  public void printNode (CTLNode node, StyledDocumentPrinter styledPrinter)
  {
    visit (node, styledPrinter);
  }
  
  protected void printNode (CTLNode node, Object o)
  {
    printNode (node, (StyledDocumentPrinter)o);
  }
  
  
  // -- get an instance of PrettyPrinter, for now we assume 
  // -- that one instance is enough
  public static CTLStyledPrinter getStyledPrinter ()
  {
    if (self == null)
      self = new CTLStyledPrinter ();
    return self;
  }
  

  public Object visitAndNode (CTLAndNode node, Object o)
  {
    // a /\ b
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;

    boolean leftBraket = false;
    boolean rightBraket = false;

    if (node.getLeft () instanceof CTLOrNode)
      leftBraket = true;
    if (node.getRight () instanceof CTLOrNode)
      rightBraket = true;
    
    if (isTemporalOperator (node.getLeft ()))
      leftBraket = true;
    
    if (leftBraket) out.print("(");
    printNode (node.getLeft (), o);
    if (leftBraket) out.print (")");
    
    //out.bold (" /\\ ");
    // -- unicode logical and
    out.bold (" \u2227 ");
    
    if (rightBraket) out.print("(");
    printNode (node.getRight (), o);
    if (rightBraket) out.print (")");
    return o;
  }
  

  public Object visitOrNode (CTLOrNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;

    boolean leftBraket = false;
    boolean rightBraket = false;

    if (node.getLeft () instanceof CTLAndNode)
      leftBraket = true;
    if (node.getRight () instanceof CTLAndNode)
      rightBraket = true;
    
    if (isTemporalOperator (node.getLeft ()))
      leftBraket = true;
    
    if (leftBraket) out.print("(");
    printNode (node.getLeft (), o);
    if (leftBraket) out.print (")");
    
    //out.bold (" \\/ ");
    // -- unicode logical or
    out.bold (" \u2228 ");

    
    
    if (rightBraket) out.print("(");
    printNode (node.getRight (), o);
    if (rightBraket) out.print (")");
    return o;
  }

  public Object visitAtomPropNode (CTLAtomPropNode node, Object o)
  {
    ((StyledDocumentPrinter)o).print (node.getName ());
    return o;
  }
  public Object visitConstantNode (CTLConstantNode node, Object o)
  {
    ((StyledDocumentPrinter)o).print (node.toString ());
    return o;
  }
  
  public Object visitMvSetNode (CTLMvSetNode node, Object o)
  {
    ((StyledDocumentPrinter)o).italic (node.toString ());
    return o;
  }
  
  public Object visitEqualsNode (CTLEqualsNode node, Object o)
  {
    boolean leftBraket = isTemporalOperator (node.getLeft ());
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    if (leftBraket) out.print ("(");
    printNode (node.getLeft (), o);
    if (leftBraket) out.print (")");

    out.bold (" = ");
    printNode (node.getRight (), o);
    return o;
  }
  
  public Object visitImplNode (CTLImplNode node, Object o)
  {
    boolean leftBraket = isTemporalOperator (node.getLeft ());
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    if (leftBraket) out.print ("(");
    printNode (node.getLeft (), o);
    if (leftBraket) out.print (")");

    // -- double right arrow
    out.bold (" \u21D2 ");
    printNode (node.getRight (), o);
    return o;
  }

  public Object visitAFNode (CTLAFNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("AF ");
    printNode (node.getRight (), o);
    return o;
  }
  
  public Object visitEFNode (CTLEFNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("EF ");
    printNode (node.getRight (), o);
    return o;
  }

  public Object visitAGNode (CTLAGNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("AG ");
    printNode (node.getRight (), o);
    CTLNode[] fairness = node.getFairness ();
    if (fairness.length > 0)
      for (int i = 0; i < fairness.length; i++)
	System.out.println ("Fairness(" + i + "): " + fairness [i]);
    else
      System.out.println ("AG without fairness");
    
    return o;
  }
  
  public Object visitEGNode (CTLEGNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("EG ");
    printNode (node.getRight (), o);
    return o;
  }

  public Object visitAXNode (CTLAXNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("AX ");
    printNode (node.getRight (), o);
    return o;
  }

  public Object visitEXNode (CTLEXNode node, Object o)
  {
    ((StyledDocumentPrinter)o).bold ("EX ");
    printNode (node.getRight (), o);
    return o;
  }

  public Object visitNegNode (CTLNegNode node, Object o)
  {
    boolean rightBraket = (node.getRight () instanceof CTLUnaryNode);
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    // -- not sybol
    out.bold ("\u00AC");
    if (rightBraket) out.print ("(");
    printNode (node.getRight (), o);
    if (rightBraket) out.print (")");
    return o;
  }


  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.print ("?" + node.getName () + "{");
    
    CTLAtomPropNode[] props = node.getProps ();

    String sep = "";    
    for (int i = 0; i < props.length; i++)
      {
	out.print (sep);
	sep = ",";
	printNode (props [i], o);
      }    
    out.print ("}");
    return o;
  }

  public Object visitEUNode (CTLEUNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("E");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" U ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }
  public Object visitAUNode (CTLAUNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("A");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" U ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }

  public Object visitARNode (CTLARNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("A");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" R ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }

  public Object visitERNode (CTLERNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("E");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" R ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }

  public Object visitAWNode (CTLAWNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("A");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" W ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }


  public Object visitEWNode (CTLEWNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("E");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" W ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }


  public Object visitEUiNode (CTLEUiNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("E");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" U_" + node.getI () + " ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }
  public Object visitAUiNode (CTLAUiNode node, Object o)
  {
    StyledDocumentPrinter out = (StyledDocumentPrinter)o;
    
    out.bold ("A");
    out.print ("[");
    printNode (node.getLeft (), o);
    out.bold (" U_" + node.getI () + " ");
    printNode (node.getRight (), o);
    out.print ("]");
    return o;
  }
  
  

  // XXX Bad name, this should reflect that it is used
  // XXX to see if a formula is complex enough to require brackets
  private boolean isTemporalOperator (CTLNode node)
  {
    
    // -- ignore negation in front of temporal operators
    if (node instanceof CTLNegNode)
      return isTemporalOperator (node.getRight ());

    // -- only negation and temporal operators are unary, and 
    // -- we already deal with negation
    if (node instanceof CTLUnaryNode && !(node instanceof CTLBinaryNode))
      return true;

    if (node instanceof CTLEqualsNode &&
	node.getRight () instanceof CTLUnaryNode) return true;
    
    // -- what is left are binary and leaf nodes
//     if (node instanceof CTLEUNode || node instanceof CTLAUNode)
//       return true;
    return false;
  }  
}
