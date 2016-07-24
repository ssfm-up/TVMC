package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class ConcurExample extends SMVModule
{
  AlgebraVariable p;
  AlgebraVariable q;

  MvSet maybe;
  
  public ConcurExample ()
  {
    super ("ConcurExample");
    p = declareAlgebraVariable ("p");
    q = declareAlgebraVariable ("q");
  }

  public MvSet computeInit ()
  {
    return q.eq (top).and (p.eq (maybe));
  }
  
  public MvSet computeTrans ()
  {
    return q.eq (top).and (p.eq (maybe)).
      and (next (q).eq (maybe).and (next (p).eq (top))).
      or 
      (q.eq (top).and (p.eq (maybe)).and (maybe).
       and (next (q).eq (top).and (next (p).eq (top)))).
      or 
      (q.eq (maybe).and (p.eq (top)).
       and (next (q).eq (maybe).and (next (p).eq (top)))).
      or 
      (q.eq (top).and (p.eq (top)).
       and (next (q).eq (top).and (next (p).eq (top))));
  }
  
  void seal ()
  {
    super.seal ();
    maybe = mvSetFactory.createConstant (getAlgebra ().getValue ("M"));
  }
  
}
