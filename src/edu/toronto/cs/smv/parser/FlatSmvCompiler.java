package edu.toronto.cs.smv.parser;

import java.io.*;

import antlr.*;

import edu.toronto.cs.smv.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.ctl.*;

/**
 * Depricated
 * @depricated
 */
public class FlatSmvCompiler implements ModelCompiler
{

  Class mvSetFactoryClass;
  IAlgebra algebra;
  
  File inputFile;
  
  // -- symbol table
  VariableTable symbolTable;

  // -- transition relation
  MvRelation trans;
  // -- the initial constraint
  MvSet init;

  public FlatSmvCompiler ()
  {
    mvSetFactoryClass = null;
    algebra = null;
    inputFile = null;
  }
  
  public void setMvSetFactoryClass (Class v)
  {
    mvSetFactoryClass = v;
  }
  public Class getMvSetFactoryClass ()
  {
    return mvSetFactoryClass;
  }
  
  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  public void setAlgebra (IAlgebra v)
  {
    algebra = v;
  }
  
  public File getInputFile ()
  {
    return inputFile;
  }
  public void setInputFile (File v)
  {
    inputFile = v;
  }
  
  
  
  

  public XKripkeStructure compile ()
  {
    // -- parse the file
    CommonAST parseTree = parseFile ();
    DDBuilder ddBuilder = new DDBuilder (mvSetFactoryClass, algebra);
    
    try 
      {
	ddBuilder.pgm (parseTree);
      }
    catch (RecognitionException ex) 
      {
	throw new RuntimeException (ex);
      }
    
    
    symbolTable = ddBuilder.getSymbolTable ();
    MvSetFactory factory = symbolTable.getMvSetFactory ();

    
    init = ddBuilder.getInit ();
    trans = new MvSetMvRelation (ddBuilder.getTrans (),
				 factory.buildCube 
				 (symbolTable.getUnPrimedVariablesIds ()),
				 factory.buildCube
				 (symbolTable.getPrimedVariablesIds ()),
				 symbolTable.getPrimeMap (),
				 symbolTable.getUnPrimeMap ());

    return new XKripkeStructure (trans,
				 init,
				 symbolTable.getPrimeMap (),
				 factory.buildCube 
				 (symbolTable.getPrimedVariablesIds ()),
				 factory.buildCube 
				 (symbolTable.getUnPrimedVariablesIds ()),
				 symbolTable.getVarNames (),
				 algebra,
				 symbolTable.getNumVars (),
				 symbolTable.getNumVars (), 
				 symbolTable.getCtlReWriter (),
				 symbolTable.getStatePresenter ());

  }

  // -- private functions

  CommonAST parseFile ()
  {
    try 
      {
	SMVLexer lexer = new SMVLexer (new FileReader (inputFile));
	SMVParser parser = new SMVParser (lexer);
	
	// -- do parsing
	parser.pgm ();
	
	return (CommonAST)parser.getAST ();
      }
    catch (Exception ex)
      {
	// XXX very bad!
	throw new RuntimeException (ex);
      }
  }
  
  
}
