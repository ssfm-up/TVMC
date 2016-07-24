package edu.toronto.cs.mvset;

import edu.toronto.cs.algebra.*;

/***
 *** A factory to produce MvSets
 ***/
public interface MvSetFactory 
{
  public static int DONT_CARE = -1;

  /***
   *** Given a value in L creates a function 
   *** f(x_0, x_1, ...) = value
   ***/
  MvSet createConstant (AlgebraValue value);
  
  /***
   *** Given an argument index (0 <= argIdx < n)
   *** constructs a projection function
   *** f(x_0, x_1, ...) = x_argIdx
   ***/
  MvSet createProjection (int argIdx);

  // -- top and bot constants
  MvSet top ();
  MvSet bot ();
  MvSet infoTop ();
  MvSet infoBot ();
  
  /**
   * <code>createCase</code> creates a case relative to argIdx
   * this is somewhat simillar to Ite (if-then-else) in CUDD
   * but extended to more than two children.
   *
   * @param argIdx an <code>int</code> value
   * @param children an array of  <code>MvSet[]</code> whose size 
   *                 must be equal the size of the logic
   *                 such that children [i] corresponds to the i^th child
   *                 of an mv-set we are creating
   * Note that argIdx must be smaller than any variable in children
   * to preserve ordering.
   * @return a <code>MvSet</code> value
   */
  MvSet createCase (int argIdx, MvSet[] children);
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  MvSet createPoint (AlgebraValue[] args,  AlgebraValue value); 
  //MvSet createPoint (MvState state, int value);
    

  /***
   *** builds a cube out of variables 
   ***/
  MvSet buildCube (int[] varIndex);


  /**
   ** Builds an mvset corresponding to   (arg = argVal) /\ value
   **/
  MvSet var (int arg, AlgebraValue argVal, AlgebraValue value);

  /***
   *** Gets the lattice for this factory.
   ***/
  IAlgebra getAlgebra ();

  void renew();
  
}





