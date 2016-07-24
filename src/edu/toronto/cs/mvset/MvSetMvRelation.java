package edu.toronto.cs.mvset;

/**
 * MvSetMvRelation.java
 *
 *
 * Created: Thu Jun 10 22:57:42 2004
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version
 */

public class MvSetMvRelation implements MvRelation
{


  /**
   * MvSet representation of the relation
   *
   */
  MvSet reln;

  /**
   * cube of pre-state variables
   *
   */
  MvSet preVariablesCube;

  /**
   * cube of post-state variables
   *
   */
  MvSet postVariablesCube;


  /**
   * map from pre- to post-state variables
   *
   */
  int[] preToPostMap;

  /**
   * map from post- to pre-state variables
   *
   */
  int[] postToPreMap;
	

  /**
   * an invariant of the relation. The actual relation is 
   * invariant /\ reln /\ currToNext (invariant)
   *
   */
  MvSet invariant;

  MvSet invariantPost;
  

  public static boolean doAssert = false;

  public MvSetMvRelation (MvSet _reln, 
			  MvSet _preVariablesCube,
			  MvSet _postVariablesCube, 
			  int[] _preToPostMap,
			  int[] _postToPreMap)
  {
    this (_reln, null, _preVariablesCube, 
	  _postVariablesCube, _preToPostMap, _postToPreMap);
  }
  

  
  /**
   * Creates a new <code>MvSetMvRelation</code> instance.
   *
   * @param _reln a <code>MvSet</code> value
   * @param _invariant a <code>MvSet</code> value
   * @param _preVariablesCube a <code>MvSet</code> value
   * @param _postVariablesCube a <code>MvSet</code> value
   * @param _preToPostMap an <code>int[]</code> value
   * @param _postToPreMap an <code>int[]</code> value
   */
  public MvSetMvRelation (MvSet _reln, 
			  MvSet _invariant,
			  MvSet _preVariablesCube,
			  MvSet _postVariablesCube, 
			  int[] _preToPostMap,
			  int[] _postToPreMap)
  {
    reln = _reln;
    invariant = _invariant;
    preVariablesCube = _preVariablesCube;
    postVariablesCube = _postVariablesCube;
    preToPostMap = _preToPostMap;
    postToPreMap = _postToPreMap;

    invariantPost = null;

    
    if (doAssert)
      assert reln.existAbstract (preVariablesCube).
	existAbstract (postVariablesCube).isConstant () : 
	"pre and post cubes for a relation are wrong!";
  }
  

  /**
   * Creates a new <code>MvSetMvRelation</code> instance.
   *
   * @param _reln a <code>MvSet</code> value
   * @param _invariant a <code>MvSet</code> value
   * @param _invariantPost a <code>MvSet</code> value
   * @param _preVariablesCube a <code>MvSet</code> value
   * @param _postVariablesCube a <code>MvSet</code> value
   * @param _preToPostMap an <code>int[]</code> value
   * @param _postToPreMap an <code>int[]</code> value
   */
  public MvSetMvRelation (MvSet _reln, 
			  MvSet _invariant,
			  MvSet _invariantPost,
			  MvSet _preVariablesCube,
			  MvSet _postVariablesCube, 
			  int[] _preToPostMap,
			  int[] _postToPreMap)
  {
    reln = _reln;
    invariant = _invariant;
    preVariablesCube = _preVariablesCube;
    postVariablesCube = _postVariablesCube;
    preToPostMap = _preToPostMap;
    postToPreMap = _postToPreMap;

    invariantPost = _invariantPost;

    if (doAssert)
      assert reln.existAbstract (preVariablesCube).
	existAbstract (postVariablesCube).isConstant () : 
	"pre and post cubes for a relation are wrong!";
  }

  
  public MvSet bwdImage (MvSet v)
  {
    if (invariant != null)
      v = v.and (invariant);

    if (invariantPost != null)
      v = v.and (invariantPost);

    
    MvSet result = reln.and (v.renameArgs (preToPostMap)).
      existAbstract (postVariablesCube);
    if (invariant != null)
      result = result.and (invariant);
    return result;
  }

  public MvSet dualBwdImage (MvSet v)
  {
    return bwdImage (v.not ()).not ();
  }


  public MvSet fwdImage (MvSet v)
  {
    if (invariant != null)
      v = v.and (invariant);


//     System.out.println ("\n !!!! fwdImage :");

//     System.out.println ("value of preVarCube");
//     System.out.println (new Node (new Node.NodeId (1), preVariablesCube));

//     System.out.println ("value of postVarCube");
//     System.out.println (new Node (new Node.NodeId (1), postVariablesCube));

//     System.out.println ("value of v");
//     System.out.println (new Node (new Node.NodeId (0), v));
    

//     System.out.println ("value of reln");
//     System.out.println (new Node (new Node.NodeId (2), reln));

//     System.out.println ("result of reln.and (v)");
//     System.out.println (new Node (new Node.NodeId (3), reln.and (v)));

//     System.out.println ("result of reln.and (v).existAbstract (preVariablesCube)");
//     System.out.println (new Node (new Node.NodeId (4), 
// 				  reln.and (v).existAbstract (preVariablesCube)));
//     System.out.println ("\n !!!!! done with fwdImage");
    
    


    MvSet result = reln.and (v).existAbstract (preVariablesCube).
      renameArgs (postToPreMap);
    if (invariant != null)
      result = result.and (invariant);

    if (invariantPost != null)
      v = v.and (invariantPost);
    

    return result;
  }

  public MvSet toMvSet ()
  {

    return reln;
  }  

  public void setTrans (MvSet v)
  {
    reln = v;


//     Node tmpNode;
//     edu.toronto.cs.yasm.YasrApp.err.println ("setTrans before");
//     tmpNode = new Node(new Node.NodeId(1000), reln);
//     edu.toronto.cs.yasm.YasrApp.err.println (tmpNode);
    
//     edu.toronto.cs.yasm.YasrApp.err.println ("setTrans after exist");
//     tmpNode = new Node(new Node.NodeId(1000), reln.existAbstract (preVariablesCube).
//       existAbstract (postVariablesCube));
//     edu.toronto.cs.yasm.YasrApp.err.println (tmpNode);

    assert reln.existAbstract (preVariablesCube).
      existAbstract (postVariablesCube).isConstant () : 
      "pre and post cubes for a relation are wrong!";
  }
  

  /**
   * Computes a composition of two relations
   * this ; r
   *
   * @param r a <code>MvSetMvRelation</code> value
   * @return a <code>MvSetMvRelation</code> value
   */
  public MvSetMvRelation compose (MvSetMvRelation r)
  {
    // -- XXX questions to answer:
    // -- XXX if a relation has an invariant what happens to it during
    // -- XXX composition. 
    // -- XXX if a relation has pre- and post- conditions, what happens
    // -- XXX to them during compositon?!
//     MvSet rMvSet = r.toMvSet ();
    
//     if (invariant != null)
//       {
// 	// -- apply the invariant
// 	rMvSet = rMvSet.and (invariant);
//       }
//     if (
    
    
//     // -- map dst variables of this to auxillary variables in r
//     rMvSet = rMvSet.renameArgs (dstToAuxMap);
    
//     // -- map dst variables of this to auxillary variables
//     MvSet thisMvSet = toMvSet ().renameArgs (dstToAuxMap);
    
//     // -- do relational composition
//     MvSet resultMvSet = thisMvSet.and (rMvSet).existAbstract (auxVarsCube);

//     // -- construct a new mv relation
//     return new MvSetMvRelation (resultMvSet, 
// 				srcVarsCube,
// 				srcToDstMap,
// 				dstVarsCube,
// 				dstToAuxMap,
// 				auxVarsCube,
// 				getPreCond (),
// 				r.getPostCond ());
    
    return null;
  }



  /**
   * return cube of pre-state variables
   *
   */
  public MvSet getPreVariablesCube ()
  {
    return preVariablesCube;    
  }
  

  /**
   * get cube of post-state variables
   *
   */
  public MvSet getPostVariablesCube ()
  {
    return postVariablesCube;    
  }

  /**
   * get map from pre- to post-state variables
   *
   */
  public int[] getPreToPostMap ()
  {
    return preToPostMap;    
  }
  

  /**
   * get map from post- to pre-state variables
   *
   */
  public int[] getPostToPreMap ()
  {
    return postToPreMap;    
  }
  
}// MvSetMvRelation
