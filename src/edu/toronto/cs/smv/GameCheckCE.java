package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.CTLNodeParser;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.proof.*;
import java.util.List;

import java.io.*;


// -- an instance of model-checker parametrised for Game model
public class GameCheckCE
{
  public static void main (String[] args) throws Exception
  {
    // -- build a compiler
    System.out.println ("Getting a compiler");
    ModelCompiler compiler = (MyGame)MyGame.initModule (args);
    
    System.out.println ("Compiling the model");
    XKripkeStructure model = compiler.compile ();
    
    // -- create the model-checker
    MvSetModelChecker modelChecker = new MvSetModelChecker (model);
    
    // -- prepare to read from stdin
    BufferedReader in = 
      new BufferedReader (new InputStreamReader (System.in));

    
    CTLUntilExpander efexpander = 
      new CTLUntilExpander (model.getMvSetFactory ().top ());
    

    while (true)
      {
	// -- read a ctl formula
	System.out.println ("Please enter a CTL property");
	String ctlStr = in.readLine ();
    
	// -- parse
	System.out.println ("Parsing: " + ctlStr);
	
	CTLNode ctl = null;
	
	try {
	  
	  //BinaryTreeToCTLConverter.setAlgebra (model.getAlgebra ());
	
	  //ctl = BinaryTreeToCTLConverter.convertToCTL (ctlStr);
	  ctl = CTLNodeParser.parse (ctlStr);
	
	}
	 catch (Exception e)
	   {
	     e.printStackTrace();
	   }
	 
	System.out.println ("The formula is " + ctl);

	System.out.println ("re-writing the formula");
	ctl = efexpander.rewrite (model.rewrite (ctl));

	

	System.out.println ("The formula is " + ctl);

	System.out.println ("Model-checking");
	
	MvSet mvSetResult = modelChecker.checkCTL (ctl);
	System.out.println ("Evaluating at initial state");

	AlgebraValue result = 
	  mvSetResult.and (model.getInit ()).
	  existAbstract (model.getUnPrimeCube ()).
	  evaluate (new AlgebraValue [0]);

	System.out.println("Done.");
	
	ProofStepFactory.setMC(modelChecker);
	ProofStepFactory.setSNG(new SimpleNameGenerator());
	ProofStepFactory.setStructure(model);
	
	
	ProofStep mps = ProofStepFactory.makeProofStep(result,
				       model.getInit(),
				       ctl);
	if (mps != null)
	  {
	    try {
	    if (mps.discharge()) 
	      System.out.println("Unfolded!\n"+mps);

	    System.out.println(ProofToDaVinci.toDaVinci(mps,true).toString());
	    
	    
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    
	  }
	
	
	System.out.println ("Final conclusion: " + result);
      }
    
    
    
  }
  
}
