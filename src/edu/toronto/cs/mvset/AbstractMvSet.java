package edu.toronto.cs.mvset;

import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;

public abstract class AbstractMvSet implements MvSet 
{
  /*** Utility functions */
  public MvSet and (MvSet v)
  {
    return ptwiseCompose (MEET, v);
  }
  
  public MvSet or (MvSet v)
  {
    return ptwiseCompose (JOIN, v);
  }

  public MvSet impl (MvSet v)
  {
    return ptwiseCompose (IMPL, v);
  }
  
  
  public MvSet not ()
  {
    return ptwiseNeg ();
  }
  
  public MvSet leq (MvSet v)
  {
    return ptwiseCompare (LEQ, v);
  }
  
  public MvSet geq (MvSet v)
  {
    return ptwiseCompare (GEQ, v);
  }
  
  public MvSet eq (MvSet v)
  {
    return ptwiseCompare (EQ, v);
  }


  public MvSet infoAnd (MvSet v)
  {
    return ptwiseCompose (INFO_AND, v);
  }
  public MvSet infoOr (MvSet v)
  {
    return ptwiseCompose (INFO_OR, v);
  }
  public MvSet infoNot ()
  {
    throw new UnsupportedOperationException ();
  }
  

  public void reorder ()
  {
    return;
  }
  
  public int size ()
  {
    return 0;
  }  

  public MvSet cofactor (MvSet point)
  {
    throw new UnsupportedOperationException ();
  }
  
  public Iterator cubeIterator ()
  {
    throw new UnsupportedOperationException ();
  }
  
  public AlgebraValue[][] expandToArray ()
  {
    throw new UnsupportedOperationException ();
  }
  
  public DaVinciGraph toDaVinci ()
  {
    throw new UnsupportedOperationException ();
  }
    
  public BitSet getImage ()
  {
    throw new UnsupportedOperationException ();
  }
    
  public Set getPreImageArray (AlgebraValue v)
  {
    throw new UnsupportedOperationException ();
  }

  public Iterator mintermIterator (MvSet vars, AlgebraValue v)
  {
    throw new UnsupportedOperationException ();
  }

  public MvRelation toMvRelation (MvSet invar, MvSet preVarCube,
				  MvSet postVarCube,
				  int[] preToPostMap,
				  int[] postToPreMap)
  {
    return new MvSetMvRelation (this, invar, preVarCube, postVarCube, 
				preToPostMap, postToPreMap);
  }
  
  public MvRelation toMvRelation (MvSet invar, 
				  MvSet invarPost, 
				  MvSet preVarCube,
				  MvSet postVarCube,
				  int[] preToPostMap,
				  int[] postToPreMap)
  {
    return new MvSetMvRelation (this, invar, invarPost, 
				preVarCube, postVarCube, 
				preToPostMap, postToPreMap);
  }

  
}
