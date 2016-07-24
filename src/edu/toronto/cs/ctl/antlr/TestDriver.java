package edu.toronto.cs.ctl.antlr;


import java.io.*;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class TestDriver 
{
  public static void main(String[] args) 
  {

      
    CTLLexer lexer = new CTLLexer (System.in);
    CTLParser parser = new CTLParser (lexer);      
    try 
      {
	parser.topLevel ();
	drawFrame (parser.getAST ());
      }
    catch (Exception ex)
      {
	ex.printStackTrace ();
	return;
      }  
    
  }
  public static void drawFrame(AST ptree)
  {
    ASTFrame frame = new ASTFrame("The tree", ptree);
    frame.setVisible(true);
  }
}



