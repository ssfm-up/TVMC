package edu.toronto.cs.proof;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;


/***
 *** Factory to generate proof steps
 *** All proof steps should be inner classes of the factory
 *** The factory should probably be non-static based on how it is 
 *** used.
 ***/
public class ProofStepFactory 
{
  // -- the model checker
  static MvSetModelChecker mc;

  // -- unique ID generator, this should be part of Util since it
  // -- is useful in general and similar implementations may exists
  // -- through out the code
  static StateNameGenerator sng;


  // -- maps mvset states to names. type MvSet -> String
  static Map stateNameMap = new HashMap();

  // -- the model for which we generate the proof
  static XKripkeStructure model;
  
  
  public static void setStructure(XKripkeStructure _model)
  {
    model = _model;
  }

  public static XKripkeStructure getStructure() 
  {
    return model;
  }
  

  public static void setSNG (StateNameGenerator _sng)
  {
    sng = _sng;
  }

  public static StateNameGenerator getSNG()
  {
    return sng;
  }
  
  
  public static void setMC (MvSetModelChecker _mc)
  {
    mc = _mc;
  }
  
  public static MvSetModelChecker getMC ()
  {
    return mc;
  }
  
  public static String makeStateName (MvSet state)
  {
    String stateName = (String) stateNameMap.get (state);
    if (stateName == null)
      {
	stateName = sng.getFreshName ();
	stateNameMap.put (state, stateName);
      }
    return stateName;
  }
  
  
  // -- creates a new proof step
  // -- I think the interpretation is 
  // --  ||_consequent|| (_state) = _v
  public static ProofStep makeProofStep(AlgebraValue _v, MvSet _state,
					CTLNode _consequent) 
  {
    String stateName = makeStateName (_state);
    
   
    // XXX There should be a better way to do this
    // XXX may be our CTLTree hierarchy needs some rework or something
    // XXX but I really do not like a big case on type of the class!
    // XXX especially since subclassing places a huge role in the ordering
    // XXX of the cases. The same goes for the MvSetModelChecker
    if (_consequent instanceof CTLEXNode)
      return new EXProofStep(_v, _state, stateName, _consequent);

    if (_consequent instanceof CTLNegNode)
      return new NegProofStep(_v, _state, stateName, _consequent);
    
    if (_consequent instanceof CTLEqualsNode)
	return new PropProofStep(_v, _state, stateName, _consequent);
    
    if (_consequent instanceof CTLEUiNode)
      return new EUiProofStep(_v, _state, stateName, _consequent,
			      ((CTLEUiNode)_consequent).getI());
    // and *not* an EUi node!
    if (_consequent instanceof CTLEUNode)
      return new EUProofStep(_v, _state, stateName, _consequent);
    
    if (_consequent instanceof CTLAndNode)
      return new ConjProofStep(_v, _state, stateName, _consequent);
    
    if (_consequent instanceof CTLEGNode)
      return new EGProofStep(_v, _state, stateName, _consequent);
    
    if (_consequent instanceof CTLOrNode)
      {
	if (_v.getParentAlgebra() instanceof TwoValAlgebra)
 	  return new DisjProofStep(_v, _state, stateName, _consequent,
 				   new Simple2ValDisjStrategy());
 	else
	  return 
	    new DisjProofStep(_v, _state, stateName, _consequent,
			      new GenericDisjStrategy(_v.getParentAlgebra()));
	
      }

    // refine this so that it handles all subtrees that
    //  are purely propositional
    if (_consequent instanceof CTLLeafNode)
      return new PropProofStep(_v, _state, stateName, _consequent);

    assert false : "Unknown CTLNode: " + _consequent;
    return null;
  }  
}
