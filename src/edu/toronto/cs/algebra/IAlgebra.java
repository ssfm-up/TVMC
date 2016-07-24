package edu.toronto.cs.algebra;

import java.util.*;
import edu.toronto.cs.util.*;

// -- a lattice interface
public interface IAlgebra 
{

  
  // -- reserved value that corresponds to nothing
  public AlgebraValue noValue ();
  
  // -- get a new lattice value based on its data
  public AlgebraValue getValue (String name);
  // -- get a new lattice value based on its id
  public AlgebraValue getValue (int id);
  
  // v1 /\ v2
  public AlgebraValue meet (AlgebraValue v1, AlgebraValue v2);
  // v1 \/ v2
  public AlgebraValue join (AlgebraValue v1, AlgebraValue v2);
  // -v
  public AlgebraValue neg (AlgebraValue v);
  // v1 -> v2
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2);
  
  public AlgebraValue top ();
  public AlgebraValue bot ();

  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2);
  public AlgebraValue leq (AlgebraValue v1, AlgebraValue v2);
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2);




  // -- returns the size of this lattice
  public int size ();

  public Set getJoinIrredundant(BitSet subset);
  
  public Set getMeetIrredundant(BitSet subset);
  

  // -- returns ids for the carrier set
  public IntIterator carrierSetId () throws UnsupportedOperationException;
  // -- returns elements of the carrier set
  public Collection carrierSet () throws UnsupportedOperationException;

  public AlgebraValue[] joinDecomposition (AlgebraValue v);
  
}
