package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class Helmut extends SMVModule
{
  AlgebraVariable a;
  AlgebraVariable b;
  AlgebraVariable c;
  AlgebraVariable d;

  
  public Helmut ()
  {
    super ("Helmut");
    a = declareAlgebraVariable ("a");
    b = declareAlgebraVariable ("b");
    c = declareAlgebraVariable ("c");
    d = declareAlgebraVariable ("d");
    
  }

  public MvSet computeInit ()
  {
    return stateA ();
  }
  

  
  public MvSet computeTrans ()
  {
    return stateA ().and (next (stateB ())).
      or (stateB().and (next (stateBD ()))).
      or (stateBD ().and (next (stateBD ()))).
      or (stateA ().and (next (stateC ()))).
      or (stateC ().and (next (stateD ()))).
      or (stateD ().and (next (stateD ())));
  }

  private MvSet stateA ()
  {
    // -- a /\ !b /\ !c /\ !d
    return a.and (b.not ()).and (c.not ()).and (d.not ());
  }
  private MvSet stateB ()
  {
    // -- !a /\ b /\ !c /\ !d
    return b.and (a.not ()).and (c.not ()).and (d.not ());
  }
  private MvSet stateC ()
  {
    // -- !a /\ !b /\ c /\ !d
    return c.and (a.not ()).and (b.not ()).and (d.not ());
  }
  private MvSet stateD ()
  {
    // -- !a /\ !b /\ !c /\ d
    return d.and (a.not ()).and (b.not ()).and (c.not ());
  }

  private MvSet stateBD ()
  {
    // -- !a /\ b /\ !c /\ d
    return d.and (a.not ()).and (b.mvSet ()).and (c.not ());
  }  
  
  void seal ()
  {
    super.seal ();
  }
  
}

