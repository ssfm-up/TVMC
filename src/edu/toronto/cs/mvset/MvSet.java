package edu.toronto.cs.mvset;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.davinci.*;

import java.util.*;

/**
 * A generic interface to represent a multi-valued set (mv-set)
 *
 * <p>
 *
 * This representation treats mv-set {@code v} as a as a function
 * {@code v}: L^n -> L where L is a set (possibly a lattice or an
 * algebra) and n is the arrity of the function.
 */

public interface MvSet
{

  /**
   * Constant for lattice meet operation.
   */
  public static final int MEET = 0;

  /**
   * Constant for lattice join operation.
   */
  public static final int JOIN = 1;

  /**
   * Constant for De Morgan negation.
   */
  public static final int NEG = 2;

  /**
   * Constant for material implication.
   */
  public static final int IMPL = 3;

  /**
   * Constant for lattice above comparison.
   */
  public static final int GEQ = 4;

  /**
   * Constant for lattice below comparison.
   */
  public static final int LEQ = 5;

  /**
   * Constant for equality.
   */
  public static final int EQ = 6;

  /**
   * Constant for existential quantification.
   */
  public static final int EXISTS = 7;

  /**
   * Constant for universal quantification.
   */
  public static final int FORALL = 8;

  /**
   * Constant for bilattice information order meet.
   */
  public static final int INFO_AND = 9;

  /**
   * Constant for bilattice information order join.
   */
  public static final int INFO_OR = 10;
  


  
  
  /** 
   * Creates a pointwise composition op is an operator: L x L -> L f
   * is the current mv-set result h (x) = f (x) op g (x)
   */
  MvSet ptwiseCompose (int op, MvSet g);

  /***
   *** Creates a ptwise negation
   *** h (x) = \neg f (x)
   ***/
  MvSet ptwiseNeg ();

  /***
   *** Ptwise compare: <=, >=, =.
   ***/
  MvSet ptwiseCompare (int op, MvSet g);

  /***
   *** Restricts an argument
   *** h (x_0, x_1, x_2, ...) = f (x_0, ..., value, ...)
   *** where value is substituted at argIdx
   ***/
  MvSet cofactor (int argIdx, AlgebraValue value);

  /***
   *** Restricts the MvSet to a particular L^n (or set of permutations
   *** on L^n if r is not total)
   ***/
  MvSet cofactor (AlgebraValue[] r);

  MvSet cofactor (MvSet point);
  

  // Expand is the dual of restrict; it adds the L^n parameterisation
  // in question with a truth value 'l'
  //  MvSet expand (int [] r, int l);

  /* XXX does stuff... :)  */
  //Set getRestrictions (int l);


  MvSet existAbstract (MvSet cube);

  MvSet forallAbstract (MvSet cube);


  /***
   *** Renames the arguments. newArgs is a map from old args to new so that
   *** h (x) = f (newArgs [0], newArgs [1], ...)
   ***/
  MvSet renameArgs (int[] newArgs);
  
  /***
   *** Evaluates this function on an input
   *** result = f (values [0], values [1], ...)
   ***/
  AlgebraValue evaluate (AlgebraValue[] values);

  /***
   *** Evaluates this function with respect to a state
   *** Note that we have a small discreptancy between what we mean 
   *** by states since here a state may contain primed and unprimed variables
   *** and thus represent a transition rather than a state
   ***/
  //int evaluate (int[] restriction);

  Set getPreImageArray(AlgebraValue v);
  

  /*** gets the factory */
  MvSetFactory getFactory ();

  /*** gets the lattice for the MvSet */
  IAlgebra getAlgebra ();

  public BitSet getImage();
  
  void reorder ();
  /*** Returns number of nodes */
  int size ();

  /*** Utility functions */
  MvSet and (MvSet v);
  MvSet or (MvSet v);
  MvSet not ();
  MvSet leq (MvSet v);
  MvSet geq (MvSet v);
  MvSet eq (MvSet v);
  MvSet impl (MvSet v);


  MvSet infoAnd (MvSet v);
  MvSet infoOr (MvSet v);
  MvSet infoNot ();
  

  // check if it is a constant function
  boolean isConstant();
  
  // if constant, return its value
  AlgebraValue getValue();

  // -- iterates over cubes of this mvset
  Iterator cubeIterator ();

  // -- iterates over minterms for a given value
  Iterator mintermIterator (MvSet vars, AlgebraValue val);
  
  // -- expands this MDD into an array
  //AlgebraValue[][] expandToArray ();
  DaVinciGraph toDaVinci ();

  MvRelation toMvRelation (MvSet invar, MvSet preVarCube, 
			   MvSet postVarCube,
			   int[] preToPostMap,
			   int[] postToPreMap);

  MvRelation toMvRelation (MvSet invar, MvSet invarPost,
			   MvSet preVarCube, 
			   MvSet postVarCube,
			   int[] preToPostMap,
			   int[] postToPreMap);

}


