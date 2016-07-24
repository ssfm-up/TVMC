package edu.toronto.cs.mvset;

import edu.toronto.cs.algebra.*;

import edu.toronto.cs.jcudd.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;

import java.io.*;
import java.util.*;




/***
 *** Implementation of an MvSet based on the JCUDD library
 ***/
public class JCUDDMvSetFactory extends AbstractMvSetFactory
{
  // -- DdManager
  int dd;
  


  // -- top and bot nodes for quick reference
  int addTop;
  int addBot;
  int addInfoTop;
  int addInfoBot;
  

  JCUDDMvSet topMvSet;
  JCUDDMvSet botMvSet;
  JCUDDMvSet infoTopMvSet;
  JCUDDMvSet infoBotMvSet;

  AlgebraValue topV;
  AlgebraValue botV;
  AlgebraValue infoTopV;
  AlgebraValue infoBotV;

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
  

    
  public JCUDDMvSetFactory (IAlgebra _algebra, int nvars)
  {  
    super (_algebra);
    //cudd = new CuddAdd (nvars, algebra);
    IntAlgebraWrapper wrapper;
    int top;
    int bot;
    int infoTop;
    int infoBot;

    deadNodes = new LinkedList ();
    
    if (_algebra instanceof BelnapAlgebra)
      {
	BelnapAlgebra balgebra = (BelnapAlgebra)_algebra;
	
	wrapper = new IntBelnapAlgebraWrapper (balgebra);
	top = balgebra.top ().getId ();
	bot = balgebra.bot ().getId ();
	infoTop = balgebra.infoTop ().getId ();
	infoBot = balgebra.infoBot ().getId ();
      }
    else
      {
	wrapper = new IntAlgebraWrapper (_algebra);
	top = _algebra.top ().getId ();
	infoBot = top;
	bot = _algebra.bot ().getId ();
	infoTop = bot;
      }
    dd = JMVLCudd.jmvlCudd_Init (nvars, wrapper, top, bot, infoTop, infoBot);

    addTop = JMVLCudd.Cudd_addConst (dd, top);
    addBot = JMVLCudd.Cudd_addConst (dd, bot);

    addInfoTop = JMVLCudd.Cudd_addConst (dd, infoTop);
    addInfoBot = JMVLCudd.Cudd_addConst (dd, infoBot);

    topMvSet = internalCreateMvSet (addTop);
    botMvSet = internalCreateMvSet (addBot);
    infoTopMvSet = internalCreateMvSet (addInfoTop);
    infoBotMvSet = internalCreateMvSet (addInfoBot);

    topV = algebra.getValue (top);
    botV = algebra.getValue (bot);
    infoTopV = algebra.getValue (infoTop);
    infoBotV = algebra.getValue (infoBot);
    
  }  

  public static MvSetFactory newMvSetFactory (IAlgebra algebra, 
					      int nvars)
  {
    return new JCUDDMvSetFactory (algebra, nvars);
  }




  public void renew ()
  {
    //JMVLCudd.cuddGarbageCollect (dd, 1);
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
    
    killDeadNodes (false);
    return createMvSet (JMVLCudd.Cudd_addConst (dd, v.getId ()));
  }    

  public MvSet buildCube (int[] varIndex)
  {
    int result = addTop;
    
    JMVLCudd.Cudd_Ref (result);
    
    for (int i = varIndex.length - 1; i >= 0; i--)
      {
	int var = JMVLCudd.Cudd_addIthVar (dd, varIndex [i]);
	int temp = JMVLCudd.jmvlCudd_And (dd, result, var);
	JMVLCudd.Cudd_RecursiveDeref (dd, var);
	JMVLCudd.Cudd_RecursiveDeref (dd, result);
	result = temp;
      }
    
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
    return createMvSet (JMVLCudd.Cudd_addIthVar (dd, argIdx));
  }
    

    
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  public MvSet createPoint (AlgebraValue[] args, AlgebraValue value)
  {
    int result = JMVLCudd.Cudd_addConst (dd, value.getId ());
    
    for (int i = args.length; i >= 0; i--)
      {
	// -- skip don't cares
	if (args [i] == algebra.noValue ()) continue;
	
	int var = JMVLCudd.Cudd_addIthVar (dd, i);
	if (args [i] == algebra.bot ())
	  {
	    int temp = JMVLCudd.jmvlCudd_Not (dd, var);
	    JMVLCudd.Cudd_RecursiveDeref (dd, var);
	    var = temp;
	  }
	int temp2 = JMVLCudd.jmvlCudd_And (dd, result, var);
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

    int variable = JMVLCudd.Cudd_addIthVar (dd, argIdx);
    int constant = JMVLCudd.Cudd_addConst (dd, value.getId ());
    
    try 
      {
	if (argVal == algebra.top ())
	  return createMvSet (JMVLCudd.Cudd_addIte (dd, variable, constant, 
						    addBot));
	else
	  return createMvSet (JMVLCudd.Cudd_addIte (dd, variable, addBot, 
						    constant));
	
      }
    finally 
      {
	JMVLCudd.Cudd_RecursiveDeref (dd, variable);
	JMVLCudd.Cudd_RecursiveDeref (dd, constant);

      }
    
			      
			      
  }


  int var (int argIdx, int branch)
  {
    assert branch == 0 || branch == 1;
    
    int result = JMVLCudd.Cudd_addIthVar (dd, argIdx);
    if (branch == 1)
      {
	int temp = JMVLCudd.jmvlCudd_Not (dd, result);
	JMVLCudd.Cudd_RecursiveDeref (dd, result);
	result = temp;
      }
    
    return result;
  }
  

  // -- this is just addIte
  public MvSet createCase (int argIdx, MvSet[] children)
  {
    assert children.length == 2;

    return createIte (argIdx, children [0], children [1]);
  }

  public MvSet createIte (int argIdx, MvSet child0, MvSet child1)
  {
    int var = JMVLCudd.Cudd_addIthVar (dd, argIdx);
    try 
      {      
	return 
	  createMvSet 
	  (JMVLCudd.Cudd_addIte (dd, var, 
				 ((JCUDDMvSet)child0).getCPtr (), 
				 ((JCUDDMvSet)child1).getCPtr ()));
      }
    finally 
      {
	JMVLCudd.Cudd_RecursiveDeref (dd, var);
      }
  }
  
  
    

  public MvSet createMvSet (int cPtr)
  {
    if (cPtr == addTop) return topMvSet;
    if (cPtr == addBot) return botMvSet;
    if (cPtr == addInfoTop) return infoTopMvSet;
    if (cPtr == addInfoBot) return infoBotMvSet;
    // -- eventually we will use an object pool for this
    return new JCUDDMvSet (cPtr);
  }

  private JCUDDMvSet internalCreateMvSet (int cPtr)
  {
    return new JCUDDMvSet (cPtr);
  }
  
  public int getDd ()
  {
    return dd;
  }    

  protected void finalize () 
  {
    killDeadNodes (true);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, addTop);
    //     JMVLCudd.Cudd_RecursiveDeref (dd, addBot);
    JMVLCudd.jmvlCudd_Quit (dd);
  }
  

  
  
  public class JCUDDMvSet extends AbstractMvSet 
  {
    
  
    // -- the ADD representing this mv-set
    int cPtr;

    public JCUDDMvSet (int _cPtr)
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
      
      return 
	cPtr == addTop ||
	cPtr == addBot ||
	cPtr == addInfoTop ||
	cPtr == addInfoBot ||
	JMVLCudd.Cudd_IsConstant (cPtr) != 0;
    }

    private boolean isConstantQuick ()
    {
      return 
	cPtr == addTop ||
	cPtr == addBot ||
	cPtr == addInfoTop ||
	cPtr == addInfoBot;
    }
    

    public AlgebraValue getValue()
    {
      assert isConstant () : "Trying to get a value of a non-constant node";
	return getAlgebra ().getValue (JMVLCudd.cuddV (cPtr));
    }
    
    public String toString()
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
      int result;
      int gNode = ((JCUDDMvSet)g).getCPtr ();
      switch (op)
	{
	case MEET:
	  if (cPtr == gNode) return this;
	  if (cPtr == addBot) return this;
	  if (gNode == addBot) return g;
	  if (cPtr == addTop) return g;
	  if (gNode == addTop) return this;
	  
	  result = JMVLCudd.jmvlCudd_And (dd, cPtr, gNode);
	  break;
	case JOIN:
	  if (cPtr == gNode) return this;
	  if (cPtr == addBot) return g;
	  if (gNode == addBot) return this;
	  if (cPtr == addTop) return this;
	  if (gNode == addTop) return g;

	  result = JMVLCudd.jmvlCudd_Or (dd, cPtr, gNode);
	  break;
	case GEQ:
	  result = JMVLCudd.jmvlCudd_Above (dd, cPtr, gNode);
	  break;
	case LEQ:
	  result = JMVLCudd.jmvlCudd_Below (dd, cPtr, gNode);
	  break;
	case EQ:
	  result = JMVLCudd.jmvlCudd_Eq (dd, cPtr, gNode);
	  break;
	case INFO_AND:
	  result = JMVLCudd.jmvlCudd_infoMeet (dd, cPtr, gNode);
	  break;
	case INFO_OR:
	  result = JMVLCudd.jmvlCudd_infoJoin (dd, cPtr, gNode);
	  break;
	default:
	  assert false : "How did we get here?";
	  result = 0;
	}
      if (result == cPtr) return this;
      if (result == gNode) return g;
      return createMvSet (result);
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
      return createMvSet (JMVLCudd.jmvlCudd_Not (dd, cPtr));
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
      int res = 
	JMVLCudd.Cudd_Cofactor (dd, 
				cPtr, ((JCUDDMvSet)cube).getCPtr ());
      return createMvSet (res);
    }


  
    

    public MvSet existAbstract (MvSet _cube)
    {
      JCUDDMvSet mvCube = (JCUDDMvSet)_cube;
      
      if (isConstantQuick () || mvCube.isConstantQuick ())
	return this;
      
      int cube = mvCube.getCPtr ();
      return createMvSet (JMVLCudd.mvlCudd_ExistAbstract (dd, cPtr, cube));
    }

    public MvSet forallAbstract (MvSet _cube)
    {
      int cube = ((JCUDDMvSet)_cube).getCPtr ();
      return createMvSet (JMVLCudd.mvlCudd_UnivAbstract (dd, cPtr, cube));
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
	createMvSet (JMVLCudd.Cudd_addPermute (dd, cPtr, cArray.cast ()));
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
      return JCUDDMvSetFactory.this;
    }

  

    public boolean equals (Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass () !=  JCUDDMvSet.class) return false;
      return cPtr == ((JCUDDMvSet)o).getCPtr ();
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
      return new JCUDDMvRelation (this, 
				  (JCUDDMvSet) invariant,
				  (JCUDDMvSet) preVariablesCube,
				  (JCUDDMvSet) postVariablesCube,
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

	  // -- set cube value
	  valCube [valCube.length - 1] = 
	    algebra.getValue (cube.getValue ());

	  // -- set the rest elements
	  CIntArray cubeArray = CIntArray.frompointer (cube.getCube ());
	    
	  for (int i = 0; i < valCube.length - 1; i++)
	    {
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
	  JMVLCudd.Cudd_RecursiveDeref (dd, node);
	}
	  
	  
      };
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
	  selfDd = _selfDd;
	  mintermVars = _mintermVars;
	  termValue = _termValue;
	  botDd = _botDd;
	  topDd = _topDd;
	  JMVLCudd.Cudd_Ref (mintermVars);
	  JMVLCudd.Cudd_Ref (topDd);
	  JMVLCudd.Cudd_Ref (botDd);
	  JMVLCudd.Cudd_Ref (selfDd);

	  gen = JMVLCudd.mvlCudd_MintermIterator (dd, selfDd);
	}
	
	public boolean hasNext ()
	{
	  return gen != 0 &&
	    JMVLCudd.mvlCudd_HasNextMinterm (gen, termValue.getId ()) != 0;
	}

	public Object next ()
	{
	  return 
	    createMvSet 
	    (JMVLCudd.mvlCudd_NextMinterm (gen, 
					   termValue.getId (),
					   mintermVars,
					   topDd,
					   botDd));
	}

	public void remove ()
	{
	  throw new UnsupportedOperationException ("remove not supported");
	}
	
	protected void finalize () 
	{
	  JMVLCudd.Cudd_GenFree (gen);
	  JMVLCudd.Cudd_RecursiveDeref (dd, mintermVars);
	  JMVLCudd.Cudd_RecursiveDeref (dd, topDd);
	  JMVLCudd.Cudd_RecursiveDeref (dd, botDd);
	  JMVLCudd.Cudd_RecursiveDeref (dd, selfDd);
	}	
			 
      };

      return new MintermIterator (dd, cPtr, ((JCUDDMvSet)vars).getCPtr (),
				  val, addTop, addBot);
      
      
    }
    
  }

  /* tester method */
  public static void main (String[] args) 
  {

    BelnapAlgebra alg = new BelnapAlgebra ();
    JCUDDMvSetFactory fac = new JCUDDMvSetFactory (alg, 10);
    
    int[] varIndex = new int [] {0, 1, 2, 3};
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

    System.out.println ("Cubes");
    int count = 0;
    for (Iterator it = and.cubeIterator (); it.hasNext (); count++)
      {
	System.out.println ("Cube " + count + ": " + Arrays.asList ((Object[])it.next ()));
      }

    System.out.println ("Minterms");
    count = 0;
    for (Iterator it = and.mintermIterator (varCube, alg.top ()); 
	 it.hasNext (); count++)
      {
	System.out.println ("minterm " + count);
	System.out.print (it.next ());
      }

    MvSet infoBot = fac.infoBot ();
    while (true)
      {
	StopWatch sw = new StopWatch ();

	infoBot.existAbstract (varCube);
	
	System.out.println ("Exist abstract in: " + sw);
      }
    
    
    
	
    
  }

  class JCUDDMvRelation implements MvRelation
  {
    JCUDDMvSet reln;
    JCUDDMvSet preVariablesCube;
    JCUDDMvSet postVariablesCube;
    
    int[] preToPostMap;
    int[] postToPreMap;
    
    CIntArray preToPostArray;
    CIntArray postToPreArray;
    
    JCUDDMvSet invariant;

    public JCUDDMvRelation (JCUDDMvSet _reln,
			    JCUDDMvSet _invariant,
			    JCUDDMvSet _preVariablesCube,
			    JCUDDMvSet _postVariablesCube,
			    int[] _preToPostMap,
			    int[] _postToPreMap)
    {
      reln = _reln;
      invariant = _invariant;
      preVariablesCube = _preVariablesCube;
      postVariablesCube = _postVariablesCube;
      preToPostMap = _preToPostMap;
      postToPreMap = _postToPreMap;
      
      preToPostArray = toCIntArray (preToPostMap);
      postToPreArray = toCIntArray (postToPreMap);
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
      int vPtr = ((JCUDDMvSet)v).getCPtr ();
	  
	  
      if (invariant != null && invariant.getCPtr () != addTop)
	{
	  int temp = 
	    JMVLCudd.jmvlCudd_And (dd, vPtr, invariant.getCPtr ());
	  vPtr = temp;
	}
      else
	JMVLCudd.Cudd_Ref (vPtr);
	  

	  
      int vPrime = 
	JMVLCudd.mvlCudd_ExistMeetAbstract (dd, reln.getCPtr (), vPtr, 
					    preVariablesCube.getCPtr ());

      JMVLCudd.Cudd_RecursiveDeref (dd, vPtr);
	  
      int res = 
	JMVLCudd.Cudd_addPermute (dd, vPrime, postToPreArray.cast ());
	  
      JMVLCudd.Cudd_RecursiveDeref (dd, vPrime);

      // -- do a second invariant intersection since 
      // -- certain thigns may depend on it
      if (invariant != null && invariant.getCPtr () != addTop)
	{
	  int temp = 
	    JMVLCudd.jmvlCudd_And (dd, res, invariant.getCPtr ());
	  JMVLCudd.Cudd_RecursiveDeref (dd, res);
	  res = temp;
	}

      
      return createMvSet (res);
    }
    
    public MvSet dualBwdImage (MvSet v)
    {
      return bwdImage (v.not ()).not ();
    }
    
    public MvSet bwdImage (MvSet v)
    {
      int vPtr = ((JCUDDMvSet)v).getCPtr ();
	  

	  
      if (invariant != null && invariant.getCPtr () != addTop)
	{
	  int temp = 
	    JMVLCudd.jmvlCudd_And (dd, vPtr, invariant.getCPtr ());
	  vPtr = temp;
	}
      else
	JMVLCudd.Cudd_Ref (vPtr);

	  
      int vPrime =   
	JMVLCudd.Cudd_addPermute (dd, vPtr, preToPostArray.cast ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPtr);
	  
      int res = 
	JMVLCudd.mvlCudd_ExistMeetAbstract (dd, reln.getCPtr (), vPrime, 
					    postVariablesCube.getCPtr ());
      JMVLCudd.Cudd_RecursiveDeref (dd, vPrime);

      
      return createMvSet (res);
    }

    
    
    public MvSet toMvSet ()
    {
      return reln;
    }

    public void setTrans (MvSet v)
    {
      reln = (JCUDDMvSet) v;
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
  
  
}

  




