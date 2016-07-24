package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class FairTester extends SMVModule
{
  AlgebraVariable p;
  AlgebraVariable q;

  public FairTester ()
  {
    super ("FairTester");
    p = declareAlgebraVariable ("p");
    q = declareAlgebraVariable ("q");
  }

  public MvSet computeInit ()
  {
    return q.eq (top).and (p.eq (top));
  }
  
  public MvSet computeTrans ()
  {
    // --
    // -- TRANS
    // --   q /\ p /\ next (!q /\ p)
    // --        \/
    // --   q /\ p /\ next (q /\ !p)
    // --        \/
    // --   !q /\ p /\ next (!q /\ p)
    // --   q /\ !p /\ next (q /\ !p)
    // -- 
    return q.eq (top).and (p.eq (top)).
      and (next (q.eq (bot)).and (next (p.eq (top)))).
      or
      (q.eq (top).and (p.eq (top)).
       and (next (q).eq (top).and (next (p).eq (bot)))).
      or 
      (q.eq (bot).and (p.eq (top)).
       and (next (q).eq (bot).and (next (p).eq (top)))).
      or
      (q.eq (top).and (p.eq (bot)).
       and (next (q).eq (top).and (next (p).eq (bot))));
  }
  
  void seal ()
  {
    super.seal ();
  }
  
}
