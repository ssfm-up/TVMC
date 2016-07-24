package edu.toronto.cs.xkripke;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.xkripke.XKripke.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mdd.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;

import edu.toronto.cs.smv.VariableTable;

import java.util.Iterator;
import java.util.Map;


// -- given a XKripke model creates an MDD for it
public class XKripkeMvSetBuilder
{

  /***
   *** mvSetFactory -- needed to create MvSets
   *** kripke -- the XKripke model to convert
   *** propOrder -- interface to order the atomic props
   ***/
  public static MvSet buildMvSet (MvSetFactory mvSetFactory, XKripke kripke)
  {
    // -- we build each transition individually and then 
    // -- join all of them together
    
    IAlgebra algebra = kripke.getAlgebra ();
    // -- start with the identity on JOIN
    MvSet result = mvSetFactory.createConstant (algebra.top ());
    VariableTable vt = kripke.getSymbolTable ();
    vt.setMvSetFactory (mvSetFactory);

    for (Iterator it = kripke.getTransitions ().iterator (); it.hasNext ();)
      result = 
	result.or (buildTransition (mvSetFactory, algebra, 
				    (XKripkeTransition)it.next (), 
				    vt));
    return result;
  }

  public static MvSet buildTransition (MvSetFactory mvSetFactory, 
				       IAlgebra algebra,
				       XKripkeTransition trans,
				       VariableTable vt)
  {
    int idx = 0; // -- variable indexing
    
    // -- this keeps the variable values on transitions
    MvSet transition = 
      mvSetFactory.createConstant (algebra.getValue (trans.getValue ()));; 
    
    for (Iterator it = trans.getSrc ().getProps ().values ().iterator ();
	 it.hasNext ();)
      {
	XKripkeProp prop = (XKripkeProp) it.next ();
	String name = prop.getName ();
	String value = prop.getValue ();
	transition = transition.and (vt.getByName (name).eq (value));
      }

    for (Iterator it = trans.getDst ().getProps ().values ().iterator ();
	 it.hasNext ();)
      {
	XKripkeProp prop = (XKripkeProp) it.next ();
	String name = prop.getName ();
	String value = prop.getValue ();
	transition = 
	  transition.and (vt.getByName (name).getNext ().eq (value));
      }

    return transition;
  }


  public static void main (String[] args) throws Exception 
  {
    XKripke kripke = XKripkeFactory.parse (args [0]);
    

    MvSetFactory mvSetFactory = 
      MDDMvSetFactory.newMvSetFactory (kripke.getAlgebra (), 
				       kripke.getSymbolTable ().getNumDDVars ());
    
    MvSet mvSet = buildMvSet (mvSetFactory, kripke);
  }
  
  
}



