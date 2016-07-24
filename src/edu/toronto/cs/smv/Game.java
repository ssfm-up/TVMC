package edu.toronto.cs.smv;

import java.util.*;

import edu.toronto.cs.smv.*;
import edu.toronto.cs.smv.SMVModule.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.ctl.antlr.*;
import edu.toronto.cs.modelchecker.*;
import java.io.*;


// A very simple program for testing

public class Game extends SMVModule
{
  
  static AlgebraVariable A;
  static AlgebraVariable B;
  
  public Game ()
  {
    super ("game");
    A = declareAlgebraVariable ("a");
    B = declareAlgebraVariable ("b");
  }
  
  public MvSet computeTrans ()
  {
    /**
     ** !a /\ !b /\ !a' /\ !b' 
     **        \/
     ** !a /\ !b /\ a' /\ b' 
     **        \/
     ** a /\ b /\ a' /\ b'
     **/
    return A.eq (bot).and (B.eq (bot).
			   and (next (A).eq (bot).and (next (B).eq (bot)))).or
      (A.eq (bot).and (B.eq (bot).
		      and (next (A).eq (top).and (next (B).eq (top))))).or
      (A.eq (top).and (B.eq (top).
		      and (next (A).eq (top).and (next (B).eq (top)))));
  }
  
  
  
  public MvSet computeInit ()
  {
    // -- !a /\ !b
    return A.eq (bot).and (B.eq (bot));
  }
  
  public static SMVModule initModule (String[] args)
  {
    Game game = new Game ();
    
    game.setAlgebra (AlgebraCatalog.getAlgebra ("upset"));
    //game.setMvSetFactoryClass (MDDMvSetFactory.class);
    game.setMvSetFactoryClass (CUADDMvSetFactory.class);

    System.out.println ("Declared Variables are: ");
    System.out.println (game.variables.toString ());
    return game;
    
  }

  public static void main (String[] args) throws Exception
  {
    Game game = (Game)initModule (args);

    CTLNode ctl;
    
    // -- prepare to read from stdin
    BufferedReader in = 
      new BufferedReader (new InputStreamReader (System.in));

    while (true)
      {
	
	// -- read a ctl formula
	System.out.println ("Please enter a CTL property");
	String ctlStr = in.readLine ();
	
	// -- parse
	System.out.println ("Parsing: " + ctlStr);
	//ctl = BinaryTreeToCTLConverter.convertToCTL (ctlStr);
	ctl = CTLNodeParser.parse (ctlStr);
	
	System.out.println ("The formula is " + ctl);
	
	ctl = new CloningRewriter ().rewrite (ctl);
	
	System.out.println ("After rewriting: " + ctl);
      }
    

  }    
  
  
  
}
