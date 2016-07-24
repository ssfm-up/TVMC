package edu.toronto.cs.mvset;


// XXX Should this extends MDDMvSetFactory ?!

import edu.toronto.cs.algebra.*;

import java.io.*;
import java.util.*;


import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;
import edu.toronto.cs.mdd.*;
import edu.toronto.cs.mdd.ApplyFunctions.*;



/***
 *** Implementation of an MvSet based on the MDD library
 ***/
public class JADDMvSetFactory extends AbstractMvSetFactory
{
  MDDManager mddManager;

  ApplyFunction[] applyFunctions;

  // -- top and bot nodes for quick reference
  MDDNode mddTop;
  MDDNode mddBot;

  // -- enum type to represent how we encode
  EnumType enumType;
  int enumWidth;
  

    
  public JADDMvSetFactory (IAlgebra _algebra, int nvars)
  {  
    super (_algebra);
    enumType = new EnumType (algebra.carrierSet ().toArray (new Object [algebra.size ()]));
    
    System.out.println ("Enum bit size is: " + enumType.bitSize ());
    
    mddManager = new MDDManager (enumType.bitSize () * nvars, 2);
    initialize ();

  }
  public JADDMvSetFactory (IAlgebra _algebra, MDDManager _mddManager)
  {
    super (_algebra);
    mddManager = _mddManager;
    initialize ();
    assert false : "Does not work";
    
  }
  
  

  public static MvSetFactory newMvSetFactory (IAlgebra algebra, 
					      int nvars)
  {
    return new JADDMvSetFactory (algebra, nvars);
  }


  // XXX move out
  private static int logCeil (int n)
  {
    // -- returns the closest power of 2 greater or equal to n
    int pow = 0;
    int count = 0;
    while (pow < n)
      pow = 1 << ++count;
    return count;
  }
  

  private void initialize ()
  {
    mddTop = mddManager.getLeafNode (top.getId ());
    mddBot = mddManager.getLeafNode (bot.getId ());

    enumWidth = enumType.bitSize ();
  
    setupApplyFunctions ();
  }
  
  private void setupApplyFunctions ()
  {
    applyFunctions = new ApplyFunction [9];
    
    applyFunctions [MvSet.MEET] = 
      new MeetFunction (mddManager, getAlgebra ());
    
    applyFunctions [MvSet.JOIN] = 
      new JoinFunction (mddManager, getAlgebra ());
      
    applyFunctions [MvSet.NEG] = 
      new NegFunction (mddManager, getAlgebra ());

    applyFunctions [MvSet.IMPL] = 
      new ImpliesFunction (mddManager, getAlgebra ());
      
    applyFunctions [MvSet.GEQ] = 
      new AboveFunction (mddManager, getAlgebra ());
    applyFunctions [MvSet.LEQ] = 
      new BelowFunction (mddManager, getAlgebra ());
    applyFunctions [MvSet.EQ] =
      new EqualsFunction (mddManager, getAlgebra ());

    applyFunctions [MvSet.EXISTS] = 
      new UniformQuantify (mddBot, 
			   (BinApplyFunction)applyFunctions [MvSet.JOIN], 
			   mddTop);
      
    applyFunctions [MvSet.FORALL] = 
      new UniformQuantify (mddTop, 
			   (BinApplyFunction)applyFunctions [MvSet.MEET], 
			   mddBot);
  }
  

  BinApplyFunction getBinOperator (int oper)
  {
    return (BinApplyFunction)applyFunctions [oper];
  }
  UnaryApplyFunction getUnaryOperator (int oper)
  {
    return (UnaryApplyFunction)applyFunctions [oper];
  }
    
  QuantifyFunction getExistQuantify ()
  {
    return (QuantifyFunction)applyFunctions [MvSet.EXISTS];
  }
  
  QuantifyFunction getForallQuantify ()
  {
    return (QuantifyFunction)applyFunctions [MvSet.FORALL];
  }
    
    

  public void setCaching (boolean v)
  {
    mddManager.setCaching (v);
  }
    
    
  /***
   *** Given a value in L creates a function 
   *** f(x_0, x_1, ...) = value
   ***/
  public MvSet createConstant (AlgebraValue v)
  {
    return createMvSet (mddManager.getLeafNode (v.getId ()));
  }    

  public MvSet buildCube (int[] varIndex)
  {
    // To build a cube we have to expend the array of varIndex into 
    // an array of ADD variables and then call buildCube
    int[] ddVarIndex = new int [varIndex.length * enumWidth];
    for (int i = 0; i < varIndex.length; i++)
      for (int j = 0; j < enumWidth; j++)
	ddVarIndex [i + j] = varIndex [i] + j;

    return 
      createMvSet (mddManager.buildCube (ddVarIndex, mddTop, mddBot));
  }  
  


  public MvSet top ()
  {
    return createMvSet (mddTop);
  }
  public MvSet bot ()
  {
    return createMvSet (mddBot);
  }
  
  
  /***
   *** Given an argument index (0 <= argIdx < n)
   *** constructs a projection function
   *** f(x_0, x_1, ...) = x_argIdx
   ***/
  public MvSet createProjection (int argIdx)
  {
    // XXX This is a very inefficient way to do this, but it 
    // XXX will do for the first pass
    MvSet[] kids = new MvSet [algebra.size ()];
    for (IntIterator it = algebra.carrierSetId (); it.hasNext ();)
      {
	int val = it.nextInt ();
	kids [val] = createMvSet (mddManager.getLeafNode (val));
      }
    return createCase (argIdx, kids);
  }
    

    
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  public MvSet createPoint (AlgebraValue[] args, AlgebraValue value)
  {

    // XXX expand args and call mddManager.buildPoint
    // XXX This should be somehow encapsulated

    int[] intArgs  = new int [args.length * enumWidth];
    for (int i = 0; i < args.length; i++)
      {
	if (args [i] == noValue)
	  for (int j = 0; j < enumWidth; j++)
	    intArgs [i + j] = MDDManager.NO_VALUE;
	else
	  {
	    int[] enm = enumType.bitValue (args [i]);
	    for (int j = 0; j < enm.length; j++)
	      {
		if (enm [j] == -1)
		  intArgs [i + j] = MDDManager.NO_VALUE;
		else
		  intArgs [i + j] = enm [j];
	      }
	    
	    
	  }
      }
    
    return createMvSet 
      (mddManager.buildPoint (intArgs, 
			      mddManager.getLeafNode (value.getId ()), 
			      mddBot));
  }


  public MvSet var (int argIdx, AlgebraValue argVal, AlgebraValue value)
  {

    // -- expand argIdx and call mddManager.buildPoint 
    int[] ddArgIdx = new int [enumWidth * mddManager.getNvars ()];
    Arrays.fill (ddArgIdx, MDDManager.NO_VALUE);
    
    int[] enm = enumType.bitValue (argVal);
    for (int i = 0; i < enm.length; i++)
      {
	if (enm [i] == -1)
	  ddArgIdx [argIdx + i] = MDDManager.NO_VALUE;
	else
	  ddArgIdx [argIdx + i] = enm [i];
      }
    


    return createMvSet 
      (mddManager.buildPoint (ddArgIdx, 
			      mddManager.getLeafNode (value.getId ()), 
			      mddBot));
    
  }


  MDDNode var (int argIdx, int branch)
  {
    int[] enm = enumType.bitValue (getAlgebra ().getValue (branch));

    int[] ddArgIdx = new int [enumWidth * mddManager.getNvars ()];
    Arrays.fill (ddArgIdx, MDDManager.NO_VALUE);
    for (int i = 0; i < enm.length; i++)
      {
	if (enm [i] == -1)
	  ddArgIdx [argIdx + i] = MDDManager.NO_VALUE;
	else
	  ddArgIdx [argIdx + i] = enm [i];
      }
    


    return mddManager.buildPoint (ddArgIdx, mddTop, mddBot);    
  }
  

  public MvSet createCase (int argIdx, MvSet[] children)
  {
    MDDNode[] mddChildren = new MDDNode [children.length];
    for (int i = 0; i < mddChildren.length; i++)
      mddChildren [i] = ((JADDMvSet)children [i]).getMddNode ();
    
    MDDNode answer = mddBot;
    
    for (int i = 0; i < mddChildren.length; i++)
      // -- answer = answer \/ (argIdx=i /\ mddChildren [i])
      answer = 
	mddManager.apply (getBinOperator (MvSet.JOIN), 
			  mddManager.apply (getBinOperator (MvSet.MEET), 
					    var (argIdx, i), mddChildren [i]),
			  answer);
    return createMvSet (answer);
  }
    

  public MvSet createMvSet (MDDNode mddNode)
  {
    // -- eventually we will use an object pool for this
    return new JADDMvSet (mddNode);
  }

  public MDDManager getMddManager ()
  {
    return mddManager;
  }    
  
  public class JADDMvSet extends AbstractMvSet 
  {
    
  
    // -- the actuall MDD representing this function
    MDDNode mdd;

    public JADDMvSet (MDDNode _mdd)
    {
      mdd = _mdd;
    }



    public boolean isConstant ()
    {
      return mdd.isConstant ();
    }
    public AlgebraValue getValue ()
    {
      assert mdd.isConstant ();
      return algebra.getValue (mdd.getValue ());
    }
    

    /*** 
     *** Creates a pointwise composition
     *** op is an operator: L x L -> L
     *** f is the current mv-set
     *** result h (x) = f (x) op g (x)
     ***/
    public MvSet ptwiseCompose (int op, MvSet g)
    {
    
      MDDNode node = mddManager.apply (getBinOperator (op), 
				       mdd, ((JADDMvSet)g).getMddNode ());
      return createMvSet (node);
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
      MDDNode node = mddManager.apply (getUnaryOperator (NEG), mdd);
      return createMvSet (node);
    }
  
    public int size ()
    {
      return mddManager.dagSize (mdd);
    }


    /***
     *** Restricts an argument
     *** h (x_0, x_1, x_2, ...) = f (x_0, ..., value, ...)
     *** where value is substituted at argIdx
     ***/
    public MvSet cofactor (int argIdx, AlgebraValue value)
    {
      return createMvSet (cofactorMDD (argIdx, value));
    }
  
    public MvSet cofactor (AlgebraValue[] r)
    {
      // XXX Can be done more efficiently!
      
      MDDNode mddNode = mdd;
      for (int i = 0; i < r.length; i++) 
	if (r[i] != noValue)
	  mddNode = cofactorMDD (i, r[i]);

      return createMvSet (mddNode);
    }

    private MDDNode cofactorMDD (MDDNode node, int argIdx, AlgebraValue value)
    {
      int[] enm = enumType.bitValue (value);
      
      MDDNode result = node;      
      for (int i = 0; i < enm.length; i++)
	{
	  if (enm [i] == -1) continue;
	  mddManager.cofactor (result, argIdx + i, enm [i]);
	}
      

      return result;
    }
    

    private MDDNode cofactorMDD (int argIdx, AlgebraValue value)
    {
      return cofactorMDD (mdd, argIdx, value);
    }
    
  
    public Set getPreImageArray(AlgebraValue v) 
    {
      assert false : "Not done";
      return null;
    }
    

    public MvSet existAbstract (MvSet _cube)
    {
      MDDNode cube = ((JADDMvSet)_cube).getMddNode ();
      MDDNode result = mddManager.quantify (getExistQuantify (), 
					    mdd, cube);
      return createMvSet (result);
    }

    public MvSet forallAbstract (MvSet _cube)
    {
      MDDNode cube = ((JADDMvSet)_cube).getMddNode ();
      MDDNode result = mddManager.quantify (getForallQuantify (), 
					    mdd, cube);
      return createMvSet (result);
    }
  
  

    /***
     *** Renames the arguments. newArgs is a map from old args to new so that
     *** h (x) = f (newArgs [0], newArgs [1], ...)
     ***/
    public MvSet renameArgs (int[] newArgs)
    {
      // -- rebuild newArgs array and call mddManager.renameVars
      int[] ddNewArgs = new int [enumWidth * newArgs.length];
      
      for (int i = 0; i < newArgs.length; i++)
	for (int j = 0; j < enumWidth; j++)
	  ddNewArgs [i + j] = newArgs [i] + j;      
      return createMvSet (mddManager.renameVars (mdd, ddNewArgs));

    }
  
  
    /***
     *** Evaluates this function on an input
     *** result = f (values [0], values [1], ...)
     ***/
    public AlgebraValue evaluate (AlgebraValue[] values)
    {
      MDDNode result = mdd;
      for (int i = 0; i < values.length; i++)
	if (values [i] != noValue)
	  result = cofactorMDD (result, i, values [i]);

      return algebra.getValue (result.getValue ());
    }  

    /*** get the lattice for the MvSet */
    public IAlgebra getAlgebra ()
    {
      return getFactory ().getAlgebra ();
    }

    public BitSet getImage()
    {
      return mdd.getImage();
    }
    
    public MDDNode getMddNode ()
    {
      return mdd;
    }

    public MvSetFactory getFactory ()
    {
      return JADDMvSetFactory.this;
    }

  

    public boolean equals (Object o)
    {
      if (o == this) return false;
      if (o.getClass () !=  this.getClass ()) return false;
      return equals ((JADDMvSet)o);
    }
    public boolean equals (JADDMvSet v)
    {

      // -- two mv-sets are equal if their mdd representations are equal
      return getMddNode () == v.getMddNode ();
    }  

  
    public int hashCode ()
    {
      return getMddNode ().hashCode ();
    }

    public AlgebraValue[][] expandToArray ()
    {
      assert false : "Not implemented";
      return null;
    }
    public DaVinciGraph toDaVinci ()
    {
      return mddManager.toDaVinci (getMddNode ());
    }
    
  }
    
}

  




