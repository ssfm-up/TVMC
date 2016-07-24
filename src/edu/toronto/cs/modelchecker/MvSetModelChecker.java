package edu.toronto.cs.modelchecker;

import java.io.*;
import java.util.*;


import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.caching.*;


/***
 *** ModelChecker engine
 ***/
public class MvSetModelChecker
{

  // -- factory to create mvSets
  MvSetFactory mvSetFactory;
  


  // -- the transition relation
  MvRelation trans;

  // -- maps current state variables to next state variables
  int[] prime;
  
  // -- all next state variables in a cube so that we can quantify them out
  MvSet primeCube;
  // -- all pre-state variables in a cube
  MvSet unPrimeCube;
  
  // -- this is going to be used to cache stuff 
  Cache cache;


  private MvSetModelChecker (MvRelation _trans, MvSet _primeCube, 
			    MvSet _unprimeCube, int[] _prime)
  {
    trans = _trans;
    prime = _prime;
    primeCube = _primeCube;
    unPrimeCube = _unprimeCube;
    mvSetFactory = primeCube.getFactory ();

    cache = new UnboundedMapCache ();

    assert _unprimeCube.getFactory () == primeCube.getFactory ();

  }
  

  XKripkeStructure xkripke;
  
  public MvSetModelChecker (XKripkeStructure model)
  {
    this (model.getTrans (), model.getPrimeCube (), model.getUnPrimeCube (),
	  model.getPrime ());
    xkripke = model;
  }

  public XKripkeStructure getXKripke ()
  {
    return xkripke;
  }
  
  

  // renew the MvSetFactory, and clear the cache
  public void renew ()
  {
    mvSetFactory.renew();
    cache = new UnboundedMapCache ();
  }
  

  public MvSetFactory getMvSetFactory ()
  {
    return mvSetFactory;
  }

  public MvRelation getTrans ()
  {
    return trans;
  }

  
  public int[] getPrime()
  {
    return prime;
  }
  public MvSet getUnPrimeCube ()
  {
    return unPrimeCube;
  }
  

  /* checks a ctl formula and restricts it to the initial state */
  public MvSet __checkCTL (CTLNode ctl, MvSet init)
  {
    return checkCTL (ctl).and (init).existAbstract (getUnPrimeCube ());
  }

  public MvSet checkCTL (CTLNode ctl, MvSet state)
  {
    MvSet res = checkCTL (ctl).cofactor (state);

    assert res.isConstant ();
    
    return res;    
  }
  


  // -- top level entry function, given a CTL expression returns an MvSet
  // -- corresponding to the answer
  public MvSet checkCTL (CTLNode v)
  {

    MvSet result = null;

    if (v.getClass () == CTLMvSetNode.class)
      result =  ((CTLMvSetNode)v).getMvSet ();
    
    else if (v.getClass () == CTLConstantNode.class)
      result =  mvSetFactory.createConstant 
	(((CTLConstantNode)v).getValue ());

    else if (v.getClass () == CTLAtomPropNode.class)
      result =  ((CTLAtomPropNode)v).getMvSet ();
    
    if (result != null) return result;



    // rewrite the ctl here, since this is what gets called by the user
    CTLNode ctl = getXKripke ().rewrite (v);
    //CTLNode ctl = v;

    // -- first we check the cache
    //StopWatch cacheSw = new StopWatch ();
    result = (MvSet) cache.get (ctl);
    //System.err.println ("CACHE LOOKUP IN: " + cacheSw);
    
    if (result != null)
      {
	//System.err.println ("CACHE HIT FOR: " + ctl);
	return result;
      }
    else // formula not cached
      {
	//System.err.println ("CACHE MISS FOR: " + ctl);
	// -- compute the solution
	result = computeCTL (ctl);
	// -- cache the result
	cache.put (ctl, result);
	return result;
      }    
  }

  // -- model checking functions
  MvSet computeCTL (CTLNode ctl)
  {
//     if (ctl instanceof CTLMvSetNode)
//       return ((CTLMvSetNode)ctl).getMvSet ();

    if (ctl instanceof CTLNegNode) 
      return checkCTL (ctl.getRight ()).not ();

    else if (ctl instanceof CTLAndNode)
      return checkCTL (ctl.getLeft ()).and (checkCTL (ctl.getRight ()));
    
    else if (ctl instanceof CTLOrNode)
      return checkCTL (ctl.getLeft ()).or (checkCTL (ctl.getRight ()));
    else if (ctl instanceof CTLImplNode)
      return checkCTL (ctl.getLeft ()).impl (checkCTL (ctl.getRight ()));
    
    else if (ctl instanceof CTLEXNode)
      return checkEX (ctl.getRight (), ctl.getFairness ());

    else if (ctl instanceof CTLAXNode)
      return checkAX (ctl.getRight (), ctl.getFairness ());
    else if (ctl instanceof CTLAFNode)
      return checkAF (ctl.getRight (), ctl.getFairness ());

    else if (ctl instanceof CTLPreEXNode)
      {
	System.err.println ("WARNING: using depricated PreEX!");
	// R /\ prime (ctl)
	return trans.toMvSet ().
	  and (checkCTL (ctl.getRight ()).renameArgs (prime));
      }
    

    else if (ctl instanceof CTLEUiNode)
      return checkEUi (((CTLEUiNode)ctl).getI (), 
		       ctl.getLeft (), ctl.getRight ());

    else if (ctl instanceof CTLEUNode)
      return checkEU (ctl.getLeft (), ctl.getRight (), ctl.getFairness ());

    else if (ctl instanceof CTLAUiNode)
      return checkAUi (((CTLAUiNode)ctl).getI (), 
		       ctl.getLeft (), ctl.getRight ());

    else if (ctl instanceof CTLAUNode)
      return checkAU (ctl.getLeft (), ctl.getRight ());

    else if (ctl instanceof CTLARNode)
      return checkAR (ctl.getLeft (), ctl.getRight ());
    else if (ctl instanceof CTLERNode)
      return checkER (ctl.getLeft (), ctl.getRight ());

    else if (ctl instanceof CTLEGNode)
      return  checkEG (ctl.getRight (), ctl.getFairness ());

    else if (ctl instanceof CTLAGNode)
      return  checkAG (ctl.getRight (), ctl.getFairness ());

    else if (ctl instanceof CTLEqualsNode)
      return checkCTL (ctl.getLeft ()).eq (checkCTL (ctl.getRight ()));

    else if (ctl instanceof CTLUnderNode)
      return checkCTL (ctl.getLeft ()).leq (checkCTL (ctl.getRight ()));

    else if (ctl instanceof CTLOverNode)
      return checkCTL (ctl.getLeft ()).geq (checkCTL (ctl.getRight ()));

    else if (ctl instanceof CTLConstantNode)
      return mvSetFactory.createConstant 
	(((CTLConstantNode)ctl).getValue ());

    else if (ctl instanceof CTLAtomPropNode)
      return ((CTLAtomPropNode)ctl).getMvSet ();

    // -- should never be here
    assert false : "Unknown CTL node " + ctl + "\n" + " type: " + 
      ctl.getClass ();
    return null;
  }  


  MvSet checkEX (CTLNode ctl, CTLNode[] fairness)
  {

    //return checkCTL (ctl.preEX ()).existAbstract (primeCube);
    
    //StopWatch sw = new StopWatch ();
    //System.err.print ("EX done in ...");
    MvSet result = trans.bwdImage (checkCTL (ctl));
    //System.err.println (" " + sw);
    return result;
  }

  MvSet checkFairAX (CTLNode ctl, CTLNode[] fairness)
  {
    return null;
  }
  
  MvSet checkAX (CTLNode ctl, CTLNode[] fairness)
  {
    if (fairness != null && fairness.length > 0)
      return checkFairAX (ctl, fairness);
    
    MvSet phi = checkCTL (ctl).renameArgs (prime);
    return trans.toMvSet ().not ().or (phi).forallAbstract (primeCube);
  }


  MvSet __frontier__checkEU (CTLNode ctll, CTLNode ctlr, CTLNode[] fairness)
  {
    // XXX this is actaully EF
    MvSet currEF = checkCTL (ctlr);
    MvSet prevEF;
    
    MvSet frontier = currEF;
    
    int i = 0;
    do 
      {
	i++;
//	System.out.println ("Check EF frontier: " + i);
	prevEF = currEF;
	frontier = trans.bwdImage (frontier);
	currEF = prevEF.or (frontier);
      } while (!prevEF.equals (currEF));

    return prevEF;
  }
  
  MvSet checkEU (CTLNode ctll, CTLNode ctlr, CTLNode[] fairness)
  {
    int i = 0;
    MvSet prevEU, curEU;
    CTLNode currCTL = ctll.eu(0, ctlr);
    
    // -- initial value of EU -- using eu_0 just to cache the result
    curEU = checkCTL (currCTL);

    // -- compute the fixpoint
    do
      {
	++i;
	//StopWatch sw = new StopWatch ();
	//System.out.println ("Check EU: iteration: " + i);
	prevEU = curEU;
	currCTL = ctll.eu(i,ctlr);
	curEU = checkCTL (currCTL);
//  	System.out.println ("current result: ");
//  	curEU.toString ();
	
	//System.err.println (" finished in " + sw);
// 	if (curEU.geq (mvSetFactory.infoTop ()).equals 
// 	    (prevEU.geq (mvSetFactory.infoTop ())))
// 	  {
// 	    System.out.println ("Reached >= D fixpoint");
// 	  }
	
      }
    while (!prevEU.equals (curEU)); // && i < 1000 /* for safety */);
    assert prevEU.equals (curEU) : "Too many iterations";

    return curEU;
  }

  MvSet checkEUi (int bound, CTLNode ctll, CTLNode ctlr)
  {

    
    if (bound == 0) return checkCTL (ctlr);

    
    // -- use E[ctll Ui ctlr] = ctlr \/ (ctll /\ EX E[ctll Ui-1 ctlr])
    return checkCTL (ctlr.or (ctll.and (ctll.eu (bound - 1, ctlr).ex ())));
  }
  


  MvSet checkAU (CTLNode ctll, CTLNode ctlr)
  {

    int i = 0;
    MvSet prevAU;
    MvSet curAU;


    curAU = checkCTL (ctll.au (0, ctlr));
     
    // -- compute fix point
    do 
      {
	i++;
	prevAU = curAU;
	curAU = checkCTL (ctll.au (i, ctlr));
      }
    while (!prevAU.equals (curAU) && i < 1000);
    assert prevAU.equals (curAU) :  "Too many iterations";

    return curAU;

  }

  MvSet checkAUi (int bound, CTLNode ctll, CTLNode ctlr)
  {
    if (bound == 0) return checkCTL (ctlr);
    // -- use A[ctll Ui ctlr] = ctlr \/ (ctll /\ AX A[ctll Ui-1 ctlr])
    return checkCTL (ctlr.or (ctll.and (ctll.au (bound - 1, ctlr).ax ())));
  }
  

  MvSet checkAF (CTLNode ctl, CTLNode[] fairness)
  {
    assert fairness != null && fairness.length > 0 : 
      "We only do fair AF directly, others should be expanded into AU";
    
    return checkFairAF (ctl, fairness);
  }
  
  
  MvSet checkFairAF (CTLNode phi, CTLNode[] fairness)
  {
    // -- 
    // -- fairAF phi == \mu Z . phi \/ \/_{i = 1,..,k} AXA[ctl R !c_i \/ Z]
    // --
    
    MvSet curAF = mvSetFactory.bot ();
    MvSet prevAF;
    
    do
      {
	prevAF = curAF;

	CTLNode z = CTLFactory.createCTLMvSetNode (prevAF);
	
	CTLNode ctl = phi;
	
	for (int i = 0; i < fairness.length; i++)
	  ctl = ctl.or (phi.ar (fairness [i].neg ().or (z)).ax ());
	curAF = checkCTL (ctl);
      }
    while (!curAF.equals (prevAF));

    return prevAF;
  }
  
  
  MvSet checkER (CTLNode phi, CTLNode psi)
  {
    // -- E [x R y] = ! A[!x U !y]
    // --           = ! \mu Z. !y \/ (!x /\ AX Z)
    // --           =  \nu Z . y /\ (x \/ EX z)
    // --           = \nu Z. (y /\ x) \/ (y /\ EX z)
    MvSet curER = mvSetFactory.top ();
    MvSet prevER;

    do 
      {
	prevER = curER;

	CTLNode z = CTLFactory.createCTLMvSetNode (prevER);
	
	curER = checkCTL (phi.and (psi).or (psi.and (z.ex ())));
      }
    while (! prevER.equals (curER));
    return prevER;
    
  }
  

  MvSet checkAR (CTLNode phi, CTLNode psi)
  {
    // -- A [phi R psi] =  ! E[!phi U !psi]
    // -- == \nu Z. phi /\ psi \/ psi AX Z

    MvSet curAR = mvSetFactory.top ();
    MvSet prevAR;

    do 
      {
	prevAR = curAR;

	CTLNode z = CTLFactory.createCTLMvSetNode (prevAR);
	
	curAR = checkCTL (phi.and (psi).or (psi.and (z.ax ())));
      }
    while (! prevAR.equals (curAR));
    return prevAR;
  }
  


  MvSet checkFairAG (CTLNode ctl, CTLNode[] fairness)
  {
    // --
    // -- fairAG ctl == AG (fair -> ctl)
    // --
    // -- where 'fair' is true in a state that is at the beginning
    // -- of some fair path, which is just
    // -- ||fairEG T||(s) != F

    CTLNode fair = CTLFactory.createCTLMvSetNode (computeFair (fairness));

    return checkCTL ((ctl.or (fair.neg ())).ag ());
  }


  // -- computes 'fair' predicate
  private MvSet computeFair (CTLNode[] fairness)
  {
    CTLNode fair = CTLFactory.createCTLMvSetNode (mvSetFactory.top ());
    fair = fair.eg (fairness);
    fair = fair.eq (CTLFactory.createCTLMvSetNode (mvSetFactory.bot ()));
    fair = fair.neg ();
    return checkCTL (fair);
  }
  
  
  MvSet checkAG (CTLNode ctl, CTLNode[] fairness)
  {
    if (fairness != null && fairness.length > 0) 
      return checkFairAG (ctl, fairness);
    
    MvSet phi = checkCTL (ctl);

    // make some fairness adjustments if necessary
    
    MvSet curAG = mvSetFactory.top ();
    MvSet prevAG = null;
    
    int count = 0;
    do {
    //  System.out.println ("Check AG: " + (count++));
      prevAG = curAG;
      // -- curAG = f /\ AX prevAG
      curAG = 
	phi.and (checkCTL (CTLFactory.createCTLMvSetNode (prevAG).ax ()));
    } while (!curAG.equals (prevAG));

    return prevAG;
  }
  

  MvSet checkEG (CTLNode ctl, CTLNode[] fairness)
  {
    if (fairness != null && fairness.length > 0)
      return checkFairEG (ctl, fairness);
    
	System.out.println ("Checking without fairness");
	
    // XXX For now ignore fairness
    MvSet phi = checkCTL (ctl);

    // make some fairness adjustments if necessary
    
    MvSet curEG = mvSetFactory.top ();
    MvSet prevEG = null;
    
    do {
      prevEG = curEG;
      // -- curEG = phi /\ EX prevEG
      curEG = 
	phi.and (checkCTL (CTLFactory.createCTLMvSetNode (prevEG).ex ()));
    } while (!curEG.equals (prevEG));

    return prevEG;
  }
  
  MvSet checkFairEG (CTLNode phi, CTLNode[] fairness)
  {
	//System.out.println ("Checking EG with fairness: " + 
	//					Arrays.asList (fairness));
	
    MvSet curEG = mvSetFactory.top ();
    MvSet prevEG = null;

    // -- this should compute
    // -- \nu Z. phi /\ /\_{i = 1,..,k} EX E [phu U c /\ Z]

    do
      {
	prevEG = curEG;

	// construct the ex terms
	CTLNode z = CTLFactory.createCTLMvSetNode (prevEG);
	
	CTLNode ctl = phi;
	
	for (int i = 0; i < fairness.length; i++)
	  ctl = ctl.and (phi.eu (fairness [i].and (z)).ex ());

	curEG = checkCTL (ctl);
      }
    while (!curEG.equals (prevEG));

    return prevEG;
  }  
}

