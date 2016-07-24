package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class AxelEx4 extends SMVModule
{
  AlgebraVariable w;
  AlgebraVariable p;
  AlgebraVariable m;
  AlgebraVariable temp1;
  AlgebraVariable temp2;

  
  public AxelEx4 ()
  {
    super ("AxelEx4");
    w = declareAlgebraVariable ("w");
    p = declareAlgebraVariable ("p");
    m = declareAlgebraVariable ("m");
    temp1 = declareAlgebraVariable ("temp1");
    temp2 = declareAlgebraVariable ("temp2");
  }

  public MvSet computeInit ()
  {
    // --   ((!w | p) & !temp1 & (!m | !p) & !temp2) 
    return (w.not ().or (p.mvSet ())).
      and (temp1.not ()).
      and (m.not ().or (p.not ())).
      and (temp2.not ());
  }
  
  public MvSet computeTrans ()
  {
    return computeTrans1 ().and (computeTrans2 ());
  }

  private MvSet computeTrans2 ()
  {
    // --    next((m&p)|temp2) = temp2
    return next ((m.and (p)).or (temp2.mvSet ())).eq (temp2.mvSet ());
    
  }
  
  private MvSet computeTrans1 ()
  {
    // --    next((w & !p)|temp1) = temp1
    return next (w.and (p.not ()).or (temp1.mvSet ())).eq (temp1.mvSet ());
  }
  
  
  void seal ()
  {
    super.seal ();
  }
  
}

/*****
 ***** Original SMV file *************

--- 
--- Model for: G (w -> p) & G (m -> !p)
---

MODULE main
VAR
   w : boolean;
   p : boolean;
   m : boolean;
   temp2 : boolean; 
   temp1 : boolean; 
TRANS
   next((m&p)|temp2) = temp2 
TRANS
   next((w & !p)|temp1) = temp1 

INIT
  ((!w | p) & !temp1 & (!m | !p) & !temp2) ;

SPEC
  AG ( (w & ! m) | (!w & m) | (!m & !w))
*****/
