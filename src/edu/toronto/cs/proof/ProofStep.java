package edu.toronto.cs.proof;
// represents a proof of '||phi||(s) = v' for a CTL formula phi, 
//  state s, and algebra value v

import java.util.*;


import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;

public abstract class ProofStep 
{
  // -- the value
  AlgebraValue v;
  // -- the state 
  MvSet state; 
  // -- its name
  String stateName;
  
  // 'phi' - the property
  CTLNode consequent; 

  // -- child nodes of this proof nodes 
  List antecedents;

  // -- has this proof step been dischared already
  boolean discharged;

  // -- ?!
  boolean unfolded;
  
  public AlgebraValue getValue() 
  {
    return v;
  }

  public String getStateName()
  {
    return stateName;
  }
  
  public MvSet getState()
  {
    return state;
  }
  
  /**
   ** unrolls the state into an array of values
   **/
  public AlgebraValue[] getStateAsArray()
  {
    AlgebraValue[] stateArray =  
      (AlgebraValue []) 
      state.getPreImageArray (v.getParentAlgebra().top()).iterator().next();

    // -- make sure that we always return a complete state
    // -- and not just a partial assignment
    return getSingleState (stateArray, v.getParentAlgebra ());
  }
  
  protected void makeSingleState ()
  {
    state = (MvSet) state.mintermIterator 
      (ProofStepFactory.getMC ().getUnPrimeCube (), 
       v.getParentAlgebra ().top ()).next ();
  }

  
  // -- takes a state as variable/value pairs, and sets all don't cares to
  // -- val
  private AlgebraValue[] getSingleState (AlgebraValue[] state, 
					IAlgebra algebra)
  {
   // System.out.println ("WARNING: using depricated " + 
	//		ProofStep.class.getName () + ".getSingleState");
    int[] prime = ProofStepFactory.model.getPrime ();
    for (int i=0; i < state.length; i++)
      if (prime [i] != i && state [i].equals (algebra.noValue ()))
	state [i] = algebra.bot ();
    return state;
  }    
  

  public CTLNode getConsequent()
  {
    return consequent;
  }

  public List getAntecedents()
  {
    return antecedents;
  }
  
  
  
  
  public boolean isDischarged() 
  {
    return discharged;
  }

  public boolean discharge() 
  {
    //System.out.println ("Discharging: " + toString ());
    // ?! create children?!
    if (!unfolded && !unfold ())
      return false;

    // -- assume we will discharge everything
    discharged = true;
    for (Iterator it = antecedents.iterator (); it.hasNext (); )
      {
	ProofStep ps = (ProofStep)it.next ();
	// -- update our discharged state
	discharged = discharged && ps.discharge ();
      }
    // -- done
    return discharged;

  }
  
  // -- creates children?!
  public abstract boolean unfold();
  

  protected ProofStep (AlgebraValue _v, MvSet _state, String _stateName,
		      CTLNode _consequent) 
  {
    v = _v;
    state = _state;
    consequent = _consequent;
    stateName = _stateName;
    antecedents = new LinkedList();
    discharged = false;
    unfolded = false;
    
    
  }
  
  // default behaviour is to refuse
  public Object accept(ProofVisitor pv, Object info)
  {
    assert false : "Can't do nuttin for ya man";
    return null;
  }
  
  public String toString()
  {
    return "[" + consequent + "](" + stateName + ")=" + v;
  }
  

}
