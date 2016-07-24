package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;


public class AxelEx1a extends SMVModule
{
  AlgebraVariable r;
  AlgebraVariable a;
  AlgebraVariable u;
  AlgebraVariable tmp1;
  AlgebraVariable tmp2;
  AlgebraVariable tmp3;
  AlgebraVariable tmp4;
  AlgebraVariable tmp5;

  
  public AxelEx1a ()
  {
    super ("AxelEx1a");
    r = declareAlgebraVariable ("r");
    a = declareAlgebraVariable ("a");
    u = declareAlgebraVariable ("u");
    tmp1 = declareAlgebraVariable ("tmp1");
    tmp2 = declareAlgebraVariable ("tmp2");
    tmp3 = declareAlgebraVariable ("tmp3");
    tmp4 = declareAlgebraVariable ("tmp4");
    tmp5 = declareAlgebraVariable ("tmp5");
  }

  
  public MvSet computeInit ()
  {
    // -- (!r | a | tmp2)
    // -- & !tmp1 & (!r | !a | u | tmp4) & !tmp3 & ((r & !u & !tmp4) | tmp5)

    return 
      tmp1.not ().and (tmp3.not ()).and
      (a.and (tmp2.and (r.not ()))).and
      (tmp4.or (u.or (a.not ().or (r.not ())))).and
      (tmp5.or (r.and (u.not ().and (tmp4.not ()))));
  }
  
  public MvSet computeTrans ()
  {
    return computeTrans1 ().and (computeTrans2 ()).
      and (computeTrans3 ().and (computeTrans4 ())).
      and (computeTrans5 ());
  }

  
  private MvSet computeTrans5 ()
  {
    // --   tmp5 = next((r& !u & !tmp4)|tmp5)  

    return next (tmp5.or (r.and (u.not ().and (tmp4.not ()))))
      .eq (tmp5.mvSet ());
    
  }
  
  private MvSet computeTrans3 ()
  {
    // -- tmp3 = next(  (r & a & !u & !tmp4)  | tmp3) 
    return next (tmp3.or (r.and (a.and (u.not ().and (tmp4.not ())))))
      .eq (tmp3.mvSet ());
    
  }

  private MvSet computeTrans4 ()
  {
    // --   tmp4 = next(u | tmp4)
    return next (u.or (tmp4)).eq (tmp4.mvSet ());
  }
  
  private MvSet computeTrans1 ()
  {
    // -- tmp1 = next((r&  !a & !tmp2) | tmp1)
    return next (tmp1.or (r.and (a.not ().and (tmp2.not ())))).
      eq (tmp1.mvSet ());

  }
  
  private MvSet computeTrans2 ()
  {
    // -- tmp2 = next(a | tmp2)
    return next (a.or (tmp2)).eq (tmp2.mvSet ());
  }
  
  
  void seal ()
  {
    super.seal ();
  }
  
}

/*****
 ***** Original SMV file *************

-- G(r -> F a) & G(r & a -> F u) & !G(r -> F u)
MODULE main
VAR
   tmp5 : boolean; 
   tmp4 : boolean; 
   tmp3 : boolean; 
   tmp2 : boolean; 
   tmp1 : boolean; 
   r:boolean;
   a:boolean;
   u:boolean;
TRANS
  tmp5 = next((r& !u & !tmp4)|tmp5)  
TRANS
  tmp3 = next(  (r & a & !u & !tmp4)  | tmp3) 
TRANS
  tmp4 = next(u | tmp4)
TRANS
  tmp1 = next((r&  !a & !tmp2) | tmp1)
TRANS
  tmp2 = next(a | tmp2)

--FAIRNESS
--   ( (!r| u | tmp4) & !tmp5) | ( r & !u & !tmp4 )
--FAIRNESS
--   (!a & !tmp2) |  a  

-- these fairness conditions do not seem to be required
--FAIRNESS
--   (!((!((!r|!a)| (( u )|(tmp4))))|(tmp3)))|(!((!r|!a)| (( u )|(tmp4)))) 
--FAIRNESS
--   (! (( u )|(tmp4)))|( u ) 
--FAIRNESS
--   (!((!(!r|(( a )|(tmp2))))|(tmp1)))|(!(!r|(( a )|(tmp2)))) 

INIT
  (!r | a | tmp2) & !tmp1 & (!r | !a | u | tmp4) & !tmp3 & ((r & !u & !tmp4) | tmp5)
   


--SPEC
--  AF(r & AF !r)


***/
