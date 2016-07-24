package edu.toronto.cs.mdd;

import java.util.*;

import edu.toronto.cs.mdd.ApplyFunctions.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;


/**
 ** Multi-Valued Decision Diagram package 
 ** add reference to authors and publications!
    @version 1.1
    @author B. Devereux
    @author A. Gurfinkel
    @auther many others
*/
public class MDDManager
{

  
  /**
   * Used as an indicator for arbitrary value for a variable.
   *
   */
  public static final int NO_VALUE = -1;
  
  
  // -- maps variable names to DD levels
  // -- MAP: Names -> Levels
  int[] names;
  // -- maps DD levels to variable names
  // -- MAP: Levels -> Names
  int[] levels;

  // -- default arity of each DD node
  int defaultArity;

  // -- number of nodes actually in use in this MDDManager
  // -- invariant: usedSize <= names.length
  int usedSize;

  // -- unique table for non-leaf nodes
  UniqueTable uniqueTable;

  // -- unique table for leaf nodes
  Map<MDDLeafNode,MDDLeafNode> leafNodes;
  


  // -- the cache
  MDDNodeCache cache;

  
  // -- instances to avoide creating new objects when looking up nodes
  // -- in eihter leafNodes or uniqueTable.
  // XXX This assumes that all calls to MDDManager are synchronized!
  MDDTreeNode lookupTreeNode;
  MDDLeafNode lookupLeafNode;
  
  // -- true if caching should be enabled. Only useful for debugging
  // -- and comparison
  boolean caching = true;
  

  /** Constructor
      @param nvars initial number of variables
      @param arity breadth factor of each DD node
  */
  
  public MDDManager(int nvars, int _arity) 
  {
    names = new int [nvars];
    levels = new int [nvars];
    
    // -- for now mapping is one to one but this should 
    // -- be factored out 
    for (int i = 0; i < names.length; i++)
      {
	names [i] = i;
	levels [names [i]] = i;
      }
    

    defaultArity = _arity;
	

    // -- Create maps to keep track of DD nodes and terminals
    uniqueTable = new UniqueTable (levels.length);
    leafNodes = new HashMap<MDDLeafNode,MDDLeafNode> ();

    // -- create the cache.
    cache = (MDDNodeCache) new MDDCacheStub ();
    

    
    // -- create a single lookup node for efficiency
    lookupTreeNode = new MDDTreeNode ();
    lookupLeafNode = new MDDLeafNode ();
  }

  /**
   * Clears the computational cache. Can be used when the semantics of the
   * operations have changed during the life-time of this manager.
   *
   */
  public void renew()
  {
    cache.cleanup();
    // XXX Shouldn't this be enabled?! Multiple runs of the XXX
    // analysis may be influenced by this, but maybe this is ok
    // //uniqueTable = new UniqueTable (levels.length);
  }
  
  
  /**
   * Number of currently allocated variables. Any existing DD can have
   * at most that many variables.
   *
   * @return an <code>int</code> value
   */
  public int getNvars() 
  {
    return usedSize;
    //return names.length;
    
    
  }
  
  /**
   * Toggles caching.
   *
   * @param v a <code>boolean</code> value
   */
  public void setCaching (boolean v)
  {
    caching = v;
    // -- clear the cache
    //      cache.cleanup ();
    //      uniqueTable = new Hashtable ();
  }
    
  

  /**
   * Returns a cannonical version of a leaf node with value {@code var}.
   * @param val value of the constant node
   * @return a <code>MDDLeafNode</code> value
   */
  public MDDLeafNode getLeafNode (int val) 
  {
    lookupLeafNode.setValue (val);
    MDDLeafNode result = leafNodes.get (lookupLeafNode);
    if (result == null)
      {
	result = new MDDLeafNode (val);
	leafNodes.put (result, result);
      }
    return result;
  }


  /**
   * Creates a decision diagram with root {@code var} and children 
   * {@code children}. The root node of each child is assumed to be
   * greater than {@code var}. This is not checked!
   *
   * @param var a NAME of the node to create
   * @param children a <code>MDDNode[]</code> value
   * @return a <code>MDDNode</code> value
   */
  public MDDNode kase (int var, MDDNode[] children)
  {
    
    //Assert.assert ("Not Implemented");
    // XXX This is a bit harder since we need to propogate var into 
    // XXX children to attain correct ordering
    return makeUnique (names [var], children);
  }


  /**
   * Ensures that all data structures can accomodate all levels upto
   * and including level {@code v}
   *
   * @param v an <code>int</code> value
   */
  private void ensureCapacity (int v)
  {
    
    assert v >= levels.length : "Have enough space: " + levels.length + 
      " and asking for " + v;
    

    // -- ensure that new size is at lest twice the old size
    // -- one is added to ensure that level v can be accomodated
    int newSize = (levels.length << 1);
    if (v >= newSize) newSize = v + 1;

    // -- allocate new names and levels arrays
    int[] newNames = new int [newSize];
    int[] newLevels = new int [newSize];
    
    // -- copy old levels and names into new ones
    System.arraycopy (names, 0, newNames, 0, names.length);
    System.arraycopy (levels, 0, newLevels, 0, levels.length);

    // -- set the names/levels map for new variables
    // -- new variables start at index names.length 
    for (int i = names.length; i < newNames.length; i++)
      {
	newNames [i] = i;
	newLevels [newNames [i]] = i;
      }
    
    // -- get rid of old levels and names and replace them with the
    // -- new ones
    levels = newLevels;
    names = newNames;

    // -- resize the unique table as well
    uniqueTable.resize (newSize);
  }
  
  /**
   * Returns the cannonical version of an {@code MDDNode} with root
   * level {@code level} and children {@code children}.
   *
   * @param level level at the root of the tree
   * @param children array of children
   * @return a cannonical <code>MDDNode</code> value with level {@code
   * level} and children {@code children}
  */
  public MDDNode makeUnique(int level, MDDNode[] children) 
  {

    // first check if it is a constant node, i.e. all children are equal
    boolean constant = true;
    MDDNode first = children [0];
    int arity = children.length;
    
    assert arity > 1 : "There must be at least 2 children";
    
    for (int i = 1; i < arity; i++)
      {
	// -- precondition: every node comes from a unique table
	// -- thus we can use object identity instead of equality
	if (first != children [i]) 
	  {
	    constant = false;
	    break;
	  }
      }
		

    // -- if this is a constant node, bail out quickly
    if (constant) 
      return first;


    // -- resize datastructures if necessary to accomodate the node
    // -- to be created
    if (level >= levels.length)
      ensureCapacity (level);
    
    // -- update usedSize, which is the number of variables used.
    if (level + 1 > usedSize) usedSize = level + 1;

    


    // XXX This is expensive, but usefull for debugging.
    // XXX check that we do not create a wrong diagram
    for (int i = 0; i < children.length; i++)
      assert levels [level] != children [i].getVarIndex () :
	"Creating a bad diagram at for variabe " + levels [level] + 
	" whose " + i + "th child has the same index";
    
    
    // -- get a cannonical version of this node from the unique table
    lookupTreeNode.setVarIndex (levels [level]);
    lookupTreeNode.setChildren (children);

    return uniqueTable.cannonize (lookupTreeNode);
  }      


  

  public MDDNode apply (BinApplyFunction function, MDDNode m1, MDDNode m2)
  {
    MDDNode answer = null;



    // -- see if we can bail out quickly
    answer = function.binApply (m1, m2);
    if (answer != null)
      return answer;

    if (caching && function.isCacheable ())
      {
	if (function.isSymetric () && 
	    m1.objectHashCode () > m2.objectHashCode ())
	  return apply (function, m2, m1);
	answer = cache.find (function, m1, m2);
      }
    
      
    //     // check cache for result.      
    //     if (caching && function.isCacheable ())
    //       {
    // 	answer = cache.find (function, m1, m2);
    // 	// -- if function is symetric, try reversing the arguments
    // 	if (answer == null && function.isSymetric ())
    // 	  answer = cache.find (function, m2, m1);
    //       }
      
      
    if (answer != null) return answer;


    // otherwise must do the computation.

    // temporary array to hold children
    MDDNode[] kids; 
    
    
    // -- both non-constant, with the same index
    if (m1.getVarIndex () == m2.getVarIndex ())
      {	
	assert m1.childrenSize () == m2.childrenSize () 
	  : "Same index means same arity";
	
	int arity = m1.childrenSize ();	
	kids = new MDDNode [arity];
	
	for (int i = 0; i < arity; i++)
	  kids [i] = apply (function, m1.getChild (i), m2.getChild (i));

	answer = makeUnique (names [m1.getVarIndex ()], kids);
      }

    // -- both non-constant, m1 < m2 or m2 is constant
    else if (m2.isConstant () || 
	     (!m1.isConstant () && 
	      names [m1.getVarIndex ()] < names [m2.getVarIndex ()]))
      {
	int arity = m1.childrenSize ();
	kids = new MDDNode [arity];
	for (int i = 0; i < arity; i++)
	  kids [i] = apply (function, m1.getChild (i), m2);
	answer = makeUnique (names [m1.getVarIndex()], kids); 
      } 
		      
    // --  both non-constant, m1 > m2, or m1 is the only constant
    else 
      {
	int arity = m2.childrenSize ();
	kids = new MDDNode [arity];
	for (int i = 0; i < arity; i++)
	  kids [i] = apply (function, m1, m2.getChild (i));
	answer = makeUnique (names [m2.getVarIndex()], kids);
      } 
	
    // -- store the result in the cache for efficiency reasons
    if (caching && function.isCacheable ())
      cache.insert(function, m1, m2, answer);
    return answer;
  }

  
  
  /** Apply a unary operation to MDDs. */
  public MDDNode apply (UnaryApplyFunction function, MDDNode m1)
  {
    MDDNode answer = null;

    // -- see if we can bail out quickly
    answer = function.unaryApply (m1);
    if (answer != null) return answer;
    
      
    if (caching && function.isCacheable ())
      answer = cache.find(function, m1);

    if (answer != null) return answer;


    int arity = m1.childrenSize ();
    // not in cache! So, we have to do the work.
    MDDNode[] kids = new MDDNode [arity];
      
    // apply 'oper' to each child, and make new unique node
    for (int i = 0; i < arity; i++)
      kids[i] = apply (function, m1.getChild (i));

    answer = makeUnique (names [m1.getVarIndex ()], kids);
		
    if (caching && function.isCacheable ())
      cache.insert(function, m1, answer);
    return answer;
  }


  public MDDNode cofactor (MDDNode m, MDDNode cube, MDDNode background)
  {
    if (cube.isConstant () || m.isConstant ()) return m;
    
    MDDNode[] kids = new MDDNode [m.childrenSize ()];
    Arrays.fill (kids, background);
    
    MDDNode answer = null;
    
    if (m.getVarIndex () == cube.getVarIndex ())
      {
	for (int i = 0; i < kids.length; i++)
	  if (cube.getChild (i) != background)
	    return cofactor (m.getChild (i), cube.getChild (i), background);
      }
    else if (names [m.getVarIndex ()] < names [cube.getVarIndex ()])
      {
	for (int i = 0; i < kids.length; i++)
	  kids [i] = cofactor (m.getChild (i), cube, background);
	
	answer = makeUnique (names [m.getVarIndex ()], kids);
      }
    else
      for (int i = 0; i < kids.length; i++)
	if (cube.getChild (i) != background)
	  return cofactor (m, cube.getChild (i), background);

    
    assert answer != null;
    
    return answer;
  }
  

  /** Restrict a specified variable to a certain value. */
  public MDDNode cofactor (MDDNode m, int var, int branch)
  {
    
    /*** Since variables are ordered, if the top level variable in
     *** 'm' has has order higher then 'names [var]', then there is no
     *** variable with order 'names [var]' in the MDD and we can bail out
     *** right away
     ***/
    if (m.isConstant())
      return m;

    if (names [m.getVarIndex ()] > names [var])
      return m;

    if (names [m.getVarIndex ()] < names [var])
      {
	int arity = m.childrenSize ();
	MDDNode[] kids = new MDDNode [arity];
	for (int i = 0; i < arity; i++)
	  kids[i] = cofactor (m.getChild (i), var, branch);
	
	return makeUnique (names [m.getVarIndex()], kids);		 
      }
    else 
      {
	assert m.childrenSize () > branch;
	return m.getChild (branch);
      }
    
  }


  /***
   *** performs universal/existentia/other quantification
   ***/
  public MDDNode quantify (QuantifyFunction function, MDDNode node, 
			   MDDNode cube)
  {
    // -- quick bail out
    if (cube.isConstant () || node.isConstant ()) return node;
    

    int largestIndex = names [cube.getCubeLastVar ()];
    // -- top most (the lowest index) variable of the node
    int nodeIndex = names [node.getVarIndex ()];


    // -- nothing to quantify over
    if (largestIndex < nodeIndex) return node;

    // -- top most (the lowest index) variable of the cube
    int cubeIndex = names [cube.getVarIndex ()];


    /** if the lowest variable in 'node' is higher than the one of the cube
     ** then the 'node' does not depend on this variable and quantification
     ** does not change anything, just skip an element of the cube and go on
     **/
    if (cubeIndex < nodeIndex)
      return quantify (function, node, cube.getChild (0));    
    
    /*** we will have to do something from here so check the cache first */
    MDDNode answer = null;

    if (caching && function.isCacheable ())
      answer = cache.find (function, node, cube);

    if (answer != null) return answer;

    int arity = node.childrenSize ();
    if (cubeIndex > nodeIndex)
      {
	// -- create a new node with 'nodeIndex' where each child is 
	// -- abstracted
	MDDNode[] kids = new MDDNode [arity];
	for (int i = 0; i < arity; i++)
	  kids [i] = quantify (function, node.getChild (i), cube);
	answer = makeUnique (nodeIndex, kids);
      }
    else // -- cubeIndex == nodeIndex
      {
	// -- we start with the identity of our quantification 
	answer = function.getIdentity (cube.getVarIndex ());
	
	MDDNode nextCube = cube.getChild (0);
	// -- just compute all cofactors and produce a JOIN of them
	for (int i = 0; i < arity; i++)
	  {
	    MDDNode child = quantify (function, node.getChild (i), nextCube);
	    answer = 
	      apply (function.getOperator (cube.getVarIndex ()), 
		     answer, child);
	    // -- we can bail out as soon as result is top
	    if (function.canTerminate (cube.getVarIndex (), answer)) break;
	  }
      }
    // -- cache the result
    if (caching && function.isCacheable ())
      cache.insert (function, node, cube, answer);
    return answer;
  }


  public MDDNode renameVars (MDDNode mdd, int[] newVars)
  {
    // XXX This is only this simple because of the variable
    // XXX ordering we currently have. This is far from working 
    // XXX in general

    // -- we do not prime constant nodes
    if (mdd.isConstant ()) return mdd;

    MDDNode answer = null;

    // XXX Are we sure caching this is good?!
    // XXXX Caching is very important for any reqursive operation on
    // XXXX DDs since without caching it is as though we operate on 
    // XXXX the tree representation of the DD. On the other this raises
    // XXXX the question of whether  we can implement some priority
    // XXXX on our caching strategy
     if (caching)
       answer = cache.find (newVars, mdd);

    if (answer != null) return answer;

    int arity = mdd.childrenSize ();
    MDDNode[] renamedChildren = new MDDNode [arity];
    
    for (int i = 0; i < arity; i++)
      renamedChildren [i] = renameVars (mdd.getChild (i), newVars);

    int varIndex = mdd.getVarIndex ();
    
    answer = makeUnique (names [newVars [varIndex]], renamedChildren);
    if (caching)
      cache.insert (newVars, mdd, answer);
    return answer;
  }


  /**
   * Sorts an array of variables {@code vars} into decision diagram order
   *
   * @param vars an array of variable names
   * @return a sorted array of variable names
   */
  private int[] sortVars (int[] vars)
  {

    int[] varsSorted;

    varsSorted = new int [names.length];
    Arrays.fill (varsSorted, NO_VALUE);
    
    for (int i = 0; i < vars.length ; i++)
      varsSorted [names [vars [i]]] = vars [i];
    return varsSorted;
  }
  

  public MDDNode buildVar (int var, int branch, MDDNode value, MDDNode bot)
  {
    return buildVar (var, defaultArity, branch, value, bot);
  }
  
  public MDDNode buildVar (int var, int arity, 
			   int branch, MDDNode value, MDDNode bot)
  {
    MDDNode[] kids = new MDDNode [arity];
    Arrays.fill (kids, bot);
    kids [branch] = value;

    // -- when creating a variable, if its name was not yet allocated
    // -- then it is not in the names table, and it's name and level
    // -- is the same. We let makeUnique worry about allocating enough space
    // -- for it.
    return makeUnique (var < names.length ? names [var] : var, kids);
  }
  
  public MDDNode buildCube (int[] vars, MDDNode top, MDDNode bot)
  {
    return buildCube (vars, defaultArity, top, bot);
  }
  
  public MDDNode buildCube (int[] vars, int arity, MDDNode top, MDDNode bot)
  {

    // -- if vars is empty, return top
    if (vars.length == 0) return top;
    
    
    // -- find the maximum variable occurring in vars and ensure
    // -- we have enough capacity to store it    
    int maxName = vars [0];
    for (int i = 1; i < vars.length; i++)
      maxName = vars [i] > maxName ? vars [i] : maxName;

    if (maxName >= names.length) ensureCapacity (maxName);
    
    int[] varsSorted = sortVars (vars);
    
    MDDNode answer = top;
    MDDNode[] kids;
    
    for (int i = varsSorted.length - 1; i >= 0 ; i--)
      {
	if (varsSorted [i] == NO_VALUE) continue;
	
	kids = new MDDNode [arity];
	Arrays.fill (kids, bot);
	kids [0] = answer;
	answer = makeUnique (names [varsSorted [i]], kids);
      }
    return answer;
  }
  
  public MDDNode buildPoint (int[] values, MDDNode terminal, MDDNode bot)
  {
    return buildPoint (values, defaultArity, terminal, bot);
  }
  
  public MDDNode buildPoint (int[] values, int arity, 
			     MDDNode terminal, MDDNode bot)
  {
    return buildPoint (values, values.length, arity, terminal, bot);
  }
  

  /**
   * Constructs a point (minterm) in which a path along {@code i =
   * values[i]} leads to {@code terminal}, and all other assignments
   * lead to {@code bot}.
   *
   * @param values an <code>int[]</code> value
   * @param arity an <code>int</code> value
   * @param terminal a <code>MDDNode</code> value
   * @param bot a <code>MDDNode</code> value
   * @return a <code>MDDNode</code> value
   */
  protected MDDNode buildPoint (int[] values, int valuesLen, 
				int arity, MDDNode terminal, MDDNode bot)
  {
    // -- if values is empty, bail out quickly
    if (valuesLen == 0) return terminal;

    // -- make sure we have enough nodes
    // -- node that index of values corresponds to a variable 
    // -- name. So we need as many names as the length of values
    if (valuesLen > names.length) ensureCapacity (valuesLen);
    
    
    int[] valuesLeveled = new int[levels.length];
    Arrays.fill (valuesLeveled, NO_VALUE);
    
    // -- order values based on levels rather than names
    for (int i = 0; i < valuesLen; i++)
      valuesLeveled [names [i]] = values [i];
    
    MDDNode res = terminal;
    
    for (int i = valuesLeveled.length - 1; i >= 0; i--)
      {
	if (valuesLeveled [i] == NO_VALUE) continue;
	MDDNode[] kids = new MDDNode [arity];
	Arrays.fill (kids, bot);
	kids [valuesLeveled [i]] = res;
	res = makeUnique (i, kids);
      }
    return res;
  }
  

  
  public int dagSize (MDDNode mddNode)
  {
    Set<MDDNode> seen = new HashSet<MDDNode> ();
    return dagSizeRecur (mddNode, seen);
  }
  
  public int sharedSize (MDDNode[] mddNodes)
  {
    Set<MDDNode> seen = new HashSet<MDDNode> ();
    int result = 0;
    for (int i = 0; i < mddNodes.length; i++)
      result += dagSizeRecur (mddNodes [i], seen);
    return result;
  }
  
  
  int dagSizeRecur (MDDNode node, Set<MDDNode> seen)
  {
    // -- don't count the ones we've seen already
    if (seen.contains (node)) return 0;

    // -- we've seen this node so don't need to count it twice
    seen.add (node);

    // -- if node is constant don't have to go any further
    if (node.isConstant ()) return 1;

    int result = 1;
    int arity = node.childrenSize ();
    // -- node is not constant so count the children and add one
    for (int i = 0; i < arity; i++)
      result += dagSizeRecur (node.getChild (i), seen);

    return result;
  }


  // -- expands this MDD into an array
  // -- currently this is a bit ugly and therefore delegated to 
  // -- MDDValueCollector
  public int[][] collectValues (MDDNode node)
  {
    return MDDValueCollector.collectValues (node, names.length);
  }




  /**
   * <code>cubeIterator</code>.
   *
   * @return an <code>Iterator</code> over the cubes of <code>node</code>
   */
  public Iterator cubeIterator (MDDNode node, MDDNode background)
  {
    return new MDDCubeIterator (node, background);
  }

  public Iterator mintermIterator (MDDNode node, MDDNode background, 
				   MDDNode vars, int termVal)
  {
    return new MDDMintermIterator (node, background, vars, termVal);
  }
  

  
  public DaVinciGraph toDaVinci (MDDNode node)
  {
    return MDDToDaVinci.toDavinci (node);
  }
  


  /**
   ** Iterates over minterms of a given value
   ** vars parameter is used to fill-in don't care variables
   **/
  public class MDDMintermIterator implements Iterator 
  {
    MDDCubeIterator cubeIterator;
    MDDNode minterm;
    MDDNode vars;
    int termVal;
    MDDNode background;
    
    public MDDMintermIterator (MDDNode node, MDDNode _background,
			       MDDNode _vars, int _termVal)
    {
      cubeIterator = new MDDCubeIterator (node, _background, false);
      vars = _vars;
      termVal = _termVal;
      background = _background;

      minterm = findMinterm ();
    }

    public boolean hasNext ()
    {
      return minterm != null;
    }

    public Object next ()
    {
      return nextMinterm ();
    }

    public void remove ()
    {
      throw 
	new UnsupportedOperationException ("remove method is not supported");
    }

    MDDNode nextMinterm ()
    {
      if (!hasNext ()) throw new NoSuchElementException ("no more minterms");

      MDDNode currentMinterm = minterm;
      minterm = findMinterm ();
      return currentMinterm;
    }
    

    MDDNode findMinterm ()
    {
      int[] cube = getNextCube ();
      if (cube == null) return null;
      
      return arrayCubeToMDD (cube, vars);
    }

    int[] getNextCube ()
    {
      while (cubeIterator.hasNext ())
	{
	  int[] cube = (int[])cubeIterator.next ();
	  if (cube [cube.length - 1] == termVal) return cube;
	}
      return null;
    }
    

    
    MDDNode arrayCubeToMDD (int[] cube, MDDNode vars)
    {
      if (vars.isConstant ()) 
	return buildPoint (cube, cube.length - 1, defaultArity, 
			   vars, background);

      if (cube [vars.getVarIndex ()] == NO_VALUE)
	cube [vars.getVarIndex ()] = 0;
      
      return arrayCubeToMDD (cube, vars.getChild (0));
    }

    int[] extractVars (int[] cube)
    {
      int size = 0;
      for (int i = 0; i < cube.length; i++)
	if (cube [i] != NO_VALUE)
	  size++;
      int[] res = new int [size];
      int count = 0;
      for (int i = 0; i < cube.length; i++)
	{
	  if (cube [i] != NO_VALUE)
	    res [count++] = cube [i];
	}
      return res;
    }
    
    
    
    
    /***
     ** given an array of assignments 'cube'
     ** ordered by levels and a cube 'vars' returns an MDDNode
     ** corresponding to the cube
     **/
    MDDNode __arrayCubeToMDD (int[] cube, MDDNode vars)
    {
      
      if (vars.isConstant ()) return vars;
      
      int index = vars.getVarIndex ();
      MDDNode[] kids = new MDDNode [vars.childrenSize ()];
      Arrays.fill (kids, background);
      
      kids [ cube[index] == NO_VALUE ? 0 : cube [index] ] =
	arrayCubeToMDD (cube, vars.getChild (0));
      
      return makeUnique (names [index], kids);
    }
    
    

    
  }
  

  public class MDDCubeIterator implements Iterator
  {
    
    MDDNode node;
    MDDNode background;

    int[] cube;
   
    // -- current path on the stack
    Stack<MDDNode> path;

    // -- whether to return a cube on levels or on indecies of variables
    // -- level cubes are only used internally
    boolean levelCube;
   
    public MDDCubeIterator (MDDNode _node, MDDNode _background)
    {
      this (_node, _background, false);
    }
    
    
    public MDDCubeIterator (MDDNode _node, MDDNode _background, 
			    boolean _levelCube)
    {
      node = _node;
      background = _background;
      levelCube = _levelCube;

      path = new Stack<MDDNode> ();

      // -- get the first cube right away
      cube = firstCube ();
    }

    public boolean hasNext ()
    {
      // -- there are more elements if currentCube is not empty
      return cube != null;
    }
    

    public Object next ()
    {
      int[] cubeToReturn = (int[])cube.clone ();
      nextCube ();
      return cubeToReturn;
    }
    
    public void remove ()
    {
      throw 
	new UnsupportedOperationException ("remove method is not supported");
    }
    

    /**
     * <code>firstCube</code>.
     *
     * @return an <code>int[]</code> of values corresponding 
     * to the first cube. The last element of the array is the cube value, 
     * the rest are the values for the variables.
     */
    public int[] firstCube ()
    {
      // -- set up the cube and fill it with no values
      int[] cube = new int [getNvars () + 1];
      Arrays.fill (cube, NO_VALUE);

      // -- the background value has no cubes at all
      if (node == background) return null;
     
      // -- if this is a constant node, then terminate right away      
      if (node.isConstant ())
	{
	  cube [cube.length - 1] = node.getValue ();
	  return cube;
	}
      
      // -- add our starting node to the stack
      path.push (node);
      
      // -- continue until we hit a terminal node
      while (true)
	{
	  // -- get the current node
	  MDDNode currentNode = path.peek ();
	 
	  // -- get its first non-background child
	  int currNodeIndex = currentNode.getVarIndex ();
	  currNodeIndex = levelCube ? names [currNodeIndex] : currNodeIndex;
	  
	  cube [currNodeIndex] 
	    = firstChild (currentNode.getChildren (), 0, background);
	  
	  // -- update current node
	  currentNode = 
	    currentNode.getChild (cube [currNodeIndex]);

	  // -- we hit a terminal
	  if (currentNode.isConstant ())
	    {
	      cube [cube.length - 1] = currentNode.getValue ();
	      // -- we are done
	      return cube;
	    }
	  
	  // -- if current node is a tree node, put it on the stack and 
	  // -- continue
	  path.push (currentNode);
	}
    }
    
    private void nextCube ()
    {

      while (true)
	{
	 
	  if (path.empty ())
	    {
	      cube = null;
	      return;
	    }
	  
	  // -- get current node of the stack
	  MDDNode currentNode = path.peek ();
	  int currNodeIndex = currentNode.getVarIndex ();
	  currNodeIndex = levelCube ? names [currNodeIndex] : currNodeIndex;
	  // -- get next non-background node
	  int nonBackChildIdx = 
	    firstChild (currentNode.getChildren (), 
			cube [currNodeIndex] + 1, 
			background);
	  // -- we have done with this node so backtrack
	  if (nonBackChildIdx == -1)
	    {
	      cube [currNodeIndex] = NO_VALUE;
	      path.pop ();
	      continue;
	    }

	  // -- we have found next child to explore
	  cube [currNodeIndex] = nonBackChildIdx;
	  
	  // -- update currentNode to new value and check if we hit
	  // -- a terminal
	  currentNode = currentNode.getChild (nonBackChildIdx);
	  if (currentNode.isConstant ())
	    {
	      cube [cube.length - 1] = currentNode.getValue ();
	      break;
	    }
	  path.push (currentNode);
	}
    }

    // -- finds first child that is not equal to background value
    int firstChild (MDDNode[] kids, int start, MDDNode background)
    {
      for (int i = start; i < kids.length; i++)
	if (kids [i] != background) return i;

      return -1;
    }
    
    
    
  }
  


  /*********
   ********* The rest does not seem to be required for immediate use
   ********* of the model checker so we'll tackle it later on.
   *********
   *********/

  /*** 
   *** Convert a DD to a new MDDManager 
   ***/
//   public MDDNode transfer (MDDManager dstManager, MDDNode srcNode, 
// 			   int[] logicMap)
//   {
//     return transfer (dstManager, srcNode, logicMap, 1);
//   }

//   public MDDNode transfer (MDDManager dstManager, MDDNode srcNode, 
// 			   int[] logicMap, int factor)
//   {
//     Map transferCache = new HashMap ();
//     return transfer (dstManager, srcNode, logicMap, factor, transferCache);
//   }
  
//   public MDDNode transfer (MDDManager dstManager, MDDNode srcNode, 
// 			   int[] logicMap, int factor, Map transferCache)
//   {
//     if (srcNode.isConstant ())
//       return dstManager.getLeafNode (logicMap [srcNode.getValue ()]);
    
//     MDDNode result = (MDDNode)transferCache.get (srcNode);
//     if (result != null) return result;

    
//     MDDNode[] children = new MDDNode [dstManager.getArity ()];
    
//     Arrays.fill (children, dstManager.getZero ());
    
//     for (int i = 0; i < srcNode.howManyChildren (); i++)
//       {
// 	// -- skip if the map is not defined, this is useful to map
// 	// -- n-valued diagaram into m-valued diagram when m < n
// 	if (logicMap [i] == Algebra.NO_VALUE) continue;
	
// 	children [logicMap [i]] = transfer (dstManager, srcNode.getChild (i),
// 					    logicMap, factor, transferCache);
//       }
    
    
//     result = dstManager.makeUnique(srcNode.getVarIndex () * factor, 
// 				   children);
//     transferCache.put (srcNode, result);
//     return result;
//   }
  

//   public MvSet transfer (MvSetFactory dstFactory, MDDNode srcNode, 
// 			 int[] logicMap)
//   {
//     return transfer (dstFactory, srcNode, logicMap, 1);
//   }

//   public MvSet transfer (MvSetFactory dstFactory, MDDNode srcNode, 
// 			 int[] logicMap, int factor)
//   {
//     Map transferCache = new HashMap ();
//     return transfer (dstFactory, srcNode, logicMap, factor, transferCache);
//   }


//   public MvSet transfer (MvSetFactory dstFactory, MDDNode srcNode, 
// 			 int[] logicMap, int factor, Map transferCache)
//   {
//     if (srcNode.isConstant ())
//       return dstFactory.createConstant (logicMap [srcNode.getValue ()]);
    
//     MvSet result = (MvSet)transferCache.get (srcNode);
//     if (result != null) return result;

    
//     MvSet[] children = new MvSet [dstFactory.getLattice ().size ()];
    
//     Arrays.fill (children, 
// 		 dstFactory.createConstant 
// 		 (dstFactory.getLattice ().idplusIdx ()));
    
//     for (int i = 0; i < srcNode.howManyChildren (); i++)
//       {
// 	// -- skip if the map is not defined, this is useful to map
// 	// -- n-valued diagaram into m-valued diagram when m < n
// 	if (logicMap [i] == Algebra.NO_VALUE) continue;
	
// 	children [logicMap [i]] = transfer (dstFactory, srcNode.getChild (i),
// 					    logicMap, factor, transferCache);
//       }
    
    
//     result = 
//       dstFactory.createCase (srcNode.getVarIndex () * factor, children);
    
//     transferCache.put (srcNode, result);
//     return result;
//   }



//   /***
//    *** Reduces the DD by treating x = src as x = dst, for all x
//    ***/
//   public MDDNode reduce (MDDNode node, int src, int dst)
//   {
//     if (node.isConstant ())
//       // -- nothing to do
//       return node;
    

//     // XXX Potentially we can do the merge and reduce at the same time
//     // XXX but this should be good enough for now

//     // -- array to hold children
//     MDDNode[] children = new MDDNode [arity];

//     // -- recursively reduce the children
//     for (int i = 0; i < children.length; i++)
//       children [i] = reduce (node.getChild (i), src, dst);
    
//     // -- merge children of src with dst
//     children [dst] = 
//       apply (children [dst], children [src], FiniteLattice.JOIN);
//     // -- remove src
//     children [src] = getZero ();
    
//     // -- done
//     return makeUnique (node.getVarIndex (), children);
//   }

//   public MDDNode killEdge (MDDNode node, boolean[] map, int val)
//   {
//     if (node.isConstant ()) return node;
    
//     MDDNode[] children = new MDDNode [arity];
    
//     for (int i = 0; i < children.length; i++)
//       children [i] = killEdge (node.getChild (i), map, val);
    
//     // -- for all boolean arguments, clean up 
//     if (!map [node.getVarIndex ()])
//       {
// 	// -- check if the node is constant except for val edge
// 	boolean constant = true;
// 	for (int i = 1; i < children.length; i++)
// 	  if (i != val && children [0] != children [i])
// 	    {
// 	      constant = false;
// 	      break;
// 	    }
	
	
// 	if (constant)
// 	  {
// 	    return children [0];
// 	  }
	
// 	else
// 	  children [val] = getZero ();
//       }
    
    
//     return makeUnique (node.getVarIndex (), children);
    
//   }

//   public MDDPoint getRandomPoint (MDDNode node, ChaosIterator varDomain)
//   {
//     // -- traverse the tree rooted at node, until a terminal node
//     MDDPoint mddPoint = new MDDPoint (getOne (), getOne ().getValue ());
    
//     return getRandomPointRecur (node, varDomain, mddPoint);
//   }
  
  
//   MDDPoint getRandomPointRecur (MDDNode node, ChaosIterator varDomain, 
// 				MDDPoint point)
//   {
//     if (node.isConstant ())
//       {
// 	point.setValue (node.getValue ());
// 	return point;
//       }
    

//     // -- pick a child
//     int childIdx = varDomain.nextInt ();

//     // -- traverse the diagram first (otherwise we get an exp. algorithm)
//     point = getRandomPointRecur (node.getChild (childIdx), 
// 				 varDomain, point);
    
//     // -- now add this node to the top of the point generated so far
//     MDDNode var = getVarEquals (node.getVarIndex (), childIdx);
//     point.setNode (apply (getVarEquals (node.getVarIndex (), childIdx),
// 			  point.getNode (),
// 			  FiniteLattice.MEET));
//     return point;
    
//   }
  

//   public static interface ApplyFunction
//   {
//     // -- id to be used for caching
//     int getId ();
//     boolean isSymetric ();
//     boolean isCacheable ();
//     // -- apply the function to two arguments
//     int binApply (int a, int b);
//   }
  

  class UniqueTable 
  {
    // XXX levels here are varIndex actually :)
    Map<MDDNode,MDDNode> [] levelTable;
    
    public UniqueTable (int levels)
    {
      levelTable = new Map [levels];
      for (int i = 0; i < levelTable.length; i++)
	levelTable [i] = newMap ();
    }

    public void put (MDDTreeNode node)
    {
      int nodeIndex = node.getVarIndex ();

      // -- create a new map if necessary
      if (levelTable [nodeIndex] == null)
	levelTable [nodeIndex] = newMap ();
      
      // -- remember the node
      levelTable [node.getVarIndex ()].put (node, node);
    }

    /***
     *** returns a cannonical version of the node
     ***/
    public MDDTreeNode cannonize (MDDTreeNode node)
    {
      MDDTreeNode result = null;
      
      if (node.getVarIndex () < levelTable.length &&
	  levelTable [node.getVarIndex ()] != null)
	result = (MDDTreeNode) levelTable [node.getVarIndex ()].get (node);

      if (result == null)
	{
	  result = new MDDTreeNode (node.getVarIndex (), node.getChildren ());
	  //levelTable [node.getVarIndex ()].put (result, result);
	  put (result);
	}
      
      return result;
    }


    /**
     * Resizes this unique table to {@code newSize}. Does nothing if
     * {@code newSize} is smaller then current table size.
     *
     * @param newSize an <code>int</code> value
     */
    protected void resize (int newSize)
    {
      if (newSize > levelTable.length) 
	{
	  Map<MDDNode,MDDNode>[] newTable =  new Map[newSize];
	  System.arraycopy (levelTable, 0, newTable, 0, levelTable.length);
	  levelTable = newTable;
	}
    }

    private Map<MDDNode,MDDNode> newMap ()
    {
      return (Map<MDDNode,MDDNode>) new SoftSoftHashMap ();
      //return new HashMap ();
    }
    
    
    
    
  }
  
  
}




