//represents a formula where a proofstep has value 'T'/'F'/...

package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;

public class ConstantFormula extends Formula 
{
  public ConstantFormula (AlgebraValue _val)
  {
    super (null, _val, null);
  }

  public String toString ()
  {
    return getValue ().toString ();
  }
  
}
