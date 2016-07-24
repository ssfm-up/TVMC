//a formula is represented as [phi](s) op value

package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;


public class Formula
{

  // BD: here's a hack for formula-typing
  public static int BELOW = 0;
  public static int ABOVE = 1;
  public static int EQUAL = 2;
  
  //CTlNode
  CTLNode ctl;

  // Algebravalue is represented by a CTLNode
  AlgebraValue val;
  // BD.. uh, no it's not!

  // state
  MvSet state;
  
  // to generate stateName
  public static StateName stateName = new StateName ();

  // map to store the names of the state
  // static Map listState = new HashMap ();
  
  // state names are labled as a0,a1,....
  static int count = 0;
  
  Formula parentFormula;

  public Formula (CTLNode _ctl, AlgebraValue _val, MvSet _state)
  {
    ctl = _ctl;
    val = _val;
    state = _state;
  }

  //function to get the consequent

  public CTLNode getConsequent()
  {
    return ctl;
  }
  
  // function to set parent formula
  
  public void setParentFormula (Formula f)
  {
    parentFormula = f;
  }
  

  // function to return parent formula

  public Formula getParentFormula ()
  {
    return parentFormula;
  }

  //function to get the ALgebravalue

  public AlgebraValue getValue ()
  {
    return val;
  }
  
  //function to get the state

  public MvSet getState ()
  {
    return state;
  }

  public String getStateName ()
  {

    //return state.toString ();
    //return stateName.getStateName (state);
    return "FOO";
  }
  

  // -- creates the same type of formula as 'f' with different arguments
  public static Formula duplicate (Formula f, CTLNode ctl, AlgebraValue val,
				   MvSet state)
  {
    if (f instanceof EqualFormula)
      return new EqualFormula (ctl, val, state);
    if (f instanceof BelowFormula)
      return new BelowFormula (ctl, val, state);
    if (f instanceof AboveFormula)
      return new AboveFormula (ctl, val, state);
    return null;
  }
}
