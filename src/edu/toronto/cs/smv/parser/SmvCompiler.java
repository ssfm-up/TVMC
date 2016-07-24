package edu.toronto.cs.smv.parser;

import java.io.*;

import antlr.*;

import edu.toronto.cs.smv.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.ctl.*;


public class SmvCompiler implements ModelCompiler
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

  MvSetFactory factory;
  

  public SmvCompiler ()
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
  
  
  

  private void seal ()
  {
    try 
      {
	factory = (MvSetFactory) 
	  ReflectUtil.callStaticMethod 
	  (mvSetFactoryClass, "newMvSetFactory",
	   new Class[] {IAlgebra.class, int.class},
	   new Object[] { algebra, 
			  new Integer (symbolTable.getNumDDVars ())});

      }
    catch (Exception ex) 
      {
	ex.printStackTrace ();
	throw new RuntimeException (ex);
      }
    
    symbolTable.setMvSetFactory (factory);
    
  }
  


  public XKripkeStructure compile ()
  {
    // -- parse the file
    CommonAST parseTree = parseFile ();

    FullDDBuilder ddBuilder = new FullDDBuilder (factory, symbolTable);
    
    try 
      {
	ddBuilder.moduleDecl (parseTree);
      }
    catch (RecognitionException ex) 
      {
	throw new RuntimeException (ex);
      }
    
    
    MvSet mvSetTrans = ddBuilder.getTrans ();
				 

    // -- work in the invariants
    MvSet invar = ddBuilder.getInvar ();
    invar = invar.and (invar.renameArgs (symbolTable.getPrimeMap ()));
    mvSetTrans = mvSetTrans.and (invar);
    
    init = ddBuilder.getInit ();

    System.out.println ("Total DD variables: " + symbolTable.getNumVars ());
    System.out.println ("Transition of size: " + mvSetTrans.size ());
    System.out.println ("Init of size: " + init.size ());

    assert !mvSetTrans.isConstant () : "Trans is constant!";
    assert !init.isConstant () : "Init is contant!";
    
    trans = new MvSetMvRelation (mvSetTrans,
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
	FullSMVLexer lexer = new FullSMVLexer (new FileReader (inputFile));
	FullSMVParser parser = new FullSMVParser (lexer);
	
	// -- do parsing
	parser.start ();
	// -- get the symbol table from the parser
	symbolTable = parser.getSymbolTable ();
	// -- create mvSetFactory 
	seal ();
	
	return (CommonAST)parser.getAST ();
      }
    catch (Exception ex)
      {
	// XXX very bad!
	throw new RuntimeException (ex);
      }
  }
  

  public static void main (String[] args)
  {
    SmvCompiler compiler = new SmvCompiler ();
    compiler.setMvSetFactoryClass (MDDMvSetFactory.class);
    compiler.setAlgebra (AlgebraCatalog.getAlgebra ("2"));
    compiler.setInputFile (new File (args [0]));
    compiler.compile ();    
  }

  
}
