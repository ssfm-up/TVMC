package edu.toronto.cs.tlq;

import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.tlq.MvSetUpsetAlgebra.*;
import edu.toronto.cs.tlq.MvSetCrossProductAlgebra.*;



/***
 *** rewrites all placeholders into XCTL expressions as described in 
 *** our papers
 ***/
public class MvSetPlaceholderReWriter extends CloningRewriter
{

  MvSetUpsetAlgebra upSetAlgebra;
  MvSetFactory mvSetFactory;
  
  // -- a map of placeholders we already rewriten
  // -- type: CTLNode -> CTLMvSetNode
  Map placeHolders;

  // -- type: CTLPlaceholderNode -> PlaceholderInfo
  List placeHoldersInfo;
  

  

  
  public MvSetPlaceholderReWriter (MvSetUpsetAlgebra _upSetAlgebra)
  {
    upSetAlgebra = _upSetAlgebra;
    placeHolders = new HashMap ();
    placeHoldersInfo = new LinkedList ();
  }

  public void renew()
  {
    placeHolders = new HashMap();      
    placeHoldersInfo = new LinkedList ();
  }

  
  
  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {

    // -- first time, initialize upset algebra
    if (placeHolders.isEmpty ())
      {
	// XXX think about this!
	mvSetFactory = node.getTerms() [0][0].getFactory ();
	upSetAlgebra.setMvSetFactory (mvSetFactory);
      }

    // -- check if we already processed a placeholder with this name
    CTLMvSetNode result = (CTLMvSetNode)placeHolders.get (node);


    // -- if this is the first time, replace it with an appropriate mvset
    if (result == null)
      {
	
	int placeholderId = placeHolders.size ();
	// -- build terms
	// -- assign a value to each term and combine join all terms
	result = CTLFactory.createCTLMvSetNode
	  (combineTerms (buildTerms 
			 (Arrays.asList (node.getTerms ()).iterator ()), 
			 placeholderId));

	placeHoldersInfo.add (new PlaceholderInfo (node, placeholderId));

	// -- record for future use
	placeHolders.put (node, result);

	// XXX another hack
	result.setName (node.toString ());
      }

    // -- done
    return result;
    
  }


  private List buildTerms (Iterator terms)
  {
    List headTerms = Arrays.asList ((MvSet[])terms.next ());
    
    // -- see if we are done with recursion
    if (!terms.hasNext ()) return headTerms;
    
    // -- recurse down the list
    List restTerms = buildTerms (terms);
    
    // -- finally we merge the two lists
    List result = new LinkedList ();

    for (Iterator it = headTerms.iterator (); it.hasNext ();)
      {
	MvSet aTerm = (MvSet)it.next ();
	for (Iterator jt = restTerms.iterator (); jt.hasNext ();)
	  result.add (aTerm.and ((MvSet)jt.next ()));
      }
    return result;
    
  }

  private MvSet combineTerms (List terms, int id)
  {
    MvSet result = null;
    
    // -- join all terms and assign a value to each
    for (Iterator it = terms.iterator (); it.hasNext ();)
      {
	MvSet term = (MvSet)it.next ();
	MvSet value = getValueForTerm (id, term);
	
	// -- first term is different
	if (result == null)
	  result = term.and (value);
	else
	  result = result.or (term.and (value));
      }
    return result;
  }

  private MvSet getValueForTerm (int id, MvSet term)
  {
    MvSetFactory factory = term.getFactory ();
    return 
      factory.createConstant (upSetAlgebra.getJoinIrreducible (id, term));
  }
  


  public CTLNode[] getSolutions (AlgebraValue v, StatePresenter presenter)
  {
    System.out.println ("MvSetPlaceholderReWriter: getting solutions from: " 
			+ v);
    if (v == upSetAlgebra.top ())
      return new CTLNode[] { CTLFactory.createCTLAtomPropNode ("anything")};
    if (v == upSetAlgebra.bot ())
      return new CTLNode[] { CTLFactory.createCTLAtomPropNode ("none") };

    MvSetUpsetAlgebra.MvSetUpsetValue value = 
      (MvSetUpsetAlgebra.MvSetUpsetValue)v;
    
    
    List solutions = new LinkedList ();
    for (Iterator it = value.getValues ().iterator (); it.hasNext ();)
      solutions.add (getSolution ((CrossProductValue)it.next (), presenter));
    
    return (CTLNode[])solutions.toArray (new CTLNode [solutions.size ()]);
  }

  // -- prints a single solution for all placeholders
  // -- this will be called ones per a minimal solution
  public CTLNode getSolution (CrossProductValue v, StatePresenter presenter)
  {
    CTLNode result = null;
    
    for (Iterator it = placeHoldersInfo.iterator (); it.hasNext ();)
      {
	PlaceholderInfo info = (PlaceholderInfo)it.next ();
	CTLPlaceholderNode node = info.getCTL ();
	CTLNode phSolution = 
	  node.eq (getOneSolution (v.getValue (info.getId ()), presenter, 
				   node.isNegated ()));

	if (result == null) result = phSolution;
	else result = result.and (phSolution);
	
      }
    return result;
  }
  
  // XXXXXXX
  // XXXXXXX This is definitely the worst written piece of code I've ever saw
  // XXXXXXX
  public CTLNode getOneSolution (MvSet v, StatePresenter presenter, 
				 boolean negated)
  {

    MvSet result = v;
    // -- this is \\upset F, which is 
    // -- any temporal formula
    if (result.equals (mvSetFactory.bot ()))
	return CTLFactory.createCTLAtomPropNode ("anything");
    
    CTLNode ctlResult = null;


    if (negated) result = result.not ();



    // XXX Not sure that this has any meaning
    if (result.equals (mvSetFactory.top ()))
      return CTLFactory.createCTLAtomPropNode ("T");    
    else if (result.equals (mvSetFactory.bot ()))
      return CTLFactory.createCTLAtomPropNode ("F");    


    for (Iterator it = result.cubeIterator (); it.hasNext () ; )
      {
	AlgebraValue[] assignment = (AlgebraValue[]) it.next ();
	CTLNode[] ctlState = presenter.toCTL (assignment);
	CTLNode bigAnd = ctlState [0];
	for (int j = 1; j < ctlState.length; j++)
	  bigAnd = bigAnd.and (ctlState [j]);

	if (ctlResult == null) ctlResult = bigAnd;
	else ctlResult = ctlResult.or (bigAnd);
      }
    return ctlResult;
  }

  class PlaceholderInfo
  {
    CTLPlaceholderNode node;
    int id;

    public PlaceholderInfo (CTLPlaceholderNode _node, int _id)

    {
      node = _node;
      id = _id;
    }
    
    public CTLPlaceholderNode getCTL ()
    {
      return node;
    }

    public int getId ()
    {
      return id;
    }
    
  }
  

  
}
