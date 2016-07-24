// represents a formula where operator is '=' 

package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class EqualFormula extends ComparisonFormula 
{
  public EqualFormula (CTLNode _ctl, AlgebraValue _val, MvSet _state)
  {
    super (_ctl, _val, _state);
  }

  public String operatorToString ()
  {
    return "=";
  }
  
}
