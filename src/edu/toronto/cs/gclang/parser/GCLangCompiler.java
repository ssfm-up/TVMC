package edu.toronto.cs.gclang.parser;

import java.io.*;

import antlr.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.ctl.*;


public class GCLangCompiler implements ModelCompiler
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

  String progName;
  

  public GCLangCompiler ()
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
  
  public void setProgName (String v)
  {
    progName = v;
  }
  public String getProgName ()
  {
    return progName;
  }

  public MvSet getInit ()
  {
    return init;
  }
  public MvRelation getTrans ()
  {
    return trans;
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
			  new Integer (symbolTable.getNumVars ())});

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

    GCLangBuilder ddBuilder = new GCLangBuilder (factory, symbolTable);
    
    try 
      {
	ddBuilder.program (parseTree);
      }
    catch (RecognitionException ex) 
      {
	throw new RuntimeException (ex);
      }
    

    setProgName (ddBuilder.getName ());

    MvSet mvSetTrans = ddBuilder.getTrans ();


    // -- work in the invariants
    MvSet invar = ddBuilder.getInvar ();
    invar = invar.and (invar.renameArgs (symbolTable.variableMap (0, 1)));
    mvSetTrans = mvSetTrans.and (invar);
    
    init = ddBuilder.getInit ();

    System.out.println ("Total DD variables: " + symbolTable.getNumVars ());
    System.out.println ("Transition of size: " + mvSetTrans.size ());
    System.out.println ("Init of size: " + init.size ());

    assert !mvSetTrans.isConstant () : "Trans is constant!";
    assert !init.isConstant () : "Init is contant!";
    

    
    trans = new MvSetMvRelation (mvSetTrans,
				 factory.buildCube 
				 (symbolTable.getVariableIds (0)),
				 factory.buildCube 
				 (symbolTable.getVariableIds (1)),
				 symbolTable.variableMap (0, 1),
				 symbolTable.variableMap (1, 0));


    return new XKripkeStructure (trans,
				 init,
				 symbolTable.variableMap (0, 1),
				 factory.buildCube 
				 (symbolTable.getVariableIds (1)),
				 factory.buildCube 
				 (symbolTable.getVariableIds (0)),
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
	GCLangLexer lexer = new GCLangLexer (new FileReader (inputFile));
	GCLangParser parser = new GCLangParser (lexer);
	
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
  

  public static void main (String[] args) throws IOException
  {
    GCLangCompiler compiler = new GCLangCompiler ();
    compiler.setMvSetFactoryClass (MDDMvSetFactory.class);
    compiler.setAlgebra (AlgebraCatalog.getAlgebra ("2"));
    compiler.setInputFile (new File (args [0]));
    compiler.compile ();    

    FileWriter f = new FileWriter (compiler.getProgName () + "_init.status");
    f.write (compiler.getInit ().toDaVinci ().toString ());
    f.close ();

    f = new FileWriter (compiler.getProgName () + "_trans.status");
    f.write (compiler.getTrans ().toMvSet ().toDaVinci ().toString ());
    f.close ();

    
  }

  
}
