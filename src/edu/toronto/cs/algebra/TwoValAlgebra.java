package edu.toronto.cs.algebra;

import edu.toronto.cs.util.*;
import edu.toronto.cs.util.IntIterator.*;
import java.util.*;
import java.io.PrintWriter;

/****
 **** 
 ****/
public class TwoValAlgebra implements DeMorganAlgebra
{

  final AlgebraValue FALSE;
  final AlgebraValue TRUE;
  final AlgebraValue NO_VALUE;
  
  
  public TwoValAlgebra () 
  { 
    FALSE = new AlgebraValue (this, "F", 0);
    TRUE = new AlgebraValue (this, "T", 1);
    NO_VALUE = new AlgebraValue (this, "-", -1);
  }


  public AlgebraValue noValue ()
  {
    return NO_VALUE;
  }
  

  /*** 
   *** IAlgebra interface
   ***/
  public AlgebraValue getValue (String name)
  {
    if (name.equals ("F")) return FALSE;
    else if (name.equals ("T")) return TRUE;
    return NO_VALUE;
  }
  
  // -- get a new lattice value based on its id
  public AlgebraValue getValue (int id)
  {
    switch (id)
      {
      case 0: return FALSE;
      case 1: return TRUE;
      default:
	throw new IllegalArgumentException ("Unknown id: " + id);	
      }
  }
  
  
  // v1 /\ v2
  public AlgebraValue meet (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1 == TRUE && v2 == TRUE) ? TRUE : FALSE;
  }
  
  // v1 \/ v2
  public AlgebraValue join (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1 == FALSE && v2 == FALSE) ? FALSE : TRUE;
  }
  
  // -v
  public AlgebraValue neg (AlgebraValue v)
  {
    return (v == TRUE) ? FALSE : TRUE;
  }

  // v1 -> v2
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1 == FALSE) ? TRUE : v2;
  }
  
  
  public AlgebraValue top ()
  {
    return TRUE;
  }
  
  public AlgebraValue bot ()
  {
    return FALSE;
  }
  

  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1 == v2) ? TRUE : FALSE;
  }
  
  public AlgebraValue leq (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1.getId () <= v2.getId ()) ? TRUE : FALSE;
  }
  
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return (v1.getId () >= v2.getId ()) ? TRUE : FALSE;
  }


  // -- returns the size of this lattice
  public int size ()
  {
    return 2;
  }

  // -- returns ids for the carrier set
  public IntIterator carrierSetId () throws UnsupportedOperationException
  {
    return new RangeIterator (0, 2);
  }
  
  // -- returns elements of the carrier set
  public Collection carrierSet () throws UnsupportedOperationException
  {
    // XXX is this used?
    List v = new ArrayList ();
    v.add (TRUE);
    v.add (FALSE);
    return v;
  }
  
  public Set getJoinIrredundant(BitSet subset)
  {
    Set s = new HashSet();
    if (subset.get(1))
      s.add(TRUE);
    
    else if (subset.get(0))
      s.add(FALSE);
    
    return s;
    
  }

  
  public Set getMeetIrredundant(BitSet subset)
  {
    Set s = new HashSet();
    if (subset.get(0))
      s.add(FALSE);
    
    else if (subset.get(1))
      s.add(TRUE);
    
    return s;
    
  }
  
  
  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {
    if (v == TRUE)
      return new AlgebraValue[] {TRUE};
    else
      return new AlgebraValue [0];
  }  
}



