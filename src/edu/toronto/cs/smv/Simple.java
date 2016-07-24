package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;


public class Simple extends SMVModule
{
  AlgebraVariable request;
  IntVariable status;
  
  public Simple ()
  {
    super ("simple");
    request = declareAlgebraVariable ("request");
    // -- 1 is ready
    // -- 2 is busy
    status = declareIntVariable ("status", 1, 2);
  }

  public MvSet computeInit ()
  {
    return status.eq (1).and (request.eq (top));
  }
  
  public MvSet computeTrans ()
  {
    return computeRequest ().and (computeStatus ());
  }
  
  
  MvSet computeRequest ()
  {
    return next (request).eq (top).or (next (request).eq (bot));
  }
  
  MvSet computeStatus ()
  {
    return new CaseStatement ().
      addCase (request.eq (top), next (status).eq (2)).
      addDefault (next (status).eq (1).or (next (status).eq (2))).compute ();
    
  }
}
