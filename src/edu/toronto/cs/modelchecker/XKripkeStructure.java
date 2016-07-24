package edu.toronto.cs.modelchecker;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;
import java.util.*;


/**
 * A structure over which we do model checking, every model is compiled
 * into this
 */
public class XKripkeStructure implements CTLReWriter
{
  // -- the transition relation
  MvRelation trans;
  // -- initial constraint -- inital state
  MvSet init;

  // -- a map from unprimed to primed variables
  int[] prime;

  int[] unprime;
  
  // -- a cube with all primed variables
  MvSet primeCube;
  MvSet unPrimeCube;

  // -- map from variables to names
  String[] varNames;
  
  // -- algebra used to specify this XKripke structure
  IAlgebra algebra;
  
  // -- number of DD variables
  int numDDVars;

  // -- namber of variables in the modela
  int numVars;
  
  // The name of the model to be dispalyed by the GUI
  String name;

  
  CTLReWriter rewriter = null;
  StatePresenter statePresenter;

  public XKripkeStructure (MvRelation _trans, MvSet _init, int[] _prime, 
			   MvSet _primeCube, MvSet _unPrimeCube, 
			   String[] _varNames, 
			   IAlgebra _algebra, int _numDDVars, int _numVars, 
			   CTLReWriter _rewriter, 
			   StatePresenter _statePresenter)
  {
    this(_trans, _init,  _prime, _primeCube, _unPrimeCube, 
			 _varNames, _algebra, _numDDVars, _numVars, 
			 _rewriter,  _statePresenter, "");
  }
  public XKripkeStructure (MvRelation _trans, MvSet _init, int[] _prime, 
			   MvSet _primeCube, MvSet _unPrimeCube, 
			   String[] _varNames, 
			   IAlgebra _algebra, int _numDDVars, int _numVars, 
			   CTLReWriter _rewriter, 
			   StatePresenter _statePresenter,
                           String _name)
  {
    trans = _trans;
    init = _init;
    prime = _prime;
    unprime = new int[prime.length];
    for (int i=0; i<prime.length; i++){
      if (prime[i] != i) 
      unprime[prime[i]] = i;
    }
    primeCube = _primeCube;
    unPrimeCube = _unPrimeCube;
    varNames = _varNames;
    algebra = _algebra;
    numDDVars = _numDDVars;
    numVars = _numVars;
    rewriter = _rewriter;
    statePresenter = _statePresenter;
    name = _name;

    //assert trans.getFactory () == init.getFactory ();
    //assert primeCube.getFactory () == trans.getFactory ();
  }
  
  public CTLNode rewrite (CTLNode ctlNode)
  {
    if (rewriter == null) return ctlNode;
    return rewriter.rewrite (ctlNode);
  }
  
  public MvRelation getTrans ()
  {
    return trans;
  }
  public MvSet getInit ()
  {
    return init;
  }
  public MvSet getPrimeCube ()
  {
    return primeCube;
  }
  public MvSet getUnPrimeCube ()
  {
    return unPrimeCube;
  }
  public MvSetFactory getMvSetFactory ()
  {
    return unPrimeCube.getFactory ();
  }
  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  public int getNumDDVars ()
  {
    return numDDVars;
  }
  public int[] getPrime ()
  {
    return prime;
  }  

  public int[] getUnPrime()
  {
    return unprime;
  }
  
  public String getName ()
  {
    return name;
  }
  
  public void setName (String v)
  {
    name = v;
  }
  
  public StatePresenter getStatePresenter ()
  {
    return statePresenter;
  }

  public AlgebraValue[] getSingleState (AlgebraValue[] stateAssignment)
  {
    return getSingleState (stateAssignment, algebra.bot ());
  }
  
  // -- takes a state as variable/value pairs, and sets all don't cares to
  // -- val
  public AlgebraValue[] getSingleState (AlgebraValue[] state, 
					AlgebraValue val)
  {
    int[] prime = getPrime ();
    for (int i=0; i < state.length; i++)
      if (prime [i] != i && state [i].equals (algebra.noValue ()))
	state [i] = val;
    return state;
  }
}
