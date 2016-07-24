package edu.toronto.cs.algebra;

import java.util.*;
import edu.toronto.cs.util.*;


public class BelnapAlgebra implements IAlgebra
{
  AlgebraValue noValue;
  AlgebraValue top;  // true
  AlgebraValue bot; // false
  AlgebraValue infoTop; // d
  AlgebraValue infoBot; // maybe


  public BelnapAlgebra ()
  {
    noValue = new AlgebraValue (this, "novalue", -1);
    bot = new AlgebraValue (this, "false", 0);
    top = new AlgebraValue (this, "true", 1);
    infoBot = new AlgebraValue (this, "maybe", 2);
    infoTop = new AlgebraValue (this, "d", 3);
  }
  
   
  public AlgebraValue noValue ()
  {
    return noValue;
  }

  public AlgebraValue getValue (String name)
  {
    if (name.equals ("true")) return top;
    else if (name.equals ("false")) return bot;
    else if (name.equals ("maybe")) return infoBot;
    else if (name.equals ("d")) return infoTop;
    return noValue;
  }

  public AlgebraValue getValue (int id)
  {
    if (id == 0) return bot;
    else if (id == 1) return top;
    else if (id == 2) return infoBot;
    else if (id == 3) return infoTop;
    else return noValue;
  }
  
  public AlgebraValue meet (AlgebraValue v1, AlgebraValue v2)
  {
    if (v1 == v2) return v1;
    if (v1 == top) return v2;
    if (v2 == top) return v1;
    return bot;
  }

  public AlgebraValue join (AlgebraValue v1, AlgebraValue v2)
  {
    if (v1 == v2) return v1;
    if (v1 == bot) return v2;
    if (v2 == bot) return v1;
    return top;
  }

  public AlgebraValue neg (AlgebraValue v)
  {
    if (v == top) return bot;
    if (v == bot) return top;
    return v;
  }
  
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    return neg (v1).join (v2);
  }

  public AlgebraValue top ()
  {
    return top;
  }
  public AlgebraValue bot ()
  {
    return bot;
  }
  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1 == v2) ? top : bot;
  }
  public AlgebraValue leq (AlgebraValue v1, AlgebraValue v2)
  {
    if (v1 == v2) return top;
    if (v1 == bot) return top;
    if (v2 == top) return top;
    return bot;
    
  }

  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return leq (v2, v1);
  }
  
  public int size ()
  {
    // XXX This is a big hack to make MDDManager create variables 
    // XXX with three children, this has to be addressed as soon as 
    // XXX possible!
    return 3;
  }

  public Set getJoinIrredundant (BitSet subset)
  {
    Set s = new HashSet ();
    if (subset.get (top.getId ()))
      {
	// -- if we have top, we are done
	s.add (top);
	return s;
      }
    if (subset.get (infoBot.getId ()))
      s.add (infoBot);
    if (subset.get (infoTop.getId ()))
      s.add (infoTop);
    return s;
  }
  
  public Set getMeetIrredundant (BitSet subset)
  {
    throw new RuntimeException ("Not here either");
  }
  
  public IntIterator carrierSetId () throws UnsupportedOperationException
  { 
    throw new UnsupportedOperationException ("Not here");
  }
  public Collection carrierSet () throws UnsupportedOperationException 
  {
    throw new UnsupportedOperationException ("Not here");
  }
  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {

    throw new UnsupportedOperationException ("not here");  
  }


  public AlgebraValue infoTop ()
  {
    return infoTop;
  }
  public AlgebraValue infoBot ()
  {
    return infoBot;
  }
  
  public AlgebraValue infoMeet (AlgebraValue v1, AlgebraValue v2)
  {
    if (v1 == v2) return v1;
    if (v1 == infoTop) return v2;
    if (v2 == infoTop) return v1;
    return infoBot;
  }

  public AlgebraValue infoJoin (AlgebraValue v1, AlgebraValue v2)
  {
    if (v1 == v2) return v1;
    if (v1 == infoBot) return v2;
    if (v2 == infoBot) return v1;
    return infoBot;
  }
  
  public AlgebraValue infoNeg (AlgebraValue v1)
  {
    if (v1 == infoTop) return infoBot;
    if (v1 == infoBot) return infoTop;
    return v1;
  }
  
  
  
  
  
  
  
  
  
  
}
