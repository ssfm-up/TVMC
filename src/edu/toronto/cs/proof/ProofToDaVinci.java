package edu.toronto.cs.proof;

import java.util.*;
import java.awt.Color;
import edu.toronto.cs.util.StringUtil;
import edu.toronto.cs.davinci.*;
import edu.toronto.cs.davinci.DaVinciGraph.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.MvSet;



public class ProofToDaVinci extends AbstractProofVisitor 
{
  
  
  static String ctlColour = DaVinciGraph.translateColour(new 
							 java.awt.Color 
							 (176, 196, 222)).
    toString();

  static String ctlToCtlEdgeColour = 
    DaVinciGraph.translateColour(new 
				 java.awt.Color(70,130,180)).toString();
  
  
  // -- true if proof nodes should be hidden by default
  boolean hideProofNodes;
  
  DaVinciGraph graph = new DaVinciGraph();
  Map seen = new HashMap();

  static String makeLabel(String stateName, MvSet state)
  {
    StringBuffer sb = new StringBuffer(stateName + ": ");
    
    StatePresenter sp = ProofStepFactory.getStructure ().getStatePresenter ();
    
    CTLNode[] lbl = sp.toCTL (state);
    for (int i=0; i<lbl.length; i++)
      {
	String lft = lbl[i].getLeft().toString();
	String rt = lbl[i].getRight().toString();
	sb.append("\\n"+lft+"="+rt);
      }
    return sb.toString();
  }
  
  static String makeLabel(String stateName, MvSet pred, MvSet curr)
  {
    StringBuffer sb = new StringBuffer(stateName+": ");
    StatePresenter sp = ProofStepFactory.getStructure().getStatePresenter();
    CTLNode[] oldlbl = sp.toCTL(pred);
    CTLNode[] newlbl = sp.toCTL(curr);

    //System.out.println ("oldLbl: " + Arrays.asList (oldlbl));
   // System.out.println ();
   // System.out.println ("newlbl: " + Arrays.asList (newlbl));
    
    int j = 0;
    
    for (int i = 0; i < oldlbl.length; i++) 
	{
	    // only add those which are different
	    if (!newlbl [i].getRight ().equals (oldlbl [i].getRight ()))
		{
		    sb.append ("\\n" + newlbl [i].getLeft ().toString () + 
			       "=" + newlbl [i].getRight ().toString ());
		    j++;
		}
	}
    return sb.toString ();
  }

  FullNode makeProofNode(String lab)
  {
    return graph.node().
      fontStyle("italic").
      fontFamily("courier").
      color(ctlColour).
      label(StringUtil.doEscapes(lab)).
      hidden (hideProofNodes);
  }
  
  FullNode makeStateNode(String lab)
  {
    return graph.node().
      fontStyle("bold").
      fontFamily("times").
      color("white").
      border("double").
      label(lab);
  }

  public ProofToDaVinci (boolean _hideProofNodes)
  {
    hideProofNodes = _hideProofNodes;
    System.out.println ("ProofToDaVinci with hideProofNodes set to: " + 
			hideProofNodes);
  }
  
  
  public static DaVinciGraph toDaVinci(ProofStep ps, boolean _hideProofNodes)
  {
    ProofToDaVinci ptdv = new ProofToDaVinci(_hideProofNodes);
//     FullNode rootState = ptdv.makeStateNode(makeLabel(ps.getStateName(),
// 						      ps.getStateAsArray()));
    FullNode rootState = ptdv.makeStateNode (makeLabel (ps.getStateName (),
							ps.getState ()));
    
    

    String lbl = new String("["+ps.getConsequent().toString()+"]=("+
			      ps.getValue()+")");
    FullNode rootProof = ptdv.makeProofNode(lbl);

    rootState.edge (rootProof);
    
    ptdv.seen.put(ps.getStateName(), rootState);
    
    ps.accept(ptdv,
	      new NodePair(rootProof, rootState));
    
    return ptdv.graph;
  }
  


  public Object visitGeneric(ProofStep ps, Object info)
  {
    NodePair nodes = (NodePair) info;
    
    for (Iterator it = ps.getAntecedents().iterator();
	 it.hasNext();
	 )
      {
	ProofStep pst = (ProofStep) it.next();
	String lbl = new String("["+pst.getConsequent().toString()+"]=("+
				  pst.getValue()+")");
	
	FullNode child = makeProofNode(lbl);
	
	nodes.fst.edge(graph.ref(child)).color(ctlToCtlEdgeColour);
	pst.accept(this, new NodePair(child, nodes.snd));
      }
    return info;
  }
  
    public Object visitEXStep (ProofStep ps, Object info)
    {
      NodePair parentNodes = (NodePair) info;
    
      FullNode[] kids = new FullNode [ps.getAntecedents ().size ()];
    
      for (Iterator it = ps.getAntecedents ().iterator (); it.hasNext(); )
        {
	  ProofStep ant = (ProofStep) it.next ();
	  String sname = ant.getStateName ();

	  // create witness state node
	  //   	FullNode fnode = makeStateNode(makeLabel(sname,
	  // 						 ps.getStateAsArray(),
	  // 						 ant.getStateAsArray()));
	  FullNode fnode = makeStateNode (makeLabel (sname,
						     ps.getState (),
						     ant.getState ()));

	
	  // create proof antecedent node
	  FullNode pnode = makeProofNode(new String("["+ant.
						    getConsequent().
						    toString()+"]=("+
						    ant.getValue()+")"));
	
	
	
	  //  	parentNode.labeledEdge(graph.ref(fnode),
	  //  			       tv.toString());

	
	  // link EX node to the witness state with transition value
	  MvSetModelChecker mcc = ProofStepFactory.getMC ();
	  XKripkeStructure struct = ProofStepFactory.getStructure ();
// 	  AlgebraValue tv =
// 	    mcc.getTrans ().toMvSet ().and(ps.getState ()).
// 	    and (ant.getState ().renameArgs (mcc.getPrime())).
// 	    existAbstract(struct.getPrimeCube().
// 			  and(struct.getUnPrimeCube())).getValue();
	  AlgebraValue tv = 
	    mcc.getTrans ().
	    fwdImage (ps.getState ()).and (ant.getState ()).
	    existAbstract (struct.getUnPrimeCube ()).getValue ();

	  parentNodes.fst.labeledEdge(graph.ref(fnode),
				      tv.toString());
	  // then link predecessor node to the witness state
	  parentNodes.snd.labeledEdge(graph.ref(fnode),
				      tv.toString()).lineType("double");
	
	  // and link the proof antecedent to the witness state.
	  fnode.edge(graph.ref(pnode));
	  ant.accept(this, new NodePair(pnode, fnode));
	
        }
      return null;
    }
}
