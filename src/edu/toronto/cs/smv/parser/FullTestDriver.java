package edu.toronto.cs.smv.parser;


import java.io.*;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class FullTestDriver {

  
  public static void main(String[] args) {

     
      // make the lexer
       FullSMVLexer lex = new FullSMVLexer(System.in);
       // and feed its output to a parser
       FullSMVParser parser = new FullSMVParser(lex);
          try {

	    parser.start ();
	    // pull out the parse-tree
    	    CommonAST parseTree = (CommonAST)parser.getAST();

	    System.out.println (parser.getSymbolTable ());

	    drawFrame(parseTree);
	  }
	  catch(Exception e) {
	  e.printStackTrace();
	  }	  
	  
    }
  

  public static void drawFrame(CommonAST ptree)
  {
    ASTFrame frame = new ASTFrame("The tree", ptree);
    frame.setVisible(true);
  }
  
    
}


