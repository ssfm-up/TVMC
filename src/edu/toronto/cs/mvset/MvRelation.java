package edu.toronto.cs.mvset;

/**
 * A generic transformer for backward (pre) and forward (post) iterations.
 *
 *
 * <p>Created: Thu Jun 10 22:56:31 2004
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version
 */

public interface MvRelation 
{

  /**
   *  Computes forward image (post) of {@code v}
   *
   *
   * @param v an input set
   * @return the result of forward image
   */
  MvSet fwdImage (MvSet v);
 
  /**
   * Computes the backward image (a.k.a, pre, EX) of {@code v}.
   *
   * @param v an input set
   * @return the result of backward image
   */
  MvSet bwdImage (MvSet v);

  /**
   * Computes the logical dual of backward image (a.k.a. AX) of {@code v}.
   * <p>
   * {@code r.dualBwdImage (v) == r.bwdImage (v.not ()).not ()}
   *
   * @param v an input set
   * @return the result of dual backward image
   */
  MvSet dualBwdImage (MvSet v);


  /**
   * Converts this transformer to an {@code MvSet} over pre- and
   * post-variables, if possible.
   *
   * @return an {@code MvSet} representation of this transformer
   */
  MvSet toMvSet ();

  /**
   * Sets the transition part of the relation from an mvset over pre-
   * and post- variables
   *
   * @param v a <code>MvSet</code> value
   */
  void setTrans (MvSet v);


  /**
   * return cube of pre-state variables
   *
   */
  MvSet getPreVariablesCube ();
  
  

  /**
   * get cube of post-state variables
   *
   */
  MvSet getPostVariablesCube ();
  /**
   * get map from pre- to post-state variables
   *
   */
  int[] getPreToPostMap ();

  /**
   * get map from post- to pre-state variables
   *
   */
  int[] getPostToPreMap ();
  

}
