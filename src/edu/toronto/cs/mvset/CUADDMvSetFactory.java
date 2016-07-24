package edu.toronto.cs.mvset;

import edu.toronto.cs.algebra.*;

import java.io.*;
import java.util.*;


import edu.toronto.cs.util.*;
import edu.toronto.cs.davinci.*;

import edu.toronto.cs.cudd.*;
import edu.toronto.cs.cudd.CuddAdd.*;

import edu.toronto.cs.davinci.*;


/***
 *** Implementation of an MvSet based on the CUADD library
 ***/
public class CUADDMvSetFactory extends AbstractMvSetFactory
{
  CuddAdd cudd;


  // -- top and bot nodes for quick reference
  ADD addTop;
  ADD addBot;


    
  public CUADDMvSetFactory (IAlgebra _algebra, int nvars)
  {  
    super (_algebra);
    cudd = new CuddAdd (nvars, algebra);
    initialize ();
  }  

  public static MvSetFactory newMvSetFactory (IAlgebra algebra, 
					      int nvars)
  {
    return new CUADDMvSetFactory (algebra, nvars);
  }


  private void initialize ()
  {
    addTop = cudd.addTop ();
    addBot = cudd.addBot ();
  }

  public void renew ()
  {
    cudd.gc ();
  }
  
  

    
    
  /***
   *** Given a value in L creates a function 
   *** f(x_0, x_1, ...) = value
   ***/
  public MvSet createConstant (AlgebraValue v)
  {
    return createMvSet (cudd.addConstant (v.getId ()));
  }    

  public MvSet buildCube (int[] varIndex)
  {
    return 
      createMvSet (cudd.buildCube (varIndex));
  }

  public MvSet top ()
  {
    return createMvSet (addTop);
  }
  public MvSet bot ()
  {
    return createMvSet (addBot);
  }
  
  
  /***
   *** Given an argument index (0 <= argIdx < n)
   *** constructs a projection function
   *** f(x_0, x_1, ...) = x_argIdx
   ***/
  public MvSet createProjection (int argIdx)
  {
    // -- we only support boolean variables so projection is easy to create
    return createMvSet (cudd.addVar (argIdx));
  }
    

    
  /***
   *** Creates a point function 
   *** f (args) = value
   ***          = 0 otherwise
   ***/
  public MvSet createPoint (AlgebraValue[] args, AlgebraValue value)
  {

    // --  Map IAlgebra.noValue () to cudd's idea of NO_VALUE 
    // -- Map top and bot to branches of the add
    // XXX we really should have the same notion of NO_VALUE throughout
    // XXX our libraries.
    int[] intArgs = new int [args.length];
    for (int i = 0; i < args.length; i++)
      {
	if (args [i] == noValue)
	  intArgs [i] = cudd.NO_VALUE;
	else if (args [i] == algebra.top ())
	  intArgs [i] = 0;
	else if (args [i] == algebra.bot ())
	  intArgs [i] = 1;
	else
	  assert false : intArgs [i];
      }
    
    
    // -- build the point
    return createMvSet 
      (cudd.buildPoint (intArgs, 
			cudd.addConstant (value.getId ())));
    
  }


  public MvSet var (int argIdx, AlgebraValue argVal, AlgebraValue value)
  {
    // -- this builds a function
    // -- argIdx == argVal : value
    // -- else             : bot

    assert argVal == algebra.top () || argVal == algebra.bot ();

    if (argVal == algebra.top ())
      return createMvSet (cudd.addIte (argIdx, 
				       cudd.addConstant (value.getId ()), 
				       addBot));
    else
      return createMvSet (cudd.addIte (argIdx, addBot, 
				       cudd.addConstant (value.getId ())));
  }


  ADD var (int argIdx, int branch)
  {
    assert branch == 0 || branch == 1;
    ADD result = cudd.addVar (argIdx);
    if (branch == 1)
      result = result.not ();    
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
    return 
      createMvSet (cudd.addIte (argIdx, ((CUADDMvSet)child0).getAddNode (), 
				((CUADDMvSet)child1).getAddNode ()));
  }
  
  
    

  public MvSet createMvSet (ADD addNode)
  {
    // -- eventually we will use an object pool for this
    return new CUADDMvSet (addNode);
  }

  public CuddAdd getCudd ()
  {
    return cudd;
  }    
  
  public class CUADDMvSet extends AbstractMvSet 
  {
    
  
    // -- the ADD representing this mv-set
    ADD addNode;

    public CUADDMvSet (ADD _addNode)
    {
      addNode = _addNode;
    }

    
    
    public boolean isConstant()
    {
      return addNode.isConstant();
    }

    public AlgebraValue getValue()
    {
      assert isConstant () : "Trying to get a value of a non-constant node";
      return getAlgebra ().getValue (addNode.getValue ());
    }
    
    public String toString()
    {
      if (addNode.isConstant ())
	return getAlgebra ().getValue (addNode.getValue ()).toString ();
      

      return addNode.toString ();
    }
    
    /*** 
     *** Creates a pointwise composition
     *** op is an operator: L x L -> L
     *** f is the current mv-set
     *** result h (x) = f (x) op g (x)
     ***/
    public MvSet ptwiseCompose (int op, MvSet g)
    {
      ADD result;
      ADD gNode = ((CUADDMvSet)g).getAddNode ();
      switch (op)
	{
	case MEET:
	  result = addNode.and (gNode);
	  break;
	case JOIN:
	  result = addNode.or (gNode);
	  break;
	case IMPL:
	  result = addNode.impl (gNode);
	  break;
	case GEQ:
	  result = addNode.geq (gNode);
	  break;
	case LEQ:
	  result = addNode.leq (gNode);
	  break;
	case EQ:
	  result = addNode.eq (gNode);
	  break;
	default:
	  assert false : "How did we get here?";
	  result = null;
	}
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
      return createMvSet (addNode.not ());
    }
  
    public int size ()
    {
      return addNode.dagSize ();
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

  
    

    public MvSet existAbstract (MvSet _cube)
    {
      
      ADD cube = ((CUADDMvSet)_cube).getAddNode ();
      return createMvSet (addNode.existAbstract (cube));
    }

    public MvSet forallAbstract (MvSet _cube)
    {
      ADD cube = ((CUADDMvSet)_cube).getAddNode ();
      return createMvSet (addNode.forallAbstract (cube));
    }
  
  

    /***
     *** Renames the arguments. newArgs is a map from old args to new so that
     *** h (x) = f (newArgs [0], newArgs [1], ...)
     ***/
    public MvSet renameArgs (int[] newArgs)
    {
      return createMvSet (addNode.permuteArgs (newArgs));
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

    
    public ADD getAddNode ()
    {
      return addNode;
    }
    

    public MvSetFactory getFactory ()
    {
      return CUADDMvSetFactory.this;
    }

  

    public boolean equals (Object o)
    {
      if (o == null) return false;
      if (o.getClass () !=  CUADDMvSet.class) return false;
      return equals ((CUADDMvSet)o);
    }
    public boolean equals (CUADDMvSet v)
    {

      // -- two mv-sets are equal if their mdd representations are equal
      return getAddNode ().equals (v.getAddNode ());
    }  

  
    public int hashCode ()
    {
      // XXX Potential problem here!
      // XXX note to self: next time explain the comment better!
      return getAddNode ().hashCode ();
    }

    public Iterator cubeIterator ()
    {
      return new Iterator ()
	{
	  Iterator cubeIterator = getAddNode ().cubeIterator ();
	  
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
	    int[] addCube = (int[])cubeIterator.next ();
	    
	    // -- convert to AlgebraValues
	    AlgebraValue[] cube = new AlgebraValue [addCube.length];
	    for (int i = 0; i < cube.length; i++)
	      cube [i] = 
		(addCube [i] == CuddAdd.NO_VALUE) ? algebra.noValue () :
		algebra.getValue (addCube [i]);
	    return cube;
	  }
	  
	};
    }
  }
}

  




