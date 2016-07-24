package edu.toronto.cs.proof2;

import de.upb.agw.util.CTLParser;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

public class CTLProver
{
  // -- the modelchecker
  MvSetModelChecker mc;
  
  // -- List of ProofRules
  java.util.List proofRules;
  
  Container contentPane;
  JFrame frame;
  DynamicTree treePanel;
  JComboBox choiceToExpand;
  
  java.util.List nodes = new LinkedList ();

  /**
   * Creates a new CTLProver
   *
   * @param _mc a <code>MvSetModelChecker</code> to be used as 
   *            the decision procedure for this prover.
   */
  public CTLProver (MvSetModelChecker _mc, ProofStep proofStep)
  {
    mc = _mc;
    proofRules = new ArrayList ();

    // initialise the window
    //    frame = new JFrame("DynamicExpansion");
    
    //contentPane = frame.getContentPane();
    //contentPane.setLayout(new GridLayout(1,1));
    //contentPane.add();


    
    //setLayout(new BorderLayout());
    //treePanel = new DynamicTree (proofStep);
    //treePanel.setPreferredSize(new Dimension(500, 550));
    //contentPane.add(treePanel, BorderLayout.CENTER);

    //  frame.addWindowListener(new WindowAdapter() {
//  	public void windowClosing(WindowEvent e) {
//  	  System.exit(0);
//              }
//        });
    
//      frame.pack();
//      frame.setVisible(true);
    
   
  }
  

  /**
   * @return a model checker used as a decision procedure in the prover
   */
  public MvSetModelChecker getModelChecker ()
  {
    return mc;
  }
  

  /**
   * Adds a proof rule to the proof rules known to this 
   * prover
   *
   * @param o the proof rule to add
   */
  public void addProofRule (ProofRule o)
  {
    proofRules.add (o);
  }
  
  /**
   * Expands a proof step once
   *
   * @param step Proof step to be expanded
   * @return returns <code>step</code>
   */
  
  public ProofStep expand (ProofStep step)
  {

    // -- if step is a leaf, nothing to do
    if (step instanceof LeafProofStep) return step;

    // -- iterator over proof rules and apply the first
    // -- applicable one
    for (Iterator it = proofRules.iterator (); it.hasNext ();)
      if (applyProofRule ((TreeProofStep)step, (ProofRule)it.next ()))
	break;

    return step;
  }

  /**
   * Applies a proof rule to a proof step
   *
   * @param step a proof step which we are trying to reduce
   * @param rule the proof rule
   * @return true iff the rule has been successful
   */

  private static  boolean applyProofRule (TreeProofStep step, ProofRule rule)
  {
    

   // System.out.println ("Applying prule: " + rule.getClass ());
    Formula[] subGoals = rule.apply (step.getFormula ());
    if (subGoals == null) return false;
    
    ProofStep[] children = new ProofStep [subGoals.length];
    
    for (int i = 0; i < children.length; i++)
      children [i] = step.newProofStep (subGoals [i]);
    
    step.setChildren (children);
    step.setProofRule (rule);
    return true;
    
  }
  
  
  /**
   * Recursively applies <code>expand</code> until the proof is 
   * expanded as much as possible
   *
   * @param step the root of the proof tree to be constructed
   * @return <code>step</code>
   */
  public  ProofStep expandFully (ProofStep step)
  {
//     if (nodes.contains (step))
//       return null;
//     else
//       nodes.add (step);
    
    if (step instanceof LeafProofStep) return step;
    
    TreeProofStep treeStep = (TreeProofStep) expand (step);
    
    // add children nodes to the parent (step) node dynamically
    //    for (int i = 0; i < treeStep.getChildLength (); i++) 
    //      ProofStepFactory.dynamicTree (step , treeStep.getChild(i));
    
    //expandFully (treeStep.getChild (i));
    return step;
  }
  


  public static class EqualsProofRule implements ProofRule
  {
    public Formula[] apply (Formula _formula)
    {
      if (!(_formula instanceof EqualFormula))
	return null;

      EqualFormula formula = (EqualFormula)_formula;
      
      Formula[] subGoals = new Formula [2];
      subGoals [0] = new BelowFormula (formula.getConsequent (), 
				       formula.getValue (),
				       formula.getState ());
      subGoals [1] = new AboveFormula (formula.getConsequent (),
				       formula.getValue (),
				       formula.getState ());
   
      subGoals [0].setParentFormula (_formula);
      subGoals [1].setParentFormula (_formula);

      return subGoals;
    }
  }



  // check formula of the type 
  //   value <= T and 
  //   value >= F
  
  public static class CheckingTopBottom 
    extends AbstractProofRule implements Axiomatic
  {
    public CheckingTopBottom (MvSetModelChecker mc)
    {
      super (mc);
    }
    
    public Formula[] apply (Formula f)
    {
      Formula subGoal = null;
      
      if (f instanceof BelowFormula && f.getValue ().equals (top) ||
	  f instanceof AboveFormula && f.getValue ().equals (bot)) 
	subGoal = new ConstantFormula (top);

      if (subGoal != null)
	subGoal.setParentFormula (f);
      return toArray (subGoal);
    }
  }
  


  public static class NegationProofRule extends AbstractProofRule
  {

    public NegationProofRule (MvSetModelChecker mc)
    {
      super (mc);
    }
    
    public Formula[] apply (Formula f)
    {
      return toArray (applyRule (f));
    }
    
    // -- handles proof rules of the form
    // -- !ctl op val
    // -- where ctl is any formula
    // -- op is either <=, >= or ==
    public Formula applyRule (Formula formula)
    {
      
      if (formula.getClass () == ConstantFormula.class )
	return null;
      
      
      if (! (formula.getConsequent ().getClass () == CTLNegNode.class))
	return null;
      
      // !a = b --> a = !b

      
      // -- strip negation
      CTLNode right = formula.getConsequent ().getRight ();
      Formula subGoal = null;
      AlgebraValue value = formula.getValue ().neg ();

      if (formula instanceof EqualFormula)
	subGoal = new EqualFormula (right, value, formula.getState ());
      else if (formula instanceof BelowFormula)
	subGoal = new AboveFormula (right, value, formula.getState ());
      else if (formula instanceof AboveFormula)
	subGoal = new BelowFormula (right, value, formula.getState ());

      if (subGoal != null) 
	subGoal.setParentFormula (formula);
      return subGoal; 
    }  
  }
  

    /**
     * Negation proof rule that always returns an equals sub-goal.
     * This rule is mostly useful for generating partial explanations
     * to learn new predicates
     */
  public static class EqNegationProofRule extends AbstractProofRule
  {

    public EqNegationProofRule (MvSetModelChecker mc)
    {
      super (mc);
    }
    
    public Formula[] apply (Formula f)
    {
      return toArray (applyRule (f));
    }
    
    // -- handles proof rules of the form
    // -- !ctl op val
    // -- where ctl is any formula
    // -- op is either <=, >= or ==
    public Formula applyRule (Formula formula)
    {
      
      if (formula.getClass () == ConstantFormula.class )
	return null;
      
      
      if (! (formula.getConsequent ().getClass () == CTLNegNode.class))
	return null;
      
      // !a = b --> a = !b

      
      // -- strip negation
      CTLNode right = formula.getConsequent ().getRight ();
      AlgebraValue value = formula.getValue ().neg ();

      Formula subGoal = new EqualFormula (right, value, formula.getState ());

      if (subGoal != null) 
	subGoal.setParentFormula (formula);
      return subGoal; 
    }  
  }
  

  
  public static class AtomicProofRule 
    extends AbstractProofRule implements Axiomatic
  {
    public AtomicProofRule (MvSetModelChecker mc)
    {
      super (mc);
    }
    
    public Formula[] apply (Formula formula)
    {
      if (formula.getConsequent () instanceof CTLAtomPropNode) 
	{
	
	  Formula f = new ConstantFormula (top);
	  f.setParentFormula (formula);
	  return new Formula[] { f };
	}
      return null;
    }
  }
  

  // 
  //  a /\ b >= c  --> a >= c1 and b >= c2 and c1 /\ c2 >= c
  // -- same for \/ and <=
  // 
  public static class AndOrProofRule extends AbstractProofRule
  {
    public AndOrProofRule (MvSetModelChecker mc)
    {
      super (mc);
    }
    
    public Formula[] apply (Formula f)
    {
      if (!(f instanceof BelowFormula || 
	    f instanceof AboveFormula)) return null;
      
      if (f.getConsequent ().getClass () != CTLAndNode.class &&
	  f.getConsequent ().getClass () != CTLOrNode.class)
	return null;

      //System.out.println (" Or --> " + f.getConsequent ());

      MvSet state = f.getState ();
      CTLNode left = f.getConsequent ().getLeft ();
      CTLNode right = f.getConsequent ().getRight ();
      
      Formula[] subGoals = new Formula [2];
      subGoals [0] = 
	Formula.duplicate (f, left, 
			   mc.checkCTL (left, state).getValue (), state);
      subGoals [1] = 
	Formula.duplicate (f, right, 
			   mc.checkCTL (right, state).getValue (), state);
      
      subGoals [0].setParentFormula (f);
      subGoals [1].setParentFormula (f);
      
      //ProofStepFactory.dynamicTree (f, subGoals);
      
      return subGoals;
    }
    
  }

  public static class EUProofRule extends AbstractProofRule
  {
    public EUProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula [] apply (Formula f)
    {
      if (!(f instanceof AboveFormula))
	return null;
      
      if (!(f.getConsequent ().getClass () == CTLEUNode.class))
	return null;
      
      //System.out.println (" EU--> " + f.getConsequent ());


      java.util.List subGoals = new ArrayList ();
      
      // calculate i such that after i transitions psi is true
      // in some state
      
      // -- brake down E[phi U psi] for convinience
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      MvSet state = f.getState ();

      MvSet result;      
      int i = 0;
      do {
	result = mc.checkCTL (phi.eu (i++, psi), state);
      }
      while (!result.getValue ().equals (f.getValue ()));

      // create a EUi node
      subGoals.add (new AboveFormula (phi.eu ((i - 1), psi), 
				      result.getValue (), state));

      Formula [] child = (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
      
      for (int j = 0; j<child.length; j++)
	child[j].setParentFormula (f);

      // ProofStepFactory.dynamicTree (f, child);

      return child;
    }
    
  }

  
  //  class to represent the E [ phi Ui psi] >= value Rule

  public static class EUiProofRule extends AbstractProofRule
  {
    
    // -- true if we should attempt to minimize the bound
    boolean minimize;
    
    public EUiProofRule (MvSetModelChecker _mc, boolean _minimize)
    {
      super (_mc);
      minimize = _minimize;
    }

    public EUiProofRule (MvSetModelChecker _mc)
    {
      this (_mc, true);
    }

    
    public Formula [] apply (Formula f)
    {
      if (!(f instanceof AboveFormula))
	return null;
      
      if (!(f.getConsequent () instanceof CTLEUiNode))
	return null;

      //System.out.println (" EUi--> " + f.getConsequent ());

      MvSet state = f.getState ();
      
      // get the bound
      int bound = ((CTLEUiNode) f.getConsequent ()).getI ();
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      //java.util.List subGoals = new ArrayList ();
      
      if (bound == 0){
	Formula[] child =  
	  new Formula[] {new AboveFormula (psi, f.getValue (), state)};
	child [0].setParentFormula (f);
	//ProofStepFactory.dynamicTree (f, child);
	return child;
	//return new Formula[] {new AboveFormula (psi, f.getValue (), state)};
      }
      
      // try to minimize the bound if possible
      if (minimize)
	bound = minimizeBound (f);

      // -- bound > 0
      // expand the EUi node
      
      CTLNode exp = psi.or (phi.and (phi.eu (bound - 1, psi).ex ()));
      Formula[] child = new Formula[]  
	{new AboveFormula (exp, f.getValue (), state)};
      child [0].setParentFormula (f);
      return child;
      
	  
//       subGoals.add (new AboveFormula (exp, f.getValue (), state));
      
//       Formula [] child = 
// 	(Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
      
//       for (int j = 0; j < child.length; j++)
// 	child[j].setParentFormula (f);


      //ProofStepFactory.dynamicTree (f,child);
      
//       return child;
    }
    
    // one improvement can be to check if we can decrease
    // the bound first. For example, it is possible that
    // ||E[p U_5 q] ||(s) >= T
    // and also ||E [p U_2 q]||(s) >= T
    // if we use the current rule this will always expand 
    // into 5 step proof, when a 2 step is sufficient.
    // So we want an additional EUi proof rule that tries to minimize
    // the bound using model-checker. It should fail if this
    // is not possible, in which case the current rule is applied
    
    // this function returns new bound

    public int minimizeBound (Formula f)
    {
      MvSet result;      
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      MvSet state = f.getState ();
      int newBound = 0;
      
      do {
	result = mc.checkCTL (phi.eu (newBound++, psi), state);
      }
      while (!result.getValue ().equals (f.getValue ()));
      
      return newBound-1;
    }
    
  }

  public static class AUProofRule extends AbstractProofRule
  {
    public AUProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula [] apply (Formula f)
    {
      if (!(f instanceof BelowFormula))
	return null;
      
      if (!(f.getConsequent ().getClass () == CTLAUNode.class))
	return null;
      
      java.util.List subGoals = new ArrayList ();
      
      // calculate i such that after i transitions psi is true
      // in some state
      
      // -- brake down A[phi U psi] for convinience
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      MvSet state = f.getState ();

      MvSet result;      
      int i = 0;
      do {
	result = mc.checkCTL (phi.au (i++, psi), state);

	System.err.println ("in loop "+ i+" value = "+result.getValue ());

      }
      while (!result.getValue ().equals (f.getValue ()));

      // create a AUi node
      subGoals.add (new BelowFormula (phi.au ((i - 1), psi), 
				      result.getValue (), state));
      
      Formula [] child = (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
      
      for (int j = 0; j < child.length; j++)
	child[j].setParentFormula (f);


      //ProofStepFactory.dynamicTree (f,child);
      
      
      return child;
    }
    
  }

  
  //  class to represent the A [ phi Ui psi] <= value Rule

  public static class AUiProofRule extends AbstractProofRule
  {
        
    public AUiProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula [] apply (Formula f)
    {
      if (!(f instanceof BelowFormula))
	return null;
      
      if (!(f.getConsequent () instanceof CTLAUiNode))
	return null;
      
      MvSet state = f.getState ();
      
      // get the bound
      int bound = ((CTLAUiNode) f.getConsequent ()).getI ();
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      java.util.List subGoals = new ArrayList ();
      
      if (bound == 0)
	return new Formula[] {new BelowFormula (psi, f.getValue (), state)};
	
      
      // try to minimize the bound if possible
      bound = minimizeBound (f);

      // -- bound > 0
      // expand the EUi node
      
      CTLNode exp = psi.or (phi.and (phi.au (bound - 1, psi).ax ()));
      subGoals.add (new BelowFormula (exp, f.getValue (), state));
     
      Formula [] child = (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
      
      for (int j = 0; j < child.length; j++)
	child[j].setParentFormula (f);

      
      //ProofStepFactory.dynamicTree (f,child);
      
      
      return child;
      
    }
    
    // one improvement can be to check if we can decrease
    // the bound first. For example, it is possible that
    // ||E[p U_5 q] ||(s) >= T
    // and also ||E [p U_2 q]||(s) >= T
    // if we use the current rule this will always expand 
    // into 5 step proof, when a 2 step is sufficient.
    // So we want an additional EUi proof rule that tries to minimize
    // the bound using model-checker. It should fail if this
    // is not possible, in which case the current rule is applied
    
    // this function returns new bound

    public int minimizeBound (Formula f)
    {
      MvSet result;      
      CTLNode phi = f.getConsequent ().getLeft ();
      CTLNode psi = f.getConsequent ().getRight ();
      MvSet state = f.getState ();
      int newBound = 0;
      
      do {
	result = mc.checkCTL (phi.au (newBound++, psi), state);
      }
      while (!result.getValue ().equals (f.getValue ()));
      
      return newBound-1;
    }
    
  }

  


  // AX proof Rule
  // AX phi <= value
  
  public static class AXProofRule extends AbstractProofRule
  {
    public AXProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula[] apply (Formula f)
    {
      // only proof of the form AX phi <= value 
      // are taken care of

      if (!(f instanceof BelowFormula))
	return null;
      
      if (!(f.getConsequent () instanceof CTLAXNode))
	return null;
      
      MvSet state = f.getState ();
      MvSetFactory fac = state.getFactory ();

      // the map of successors of states
      MvSet succMap;
      
      // vector of assignments corresponding to state
      AlgebraValue[] stateEnv;
      Set img;
      
      stateEnv = ProofStepFactory.getStateAsArray (f);
      
      // have to calculate 
      // t E S, ||R(s,t)->phi||(s)
      // which is equivalent to calculating
      // !R(s,t) \/ phi, or
      // !(R(s,t) /\ !phi)
      
      succMap = mc.checkCTL (f.getConsequent ().getRight ().neg ().preEX ().neg (), 
			     state).renameArgs (xkripke.getUnPrime ());

      img = algebra.getMeetIrredundant (succMap.getImage ());

      java.util.List subGoals = new ArrayList ();
      
      for (Iterator it = img.iterator (); it.hasNext ();)
	{
	  AlgebraValue tv = (AlgebraValue)it.next ();

// 	  Set xm = succMap.getPreImageArray (tv);
	  
// 	  AlgebraValue[] ns = (AlgebraValue[]) xm.iterator ().next ();
// 	  int[] prime = xkripke.getPrime ();
	  
// 	  for (int i=0; i < ns.length; i++)
// 	    if (prime [i] != i && ns [i].equals (algebra.noValue ()))
// 	      ns [i] = algebra.bot ();
	  
// 	  MvSet nxt = fac.createPoint (ns, algebra.top ());
	  // XXX new way to get a single state
	  MvSet nxt = (MvSet)
	    succMap.mintermIterator (xkripke.getUnPrimeCube (), tv).next ();
	  

	  CTLNode phi = f.getConsequent ().getRight ();

	  CTLMvSetNode trans = CTLFactory.createCTLMvSetNode (succMap);
	  if (trans.getName () != null)
	    trans.setName ("Trans");

	  subGoals.add (new BelowFormula (phi.or(trans.neg ()), tv, nxt));

	}
    
      Formula [] child = (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
      
      for (int j = 0; j < child.length; j++)
	child[j].setParentFormula (f);

      
      //ProofStepFactory.dynamicTree (f,child);
      
      
      return child;
            
    }
  }
  




  // EG proof rule
  // ||EG phi||(s) = (phi /\ (EX E[phi U {s}] \/ EX EG (phi /\ {not (s)})))

  public static class EGProofRule extends AbstractProofRule
  {
    public EGProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula[] apply (Formula f)
    {
      if (!(f instanceof AboveFormula))
	return null;
      
      if (!(f.getConsequent () instanceof CTLEGNode))
	return null;
      
      // do not need to prove EG phi >= F
      // leads to unnecessary expansion of the proof 
      // eats lots of memory
      
      //System.out.println (" EG--> " + f.getConsequent ());

//       if (f.getValue ().equals (bot))
// 	return toArray (new ConstantFormula (top));
      
      MvSet state = f.getState ();
            
      // subgoals
      
      Formula[] subGoals = new Formula [1];
      
      CTLMvSetNode currState = CTLFactory.createCTLMvSetNode (state);
      currState.setName (Formula.stateName.getStateName (state));
      CTLMvSetNode notCurrState = 
	CTLFactory.createCTLMvSetNode (state.not ());

      notCurrState.setName ("!" + currState.getName ());
      
      
      CTLNode right = f.getConsequent ().getRight ();

      // expansion of EG node
      
      //TODO: Fairness: !!!NEW!!
      //TODO: prolly remove ;)
      CTLNode goal = right.and (right.eu (currState).ex ().
			     or 
			     (right.and (notCurrState).eg ().ex ()));
      CTLParser.addDeepFairness(goal, f.getConsequent().getFairness());
      //**
      
      subGoals [0] = 
	new AboveFormula (goal, 
			  f.getValue (), state);
      
      subGoals [0].setParentFormula (f);
      
      //ProofStepFactory.dynamicTree (f, subGoals);
      return subGoals;
    }
  }
  

  // more inner classes.. gotta refactor all this somehow!
  public static abstract class BaseEXProofRule extends AbstractProofRule 
  {
    MvSet succMap;
    CTLMvSetNode trans;
    Set img;
    
    public BaseEXProofRule(MvSetModelChecker _mc) 
    {
      super(_mc);
    }

    // -- takes a state as variable/value pairs, and sets all don't cares to
    // -- val
    public AlgebraValue[] getSingleState (AlgebraValue[] state, 
					  AlgebraValue val)
    {
      int[] prime = xkripke.getPrime ();
      for (int i=0; i < state.length; i++)
	if (prime [i] != i && state [i].equals (algebra.noValue ()))
	  state [i] = val;
      return state;
    }    
    
    protected abstract Formula[] exApply(Formula f);
    
    public Formula[] apply (Formula f)
    {
      if (!applicable(f))
	return null;
      else
	{
	  // set up succMap, trans
	  getSuccessorMap(f.getState(), f.getStateName(),
			  f.getConsequent());
	  return exApply(f);
	  
	  
	}
      
      
      
    }
    
    static boolean applicable(Formula f) 
    {
      return ( (f instanceof AboveFormula) && 
	       (f.getConsequent() instanceof CTLEXNode));
    }

    protected abstract void getImageMap();
    

    
    public void getSuccessorMap(MvSet state, String name, CTLNode conseq)
    {
     
      succMap = 
	mc.getTrans ().fwdImage (state).
	and (mc.checkCTL (conseq.getRight ()));
      
//       suaccMap = mc.checkCTL(conseq.getRight().preEX(), state).
// 	renameArgs(xkripke.getUnPrime());
      
      trans = CTLFactory.createCTLMvSetNode (succMap);
     
      if (trans.getName () == null)
	trans.setName ("Succ("+name+")");

      
    }
    
  }
  
  public static abstract class PreferAvoidEXProofRule extends BaseEXProofRule
  {
    public PreferAvoidEXProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    protected abstract MvSet getPreferred(MvSet allSucc);
    
    protected Formula[] exApply(Formula f)
    {
      MvSet state = f.getState();
      
      MvSetFactory fac = state.getFactory ();
      java.util.List subGoals = new ArrayList ();
      for (Iterator it = img.iterator (); it.hasNext ();)
	{
	  
	  MvSet prefStates = getPreferred(succMap);
	  AlgebraValue tv = (AlgebraValue)it.next ();
	  
	  Set xm = succMap.getPreImageArray (tv);
	
	}
      return null;
      
      
      // check if prefStates empty
      
    }
    
				
    

  }
  
  // replacement for EX phi >= value ProofRule, using base
  public static class NewEXProofRule extends BaseEXProofRule
  {
    public NewEXProofRule(MvSetModelChecker _mc) 
    {
      super(_mc);
    }

    
    protected void getImageMap() 
    {
    	  // set up img
      img = algebra.getJoinIrredundant (succMap.getImage ());
    }
    
    protected Formula[] exApply(Formula f)
    {
      System.err.println("NewEXProofRule created\n");
      
      MvSet state = f.getState();
      
      MvSetFactory fac = state.getFactory ();
      java.util.List subGoals = new ArrayList ();
      for (Iterator it = img.iterator (); it.hasNext ();)
	{
	  AlgebraValue tv = (AlgebraValue)it.next ();
	  
	  //Set xm = succMap.getPreImageArray (tv);
	  
	  //for (Iterator it2 = xm.iterator (); it2.hasNext ();)
	  for (Iterator it2 = 
		 succMap.mintermIterator (xkripke.getUnPrimeCube (),
					  tv); it2.hasNext ();)
	    {
	      //AlgebraValue[] ns = (AlgebraValue[]) it2.next ();
	      // -- pick a single state out of sets of states in ns
	      //ns = getSingleState (ns, algebra.bot ());
	      
	      // -- create an mv-set representation of this state
	      //MvSet nxt = fac.createPoint (ns, algebra.top ());
	      MvSet nxt = (MvSet) it2.next ();

	      // -- extract the formula from under EX
	      CTLNode phi = f.getConsequent ().getRight ();
	      
	      // add a new sub-goal
	      Formula subGoalFormula = 
		new AboveFormula (phi.and (trans), tv, nxt);
	      subGoalFormula.setParentFormula (f);
	      subGoals.add (subGoalFormula);
	    }  
	}
      return (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);

    }
    
  }
  
  // class to represent EX phi >= value ProofRule
  
  public static class EXProofRule extends AbstractProofRule
  {
    
    boolean showAll = true;
    
    public EXProofRule (MvSetModelChecker _mc, boolean _showAll)
    {
      super (_mc);
      showAll = _showAll;
    }
    public EXProofRule (MvSetModelChecker _mc)
    {
      this (_mc, true);
    }
    
    public Formula[] apply (Formula f)
    {
      if (!(f instanceof AboveFormula))
	return null;

      if (!(f.getConsequent () instanceof CTLEXNode))
	return null;

      //if (f.getValue ().equals (bot))
      //return toArray (new ConstantFormula (top));
      
      MvSet state = f.getState ();
      MvSetFactory fac = state.getFactory ();

      // the map of successors of states
      MvSet succMap;
      
      // vector of assignments corresponding to state
      Set img;
      
      System.err.println("EX: phi="+(f.getConsequent().getRight()));
      
      //succMap = mc.checkCTL (f.getConsequent ().getRight ().preEX (), state).
      //renameArgs (xkripke.getUnPrime ());
      
   
      succMap = mc.checkCTL (f.getConsequent ().getRight ()).
	and (mc.getTrans ().fwdImage (state));

      //System.err.println("Premap="+(f.getConsequent().getRight().preEX()));
      //System.err.println("Succmap="+ succMap);
      //CTLProver.dumpMvSet(succMap);
      
      System.err.println("All bits"+ succMap.getImage ());
      img = algebra.getJoinIrredundant (succMap.getImage ());
      System.err.println ("Join irreduntant we have: " + img);
      System.err.println ("Extracted from " + succMap.getImage ());
      java.util.List subGoals = new ArrayList ();

      // -- create an mv set for the R(s)
      CTLMvSetNode trans = 
	CTLFactory.createCTLMvSetNode (mc.getTrans ().fwdImage (state));
     
      if (trans.getName () == null)
	trans.setName ("Succ("+f.getStateName()+")");

      
      for (Iterator it = img.iterator (); it.hasNext ();)
	{
	  AlgebraValue tv = (AlgebraValue)it.next ();
	  
	  //	  Set xm = succMap.getPreImageArray (tv);
	  
          
	  //for (Iterator it2 = xm.iterator (); it2.hasNext ();)
	  for (Iterator it2 = succMap.mintermIterator 
		 (xkripke.getUnPrimeCube (), tv); it2.hasNext ();)
	    {
	      //AlgebraValue[] ns = (AlgebraValue[]) it2.next ();
	      // -- pick a single state out of sets of states in ns
	      //ns = getSingleState (ns, algebra.bot ());
	      
	      // -- create an mv-set representation of this state
	      //MvSet nxt = fac.createPoint (ns, algebra.top ());
	      // XXX new way to get a single state
	      MvSet nxt = (MvSet)it2.next ();

	      System.err.println ("Got a new state");
	      System.err.println 
		(Arrays.asList (xkripke.getStatePresenter ().toCTL (nxt)));
	      System.err.println ("****************");
	      System.err.println ();
	      
	      // -- extract the formula from under EX
	      CTLNode phi = f.getConsequent ().getRight ();
	      
	      // add a new sub-goal
	      //if (!tv.toString ().equals("d")) {
                 Formula subGoalFormula = 
		    new AboveFormula (phi.and (trans), tv, nxt);
	         subGoalFormula.setParentFormula (f);
	         subGoals.add (subGoalFormula);
             // } 
             // else {
                 //AlgebraValue tv1 = (AlgebraValue)it.next();
                 //Iterator it3 = succMap.mintermIterator 
		 //                    (xkripke.getUnPrimeCube (), tv1);
                 //it = img.iterator();
                 //it.next();
                 //MvSet nxt1 = (MvSet)it3.next ();
                 //nxt1 = nxt1.infoAnd ((MvSet) it3.next());
              //   Formula subGoalFormula = 
              //      new AboveFormula (phi.and (trans), tv, null);
              //   subGoalFormula.setParentFormula (f);
	      //   subGoals.add (subGoalFormula); 
             // }
           

	      // -- stop after one state if don't need to show all states
	      if (!showAll) break;
	    }  
	}
      return (Formula []) subGoals.toArray (new Formula [subGoals.size ()]);
    }
    
    // -- takes a state as variable/value pairs, and sets all don't cares to
    // -- val
    public AlgebraValue[] getSingleState (AlgebraValue[] state, 
					  AlgebraValue val)
    {
      int[] prime = xkripke.getPrime ();
      for (int i=0; i < state.length; i++)
	if (prime [i] != i && state [i].equals (algebra.noValue ()))
	  state [i] = val;
      return state;
    }    
  }


  public static class EXAboveMProofRule extends AbstractProofRule
  {
    
    BelnapAlgebra balgebra;
    public EXAboveMProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
      balgebra = (BelnapAlgebra)algebra;
    }
    
    public Formula[] apply (Formula f)
    {
      if (!(f instanceof AboveFormula))
	return null;

      if (!(f.getConsequent () instanceof CTLEXNode))
	return null;
      if (!(f.getValue ().equals (balgebra.infoBot ())))
	return null;
      
      MvSet state = f.getState ();
      MvSetFactory fac = state.getFactory ();

      // -- get the set of successors
      MvSet succMap = mc.checkCTL (f.getConsequent ().getRight ()).
	and (mc.getTrans ().fwdImage (state));

      
      java.util.List subGoals = new ArrayList ();

      // -- create an mv set for the R(s)
      CTLMvSetNode trans = 
	CTLFactory.createCTLMvSetNode (mc.getTrans ().fwdImage (state));
     
      if (trans.getName () == null)
	trans.setName ("Succ("+f.getStateName()+")");

      AlgebraValue tv = f.getValue ();

      // -- pick one state
      MvSet nxt = (MvSet) succMap.
	mintermIterator (xkripke.getUnPrimeCube (), tv).next ();
      
      System.err.println ("Got a new state");
      System.err.println 
	(Arrays.asList (xkripke.getStatePresenter ().toCTL (nxt)));
      System.err.println ("****************");
      System.err.println ();	
	      
      // -- extract the formula from under EX
      CTLNode phi = f.getConsequent ().getRight ();
	      
      Formula subGoalFormula = 
	new AboveFormula (phi.and (trans), tv, nxt);
      subGoalFormula.setParentFormula (f);
      return new Formula[] { subGoalFormula };
    }
    
  }



  // class to represent EX phi <= value ProofRule
  
  public static class EXCexProofRule extends AbstractProofRule
  {
    
    public EXCexProofRule (MvSetModelChecker _mc)
    {
      super (_mc);
    }
    
    public Formula[] apply (Formula f)
    {
      if (!(f instanceof BelowFormula))
	return null;

      if (!(f.getConsequent () instanceof CTLEXNode))
	return null;
      
      
      MvSet state = f.getState ();
      MvSetFactory fac = state.getFactory ();

      // the map of successors of states
      MvSet succMap;
      
      // vector of assignments corresponding to state
      AlgebraValue[] stateEnv;
      Set img;
      
      // -- convert the state into an array 
      stateEnv = getStateAsArray (f);
      
      // -- given ||EX p||(s), compute (R /\ ~p)(s)
      succMap = mc.checkCTL (f.getConsequent ().getRight ().neg ().preEX (), 
			     state).
	renameArgs (xkripke.getUnPrime ());

      // -- get a state for which the above function is false
      AlgebraValue[] ns = (AlgebraValue[])
	succMap.getPreImageArray (algebra.bot ()).iterator ().next ();

      // -- fill in all don't cares with false
      int[] prime = xkripke.getPrime ();
      for (int i = 0; i < ns.length; i++)
	if (prime [i] != i && ns [i].equals (algebra.noValue ()))
	  ns [i] = algebra.bot ();

      MvSet nextState = fac.createPoint (ns, algebra.top ());

      java.util.List subGoals = new ArrayList ();

      CTLTransitionNode trans = CTLFactory.createCTLTransitionNode (succMap);
      if (trans.getName () == null)
	trans.setName ("Succ("+f.getStateName()+")");

      subGoals.add 
	(new BelowFormula (f.getConsequent ().getRight ().and(trans),
			   algebra.bot (), nextState));

      Formula [] child = (Formula []) 
	subGoals.toArray (new Formula [subGoals.size ()]);
      
      for (int j = 0; j < child.length; j++)
	child[j].setParentFormula (f);
      return child;
    }
    

    public AlgebraValue[] getStateAsArray(Formula f)
    {
      return (AlgebraValue[]) f.getState ().
	getPreImageArray (f.getValue ().getParentAlgebra().top())
	.iterator().next();
    } 
    
  }



  

  // public static class ConstantProofRule extends AbstractProofRule
//   {
//     public ConstantProfRule (MvSetModelChecker _mc)
//     {
//       super (_mc);
//     }
    
//     public Formula[] apply (Formula f)
//     {
//       if (f instanceof AboveFormula)
// 	if (f.getConsequent () instanceof CTLConstantNode)
// 	  return f.getConsequent ().getValue ().geq (f.getValue ());
// 	else 
// 	  return null;
  public static void dumpMvSet (MvSet mvSet, AlgebraValue x)
  {
    for (Iterator it = mvSet.mintermIterator (mvSet, x); it.hasNext ();)
      System.err.println (Arrays.asList ((Object[])it.next ()));
  }
      
  
}


    
