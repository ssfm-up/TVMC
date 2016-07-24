package edu.toronto.cs.algebra;

/***
 *** Provides an int based wrapper methods to IAlgebra so that it can
 *** be used with C programs.
 ***/
public class IntAlgebraWrapper
{
  IAlgebra algebra;
  
  public IntAlgebraWrapper (IAlgebra _algebra)
  {
    algebra = _algebra;
  }
  

  public int meet (int v1, int v2)
  {
    return algebra.meet (algebra.getValue (v1), 
			 algebra.getValue (v2)).getId ();
  }
  public int join (int v1, int v2)
  {
    return algebra.join (algebra.getValue (v1), 
			 algebra.getValue (v2)).getId ();
  }
  public int neg (int v1)
  {
    return algebra.neg (algebra.getValue (v1)).getId ();
  }

  public int infoMeet (int v1, int v2)
  {
    return join (v1, v2);
  }
  public int infoJoin (int v1, int v2)
  {
    return meet (v1, v2);
  }
  
  public int infoNeg (int v1)
  {
    return v1;
  }


  public int impl (int v1, int v2)
  {
    return algebra.impl (algebra.getValue (v1),
			 algebra.getValue (v2)).getId ();
  }
  
  public int eq (int v1, int v2)
  {
    return algebra.eq (algebra.getValue (v1),
			 algebra.getValue (v2)).getId ();
  }
  public int leq (int v1, int v2)
  {
    return algebra.leq (algebra.getValue (v1),
			 algebra.getValue (v2)).getId ();
  }
  public int geq (int v1, int v2)
  {
    return algebra.leq (algebra.getValue (v1),
			 algebra.getValue (v2)).getId ();
  }

  public int top ()
  {
    return algebra.top ().getId ();
  }
  public int bot ()
  {
    return algebra.bot ().getId ();
  }
  
  public int noValue ()
  {
    return algebra.noValue ().getId ();
  }
  
  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  

}
