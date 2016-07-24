// represents a formula where operator is one of '[', ']' or '='

package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public abstract class ComparisonFormula extends Formula
{
  public ComparisonFormula (CTLNode _ctl, AlgebraValue _val, MvSet _state)
  {
    super (_ctl, _val, _state);
  }

  public abstract String operatorToString ();
  
  public String toString ()
  {
    return "||" +  CTLPrettyPrinter.toString (getConsequent ()) + "||(" + 
      getStateName ()  + ")"  + " " + operatorToString () + " " + getValue ();
    
  }
  
}
