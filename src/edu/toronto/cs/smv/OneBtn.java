package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;

public class OneBtn extends SMVModule
{
  AlgebraVariable p;
  AlgebraVariable r;
  AlgebraVariable f;
  
  public OneBtn ()
  {
    super ("Button");
    p = declareAlgebraVariable ("p");
    r = declareAlgebraVariable ("r");
    f = declareAlgebraVariable ("f");
  }
  
  public MvSet computeInit ()
  {
    return p.and (r.not ()).and (f.not ());
  }
  public MvSet computeTrans ()
  {
    MvSet trans = computeTransR ().and (computeTransF ());
//     System.out.println (trans.toDaVinci ().toString ());
    return trans;
  }
  
  MvSet computeTransR ()
  {

//     return f.and (next (r).eq (bot)).or
//       (p.and (f.not ()).and (next (r).eq (top))).or
//       (f.not ().and (p.not ()).and (next (r).eq (r)));

    MvSet trans = new CaseStatement ().
      addCase (f, next (r).not ()).
      addCase (p, next (r).mvSet ()).
      addDefault (next (r).eq (r)).
      compute ();
    return trans;
  }
  
  MvSet computeTransF ()
  {
//     return p.and (f.not ()).and (next (f).not ());
    MvSet trans = new CaseStatement ().
      addCase (p.and (r.not ()), next (f).not ()).
      addDefault (top).
      compute ();
    return trans;
  }
  
  
}
