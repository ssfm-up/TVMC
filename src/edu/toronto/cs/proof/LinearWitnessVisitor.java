package edu.toronto.cs.proof;

import java.util.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;

public class LinearWitnessVisitor extends AbstractProofVisitor 
{
  IAlgebra alg;
  MvSetModelChecker mc;
  Set statesSeen;
  
  public LinearWitnessVisitor()
  {
    mc = ProofStepFactory.getMC();
    alg = mc.getTrans().toMvSet ().getAlgebra();
    statesSeen = new HashSet();
    
  }
  
  public List traverse(ProofStep ps)
  {
    List l = new LinkedList();

    l.add (new WitnessStep (alg.noValue (),
			    ps.getStateAsArray (),
			    ps.getStateName ()));
    
    return (List) ps.accept(this, l);
  }
  
  public class WitnessStep
  {
    AlgebraValue v;
    AlgebraValue[] succ;
    String stateName;
    
    public AlgebraValue getTransValue()
    {
      return v;
    }

    public AlgebraValue[] getSuccessor()
    { 
      return succ;
    }
    
    public WitnessStep(AlgebraValue _v,
		       AlgebraValue[] _succ,
		       String sn)

    {
      v= _v;
      succ = _succ;
      stateName =sn;
    }

    public String toString()
    {
      if (v.equals(v.getParentAlgebra().noValue()))
	return new String (stateName+": "+AlgebraValue.toString(succ));
      else
	return new String("--"+v+"-> "+stateName+": "+AlgebraValue.toString(succ));
    }
    
    
  }

  public static String toString(Object[] ws)
  {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<ws.length; i++)
      sb.append( (WitnessStep)ws[i] + "\n");
    return sb.toString();
  }
  
  public Object visitNegStep(ProofStep ps, Object info)
  {
    return ((ProofStep) ps.getAntecedents().iterator().next()).accept(this,
								      info);
  }

  public Object visitPropStep(ProofStep ps, Object info)
  {
    return info;
  }
  
  public Object visitEUStep(ProofStep ps, Object info)
  {
    return ((ProofStep) ps.getAntecedents().iterator().next()).accept(this,
								      info);
 }

  public Object visitEUiStep(ProofStep ps, Object info)
  {
    return ((ProofStep) ps.getAntecedents().iterator().next()).accept(this,
								      info);
 }

  public Object visitOrStep(ProofStep ps, Object info)
  {
    return ((ProofStep) ps.getAntecedents().iterator().next()).accept(this,
								      info);
 }
  
  public Object visitEGStep(ProofStep ps, Object info)
  {
    return ((ProofStep) ps.getAntecedents().iterator().next()).accept(this,
								      info);
 }

  public Object visitAndStep(ProofStep ps, Object info)
  {
    // assume only two
    List l = (List) info;
    Iterator it = ps.getAntecedents().iterator();
    
    List l1 = (List) ((ProofStep)it.next()).accept(this,
						  new LinkedList(l));
    
    List l2 = (List) ((ProofStep)it.next()).accept(this,
						  new LinkedList(l));
    
    if (l1.size() > l2.size())
      return l1;
    else
      return l2;
  }
  
  
  public Object visitEXStep(ProofStep ps, Object info)
  {

    List trace = (List) info;
    ProofStep succ = (ProofStep) ps.getAntecedents().iterator().next();
    AlgebraValue[] curr = ps.getStateAsArray();
    AlgebraValue[] nxt = succ.getStateAsArray();
    int[] prime = mc.getPrime();
    
    MvSet tval =
      mc.getTrans().toMvSet ().
      cofactor(curr).cofactor(AlgebraValue.renameArgs(nxt,
						      prime));

    AlgebraValue transV;
    
    if (tval.isConstant())
      transV = tval.getValue();
    
    else
      throw new RuntimeException("Transition value is not a leaf node!");
    

   // System.out.println(AlgebraValue.toString(curr)+ "--"+
	//	       transV+"->"+AlgebraValue.toString(
	//						 AlgebraValue.renameArgs(nxt,prime)));
    
    String sn = ps.getStateName();
    if (statesSeen.contains(sn)) 
      {
	trace.add(new WitnessStep(transV, nxt, sn));
	statesSeen.add(sn);
      }
    else
      trace.add(new WitnessStep(transV, nxt, sn));
    

    return succ.accept(this, trace);
    
  }
  
  
}
