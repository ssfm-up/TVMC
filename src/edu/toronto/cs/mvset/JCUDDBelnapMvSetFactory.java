package edu.toronto.cs.mvset;


import edu.toronto.cs.algebra.*;
import edu.toronto.cs.jcudd.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;

import java.io.*;
import java.util.*;




/***
 *** Implementation of an MvSet based on the JCUDDBelnap library
 ***/
public class JCUDDBelnapMvSetFactory extends AbstractMvSetFactory
{
  // -- DdManager
  int dd;
  


  // -- top and bot nodes for quick reference
  int bddTop;
  int bddBot;
  int bddInfoTop;
  int bddInfoBot;
  
  int choiceVar;
  int notChoiceVar;
  

  JCUDDBelnapMvSet topMvSet;
  JCUDDBelnapMvSet botMvSet;
  JCUDDBelnapMvSet infoTopMvSet;
  JCUDDBelnapMvSet infoBotMvSet;

  AlgebraValue topV;
  AlgebraValue botV;
  AlgebraValue infoTopV;
  AlgebraValue infoBotV;

  BelnapAlgebra balgebra;


  // -- list of dead nodes
  LinkedList deadNodes;
  int killTries;
  static final int KILL_RATE = 4;
  
  static 
  {
    try 
      {
	System.loadLibrary ("Cudd");
      }
    catch (Exception ex)
      {
	System.err.println ("Error loadling Cudd: " + ex);
	System.exit (1);
      }
    
    
  }
  

    
  public JCUDDBelnapMvSetFactory (IAlgebra _algebra, int nvars)
  {  
    super (_algebra);
    deadNodes = new LinkedList ();

    balgebra = (BelnapAlgebra)_algebra;
    //cudd = new CuddAdd (nvars, algebra);
    IntAlgebraWrapper wrapper;
    int top;
    int bot;
    int infoTop;
    int infoBot;
    
    
    wrapper = new IntBelnapAlgebraWrapper (balgebra);
    top = balgebra.top ().getId ();
    bot = balgebra.bot ().getId ();
    infoTop = balgebra.infoTop ().getId ();
    infoBot = balgebra.infoBot ().getId ();
    dd = JMVLCudd.jmvlCudd_Init (nvars, wrapper, top, bot, infoTop, infoBot);

    choiceVar = JMVLCudd.Cudd_bddIthVar (dd, 0);
    //notChoiceVar = JMVLCudd.Cudd_Not (choiceVar);
    notChoiceVar = quickNot (choiceVar);
    JMVLCudd.Cudd_Ref (notChoiceVar);
    
    bddTop = JMVLCudd.Cudd_ReadOne (dd);
    bddBot = JMVLCudd.Cudd_ReadLogicZero (dd);

    //     JMVLCudd.Cudd_Ref (choiceVar);
    //     JMVLCudd.Cudd_Ref (notChoiceVar);

    bddInfoTop = choiceVar;
    bddInfoBot = notChoiceVar;
    
    
    topMvSet = internalCreateMvSet (bddTop);
    botMvSet = internalCreateMvSet (bddBot);
    infoTopMvSet = internalCreateMvSet (bddInfoTop);
    infoBotMvSet = internalCreateMvSet (bddInfoBot);

    topV = algebra.getValue (top);
    botV = algebra.getValue (bot);
    infoTopV = algebra.getValue (infoTop);
    infoBotV = algebra.getValue (infoBot);
    

    // -- dynamic variable reordering
    JMVLCudd.Cudd_AutodynEnable (dd, JMVLCuddConstants.CUDD_REORDER_SIFT);
  }  

  public static MvSetFactory newMvSetFactory (IAlgebra algebra, 
					      int nvars)
  {
    return new JCUDDBelnapMvSetFactory (algebra, nvars);
  }




  public void renew ()
  {
    //JMVLCudd.cuddGarbageCollect (dd, 0);
    killDeadNodes (true);
  }
  
  public void killDeadNodes (boolean force)
  {
    /**
     ** kill nodes once in a while 
     **/
    if (!force && killTries < KILL_RATE)
      {
	killTries++;
	return;
      }
    else
      killTries = 0;

    
    if (!deadNodes.isEmpty ())
      {
	synchronized (deadNodes)
	  {
	    while (!deadNodes.isEmpty ())
	      {
		int node = ((Integer)deadNodes.removeFirst ()).intValue ();
		JMVLCudd.Cudd_RecursiveDeref (dd, node);
	      }
	    
	  }
	
      }    
    //System.out.println ("FORCING GARBAGE COLLECTION");
    //JMVLCudd.cuddGarbageCollect (dd, 1);
  }
  
  

    
    
  /***
   *** Given a value in L creates a function 
   *** f(x_0, x_1, ...) = value
   ***/
  public MvSet createConstant (AlgebraValue v)
  {
    if (v == topV) return topMvSet;
    if (v == botV) return botMvSet;
    if (v == infoTopV) return infoTopMvSet;
    if (v == infoBotV) return infoBotMvSet;
    
    throw new RuntimeException ("Unsupported value: " + v);
  }    

  public MvSet buildCube (int[] varIndex)
  {
    int result = bddTop;
    
    JMVLCudd.Cudd_Ref (result);
    
    for (int i = varIndex.length - 1; i >= 0; i--)
      {
	int var = JMVLCudd.Cudd_bddIthVar (dd, varIndex [i]);
	int temp = JMVLCudd.Cudd_bddAnd (dd, result, var);
	JMVLCudd.Cudd_RecursiveDeref (dd, var);
	JMVLCudd.Cudd_RecursiveDeref (dd, result);
	result = temp;
      }
    
    //     assert JMVLCudd.isCube (dd, result) == 1 : 
    //       "result of build cube is not a cube";
    
    return createMvSet (result);
  }

  public MvSet top ()
  {
    return topMvSet;
  }
  public MvSet bot ()
  {
    return botMvSet;
  }
  public MvSet infoTop ()
  {
    return infoTopMvSet;
  }
  
  public MvSet infoBot ()
  {
    return infoBotMvSet;
  }
  
  
  
  /***
   *** Given an argument index (0 <= argIdx < n)
   *** constructs a projection function
   *** f(x_0, x_1, ...) = x_argIdx
   ***/
  public MvSet createProjection (int argIdx)
  {
    // -- we only support boolean variables so projection is easy to create
    return createMvSet (JMVLCudd.Cudd_bddIthVar (dd, argIdx));
  }
    

    
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  public MvSet createPoint (AlgebraValue[] args, AlgebraValue value)
  {
    int result = ((JCUDDBelnapMvSet)createConstant (value)).getCPtr ();

    JMVLCudd.Cudd_Ref (result);
    
    for (int i = args.length; i >= 0; i--)
      {
	// -- skip don't cares
	if (args [i] == algebra.noValue ()) continue;
	
	int var = JMVLCudd.Cudd_bddIthVar (dd, i);
	if (args [i] == algebra.bot ())
	  {
	    // 	    int temp = JMVLCudd.Cudd_Not (var);
	    // 	    JMVLCudd.Cudd_RecursiveDeref (dd, var);
	    // 	    var = temp;
	    var = quickNot (var);
	  }
	int temp2 = JMVLCudd.Cudd_bddAnd (dd, result, var);
	JMVLCudd.Cudd_RecursiveDeref (dd, result);
	JMVLCudd.Cudd_RecursiveDeref (dd, var);
	result = temp2;
      }

    return createMvSet (result);
  }


  public MvSet var (int argIdx, AlgebraValue argVal, AlgebraValue value)
  {
    // -- this builds a function
    // -- argIdx == argVal : value
    // -- else             : bot

    assert argVal == algebra.top () || argVal == algebra.bot ();

    int variable = JMVLCudd.Cudd_bddIthVar (dd, argIdx);
    int constant = ((JCUDDBelnapMvSet)createConstant (value)).getCPtr ();

    try 
      {
	if (argVal == algebra.top ())
	  return createMvSet (JMVLCudd.Cudd_bddIte (dd, variable, constant, 
						    bddBot));
	else
	  return createMvSet (JMVLCudd.Cudd_bddIte (dd, variable, bddBot, 
						    constant));
	
      }
    finally 
      {
	JMVLCudd.Cudd_RecursiveDeref (dd, variable);
      }
    
			      
			      
  }


  int var (int argIdx, int branch)
  {
    assert branch == 0 || branch == 1;
    
    int result = JMVLCudd.Cudd_bddIthVar (dd, argIdx);
    if (branch == 1)
      {
	// 	int temp = JMVLCudd.Cudd_Not (result);
	// 	JMVLCudd.Cudd_RecursiveDeref (dd, result);
	// 	result = temp;
	result = quickNot (result);
      }
    
    return result;
  }
  

  // -- this is just bddIte
  public MvSet createCase (int argIdx, MvSet[] children)
  {
    assert children.length == 2;

    return createIte (argIdx, children [0], children [1]);
  }

  public MvSet createIte (int argIdx, MvSet child0, MvSet child1)
  {
    int var = JMVLCudd.Cudd_bddIthVar (dd, argIdx);
    try 
      {      
	return 
	  createMvSet 
	  (JMVLCudd.Cudd_bddIte (dd, var, 
				 ((JCUDDBelnapMvSet)child0).getCPtr (), 
				 ((JCUDDBelnapMvSet)child1).getCPtr ()));
      }
    finally 
      {
	JMVLCudd.Cudd_RecursiveDeref (dd, var);
      }
  }
  
  
    

  public MvSet createMvSet (int cPtr)
  {
    if (cPtr == bddTop) return topMvSet;
    if (cPtr == bddBot) return botMvSet;
    if (cPtr == bddInfoTop) return infoTopMvSet;
    if (cPtr == bddInfoBot) return infoBotMvSet;

    // -- do garbage collection
    killDeadNodes (false);
    
    // -- eventually we will use an object pool for this
    return new JCUDDBelnapMvSet (cPtr);
  }

  private JCUDDBelnapMvSet internalCreateMvSet (int cPtr)
  {
    return new JCUDDBelnapMvSet (cPtr);
  }
  
  public int getDd ()
  {
    return dd;
  }    

  protected void finalize () 
  {
    killDeadNodes (true);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, bddTop);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, bddBot);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, choiceVar);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, notChoiceVar);
    
    JMVLCudd.jmvlCudd_Quit (dd);
  }
  

  private static final int quickNot (int cPtr)
  {
    return (int) (((long)cPtr) ^ 01);
  }

  
  public class JCUDDBelnapMvSet extends AbstractMvSet 
  {
    
  
    // -- the BDD representing this mv-set
    int cPtr;

    public JCUDDBelnapMvSet (int _cPtr)
    {
      cPtr = _cPtr;
    }


    protected void finalize () 
    {
      //JMVLCudd.Cudd_RecursiveDeref (dd, cPtr);
      synchronized (deadNodes)
	{
	  deadNodes.addLast (new Integer (cPtr));
	}
      
    }

    
    public int getCPtr ()
    {
      return cPtr;
    }
    
    
    public boolean isConstant()
    {
      
      return isConstantQuick ();
    }

    private boolean isConstantQuick ()
    {
      return 
	cPtr == bddTop ||
	cPtr == bddBot ||
	cPtr == bddInfoTop ||
	cPtr == bddInfoBot;
    }
    

    public AlgebraValue getValue()
    {
      if (cPtr == bddTop) return topV;
      if (cPtr == bddBot) return botV;
      if (cPtr == bddInfoTop) return infoTopV;
      if (cPtr == bddInfoBot) return infoBotV;
      throw new RuntimeException ("Unknown value: " + this);
    }
    
    public String toString ()
    {
      // XXX for debuging
      //JMVLCudd.Cudd_PrintMinterm (dd, cPtr);
      return isConstant () ? getValue ().toString () : String.valueOf (cPtr);
    }
    
    /*** 
     *** Creates a pointwise composition
     *** op is an operator: L x L -> L
     *** f is the current mv-set
     *** result h (x) = f (x) op g (x)
     ***/
    public MvSet ptwiseCompose (int op, MvSet g)
    {
      int result = 0;
      int gNode = ((JCUDDBelnapMvSet)g).getCPtr ();
      switch (op)
	{
	case MEET:
	  if (cPtr == gNode) return this;
	  if (cPtr == bddBot) return this;
	  if (gNode == bddBot) return g;
	  if (cPtr == bddTop) return g;
	  if (gNode == bddTop) return this;
	  
	  result = JMVLCudd.Cudd_bddAnd (dd, cPtr, gNode);
	  break;
	case JOIN:
	  if (cPtr == gNode) return this;
	  if (cPtr == bddBot) return g;
	  if (gNode == bddBot) return this;
	  if (cPtr == bddTop) return this;
	  if (gNode == bddTop) return g;

	  result = JMVLCudd.Cudd_bddOr (dd, cPtr, gNode);
	  break;
	case GEQ:
	  if (g.isConstant ())
	    result = doGeqConstant (gNode);
	  if (result == 0)
	    return g.leq (this);
	  break;
	case LEQ:
	  result = doImpl (gNode);
	  break;
	case EQ:
	  result = doEquals (gNode);
	  break;
	case INFO_AND:
	  result = doInfoMeet (gNode);
	  break;
	case INFO_OR:
	  result = doInfoJoin (gNode);
	  break;
	default:
	  assert false : "How did we get here?";
	  result = 0;
	}

      if (result == cPtr)  return this;
      if (result == gNode) return g;
      return createMvSet (result);
    }

    private int doEquals (int gNode)
    {
      // -- (a == b) equivalent to '!(a xor b)'
      int xor = JMVLCudd.Cudd_bddXor (dd, cPtr, gNode);
      //int eq = JMVLCudd.Cudd_Not (xor);
      int eq = quickNot (xor);
      //JMVLCudd.Cudd_RecursiveDeref (dd, xor);
      int res = JMVLCudd.Cudd_bddUnivAbstract (dd, eq, choiceVar);
      JMVLCudd.Cudd_RecursiveDeref (dd, eq);
      return res;
    }

    private int doGeqConstant (int gNode)
    {
      if (gNode == bddInfoTop || gNode == bddInfoBot)
	return JMVLCudd.Cudd_Cofactor (dd, cPtr, gNode);
      return 0;
    }

    private int doImpl (int gNode)
    {
      // -- (a -> b) == (!a || b)
      //int notA = JMVLCudd.Cudd_Not (cPtr);
      int notA = quickNot (cPtr);
      int impl = JMVLCudd.Cudd_bddOr (dd, notA, gNode);
      //JMVLCudd.Cudd_RecursiveDeref (dd, notA);
      int res = JMVLCudd.Cudd_bddUnivAbstract (dd, impl, choiceVar);
      JMVLCudd.Cudd_RecursiveDeref (dd, impl);
      return res;
    }

    private int doInfoJoin (int gNode)
    {
      
      int f_m = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoBot);
      int g_m = JMVLCudd.Cudd_Cofactor (dd, gNode, bddInfoBot);
      
      int h_m = JMVLCudd.Cudd_bddAnd (dd, f_m, g_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, f_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, g_m);

      int f_d = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoTop);
      int g_d = JMVLCudd.Cudd_Cofactor (dd, gNode, bddInfoTop);
      
      int h_d = JMVLCudd.Cudd_bddOr (dd, f_d, g_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, f_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, g_d);

      
      int res = JMVLCudd.Cudd_bddIte (dd, choiceVar, h_d, h_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, h_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, h_d);
      return res;
    }

    private int doInfoMeet (int gNode)
    {
      int f_m = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoBot);
      int g_m = JMVLCudd.Cudd_Cofactor (dd, gNode, bddInfoBot);
      
      int h_m = JMVLCudd.Cudd_bddOr (dd, f_m, g_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, f_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, g_m);

      int f_d = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoTop);
      int g_d = JMVLCudd.Cudd_Cofactor (dd, gNode, bddInfoTop);
      
      int h_d = JMVLCudd.Cudd_bddAnd (dd, f_d, g_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, f_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, g_d);

      
      int res = JMVLCudd.Cudd_bddIte (dd, choiceVar, h_d, h_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, h_m);
      JMVLCudd.Cudd_RecursiveDeref (dd, h_d);
      return res;
    }
    
    

    public MvSet ptwiseCompare (int op, MvSet g)
    {
      return ptwiseCompose (op, g);
    }


    /***
     *** Creates a ptwise negation
     *** h (x) = \neg f (x)
     ***/
    public MvSet ptwiseNeg ()
    {
      if (cPtr == bddTop) return botMvSet;
      if (cPtr == bddBot) return topMvSet;
      if (cPtr == bddInfoTop) return infoTopMvSet;
      if (cPtr == bddInfoBot) return infoBotMvSet;
      
	  
	  
      int f_d = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoTop);
      int f_m = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoBot);

      //       int not_f_d = JMVLCudd.Cudd_Not (f_d);
      //       int not_f_m = JMVLCudd.Cudd_Not (f_m);
      int not_f_d = quickNot (f_d);
      int not_f_m = quickNot (f_m);

	  
      //       JMVLCudd.Cudd_RecursiveDeref (dd, f_d);
      //       JMVLCudd.Cudd_RecursiveDeref (dd, f_m);
      MvSet res = 
	createMvSet 
	(JMVLCudd.Cudd_bddIte (dd, choiceVar, not_f_m, not_f_d));

      JMVLCudd.Cudd_RecursiveDeref (dd, not_f_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, not_f_m);
      return res;
    }
  
    public MvSet infoNot ()
    {
      if (cPtr == bddTop) return topMvSet;
      if (cPtr == bddBot) return botMvSet;
      if (cPtr == bddInfoTop) return infoBotMvSet;
      if (cPtr == bddInfoBot) return infoTopMvSet;

      int f_d = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoTop);
      int f_m = JMVLCudd.Cudd_Cofactor (dd, cPtr, bddInfoBot);

      // -- rotate f_d and f_m
      // -- f == [f_d, f_m]
      // -- infoNot (f) == [f_m, f_d]
      MvSet res = 
	createMvSet 
	(JMVLCudd.Cudd_bddIte (dd, choiceVar, f_m, f_d));
      
      JMVLCudd.Cudd_RecursiveDeref (dd, f_d);
      JMVLCudd.Cudd_RecursiveDeref (dd, f_m);
      return res;
    }
    
    public int size ()
    {
      return JMVLCudd.Cudd_DagSize (cPtr);
    }


    /***
     *** Restricts an argument
     *** h (x_0, x_1, x_2, ...) = f (x_0, ..., value, ...)
     *** where value is substituted at argIdx
     ***/
    public MvSet cofactor (int argIdx, AlgebraValue value)
    {
      throw new UnsupportedOperationException ("Cofactor is not supported");
    }
  
    public MvSet cofactor (AlgebraValue[] r)
    {
      throw new UnsupportedOperationException ("Cofactor is not supported");
    }

    public MvSet cofactor (MvSet cube)
    {
      //       System.out.println ("Doing cofactor with: " + 
      // 			  ((JCUDDBelnapMvSet)cube).getCPtr ());
      //       assert JMVLCudd.isCube (dd, ((JCUDDBelnapMvSet)cube).getCPtr ()) == 1 
      // 	: "Not a cube!";
      
      int res = 
	JMVLCudd.Cudd_Cofactor (dd, 
				cPtr, ((JCUDDBelnapMvSet)cube).getCPtr ());
      return createMvSet (res);
      
    }
    
    
    

    public MvSet existAbstract (MvSet _cube)
    {
      JCUDDBelnapMvSet mvCube = (JCUDDBelnapMvSet)_cube;
      
      if (isConstantQuick () || mvCube.isConstantQuick ())
	return this;
      
      int cube = mvCube.getCPtr ();
      return createMvSet (JMVLCudd.Cudd_bddExistAbstract (dd, cPtr, cube));
    }

    public MvSet forallAbstract (MvSet _cube)
    {
      int cube = ((JCUDDBelnapMvSet)_cube).getCPtr ();
      return createMvSet (JMVLCudd.Cudd_bddUnivAbstract (dd, cPtr, cube));
    }
  
  

    /***
     *** Renames the arguments. newArgs is a map from old args to new so that
     *** h (x) = f (newArgs [0], newArgs [1], ...)
     ***/
    public MvSet renameArgs (int[] newArgs)
    {
      if (newArgs.length == 0 || isConstantQuick ()) return this;
      
      /* copy java array into C */
      CIntArray cArray = new CIntArray (newArgs.length);
      for (int i = 0; i < newArgs.length; i++)
	cArray.setitem (i, newArgs [i]);
      
      return 
	createMvSet (JMVLCudd.Cudd_bddPermute (dd, cPtr, cArray.cast ()));
    }
  
  
    /***
     *** Evaluates this function on an input
     *** result = f (values [0], values [1], ...)
     ***/
    public AlgebraValue evaluate (AlgebraValue[] values)
    {
      throw new UnsupportedOperationException ();
    }  

    /*** get the lattice for the MvSet */
    public IAlgebra getAlgebra ()
    {
      return getFactory ().getAlgebra ();
    }

    

    public MvSetFactory getFactory ()
    {
      return JCUDDBelnapMvSetFactory.this;
    }

  

    public boolean equals (Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass () !=  JCUDDBelnapMvSet.class) return false;
      return cPtr == ((JCUDDBelnapMvSet)o).getCPtr ();
    }
  
    public int hashCode ()
    {
      // XXX Potential problem here!
      // XXX note to self: next time explain the comment better!
      long hash =  ((long) cPtr) * Primes.primes [0];
      return (int) (hash >>> 32);
    }

    public MvRelation toMvRelation (MvSet invariant, 
				    MvSet preVariablesCube,
				    MvSet postVariablesCube,
				    int[] preToPostMap,
				    int[] postToPreMap)
    {
      return new JCUDDBelnapMvRelation (this, 
					(JCUDDBelnapMvSet) invariant,
					(JCUDDBelnapMvSet) preVariablesCube,
					(JCUDDBelnapMvSet) postVariablesCube,
					preToPostMap,
					postToPreMap);
    }
    

    public Iterator cubeIterator ()
    {
      class CubeIterator implements Iterator
      {
	  
	int node;
	int dd;
	int gen;
	CuddCube cube = null;
	IAlgebra algebra;
	  
	CubeIterator (IAlgebra _algebra, int _dd, int _node)
	{
	  algebra = _algebra;
	  dd = _dd;
	  node = _node;
	  JMVLCudd.Cudd_Ref (node);
	  cube = new CuddCube ();
	  gen = JMVLCudd.Cudd_FirstCube (dd, node, cube);
	}
	  

	public void remove ()
	{
	  throw new UnsupportedOperationException ("remove not supported");
	}
	  
	public boolean hasNext ()
	{
	  return cube != null;
	}
	  
	public Object next ()
	{
	  AlgebraValue[] valCube = 
	    new AlgebraValue [JMVLCudd.ddSize (dd) + 1];


	  // -- set the rest elements
	  CIntArray cubeArray = CIntArray.frompointer (cube.getCube ());

	    
	  // -- set cube value
	  int cubeValue = cubeArray.getitem (0);
	  if (cubeValue == 2)
	    valCube [valCube.length - 1] = topV;
	  else if (cubeValue == 1)
	    valCube [valCube.length - 1] = infoTopV;
	  else if (cubeValue == 0)
	    valCube [valCube.length - 1] = infoBotV;
	      

	  for (int i = 0; i < valCube.length - 1; i++)
	    {
	      if (i == 0)
		{
		  valCube [i] = algebra.noValue ();
		  continue;
		}
		      
	      int ival = cubeArray.getitem (i);
		
	      valCube [i] = 
		(ival == 2) ? algebra.noValue () : algebra.getValue (ival);
	    }

	  // -- ready for a next one
	  if (JMVLCudd.Cudd_NextCube (gen, cube) == 0)
	    // -- 0 status means that there is nothing else left
	    cube = null;


	  return valCube;
	}

	protected void finalize () 
	{
	  JMVLCudd.Cudd_GenFree (gen);
	  synchronized (deadNodes)
	    {
	      deadNodes.addLast (new Integer (node));
	    }
	  //JMVLCudd.Cudd_RecursiveDeref (dd, node);
	}
	  
	  
      };

      //JMVLCudd.Cudd_PrintMinterm (dd, cPtr);
      return new CubeIterator (getAlgebra (), dd, cPtr);
    }

    public Iterator mintermIterator (MvSet vars, AlgebraValue val)
    {
      
      class MintermIterator implements Iterator 
      {
	int dd;
	int mintermVars;
	int topDd;
	int botDd;
	int gen;
	int selfDd;
	AlgebraValue termValue;
	
	MintermIterator (int _dd, 
			 int _selfDd,
			 int _mintermVars, 
			 AlgebraValue _termValue,
			 int _topDd,
			 int _botDd)
	{
	  dd = _dd;
	  if (_termValue != infoBotV)
	    throw new 
	      RuntimeException 
	      ("Minterms for non-maybe values are not supported");
	  selfDd = JMVLCudd.Cudd_Cofactor (dd, _selfDd, bddInfoBot);	  
	  mintermVars = _mintermVars;
	  termValue = _termValue;
	  topDd = _topDd;
	  botDd = _botDd;
	  JMVLCudd.Cudd_Ref (mintermVars);
	  JMVLCudd.Cudd_Ref (topDd);
	  JMVLCudd.Cudd_Ref (botDd);

	  gen = JMVLCudd.mvlCudd_MintermIterator (dd, selfDd);
	}
	
	public boolean hasNext ()
	{
	  return gen != 0 &&
	    JMVLCudd.mvlCudd_HasNextMinterm (gen, topV.getId ()) != 0;
	}

	public Object next ()
	{
	  int res 
	    = JMVLCudd.mvlCudd_bddNextMinterm (gen, 
					       topV.getId (),
					       mintermVars,
					       topDd,
					       botDd);
	  // 	  assert JMVLCudd.isCube (dd, res) == 1 : "minterm not a cube: " + res;
	  
	  return createMvSet (res);

	  // 	  return 
	  // 	    createMvSet 
	  // 	    (JMVLCudd.mvlCudd_bddNextMinterm (gen, 
	  // 					      topV.getId (),
	  // 					      mintermVars,
	  // 					      topDd,
	  // 					      botDd));
	}

	public void remove ()
	{
	  throw new UnsupportedOperationException ("remove not supported");
	}
	
	protected void finalize () 
	{
	  JMVLCudd.Cudd_GenFree (gen);
	  synchronized (deadNodes)
	    {
	      deadNodes.addLast (new Integer (mintermVars));
	      deadNodes.addLast (new Integer (topDd));
	      deadNodes.addLast (new Integer (botDd));
	      deadNodes.addLast (new Integer (selfDd));	      
	    }

	  // 	  JMVLCudd.Cudd_RecursiveDeref (dd, mintermVars);
	  // 	  JMVLCudd.Cudd_RecursiveDeref (dd, topDd);
	  // 	  JMVLCudd.Cudd_RecursiveDeref (dd, botDd);
	  // 	  JMVLCudd.Cudd_RecursiveDeref (dd, selfDd);
	}	
			 
      };

      return new MintermIterator (dd, cPtr, ((JCUDDBelnapMvSet)vars).getCPtr (),
				  val, bddTop, bddBot);
      
    }
    
  }

  class JCUDDBelnapMvRelation implements MvRelation
  {
    JCUDDBelnapMvSet reln;
    JCUDDBelnapMvSet preVariablesCube;
    JCUDDBelnapMvSet postVariablesCube;

    JCUDDBelnapMvSet infoNotReln;
    JCUDDBelnapMvSet infoNotInvar;
    
    int[] preToPostMap;
    int[] postToPreMap;
    
    CIntArray preToPostArray;
    CIntArray postToPreArray;
    
    JCUDDBelnapMvSet invariant;

    public JCUDDBelnapMvRelation (JCUDDBelnapMvSet _reln,
				  JCUDDBelnapMvSet _invariant,
				  JCUDDBelnapMvSet _preVariablesCube,
				  JCUDDBelnapMvSet _postVariablesCube,
				  int[] _preToPostMap,
				  int[] _postToPreMap)
    {
      setTrans (_reln);
      invariant = _invariant;
      preVariablesCube = _preVariablesCube;
      postVariablesCube = _postVariablesCube;
      preToPostMap = _preToPostMap;
      postToPreMap = _postToPreMap;
      
      preToPostArray = toCIntArray (preToPostMap);
      postToPreArray = toCIntArray (postToPreMap);

      if (invariant != null)
	infoNotInvar = (JCUDDBelnapMvSet)invariant.infoNot ();
      else
	infoNotInvar = null;

      if (invariant != null && invariant.getCPtr () == bddTop)
	{
	  invariant = null;
	  infoNotInvar = null;
	}
      
      
    }

    public void setTrans (MvSet v)
    {
      setTrans ((JCUDDBelnapMvSet) v);
    }
    
    protected void setTrans (JCUDDBelnapMvSet v)
    {
      reln = v;
      infoNotReln = (JCUDDBelnapMvSet) reln.infoNot ();
    }
    
    
    private CIntArray toCIntArray (int[] array)
    {
      CIntArray cArray = new CIntArray (array.length);
      for (int i = 0; i < array.length; i++)
	cArray.setitem (i, array [i]);
      return cArray;
    }
    

    public MvSet fwdImage (MvSet v)
    {
      //       synchronized (JMVLCudd.class)
      // 	{
      int vPtr = ((JCUDDBelnapMvSet)v).getCPtr ();
	  
	  
      if (invariant != null)
	{
	  int temp = 
	    JMVLCudd.Cudd_bddAnd (dd, vPtr, invariant.getCPtr ());
	  vPtr = temp;
	}
      else
	JMVLCudd.Cudd_Ref (vPtr);
	  

	  
      int vPrime = 
	JMVLCudd.Cudd_bddAndAbstract (dd, reln.getCPtr (), vPtr, 
				      preVariablesCube.getCPtr ());

      JMVLCudd.Cudd_RecursiveDeref (dd, vPtr);
	  
      int res = 
	JMVLCudd.Cudd_bddPermute (dd, vPrime, postToPreArray.cast ());
	  
      JMVLCudd.Cudd_RecursiveDeref (dd, vPrime);

      // -- do a second invariant intersection since 
      // -- certain thigns may depend on it
      if (invariant != null)
	{
	  int temp = 
	    JMVLCudd.Cudd_bddAnd (dd, res, invariant.getCPtr ());
	  JMVLCudd.Cudd_RecursiveDeref (dd, res);
	  res = temp;
	}

      
      return createMvSet (res);
      // 	}
    }

    public MvSet dualBwdImage (MvSet v)
    {
      int vPtr = quickNot (((JCUDDBelnapMvSet)v).getCPtr ());
      
      if (infoNotInvar != null)
	{
	  int temp = 
	    JMVLCudd.Cudd_bddAnd (dd, vPtr, infoNotInvar.getCPtr ());
	  vPtr = temp;
	}
      else
	JMVLCudd.Cudd_Ref (vPtr);

	  
      int vPrime =   
	JMVLCudd.Cudd_bddPermute (dd, vPtr, preToPostArray.cast ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPtr);
	  
      int res = 
	JMVLCudd.Cudd_bddAndAbstract (dd, infoNotReln.getCPtr (), vPrime, 
				      postVariablesCube.getCPtr ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPrime);

      res = quickNot (res);
      
      // -- uncomment to test with old and trusted implementation
      //       JCUDDBelnapMvSet old = (JCUDDBelnapMvSet)bwdImage (v.not ()).not ();
      //       System.out.println ("in " + JCUDDBelnapMvSet.class + " dualBwd");
      //       assert res == old.getCPtr () : 
      // 	"new result is " + res + " old result is " + old.getCPtr ();
	  
      return createMvSet (res);
      
    }

    
    public MvSet bwdImage (MvSet v)
    {
      //       synchronized (JMVLCudd.class)
      // 	{
      int vPtr = ((JCUDDBelnapMvSet)v).getCPtr ();
      if (vPtr == bddBot) return botMvSet;

	  
      if (invariant != null)
	{
	  int temp = 
	    JMVLCudd.Cudd_bddAnd (dd, vPtr, invariant.getCPtr ());
	  vPtr = temp;
	}
      else
	JMVLCudd.Cudd_Ref (vPtr);

	  
      int vPrime =   
	JMVLCudd.Cudd_bddPermute (dd, vPtr, preToPostArray.cast ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPtr);
	  
      int res = 
	JMVLCudd.Cudd_bddAndAbstract (dd, reln.getCPtr (), vPrime, 
				      postVariablesCube.getCPtr ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPrime);

      // -- do a second invariant intersection since 
      // -- certain thigns may depend on it
      // 	  if (invariant != null && invariant.getCPtr () != bddTop)
      // 	    {
      // 	      int temp = 
      // 		JMVLCudd.Cudd_bddAnd (dd, res, invariant.getCPtr ());
      // 	      JMVLCudd.Cudd_RecursiveDeref (dd, res);
      // 	      res = temp;
      // 	    }

      
      return createMvSet (res);
      // 	}      

    }


    
    public MvSet toMvSet ()
    {
      return reln;
    }
    



    /**
     * return cube of pre-state variables
     *
     */
    public MvSet getPreVariablesCube ()
    {
      throw new UnsupportedOperationException 
	(this.getClass () + 
	 " does not support this method");
	
    }
    
    
    /**
     * get cube of post-state variables
     *
     */
    public MvSet getPostVariablesCube ()
    {
      throw new UnsupportedOperationException 
	(this.getClass () + 
	 " does not support this method");
	
    }
    
    
    
    /**
     * get map from pre- to post-state variables
     *
     */
    public int[] getPreToPostMap ()
    {
      throw new UnsupportedOperationException 
	(this.getClass () + 
	 " does not support this method");
	
    }

    
    
    /**
     * get map from post- to pre-state variables
     *
     */
    public int[] getPostToPreMap ()
    {
      throw new UnsupportedOperationException 
	(this.getClass () + 
	 " does not support this method");
	
    }



  }


  /* tester method */
  public static void main (String[] args) 
  {

    BelnapAlgebra alg = new BelnapAlgebra ();
    JCUDDBelnapMvSetFactory fac = new JCUDDBelnapMvSetFactory (alg, 10);
    
    int[] varIndex = new int [] {1, 2, 3, 4};
    MvSet varCube = fac.buildCube (varIndex);
    
    System.out.println ("Variable cube");
    System.out.println (varCube);
    
    MvSet var1 = fac.createProjection (1);
    System.out.println ("var1");
    System.out.println (var1);

    MvSet var2 = fac.createProjection (2);
    
    MvSet and = var1.or (var2);
    System.out.println ("Disjunction");
    System.out.println (and);

    and = and.and (fac.infoBot ());
    System.out.println ("and with maybe");
    System.out.println (and);

    System.out.println ("Cubes");
    int count = 0;
    for (Iterator it = and.cubeIterator (); it.hasNext (); count++)
      {
	System.out.println ("Cube " + count + ": " + Arrays.asList ((Object[])it.next ()));
      }

    System.out.println ("Minterms");
    count = 0;
    for (Iterator it = and.mintermIterator (varCube, alg.infoBot ()); 
	 it.hasNext (); count++)
      {
	System.out.println ("minterm " + count);
	System.out.print (it.next ());
      }

    System.out.println ("exist abstract");
    System.out.println (and.existAbstract (var2));
    System.out.println ("negation");
    System.out.println (and.not ());
    
    System.out.println ("info or");
    System.out.println ((var1.and (var2)).infoOr (fac.infoTop ()));
    
      
    

    MvSet infoBot = fac.infoBot ();
    //     while (true)
    //       {
    // 	StopWatch sw = new StopWatch ();

    // 	infoBot.existAbstract (varCube);
	
    // 	System.out.println ("Exist abstract in: " + sw);
    //       }
    
    
    
	
    
  }
  
  
}

  




