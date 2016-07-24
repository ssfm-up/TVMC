package edu.toronto.cs.proof2;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;
import java.util.*;

/** It can easily be shown that
 *  [|EX phi|](s) >= l if
 *  [|EX (phi /\ psi)|](s) >= l
 */
public class VisitedEXProofRule extends AbstractProofRule 
{
  // the states already visited
  MvSet visited;
  CTLTemplate exTemplate;
  CTLProver.EXProofRule exPR;
  CTLProver.EXCexProofRule excPR;
  
  

  public VisitedEXProofRule(MvSetModelChecker mc, MvSet _visited)
  {
    super (mc);
    exPR = new CTLProver.EXProofRule(mc);
    excPR = new CTLProver.EXCexProofRule(mc);
    
    visited = _visited;
    // XXX - go back and do proper name-clash avoidance
    // seriously, if anybody actually has a state
    //  variable called 'pomplemousse' in their model,
    //  just shoot them.

    CTLNode matchingNode = CTLFactory.createCTLAtomPropNode("pomplemousse");
    
    CTLNode exNode = CTLFactory.createCTLEXNode(matchingNode);
    
    HashSet names = new HashSet(1);
    names.add("pomplemousse");
    
    exTemplate = new CTLTemplate(exNode, names);
    
    
  }


  public Formula[] apply (Formula f)
  {
    Formula[] subgoals;
    boolean isAboveFormula;
    System.out.println("--VisitedEX: checking applicability to "+f);
    
    if ((f instanceof EqualFormula))
      return null;
    
    isAboveFormula = (f instanceof AboveFormula);
    System.out.println(" Applying to "+(isAboveFormula ? "above" : "below")+" formula");
    
    
    Map m = exTemplate.matchCTLNode(f.getConsequent());
    if (m == null)
      return null;
    
    // retrieve the phi node under the EX
    CTLNode phi = (CTLNode) m.get("pomplemousse");

    System.out.println("phi="+phi);
    
    // get the mvset of phi states
    MvSet phiStates = mc.checkCTL(phi);
    
    System.out.println("visited: "+visited+", phistates: "+phiStates);
    
    System.out.println("phistates leq visited: "+phiStates.leq(visited));
    
    // if it is *already* in the visited set, then
    //  use the default EX rule
    if (phiStates.leq(visited).equals(phiStates.getFactory().top()))
      {
	// add all the states used in the EX step
	//  to the visited states
	System.out.println("Using default EX rule");
	
	subgoals = ( isAboveFormula ? exPR.apply(f) : excPR.apply(f));
	
	for (int j=0; j <subgoals.length; j++)
	  visited = visited.or(subgoals[j].getState());
	// and return the result of applying default EX
	return subgoals;
	
      }
    
    
    // otherwise..
    
    // first check whether we *can* restrict to visited states
    CTLNode augmentedNode = CTLFactory.createCTLEXNode
      (CTLFactory.createCTLAndNode
       (phi, CTLFactory.createCTLMvSetNode(visited)));
    
    if (mc.checkCTL(augmentedNode, f.getState()).getValue().geq(f.getValue()).
	isTop())
      {
	List goal = new ArrayList();
	if (isAboveFormula)
	  goal.add
	    (new AboveFormula (CTLFactory.createCTLEXNode
			       (CTLFactory.createCTLAndNode
				(phi, CTLFactory.createCTLMvSetNode(visited))),
			       f.getValue(),
			       f.getState()));
	else
	  goal.add
	    (new BelowFormula (CTLFactory.createCTLEXNode
			       (CTLFactory.createCTLAndNode
				(phi, CTLFactory.createCTLMvSetNode(visited))),
			       f.getValue(),
			       f.getState()));
	  
	System.out.println("Restricting to visited");
	Formula [] child = (Formula [])goal.toArray(new Formula [goal.size()]);
	for (int j = 0; j < child.length; j++)
	  child[j].setParentFormula (f);
	return child;
      }
    else {
	// add all the states used in the EX step
	//  to the visited states
	System.out.println("Using default EX rule");
	
	subgoals = (isAboveFormula ? exPR.apply(f) : excPR.apply(f));
	
	for (int j=0; j <subgoals.length; j++)
	  visited = visited.or(subgoals[j].getState());
	// and return the result of applying default EX
	return subgoals;
    }
    
  }
  
}
