package edu.toronto.cs.proof2;

import edu.toronto.cs.grappa.*;
import edu.toronto.cs.grappa.GrappaGraph.*;

/***
 ***  A tuple class -- don't we have one in util package?
 **** in any case, can't we just use arrays for that?
 ***/ 
public  class GrappaNodePair 
  {
    public GrappaNode fst;
    public GrappaNode snd;
    
    public GrappaNodePair(GrappaNode _fst, GrappaNode _snd)
    {
      fst = _fst;
      snd = _snd;
    }
    
  }
