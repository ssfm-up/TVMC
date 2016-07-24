package edu.toronto.cs.mvset;


import edu.toronto.cs.algebra.*;

public abstract class AbstractMvSetFactory implements MvSetFactory
{
  IAlgebra algebra;

  AlgebraValue top;
  AlgebraValue bot;
  AlgebraValue noValue;
  

  public AbstractMvSetFactory (IAlgebra _algebra)
  {
    algebra = _algebra;
    top = algebra.top ();
    bot = algebra.bot ();
    noValue = algebra.noValue ();
  }
  
  // default is to do nothing..
  public void renew()
  {
    return;
  }
  
  
  public IAlgebra getAlgebra ()
  {
    return algebra;
  }

  public MvSet infoTop ()
  {
    throw new UnsupportedOperationException ();
  }
  public MvSet infoBot ()
  {
    throw new UnsupportedOperationException ();
  }
  
  

}
