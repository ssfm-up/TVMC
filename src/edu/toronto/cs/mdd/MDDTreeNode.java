package edu.toronto.cs.mdd;

import java.util.*;

import edu.toronto.cs.util.*;
import java.lang.*;


public class MDDTreeNode extends MDDNode
{
  // empty BitSet constant
  public static final BitSet EMPTY_BIT_SET = new BitSet ();


  // -- children of this tree node
  MDDNode[] children;
  // -- variable index 
  int varIndex;
  // -- set of reachable terminal nodes
  BitSet image;
  
  // -- cache to hold the largest variable index of any descendants
  int largestIndex = -1;

  protected MDDTreeNode ()
  {
    this (-1, null);
  }
  
  
  public MDDTreeNode (int _varIndex, MDDNode[] _children)
  {
    children = _children;
    varIndex = _varIndex;

    // -- we can have no children if this is a lookup node
    if (children != null)
      recalcImage ();
  }


//   protected void finalize() throws Throwable
//   {
//     System.out.println ("----------------------------------");
//     System.out.println ("In finalize method for this object: " +this);
//     System.out.println ("----------------------------------");
//   }
  

   
  public int childrenSize ()
  {
    return children.length;
  }
  public boolean isConstant ()
  {
    return false;
  }
  public MDDNode[] getChildren ()
  {
    return children;
  }
  public void setChildren (MDDNode[] v)
  {
    children = v;
  }
  public MDDNode getChild (int i)
  {
    return children [i];
  }
  

  public int getVarIndex ()
  {
    return varIndex;
  }
  public void setVarIndex (int v)
  {
    varIndex = v;
  }
  

  public int getValue ()
  {
    assert false: MDDTreeNode.class.getName () + " has no value";
    return -1;
  }


  /***
   *** Find what is the largest variable index along a path 
   *** over a branch 'val'
   ***/
  public int getCubeLastVar ()
  {
    if (largestIndex < 0) 
      largestIndex = getCubeLastVarRecur ();
    return largestIndex;
  }
  
  private int getCubeLastVarRecur ()
  {
    if (getChild (0).isConstant ())
      return getVarIndex ();
    
    return getChild (0).getCubeLastVar ();
  }
  
    

  private void recalcImage ()
  {
    // -- This is currently used by counter-example generator
    image = new BitSet (children.length);
    for (int i = 0; i < children.length; i++)
      image.or (children [i].getImage ());
  }



  /** Check to see if value v is in image */
  public boolean inImage (int v) 
  {
    return image.get(v);
  }

  public BitSet cloneImage()
  {
    return (BitSet)image.clone();
  }

  public BitSet getImage ()
  {
    return image;
  }
  

  public boolean equals (Object o)
  {
    if (o==null) return false;
    if (this == o) return true;
    if (o.getClass () ==  MDDTreeNode.class)
      return equals ((MDDTreeNode)o);
    return false;
  }
  public boolean equals (MDDTreeNode node)
  {
    if (node == null) return false;
    if (varIndex != node.varIndex) return false;
    
    for (int i = 0; i < children.length; i++)
      if (children [i] != node.children [i]) return false;
    return true;
  }

  public int hashCode ()
  {
    long hash = varIndex * primes [0];
    int len = children.length > hashChildren ? hashChildren : children.length;
    
    for (int i = 0; i < len; i++)
      hash += ((long)System.identityHashCode (children [i])) * primes [i];
    return (int)(hash >>> 32);
    //return (int)hash;
  }

    

  // XXX This is probably used for counterexample generation so 
  // XXX we do not need this for now
//   MDDNode restrictMDD (List stateRestriction, MDDNode mdd)
//     {
//       MDDNode result = mdd;
//       int varIndex=0;
//       for (Iterator it = stateRestriction.iterator ();
// 	   it.hasNext (); varIndex++)
// 	{
// 	  int varValue = ((Integer)it.next ()).intValue ();
// 	  result = manager.Restrict (result, varIndex, varValue);
// 	}
//       return result;
//     }

//   /**
//    ** Takes a BitSet specifying which lattice values to find a
//    ** restriction for.
//    **
//    ** @param values -- BitSet showing lattice values in which you are
//    ** interested
//    **
//    ** @return -- Set of restrictions for the given values.
//    **/
//   public Set findRestrictions (BitSet values)
//   {
//     //log.debug ("Looking for restrictions for: " + values);
//     Set restrictions = new HashSet ();

//     // get an MvState for every value
//     for (int val = 0; val < values.size (); val++)
//       {
// 	if (values.get (val))
// 	  {
// 	    // first create a 'garbage' state, which will be changed to
// 	    // become the restriction we want
// 	    int [] currentState = new int [manager.getNumVars ()];
// 	                            //createGarbageState ();
// 	    for (int i = 0; i < currentState.length; i++)
// 	      currentState [i] = ((i%2) == 0)? manager.getAlgebra ().idtimesIdx () : FiniteLattice.NO_VALUE;
// 	    // fill out the restriction
// 	    makeRestricted (currentState, val);
// 	    // add the state to restrictions
// 	    restrictions.add (currentState);
// 	  }
//       }

//     return restrictions;
//   }



//   public Set getRestrictions ()
//   {
//     // XXX hack
//     return getRestrictions (manager.getOne ().getValue ());
//   }

//   public Set getRestrictions (int value)
//   {
//     Set results = new HashSet ();
//     if (isConstant ())
//       return results;
//     int [] trace = new int [manager.getNumVars ()];
//     Arrays.fill (trace, FiniteLattice.NO_VALUE);
//     makeRestricted (results, trace, value);
//     return results;
//   }
  
//   Set makeRestricted (Set results, int [] s, int value)
//   {
//     if (isConstant ())
//       {
// 	results.add (s);
// 	return results;
//       }
//     for (int i = 0; i < howManyChildren (); i++)
//       {
// 	if (getChild (i).inImage (value))
// 	  {
// 	    int [] next = (int []) s.clone ();
// 	    next [variableIndex] = i;
// 	    getChild (i).makeRestricted (results, next, value);
// 	  }
//       }
//     return results;
//   }
    
//   /** Finds a path through a decision diagram leading to 'value'
//    ** recording it in 's'
//    **/

//   int [] makeRestricted (int [] s, int value)
//   {
//     // -- nothing to do for constant nodes
//     if (isConstant ()) 
//       {
// 	Assert.assert (value == getValue ());
// 	return s;
//       }
    
//     // -- otherwise find a first child that is on the path 
//     // -- to 'value' and follow it
    
//     for (int i = 0; i < howManyChildren (); i++)
//       {
// 	// -- check for a candidate
// 	if (getChild (i).inImage (value))
// 	  {
// 	    // -- got our candidate, set value and recurse
// 	    s [getVarIndex ()] = i;
// 	    return getChild (i).makeRestricted (s, value);
// 	  }
//       }
    
//     // -- hmm... this should probably be an assertion or exception
//     // -- since we assume we always have what we are looking for...

//     return s;
    
//   }
  

  

  
}
