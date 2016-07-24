package edu.toronto.cs.tlq;

import java.util.*;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;


/***
 *** rewrites all placeholders into XCTL expressions as described in 
 *** our papers
 ***/
public class PlaceholderReWriter extends CloningRewriter
{

  UpSetAlgebra upSetAlgebra;
  MvSetFactory mvSetFactory;
  
  int topBit;


  // -- a map of placeholders we already rewriten
  // -- type: CTLNode -> MvSet
  Map placeHolders;

  // -- type: CTLPlaceholderNode -> PlaceholderInfo
  List placeHoldersInfo;
  

  // -- a map from bits to mv sets
  Map bitsToTerms;

  public PlaceholderReWriter (UpSetAlgebra _upSetAlgebra)
  {
    this (_upSetAlgebra, 0);
  }
  

  public void renew()
  {
    placeHolders = new HashMap();      
    bitsToTerms = new HashMap();    
    placeHoldersInfo = new LinkedList ();
    topBit = 0;
  }
  
  protected PlaceholderReWriter (UpSetAlgebra _upSetAlgebra, int _topBit)
  {
    upSetAlgebra = _upSetAlgebra;
    topBit = _topBit;
    placeHolders = new HashMap ();
    bitsToTerms = new HashMap ();
    placeHoldersInfo = new LinkedList ();
  }
  
  
  public Object visitPlaceholderNode (CTLPlaceholderNode node, Object o)
  {

    // -- check if we already processed a placeholder with this name
    CTLMvSetNode result = (CTLMvSetNode)placeHolders.get (node);

    // -- if this is the first time, replace it with an appropriate mvset
    if (result == null)
      {
	int startBit = topBit;

	// -- build terms
	// -- assign a value to each term and combine join all terms
	result = CTLFactory.createCTLMvSetNode
	  (combineTerms (buildTerms 
			 (Arrays.asList (node.getTerms ()).iterator ())));

	// -- record for future use
	placeHolders.put (node, result);

	// XXX another hack
	mvSetFactory = result.getMvSet ().getFactory ();
	
	result.setName (node.toString ());

	placeHoldersInfo.add (new PlaceholderInfo (node, startBit, topBit));
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

  private MvSet combineTerms (List terms)
  {
    MvSet result = null;
    
    // -- join all terms and assign a value to each
    for (Iterator it = terms.iterator (); it.hasNext ();)
      {
	MvSet term = (MvSet)it.next ();
	MvSet value = term.getFactory ().createConstant (getNextFreeValue ());

	// XXX This is very very bad but will suffice for now
	bitsToTerms.put (new Integer (topBit - 1), term);

	// -- first term is different
	if (result == null)
	  result = term.and (value);
	else
	  result = result.or (term.and (value));
      }
    return result;
  }

  private AlgebraValue getNextFreeValue ()
  {
    return upSetAlgebra.getUpMinTerm (topBit++);
  }
  
  


  public CTLNode[] getSolutions (AlgebraValue v, StatePresenter presenter)
  {
    System.out.println ("PlaceholderReWriter: getting solutions from: " + v);
    if (v == upSetAlgebra.top ())
      return new CTLNode[] { CTLFactory.createCTLAtomPropNode ("anything")};
    if (v == upSetAlgebra.bot ())
      return new CTLNode[] { CTLFactory.createCTLAtomPropNode ("none") };

    UpSetAlgebra.UpSetValue value = (UpSetAlgebra.UpSetValue)v;
    
    int[] data = value.getData ();
    int len = value.getLength ();
    
    List solutions = new LinkedList ();
    for (int i = 0; i < len; i++)
      solutions.add (getSolution (data [i], presenter));

    return (CTLNode[])solutions.toArray (new CTLNode [solutions.size ()]);
  }

  // -- prints a single solution for all placeholders
  // -- this will be called ones per a minimal solution
  public CTLNode getSolution (int bits, StatePresenter presenter)
  {
    CTLNode result = null;
    
    for (Iterator it = placeHoldersInfo.iterator (); it.hasNext ();)
      {
	PlaceholderInfo info = (PlaceholderInfo)it.next ();
	CTLPlaceholderNode node = info.getCTL ();

	// -- compute a solution for one place holder
	CTLNode phSolution = 
	  node.eq (getOneSolution (info.extractBits (bits), presenter, 
				   node.isNegated ()));
	// -- combine with solutions for other placeholders if any
	if (result == null) 
	  result = phSolution;
	else
	  result = result.and (phSolution);
      }
    return result;
  }
  
  // XXXXXXX
  // XXXXXXX This is definitely the worst written piece of code I've ever saw
  // XXXXXXX
  public CTLNode getOneSolution (int bits, StatePresenter presenter, 
				 boolean negated)
  {

    // -- if all bits are 0, that means this is \\upset F, which is 
    // -- any temporal formula
    if (bits == 0) return CTLFactory.createCTLAtomPropNode ("anything");
    
    CTLNode ctlResult = null;
    MvSet result = mvSetFactory.bot ();

    for (int i = 0; i < topBit; i++)
      {
	if (((bits >>> i) & 1) == 1)
	  {
	    MvSet term = (MvSet)bitsToTerms.get (new Integer (i));
	    result = result.or (term);
	  }
      }    
    
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
    int startBit;
    int stopBit;

    public PlaceholderInfo (CTLPlaceholderNode _node, 
			    int _startBit, int _stopBit)
    {
      node = _node;
      startBit = _startBit;
      stopBit = _stopBit;
    }
    
    public CTLPlaceholderNode getCTL ()
    {
      return node;
    }

    public int extractBits (int num)
    {
      int result = 0;
      
      // -- for each bit, extract it from num and put 
      // -- in the same place in the result
      for (int i = startBit ; i < stopBit ; i++)
	result |= (num & (1 << i));
      return result;
    }
    
  }
  

  
}
