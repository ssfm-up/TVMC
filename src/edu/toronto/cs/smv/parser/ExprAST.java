package edu.toronto.cs.smv.parser;

import antlr.CommonAST;
import antlr.collections.AST;

import java.util.*;
import antlr.Token;

public class ExprAST extends CommonAST
{
  boolean enm;
  
  List values;

  public ExprAST()
  {
    super();
  }
  
  public ExprAST(Token tok) 
  {
    super(tok);
    
  };
  
  
    public boolean isEnum()
    {
    return enm;
    
  }

//    public AST getNextSibling() 
//    {
//      AST temp = super.getNextSibling();

//      if (temp != null)
//      System.out.println("me: "+getText()+" sib: "+
//  		       temp.getText());
    
//      return temp;
    
//    }
  
  public void setProp()
  {
    enm = false;
    values = null;
    
  }
  
  public void setEnum(List _values)
  {
    enm = true;
    values = _values;
  }
}
