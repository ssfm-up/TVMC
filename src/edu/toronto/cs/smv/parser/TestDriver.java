package edu.toronto.cs.smv.parser;


import java.io.*;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class TestDriver {
    public static void main(String[] args) {

     
      // make the lexer
       SMVLexer lex = new SMVLexer(System.in);
       // and feed its output to a parser
       SMVParser psr = new SMVParser(lex);
          try {

	    
	  
	    // start with the top nonterminal
	    psr.pgm();

	    // pull out the parse-tree
    	    CommonAST parseTree = (CommonAST)psr.getAST();

	    // hand it to the translator along with an
	    // MvSetFactory and the algebra to use
    	    DDBuilder ddb = new DDBuilder(MDDMvSetFactory.class,
    					  AlgebraCatalog.getAlgebra("2"));

	    System.out.println(parseTree.toStringList());
	    drawFrame(parseTree);
	    
	      
	    
	    // and start reading from the top.
	    ddb.pgm(parseTree);
	    
	    // ddb implements compile()
	    // well actually it doesn't!
	    // but when it does, uncomment this.
	    // XKripkeStructure ks = ddb.compile();
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


