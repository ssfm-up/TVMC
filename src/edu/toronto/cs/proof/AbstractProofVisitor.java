package edu.toronto.cs.proof;

public abstract class AbstractProofVisitor implements ProofVisitor
{
  public Object visit(ProofStep ps, Object info)
  {
    if (ps != null)
      return ps.accept(this, info);
    
    return null;
    
  }

  public Object visitGeneric(ProofStep ps, Object info)
  {
    return info;
  }

  public Object visitNegStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }
  public Object visitPropStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }
  public Object visitOrStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }
    public Object visitAndStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }

  public Object visitEUStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }

  public Object visitEUiStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }

  public Object visitEXStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }

  public Object visitEGStep(ProofStep ps, Object info)
  {
    return visitGeneric(ps, info);
  }

}

