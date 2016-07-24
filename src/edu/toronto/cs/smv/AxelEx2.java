package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class AxelEx2 extends SMVModule
{
  AlgebraVariable w;
  AlgebraVariable t;
  AlgebraVariable g;
  AlgebraVariable temp1;
  AlgebraVariable temp2;

  
  public AxelEx2 ()
  {
    super ("AxelEx2");
    w = declareAlgebraVariable ("w");
    t = declareAlgebraVariable ("t");
    g = declareAlgebraVariable ("g");
    temp1 = declareAlgebraVariable ("temp1");
    temp2 = declareAlgebraVariable ("temp2");
  }

  public MvSet computeInitSingleState ()
  {
    return w.and (t.and (g.and (temp2.and (temp1.not ()))));
  }
  
  public MvSet computeInit ()
  {
    // -- ((!w | t) &  !temp1  & ((g & !t)  |  temp2 ))
    return t.or (w.not ()).and (temp1.not ()).
      and (g.and (t.not ()).or (temp2.mvSet ()));
  }
  
  public MvSet computeTrans ()
  {
    return computeTrans1 ().and (computeTrans2 ());
  }

  private MvSet computeTrans2 ()
  {
    //next((g & !t) | temp2) = temp2    
    return next (g.and (t.not ()).or (temp2.mvSet ())).eq (temp2.mvSet ());
  }
  
  private MvSet computeTrans1 ()
  {
    // -- next((w & !t) |temp1) = temp1 
    return next (w.and (t.not ()).or (temp1.mvSet ())).eq (temp1.mvSet ());

  }
  
  
  void seal ()
  {
    super.seal ();
  }
  
}

/*****
 ***** Original SMV file *************
---
--- A model for LTL G (w -> t) & !G(g -> t)
---

MODULE main
VAR
   w: boolean;
   t: boolean;
   g: boolean;
   temp2 : boolean; 
   temp1 : boolean; 
TRANS
   next((g & !t) | temp2) = temp2 
TRANS
   next((w & !t) |temp1) = temp1 


FAIRNESS
   ((!g | t) & !temp2) | (g & !t) 
FAIRNESS
   ((!w | t) & !temp1) | (w & !t) 
INIT
   ((!w | t) &  !temp1  & ((g & !t)  |  temp2 ))
SPEC
  AF (g & !w)
SPEC
  AF (g & !t)

*****/
