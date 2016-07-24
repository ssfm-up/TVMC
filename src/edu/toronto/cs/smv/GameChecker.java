package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.mvset.MDDMvSetFactory.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.CTLNodeParser;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.tlq.*;

import java.io.*;
import java.util.*;


// -- an instance of model-checker parametrised for Game model
public class GameChecker
{
  public static void main (String[] args) throws Exception
  {
    // -- build a compiler
    System.out.println ("Getting a compiler");
    ModelCompiler compiler = (Game)Game.initModule (args);
    
    System.out.println ("Compiling the model");
    XKripkeStructure model = compiler.compile ();
    
    // -- create the model-checker
    MvSetModelChecker modelChecker = new MvSetModelChecker (model);
    
    // -- prepare to read from stdin
    BufferedReader in = 
      new BufferedReader (new InputStreamReader (System.in));

    
    CTLUntilExpander efexpander = 
      new CTLUntilExpander (model.getMvSetFactory ().top ());
    

    PlaceholderReWriter phRewriter = null;
    
    try {
      phRewriter= new PlaceholderReWriter ((UpSetAlgebra)model.getAlgebra ());
    }
    catch (ClassCastException ex){
    }
    

    while (true)
      {
	// -- read a ctl formula
	System.out.println ("Please enter a CTL property");
	String ctlStr = in.readLine ();
    
	// -- parse
	System.out.println ("Parsing: " + ctlStr);
	
	//BinaryTreeToCTLConverter.setAlgebra (model.getAlgebra ());
	
	//CTLNode ctl = BinaryTreeToCTLConverter.convertToCTL (ctlStr);
	CTLNode ctl = CTLNodeParser.parse (ctlStr);
	
	System.out.println ("The formula is " + ctl);

	System.out.println ("re-writing the formula");
	ctl = efexpander.rewrite (model.rewrite (ctl));
	if (phRewriter != null)
	  ctl = phRewriter.rewrite (ctl);
	
	

	System.out.println ("The formula is " + ctl);

	System.out.println ("Model-checking");
	
	MvSet mvSetResult = modelChecker.checkCTL (ctl);
	System.out.println ("Evaluating at initial state");


	System.out.println ("Transition relation is");
// 	printAssignments (model.getTrans ().expandToArray (), 
// 			  model.getAlgebra ());
	System.out.println ("Same from iterator");
	printCube (model.getTrans ().toMvSet ());

	try 
	  {
	    FileWriter out = new FileWriter ("trans.status");
	    out.write (model.getTrans ().toMvSet ().toDaVinci ().toString ());
	    out.close ();
	  }
	catch (Exception ex) 
	  {
	    // -- do nothing since this is debug code
	  }
	
	
	

	System.out.println ("Init constraint is");
// 	printAssignments (model.getInit ().expandToArray (), 
// 			  model.getAlgebra ());
	System.out.println ("Same as iterator");
	printCube (model.getInit ());
	
	System.out.println ("Init constraint as CTL");
// 	printAsCTL (model.getStatePresenter (), 
// 		    model.getInit ().expandToArray (),
// 		    model.getAlgebra ());
	System.out.println ("Same using iterator");
	printAsCTL (model.getStatePresenter (), model.getInit ());
	
	
	System.out.println ("Model checking result");
// 	printAssignments (mvSetResult.expandToArray (), 
// 			  model.getAlgebra ());

	System.out.println ("Model checking result from iterator");
	printCube (mvSetResult);
	
	System.out.println ("The same as CTL");
// 	printAsCTL (model.getStatePresenter (),
// 		    mvSetResult.expandToArray (),
// 		    model.getAlgebra ());
	System.out.println ("Same using iterator");
	printAsCTL (model.getStatePresenter (), mvSetResult);
	


	AlgebraValue result = 
	  mvSetResult.and (model.getInit ()).
	  existAbstract (model.getUnPrimeCube ()).
	  getValue ();
	System.out.println ("Final conclusion: " + result);
      }
    
  }

  static void printAsCTL (StatePresenter statePresenter, MvSet mvSet)
  {
    for (Iterator it = mvSet.cubeIterator (); it.hasNext ();)
      {
	AlgebraValue[] cube = (AlgebraValue[])it.next ();
	CTLNode[] ctlState = statePresenter.toCTL (cube);
	System.out.println (Arrays.asList (ctlState).toString ());
      }
    
  }
  
  static void printAsCTL (StatePresenter statePresenter, 
			  AlgebraValue[][] assigments, IAlgebra algebra)
  {

    String sep = "  ";    
    for (int i = 0; i < assigments.length; i++)
      {
	if (assigments [i] [assigments [i].length - 1] == algebra.bot ())
	  continue;
	
	CTLNode[] ctlState = statePresenter.toCTL (assigments [i]);
	for (int j = 0; j < ctlState.length; j++)
	  System.out.print (ctlState [j] + ",");
	System.out.println ();
      }
  }


  static void printCube (MvSet mvSet)
  {
    for (Iterator it = mvSet.cubeIterator (); it.hasNext (); )
      {
	AlgebraValue[] cube = (AlgebraValue[]) it.next ();
	for (int i = 0; i < cube.length; i++)
	  System.out.print (cube [i] + "  ");
	System.out.println ();
      }
    
  }  

  static void printAssignments (AlgebraValue[][] assigments, IAlgebra algebra)
  {

    String sep = "  ";
    
    // -- print a line of numbers
    for (int i = 0; i < assigments [0].length; i++)
      System.out.print (i + sep);
    System.out.println ();
    
    for (int i = 0; i < assigments.length; i++)
      {
	if (assigments [i] [assigments [i].length - 1] != algebra.bot ())
	  {
	    for (int j = 0; j < assigments [i].length; j++)
	      System.out.print (assigments [i][j] + sep);
	    System.out.println ();
	  }
      }
  }
  
  
}
