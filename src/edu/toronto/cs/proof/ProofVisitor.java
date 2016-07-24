package edu.toronto.cs.proof;

public interface ProofVisitor
{
  public Object visit(ProofStep ps, Object info);
  public Object visitNegStep(ProofStep ps, Object info);
  public Object visitPropStep(ProofStep ps, Object info);
  public Object visitOrStep(ProofStep ps, Object info);
  public Object visitAndStep(ProofStep ps, Object info);
  public Object visitEUStep(ProofStep ps, Object info);
  public Object visitEUiStep(ProofStep ps, Object info);
  public Object visitEXStep(ProofStep ps, Object info);
   public Object visitEGStep(ProofStep ps, Object info);
}
