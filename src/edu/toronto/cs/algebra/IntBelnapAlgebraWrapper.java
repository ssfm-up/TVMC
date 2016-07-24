package edu.toronto.cs.algebra;

/***
 *** Provides an int based wrapper methods to IAlgebra so that it can
 *** be used with C programs.
 ***/
public class IntBelnapAlgebraWrapper extends IntAlgebraWrapper
{
  BelnapAlgebra balgebra;
  
  public IntBelnapAlgebraWrapper (BelnapAlgebra _algebra)
  {
    super (_algebra);
    balgebra = _algebra;
  }
  
  public int infoMeet (int v1, int v2)
  {
    return balgebra.infoMeet (algebra.getValue (v1), 
			     algebra.getValue (v2)).getId ();
  }
  public int infoJoin (int v1, int v2)
  {
    return balgebra.infoJoin (algebra.getValue (v1),
			     algebra.getValue (v2)).getId ();
  }
  public int infoNeg (int v1)
  {
    return balgebra.infoNeg (algebra.getValue (v1)).getId ();
  }
  
  
  
  
  

}
