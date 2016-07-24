package edu.toronto.cs.proof2;

import java.util.*;
import edu.toronto.cs.util.StringUtil;
import edu.toronto.cs.grappa.*;
import edu.toronto.cs.grappa.GrappaGraph.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;




public class ProofToGrappa 
{  
  
  GrappaGraph graph = new GrappaGraph ();
  Map seen = new HashMap ();


  static String makeLabel (String stateName,
			   AlgebraValue[] state, MvSetModelChecker _mc)
  {
    StringBuffer sb = new StringBuffer (stateName + ": ");
    
    StatePresenter sp = _mc.getXKripke ().getStatePresenter ();
    
    CTLNode[] lbl = sp.toCTL (state);

    for (int i = 0; i < lbl.length; i++)
      {
	//String lft = lbl[i].getLeft ().toString ();
	//String rt = lbl[i].getRight ().toString ();
	
	//System.out.println ("value is " + rt);
	
	//sb.append("\\n" + lft + "=" + rt);
	sb.append ("\\n" + lbl [i]);
      }
    return sb.toString();
  }
  
    
  static String makeLabel(String stateName, 
			  AlgebraValue[] pred,
			  AlgebraValue[] curr,
			  MvSetModelChecker _mc)
  {
    StringBuffer sb = new StringBuffer (stateName+": ");
    StatePresenter sp = _mc.getXKripke ().getStatePresenter ();
    CTLNode[] oldlbl = sp.toCTL (pred);
    CTLNode[] newlbl = sp.toCTL (curr);
    
    int j = 0;
    for (int i = 0; i < oldlbl.length; i++)
      {
	// only add those which are different
	if (!newlbl [i].equals (oldlbl [i]))
	  {
	    sb.append ("\\n" + newlbl [i]);
	    j++;
	  }
	
// 	if (!newlbl[i].getRight ().equals (oldlbl[i].getRight ()))
// 	  {
// 	    sb.append("\\n" + newlbl[i].getLeft ().toString () + "=" +
// 		      newlbl[i].getRight ().toString ());
// 	    j++;
// 	  }
      }
    return sb.toString();
  }

  GrappaNode makeProofNode (String lab)
  {
    return graph.node().
      shape("box").
      fontStyle("italic").
      fontFamily("times").     
      color("lightblue").      
      style("filled").
      label(lab);
  }
  
  GrappaNode makeStateNode(String lab)
  {
    return graph.node().
      shape ("box").
      fontStyle ("bold").
      fontFamily ("times").
      label (lab);
  }
  


  public static GrappaGraph toGrappaState (ProofStep ps, MvSetModelChecker _mc)
  {

    if (ps==null)
    {
      System.out.println (" ps is empty");
      return null;
    }
    

    ProofToGrappa stateProve = new ProofToGrappa ();
    GrappaNode rootState =
      stateProve.makeStateNode (makeLabel (ps.getFormula ().getStateName (),
					   ProofStepFactory.getStateAsArray (ps.getFormula ()),
				     _mc));
    
    String lbl;
    
    lbl = new String("[" + ps.getFormula ().getConsequent () + "]=(" +
		     ps.getFormula ().getValue () + ")");
    
    GrappaNode rootProof = stateProve.makeProofNode (lbl);

    //rootState.edge (rootProof);
    
    stateProve.seen.put (ps.getFormula ().getStateName (), rootState);
    
    stateProve.visitGeneric (ps,
			     new GrappaNodePair (rootProof, rootState),
			     _mc,
			     1);

    rootProof.deleteNode ();

    return stateProve.graph;
  }


  

  
  public static GrappaGraph toGrappa (ProofStep ps, MvSetModelChecker _mc)
  {
    ProofToGrappa ptdv = new ProofToGrappa ();
    GrappaNode rootState = 
      ptdv.makeStateNode 
      (makeLabel (ps.getFormula ().getStateName (),
		  ProofStepFactory.getStateAsArray (ps.getFormula ()), _mc));
    String lbl;
    
    lbl = new String("[" + ps.getFormula ().getConsequent ().
		     toString() + "]=(" +
		     ps.getFormula ().getValue () + ")");
    

    GrappaNode rootProof = ptdv.makeProofNode (lbl);

    rootState.edge (rootProof);
    
    ptdv.seen.put (ps.getFormula ().getStateName (), rootState);
    
    ptdv.visitGeneric (ps,
		       new GrappaNodePair (rootProof, rootState),
		       _mc,
		       0);
    
    return ptdv.graph;
  }
  


  public Object visitGeneric (ProofStep ps, Object info, MvSetModelChecker _mc, int k)
  {
    
    if (!(ps.getClass () == TreeProofStep.class))
      return null;
  
    GrappaNodePair nodes = (GrappaNodePair)info;

    for (int i= 0; i < ((TreeProofStep)ps).getChildLength (); i++)
      {
	ProofStep pst = ((TreeProofStep)ps).getChild (i);
	
	String lbl;
	if (pst.getFormula () instanceof AboveFormula)
	
	  lbl = new String("[" + pst.getFormula ().getConsequent () + "]>=(" +
			   pst.getFormula ().getValue () + ")");
	else
	  
	  lbl = new String("[" + pst.getFormula ().getConsequent () + "]<=(" +
			   pst.getFormula ().getValue () + ")");
	
	GrappaNode child = makeProofNode (lbl);
	
	if (k==0)
	  nodes.fst.edge (child).color ("lightpurple");
	
	 

 	if (pst.getFormula ().getConsequent () instanceof CTLEXNode)
	  this.visitEXStep (pst,
			    new GrappaNodePair (child, nodes.snd),
			    _mc,
			    k);
	else{
	  if (pst.getFormula ().getConsequent () instanceof CTLAXNode)
	    this.visitAXStep (pst,
			      new GrappaNodePair (child, nodes.snd),
			      _mc,
			      k);
	  else
	    if (!(pst.getFormula ().getConsequent () instanceof CTLAtomPropNode))
	      this.visitGeneric (pst,
				 new GrappaNodePair (child, nodes.snd),
				 _mc,
				 k);
	}
	
	if (k==1)
	  child.deleteNode ();
	
	//visitGeneric (this, new GrappaNodePair(child, nodes.snd));
      }
    return info;
  }
  


  public Object visitEXStep(ProofStep ps, Object info, MvSetModelChecker _mc, int k)
  {

    if (!(ps.getClass () == TreeProofStep.class))
      return null;
      
    MvSetModelChecker mc = _mc;
    XKripkeStructure xkripke = _mc.getXKripke ();
      
    GrappaNodePair parentNodes = (GrappaNodePair) info;
    
    GrappaNode[] kids = new GrappaNode[((TreeProofStep)ps).getChildLength ()];


    for (int i=0; i < ((TreeProofStep)ps).getChildLength (); i++) 
      {
	ProofStep ant = ((TreeProofStep)ps). getChild (i);
	String sname = ant.getFormula ().getStateName ();
	  
	if (!(ant.getFormula () instanceof ConstantFormula))
	  {
	      
	    // create witness state node
	    GrappaNode fnode = makeStateNode (makeLabel(sname,
							ProofStepFactory.
							getStateAsArray (ps.getFormula ()),
							ProofStepFactory.
							getStateAsArray(ant.getFormula ()),
							mc));
	      
	      
	    // create proof antecedent node
	    GrappaNode pnode;
	    if (ant.getFormula () instanceof AboveFormula)
	      pnode = makeProofNode(new String("["+ant.getFormula ().
					       getConsequent ().
					       toString()+"]>=("+
					       ant.getFormula ().getValue ()+")"));
	    else
	      pnode = makeProofNode(new String("["+ant.getFormula ().
					       getConsequent ().
					       toString()+"]<=("+
					       ant.getFormula ().getValue ()+")"));
	      
	      
	    // link EX node to the witness state with transition value
	    //MvSetModelChecker mcc = ProofStepFactory.getMC();
	    //XKripkeStructure struct = ProofStepFactory.getStructure();
	    AlgebraValue tv =
	      mc.getTrans ().toMvSet ().and (ps.getFormula ().getState ()).
	      and (ant.getFormula ().getState ().renameArgs (mc.getPrime ())).
	      existAbstract (xkripke.getPrimeCube ().
			     and (xkripke.getUnPrimeCube ())).getValue ();
	     
	    if (k==0)
	      parentNodes.fst.edge(fnode).label(tv.toString());
	      
	    //parentNodes.snd.edge(fnode).label(tv.toString()).lineType("double");
	    parentNodes.snd.edge(fnode).label(tv.toString());
 
	    
	    // and link the proof antecedent to the witness state.
	    if (k==0)
	      fnode.edge(pnode);
	      
	      
	    //ant.accept(this, new GrappaNodePair(pnode, fnode));
	      
	    if (ant.getFormula ().getConsequent () instanceof CTLEXNode)
	      this.visitEXStep (ant,
				new GrappaNodePair (pnode, fnode),
				_mc,
				k);
	    else{
	      if (ant.getFormula ().getConsequent () instanceof CTLAXNode)
		this.visitAXStep (ant,
				  new GrappaNodePair (pnode, fnode),
				  _mc,
				  k);
	      else 
		this.visitGeneric (ant,
				   new GrappaNodePair (pnode, fnode),
				   _mc,
				   k);
	    }
	    
	    if (k==1)
	      pnode.deleteNode ();
	  }
	
      }
    return null;
  }





  public Object visitAXStep(ProofStep ps, Object info, MvSetModelChecker _mc, int k)
  {
    
    if (!(ps.getClass () == TreeProofStep.class))
      return null;
      
    MvSetModelChecker mc = _mc;
    XKripkeStructure xkripke = _mc.getXKripke ();
      
    GrappaNodePair parentNodes = (GrappaNodePair) info;
    
    GrappaNode[] kids = new GrappaNode[((TreeProofStep)ps).getChildLength ()];


    for (int i=0; i < ((TreeProofStep)ps).getChildLength (); i++) 
      {
	ProofStep ant = ((TreeProofStep)ps). getChild (i);
	String sname = ant.getFormula ().getStateName ();
	  
	if (!(ant.getFormula () instanceof ConstantFormula))
	  {
	      
	    // create witness state node
	    GrappaNode fnode = makeStateNode (makeLabel(sname,
							ProofStepFactory.
							getStateAsArray (ps.getFormula ()),
							ProofStepFactory.
							getStateAsArray(ant.getFormula ()),
							mc));
	      
	      
	    // create proof antecedent node
	    GrappaNode pnode;
	    if (ant.getFormula () instanceof AboveFormula)
	      pnode = makeProofNode(new String("["+ant.getFormula ().
					       getConsequent ().
					       toString()+"]>=("+
					       ant.getFormula ().getValue ()+")"));
	    else
	      pnode = makeProofNode(new String("["+ant.getFormula ().
					       getConsequent ().
					       toString()+"]<=("+
					       ant.getFormula ().getValue ()+")"));
	      
	      
	    // link EX node to the witness state with transition value
	    //MvSetModelChecker mcc = ProofStepFactory.getMC();
	    //XKripkeStructure struct = ProofStepFactory.getStructure();
	    AlgebraValue tv =
	      mc.getTrans ().toMvSet ().and (ps.getFormula ().getState ()).
	      and (ant.getFormula ().getState ().renameArgs (mc.getPrime ())).
	      existAbstract (xkripke.getPrimeCube ().
			     and (xkripke.getUnPrimeCube ())).getValue ();
	     
	    if (k==0)
	      parentNodes.fst.edge(fnode).label(tv.toString());
	      
	    //parentNodes.snd.edge(fnode).label(tv.toString()).lineType("double");
	    parentNodes.snd.edge(fnode).label(tv.toString());
 
	    
	    // and link the proof antecedent to the witness state.
	    if (k==0)
	      fnode.edge(pnode);
	      
	      
	    //ant.accept(this, new GrappaNodePair(pnode, fnode));
	      
	    if (ant.getFormula ().getConsequent () instanceof CTLEXNode)
	      this.visitEXStep (ant,
				new GrappaNodePair (pnode, fnode),
				_mc,
				k);
	    else {
	      if (ant.getFormula ().getConsequent () instanceof CTLAXNode)
		this.visitAXStep (ant,
				  new GrappaNodePair (pnode, fnode),
				  _mc,
				  k);
	      else 
	      
		this.visitGeneric (ant,
				   new GrappaNodePair (pnode, fnode),
				   _mc,
				   k);
	    }
	    
	    
	    if (k==1)
	      pnode.deleteNode ();
	  }
	
      }
    return null;
  }

}

