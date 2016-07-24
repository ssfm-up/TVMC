package edu.toronto.cs.proof;

import java.util.*;
import edu.toronto.cs.util.StringUtil;
import edu.toronto.cs.grappa.*;
import edu.toronto.cs.grappa.GrappaGraph.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.MvSet;



public class ProofToGrappa extends AbstractProofVisitor 
{  
  
  GrappaGraph graph = new GrappaGraph ();
  Map seen = new HashMap ();

  static String makeLabel (String stateName,
			   MvSet state)
  {
    StringBuffer sb = new StringBuffer (stateName + ": ");
    
    StatePresenter sp = ProofStepFactory.getStructure ().getStatePresenter ();
    
    CTLNode[] lbl = sp.toCTL (state);

    for (int i = 0; i < lbl.length; i++)
      {
	System.out.println ("Doing lbl [i]: " + lbl [i]);

	if (lbl [i] instanceof CTLEqualsNode)
	  {
	    String lft = lbl[i].getLeft ().toString ();
	    String rt = lbl[i].getRight ().toString ();
	    sb.append("\\n" + lft + "=" + rt);
	  }
	else
	  sb.append ("\\n" + lbl [i]);
      }
    return sb.toString();
  }
  
  static String makeLabel(String stateName, 
			  MvSet pred,
			  MvSet curr)
  {
    StringBuffer sb = new StringBuffer (stateName+": ");
    StatePresenter sp = ProofStepFactory.getStructure ().getStatePresenter ();
    CTLNode[] oldlbl = sp.toCTL (pred);
    CTLNode[] newlbl = sp.toCTL (curr);
    
    int j = 0;
    for (int i = 0; i < oldlbl.length; i++)
      {
	  //if (!newlbl [i].equals (oldlbl [i]))
//  	  if (true)
//  	  {
//  	    sb.append ("\\n" + newlbl [i]);
//  	    j++;
//  	  }
	
	// only add those which are different
	if (!newlbl[i].getRight ().equals (oldlbl[i].getRight ()))
 	  {
 	    sb.append("\\n" + newlbl[i].getLeft ().toString () + "=" +
 		      newlbl[i].getRight ().toString ());
 	    j++;
 	  }
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
  
  public static GrappaGraph toGrappa (ProofStep ps)
  {
    ProofToGrappa ptdv = new ProofToGrappa ();
    GrappaNode rootState = 
      ptdv.makeStateNode (makeLabel (ps.getStateName (),
				     ps.getState ()));

//       ptdv.makeStateNode (makeLabel (ps.getStateName (),
// 				     ps.getStateAsArray ()));
    
    String lbl = new String("[" + ps.getConsequent ().toString() + "]=(" +
			    ps.getValue() + ")");
    GrappaNode rootProof = ptdv.makeProofNode (lbl);

    rootState.edge (rootProof);
    
    ptdv.seen.put (ps.getStateName (), rootState);
    
    ps.accept (ptdv,
	       new GrappaNodePair (rootProof, rootState));
    return ptdv.graph;
  }
  


  public Object visitGeneric (ProofStep ps, Object info)
  {
    System.out.println (" i m in visitGeneric");
    
    GrappaNodePair nodes = (GrappaNodePair)info;
    
    for (Iterator it = ps.getAntecedents ().iterator (); it.hasNext (); )
      {
	ProofStep pst = (ProofStep)it.next ();
	String lbl = new String("[" + pst.getConsequent () + "]=(" +
				pst.getValue() + ")");
	
	//GrappaNode child = makeProofNode (lbl);
	
	//nodes.fst.edge (child).color ("lightpurple");

	//pst.accept (this, new GrappaNodePair(child, nodes.snd));
	//pst.accept (this, nodes);
	pst.accept (this, new GrappaNodePair (nodes.snd, nodes.snd));
      }
    return info;
  }
  
    public Object visitEXStep(ProofStep ps, Object info)
    {
      System.out.println (" i m in visitEX ");
      
      
      GrappaNodePair parentNodes = (GrappaNodePair) info;
    
      GrappaNode[] kids = new GrappaNode[ps.getAntecedents().size()];
    
      for (Iterator it = ps.getAntecedents().iterator();
  	 it.hasNext();
  	 )
        {
  	ProofStep ant = (ProofStep) it.next();
  	String sname = ant.getStateName();

	// create witness state node
  	GrappaNode fnode = makeStateNode(makeLabel(sname,
						   ps.getState (),
						   ant.getState ()));
// 						   ps.getStateAsArray(),
// 						   ant.getStateAsArray()));
	
	// create proof antecedent node
	GrappaNode pnode = makeProofNode(new String("["+ant.
						  getConsequent().
						  toString()+"]=("+
						  ant.getValue()+")"));
	
	// link EX node to the witness state with transition value
  	MvSetModelChecker mcc = ProofStepFactory.getMC();
  	XKripkeStructure struct = ProofStepFactory.getStructure();
//   	AlgebraValue tv =
// 	  mcc.getTrans().toMvSet ().and(ps.getState()).
//   	  and(ant.getState().renameArgs(mcc.getPrime())).
//   	  existAbstract(struct.getPrimeCube().
//   			and(struct.getUnPrimeCube())).getValue();
	  AlgebraValue tv = 
	    mcc.getTrans ().
	    fwdImage (ps.getState ()).and (ant.getState ()).
	    existAbstract (struct.getUnPrimeCube ()).getValue ();


	  //parentNodes.fst.edge(fnode).label(tv.toString());

	parentNodes.snd.edge(fnode).label(tv.toString()).color("navyblue").lineType("dotted");

	
	// and link the proof antecedent to the witness state.
	fnode.edge(pnode);
  	ant.accept(this, new GrappaNodePair(pnode, fnode));
	
        }
      return null;
    }
}
