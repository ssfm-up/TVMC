package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.MvSet;

/***
 *** Converts an collection of variable, value assignment to a set of CTL
 *** expressions that evaluate to top on this assignment, and to bot 
 *** on any other assignment
 ***/
public interface StatePresenter
{
  // -- one set of assignments to CTL formulas
  CTLNode[] toCTL (AlgebraValue[] state);
  // -- many assignments to many CTL formulas
  CTLNode[][] toCTL (AlgebraValue[][] states);
  
  // -- a cube to ctl representation
  CTLNode[] toCTL (MvSet cube);
  //CTLNode[][] doCTL (MvSet mvSet);
  
}
