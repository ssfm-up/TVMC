package edu.toronto.cs.proof;

import edu.toronto.cs.davinci.*;
import edu.toronto.cs.davinci.DaVinciGraph.*;

/***
 ***  A tuple class -- don't we have one in util package?
 **** in any case, can't we just use arrays for that?
 ***/ 
public  class NodePair 
  {
    public FullNode fst;
    public FullNode snd;
    
    public NodePair(FullNode _fst, FullNode _snd)
    {
      fst = _fst;
      snd = _snd;
    }
    
  }
