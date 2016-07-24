package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class AxelEx1b extends SMVModule
{
  AlgebraVariable r;
  AlgebraVariable a;
  AlgebraVariable u;
  AlgebraVariable temp1;
  AlgebraVariable temp2;
  AlgebraVariable temp3;
  AlgebraVariable temp4;

  
  public AxelEx1b ()
  {
    super ("AxelEx1b");
    r = declareAlgebraVariable ("r");
    a = declareAlgebraVariable ("a");
    u = declareAlgebraVariable ("u");
    temp1 = declareAlgebraVariable ("temp1");
    temp2 = declareAlgebraVariable ("temp2");
    temp3 = declareAlgebraVariable ("temp3");
    temp4 = declareAlgebraVariable ("temp4");
  }

  public MvSet computeInitSingleState ()
  {
    return r.and (a.and (u.and (temp4.and (temp2)))).
      and (temp3.not ()).and (temp1.not ());    
  }
  
  public MvSet computeInit ()
  {
    // -- ((!r | a | temp2) & !temp1 & (!r | !a | u | temp4) & !temp3)
    return (temp2.or (a.or (r.not ()))).
      and (temp1.not ()).
      and (temp3.not ()).
      and (temp4.or (u.or (a.not ()).or (r.not ())));
  }
  
  public MvSet computeTrans ()
  {
    return computeTrans1 ().and (computeTrans2 ()).
      and (computeTrans3 ().and (computeTrans4 ()));
  }

  
  private MvSet computeTrans3 ()
  {
    // --    next(((r & a & !u & !temp4)|temp3)) = temp3 

    return next (temp3.or (r.and (a).and (u.not ()).and (temp4.not ()))).
      eq (temp3.mvSet ());
  }

  private MvSet computeTrans4 ()
  {
    // -- next((u | temp4)) = temp4 
    return next (u.or (temp4)).eq (temp4.mvSet ());
  }
  
  private MvSet computeTrans1 ()
  {
    // -- next(((r & !a & !temp2) | temp1)) = temp1 

    return next (temp1.or (r.and (a.not ()).and (temp2.not ()))).
      eq (temp1.mvSet ());
  }
  
  private MvSet computeTrans2 ()
  {
    // -- next((a | temp2)) = temp2 
    return next (a.or (temp2)).eq (temp2.mvSet ());
  }
  
  
  void seal ()
  {
    super.seal ();
  }
  
}

/*****
 ***** Original SMV file *************

---
--- A model for LTL formula
--- G (r -> F a) & G (r & a -> F u)
---
MODULE main
VAR
  r: boolean;
  a: boolean;
  u: boolean;
   temp4 : boolean; 
   temp3 : boolean; 
   temp2 : boolean; 
   temp1 : boolean; 
TRANS
   next(((r & a & !u & !temp4)|temp3)) = temp3 
TRANS
   next((u | temp4)) = temp4 
TRANS
   next(((r & !a & !temp2) | temp1)) = temp1 
TRANS
   next((a | temp2)) = temp2 

FAIRNESS
   (!(u | temp4))|( u ) 
FAIRNESS
   (!(a | temp2))|( a ) 

INIT
  ((!r | a | temp2) & !temp1 & (!r | !a | u | temp4) & !temp3)

SPEC
 AG (r -> AF (u | !r))

 When run with the fairness conditions for the query
 AG (!r | AF ?x{u,r}) we get 2! solutions
    a) ?x = r   -- that is AG (r -> AF r) is true, but this is trivial 
    b) ?x = !r | (r & u) -- that is AG (r -> AF (!r | (r & u)))
*****/
