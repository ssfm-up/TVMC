package edu.toronto.cs.mvset;

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
public class MDDMvSetFactory extends AbstractMvSetFactory
{
  MDDManager mddManager;

  ApplyFunction[] applyFunctions;

  // -- top and bot nodes for quick reference
  MDDNode mddTop;
  MDDNode mddBot;

  MDDNode mddInfoTop;
  MDDNode mddInfoBot;

  MvSet mvSetTop;
  MvSet mvSetBot;
  MvSet mvSetInfoTop;
  MvSet mvSetInfoBot;
    
  public MDDMvSetFactory (IAlgebra _algebra, int nvars)
  {  
    super (_algebra);
    mddManager = new MDDManager (nvars, algebra.size ());
    initialize ();

  }
  public MDDMvSetFactory (IAlgebra _algebra, MDDManager _mddManager)
  {
    super (_algebra);
    mddManager = _mddManager;
    initialize ();
    
  }
  
  

  public static MvSetFactory newMvSetFactory (IAlgebra algebra, 
					      int nvars)
  {
    return new MDDMvSetFactory (algebra, nvars);
  }


  private void initialize ()
  {
    mddTop = mddManager.getLeafNode (top.getId ());
    mvSetTop = createMvSet (mddTop);
    mddBot = mddManager.getLeafNode (bot.getId ());
    mvSetBot = createMvSet (mddBot);

    if (getAlgebra () instanceof BelnapAlgebra)
      {
	BelnapAlgebra belnap = (BelnapAlgebra) getAlgebra ();
	mddInfoTop = mddManager.getLeafNode (belnap.infoTop ().getId ());
	mvSetInfoTop = createMvSet (mddInfoTop);
	mddInfoBot = mddManager.getLeafNode (belnap.infoBot ().getId ());
	mvSetInfoBot = createMvSet (mddInfoBot);
      }
    else
      {
	mddInfoTop = null;
	mddInfoBot = null;
      }
    setupApplyFunctions ();
  }

  public void renew()
  {
    mddManager.renew();
  }
  
  private void setupApplyFunctions ()
  {
    applyFunctions = new ApplyFunction [11];
    
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
    if (getAlgebra () instanceof BelnapAlgebra)
      {
	applyFunctions [MvSet.INFO_AND] = 
	  new InfoMeetFunction (mddManager, (BelnapAlgebra) getAlgebra ());
	applyFunctions [MvSet.INFO_OR] =
	  new InfoJoinFunction (mddManager, (BelnapAlgebra) getAlgebra ());
      }
    else
      {
	applyFunctions [MvSet.INFO_AND] = null;
	applyFunctions [MvSet.INFO_OR] = null;
      }
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
    return 
      createMvSet (mddManager.buildCube (varIndex, mddTop, mddBot));
  }  
  


  public MvSet top ()
  {
    //return createMvSet (mddTop);
    return mvSetTop;
  }
  public MvSet bot ()
  {
    //return createMvSet (mddBot);
    return mvSetBot;
  }
  public MvSet infoTop ()
  {
    //return createMvSet (mddInfoTop);
    return mvSetInfoTop;
  }
  public MvSet infoBot ()
  {
    //return createMvSet (mddInfoBot);
    return mvSetInfoBot;
  }
  
  
  
  /***
   *** Given an argument index (0 <= argIdx < n)
   *** constructs a projection function
   *** f(x_0, x_1, ...) = x_argIdx
   ***/
  public MvSet createProjection (int argIdx)
  {
    MDDNode[] kids = new MDDNode [algebra.size ()];
    
    for (IntIterator it = algebra.carrierSetId (); it.hasNext (); )
      {
	int val = it.nextInt ();
	kids [val] = mddManager.getLeafNode (val);
      }
    
    return createMvSet (mddManager.kase (argIdx, kids));
  }
    

    
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  public MvSet createPoint (AlgebraValue[] args, AlgebraValue value)
  {

    int[] intArgs = new int [args.length];
    for (int i = 0; i < args.length; i++)
      intArgs [i] = (args [i] == noValue) ? 
	MDDManager.NO_VALUE : args [i].getId ();
    
    
    return createMvSet 
      (mddManager.buildPoint (intArgs, 
			      mddManager.getLeafNode (value.getId ()), 
			      mddBot));
    
  }


  public MvSet var (int argIdx, AlgebraValue argVal, AlgebraValue value)
  {
    return createMvSet (mddManager.buildVar 
		      (argIdx, argVal.getId (), 
		       mddManager.getLeafNode (value.getId ()), 
		       mddBot));
  }


  MDDNode var (int argIdx, int branch)
  {
    return mddManager.buildVar (argIdx, branch, mddTop, mddBot);
  }
  

  public MvSet createCase (int argIdx, MvSet[] children)
  {
    MDDNode[] mddChildren = new MDDNode [children.length];
    for (int i = 0; i < mddChildren.length; i++)
      mddChildren [i] = ((MDDMvSet)children [i]).getMddNode ();

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
    

  Map uniqueTable = new HashMap ();
  
  public MvSet createMvSet (MDDNode mddNode)
  {
    // -- eventually we will use an object pool for this
    return new MDDMvSet (mddNode);
  }

  public MDDManager getMddManager ()
  {
    return mddManager;
  }    
  
  public class MDDMvSet extends AbstractMvSet 
  {
    
  
    // -- the actuall MDD representing this function
    MDDNode mdd;

    public MDDMvSet (MDDNode _mdd)
    {
      mdd = _mdd;
    }


    public boolean isConstant()
    {
      return mdd.isConstant();
    }

    public AlgebraValue getValue()
    {
      if (mdd.isConstant())
	return getAlgebra().getValue(mdd.getValue());
      
      throw new RuntimeException("Cannot get value of non-constant node");
    }
    
    public String toString()
    {
      if (mdd.isConstant())
	return getAlgebra().getValue(mdd.getValue()).toString();
      return dumpMvSet (this);
      //return mdd.toString();
    }
    
  private String dumpMvSet (MvSet mvSet)
    {
      StringBuffer sb = new StringBuffer ();
      for (Iterator it = mvSet.cubeIterator (); it.hasNext ();)
	{
	  sb.append (Arrays.asList ((Object[])it.next ()).toString ());
	  sb.append ("\n");
	}
      return sb.toString ();
    }
    
    /*** 
     *** Creates a pointwise composition
     *** op is an operator: L x L -> L
     *** f is the current mv-set
     *** result h (x) = f (x) op g (x)
     ***/
    public MvSet ptwiseCompose (int op, MvSet g)
    {

      assert g != null : "Composing with null";
      

      MDDNode node = mddManager.apply (getBinOperator (op), 
				       mdd, ((MDDMvSet)g).getMddNode ());
      if (node == getMddNode ()) return this;
      else if (node == ((MDDMvSet)g).getMddNode ()) return g;
      
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


    public MvSet cofactor (MvSet point)
    {
      MDDNode res = mddManager.cofactor (mdd, 
					 ((MDDMvSet)point).getMddNode (),
					 mddBot);
      return createMvSet (res);
    }
    
    /***
     *** Restricts an argument
     *** h (x_0, x_1, x_2, ...) = f (x_0, ..., value, ...)
     *** where value is substituted at argIdx
     ***/
    public MvSet cofactor (int argIdx, AlgebraValue value)
    {
      return createMvSet (mddManager.cofactor (mdd, argIdx, value.getId ()));
    }
  
    public MvSet cofactor (AlgebraValue[] r)
    {
      // XXX Can be done more efficiently!
      
      MDDNode mddNode = mdd;
      for (int i = 0; i < r.length; i++) {

	if (r[i] != noValue)
	  mddNode = mddManager.cofactor (mddNode, i, r[i].getId ());
      }
      

      return createMvSet (mddNode);
    }

  
    /***
	 returns all cubes that lead to v
     ***/
    public Set getPreImageArray (AlgebraValue v) 
    {
      MDDNode mddNode = mdd;
      Set answerSet = new HashSet ();

      if (mddNode.isConstant () && mddNode.getValue () == v.getId ())
	answerSet.add (blankAssignmentVector ());
      // is in image?
      else if (mddNode.getImage ().get (v.getId ()))
	{
	  MDDNode[] kidz = mddNode.getChildren ();
	    
	  // loop over children
	  for (int i=0; i < kidz.length; i++)
	    {
	      // -- skip all children that cannot reach v
	      if (!kidz [i].getImage ().get (v.getId ()))
		continue;
	      
	      // -- recurse on children
	      Set childSet = createMvSet (kidz [i]).getPreImageArray (v);
	      // and over the assignments for each child
	      for (Iterator it = childSet.iterator (); it.hasNext();)
		{
		  AlgebraValue[] vec = (AlgebraValue []) it.next ();
		  vec = (AlgebraValue[]) vec.clone ();
		  vec [mddNode.getVarIndex ()] = algebra.getValue (i);
		  answerSet.add (vec);
		}
	    } 
	}
      return answerSet;
    }
    

    public MvSet existAbstract (MvSet _cube)
    {
      MDDNode cube = ((MDDMvSet)_cube).getMddNode ();
      MDDNode result = mddManager.quantify (getExistQuantify (), 
					    mdd, cube);
      return createMvSet (result);
    }

    public MvSet forallAbstract (MvSet _cube)
    {
      MDDNode cube = ((MDDMvSet)_cube).getMddNode ();
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
      return createMvSet (mddManager.renameVars (mdd, newArgs));
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
	  result = mddManager.cofactor (result, i, values [i].getId ());

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
      return MDDMvSetFactory.this;
    }

  

    public boolean equals (Object o)
    {
      if (this == o) return true;
      if (o == null || o.getClass () !=  MDDMvSet.class) return false;
      return getMddNode () == ((MDDMvSet)o).getMddNode ();
    }

    public boolean equals (MDDMvSet v)
    {

      // -- two mv-sets are equal if their mdd representations are equal
      return getMddNode () == v.getMddNode ();
    }  

    public AlgebraValue[] blankAssignmentVector()
    {
      AlgebraValue v[] = new AlgebraValue[mddManager.getNvars()];
      for (int i=0; i<v.length; i++)
	v[i] = noValue;
      return v;
    
    }
  
    public int hashCode ()
    {
      return getMddNode ().hashCode ();
    }


    public Iterator cubeIterator ()
    {
      return new Iterator ()
	{
	  Iterator cubeIterator = mddManager.cubeIterator (getMddNode (),
							    mddBot);
	  
	  public void remove ()
	  {
	    throw new UnsupportedOperationException ();
	  }
	  
	  public boolean hasNext ()
	  {
	    return cubeIterator.hasNext ();
	  }
	  
	  public Object next ()
	  {
	    int[] mddCube = (int[])cubeIterator.next ();
	    
	    // -- covert into AlgebraValues
	    AlgebraValue[] cube = new AlgebraValue [mddCube.length];
	    for (int i = 0; i < cube.length; i++)
	      cube [i] = (mddCube [i] == MDDManager.NO_VALUE) ? 
		algebra.noValue () : algebra.getValue (mddCube [i]);
	    return cube;
	  }
	};
    }

    public Iterator mintermIterator (MvSet _vars, AlgebraValue val)
    {
      MDDMvSet vars = (MDDMvSet)_vars;
      Iterator mintermIterator = 
	mddManager.mintermIterator (getMddNode (), mddBot, 
				    vars.getMddNode (), 
				    val.getId ());
      
      return new MintermIterator (mintermIterator);
    }
    
    class MintermIterator implements Iterator
    {
      Iterator mintermIterator;
      public MintermIterator (Iterator _mintermIterator)
      {
	mintermIterator = _mintermIterator;
      }
	  
      public void remove ()
      {
	throw new UnsupportedOperationException ();
      }
      public boolean hasNext ()
      {
	return mintermIterator.hasNext ();
      }
      public Object next ()
      {
	return createMvSet ((MDDNode) mintermIterator.next ());
      }
    }

      
    public AlgebraValue[][] __expandToArray ()
    {
      int[][] values = mddManager.collectValues (getMddNode ());
      
      AlgebraValue[][] algValues = new AlgebraValue [values.length][];
      
      for (int i = 0; i < values.length; i++)
	{
	  algValues [i] = new AlgebraValue [values [i].length];
	  for (int j = 0; j < values [i].length; j++)
	    {
	      if (values [i][j] == MDDManager.NO_VALUE)
		algValues [i][j] = algebra.noValue ();
	      else
		{
		  assert  
		    algebra.getValue (values [i][j]) != algebra.noValue ();
		  algValues [i][j] = algebra.getValue (values [i][j]);
		}
	      
	    }
	  
	}
      return algValues;
    }
    public DaVinciGraph toDaVinci ()
    {
      return mddManager.toDaVinci (getMddNode ());
    }
    
  }
    
}

  




