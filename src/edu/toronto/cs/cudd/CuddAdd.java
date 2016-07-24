package edu.toronto.cs.cudd;

import edu.toronto.cs.algebra.*;
import java.util.Iterator;

/**
   A Java wrapper for Cudd's ADD (MBTDD) 
   @author Arie Gurfinkel
 ***/
public class CuddAdd
{

  public static final int NO_VALUE = -1;
  
  // -- pointer to the CuddAdd manager
  int mgrPtr;

  IAlgebra algebra;

  ADD addOne = null;
  ADD addZero = null;

  static 
  {
    System.loadLibrary ("jcudd");
  }

  /**
   * Creates a new <code>CuddAdd</code> instance.
   *
   * @param nvars initial number of variables
   */
  public CuddAdd (int nvars, IAlgebra _algebra)
  {
    algebra = _algebra;
    init (new IntAlgebraWrapper (algebra), nvars, 
	  algebra.top ().getId (), algebra.bot ().getId ());
  }
  

  
  /**
   * Creates a new cudd manager 
   *
   * @param nvars initial number of variables
   */
  private native void init (IntAlgebraWrapper algebra, int nvars, 
			    int top, int bot);


  /**
   * @return a pointer to an ADD corresponding to 1
   */
  private native int topPtr ();
  /**
   * @return a pointer to an ADD corresponding to 0
   */
  private native int botPtr ();

  public int addTopPtr ()
  {
    return topPtr ();
  }
  public int addBotPtr ()
  {
    return botPtr ();
  }
  
  

  public native synchronized int addVarPtr (int index);  
  public native synchronized int addConstantPtr (int value);
  public native synchronized int addAndPtr (int v1, int v2);
  public native synchronized int addOrPtr (int v1, int v2);
  public native synchronized int addEqPtr (int v1, int v2);
  public native synchronized int addLeqPtr (int v1, int v2);
  public native synchronized int addGeqPtr (int v1, int v2);
  public native synchronized int addImplPtr (int v1, int v2);
  public native synchronized int addPlusPtr (int v1, int v2);
  public native synchronized int addNotPtr (int v1);

  public native synchronized int addPermutePtr (int a, int[] map);
  public native synchronized int addGetValue (int a);
  public native synchronized int existAbstract (int v, int cube);
  public native synchronized int forallAbstract (int v, int cube);
  public native synchronized int addCofactorPtr (int f, int c);

  public native synchronized int addItePtr (int var, int left, int right);
  

  public native synchronized void ref (int ptr);
  public native synchronized void deref (int ptr);
  public native synchronized void recursiveDeref (int ptr);

  public native synchronized boolean isConstantPtr (int ptr);

  /***
   *** Manager functions
   ***/
  public native synchronized int checkZeroRef ();
  
  /**
   * Runs CUDD's garbage collector
   *
   * @param clearCache true mean that the cache must be cleared as well
   * @return number of deleted nodes
   */
  public native synchronized int gc (boolean clearCache);
  public native synchronized void quit ();

  // -- outputs info on stdout
  public native synchronized void info ();

  public native synchronized void printMintermPtr (int a);

  public native synchronized void reorder (int minsize);
  public native synchronized int dagSize (int ptr);
  public native synchronized int sharingSize (int[] ptrs);


  public native synchronized int dumpDaVinciPtr (String fname, int addPtr);



  /**
   * Runs Cudd's garbage collector and clears the cache
   *
   * @return number of deleted nodes
   */
  public int gc ()
  {
    return gc (true);
  }
  

  protected void finalize () throws Throwable
  {
    super.finalize ();
    if (mgrPtr == 0) return;
    
    int retval = checkZeroRef ();
    while (checkZeroRef () != 0)
      Thread.currentThread ().sleep (100);
    
    if (retval != 0)
      System.out.println (retval + " unexpected non-zero reference count");
    else
      System.out.println  ("All went well");
    quit ();
    mgrPtr = 0;
  }


  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  
  
  
  // XXX Very strange function, restricts exactly one
  // XXX variable of an ADD -- this should be replaced with 
  // XXX a function that restricts many values instead, 
  // XXX or maybe we don't even need something like that at all.
//   public int restrictVar (int ptr, int var, boolean val)
//   {
//     int varAdd = addVarPtr (var);
    
//     if (!val)
//       {
// 	int temp = addNotPtr (varAdd);
// 	recursiveDeref (varAdd);
// 	varAdd = temp;
//       }

//     int result = addConstrainPtr (ptr, varAdd);
//     recursiveDeref (varAdd);
//     return result;
//   }
  
  
  // -- returns a add corresponding to 'index'
  public ADD addVar (int index)
  {
    return createADD (addVarPtr (index));
  }
  
  public ADD addTop ()
  {
    if (addOne == null)
      addOne = createADD (addTopPtr ());
    return addOne;
  }
  
  public ADD addBot ()
  {
    if (addZero == null)
      addZero = createADD (addBotPtr ());
    return addZero;
  }

  public ADD addConstant (int value)
  {
    return createADD (addConstantPtr (value));
  }

  public ADD buildCube (int[] varIndex)
  {
    ADD result = addTop ();
    
    for (int i = varIndex.length - 1; i >= 0; i--)
      result = result.and (addVar (varIndex [i]));
    return result;
  }
  
  public ADD addIte (int var, ADD child0, ADD child1)
  {
    return createADD (addItePtr (var, 
				 child0.getNodePtr (), child1.getNodePtr ()));
  }

  public ADD buildPoint (int[] intArgs, ADD value)
  {
    ADD result = value;
    
    for (int i = intArgs.length; i >= 0; i--)
      {
	if (intArgs [i] == NO_VALUE) continue;
	
	ADD var = addVar (i);
	if (intArgs [i] == 1)
	  var = var.not ();
	
	result = result.and (var);
      }
    return result;
  }  
  

  public String toString ()
  {
    return String.valueOf (mgrPtr);
  }
  
  public static void main (String[] args)
  {
    System.out.println ("Hello");

    CuddAdd cuddAdd = new CuddAdd (10, AlgebraCatalog.getAlgebra ("Kleene"));

    cuddAdd.addTop ();
    cuddAdd.addVar (3);
    cuddAdd.addBot ();

    ADD add = cuddAdd.addVar (3).and 
      (cuddAdd.addConstant (cuddAdd.getAlgebra ().getValue ("M").getId ()));

    System.out.println ("Using iterator");
    for (Iterator it = add.cubeIterator (); it.hasNext ();)
      {
	int[] cube = (int[]) it.next ();
	for (int i = 0; i < cube.length - 1; i++)
	  System.out.print (cube [i] + "  ");
	System.out.print (cuddAdd.getAlgebra ().
			  getValue (cube [cube.length - 1]));
	
	System.out.println ();
      }
    

    
  }
  
//    public static void main (String[] args)
//    {
//      CuddAdd CuddAdd = new CuddAdd ();
//      System.out.println ("Got: " + CuddAdd);
//      System.out.println ("1: " + CuddAdd.addOnePtr ());
//      System.out.println ("0: " + CuddAdd.addZeroPtr ());
//      System.out.println ("1 * 0: " + 
//  			CuddAdd.addAndPtr 
//  			(CuddAdd.addOnePtr (), 
//  			 CuddAdd.addZeroPtr ()));
//      System.out.println ("1 + 0: " + 
//  			CuddAdd.addOrPtr 
//  			(CuddAdd.addOnePtr (), 
//  			 CuddAdd.addZeroPtr ()));
    
//    }

  public boolean equals (Object o)
  {
    return o != null && 
      o.getClass () == CuddAdd.class && 
      getMgrPtr () == ((CuddAdd)o).getMgrPtr ();
  }
  
  int getMgrPtr ()
  {
    return mgrPtr;
  }
  
  private ADD createADD (int nodePtr)
  {
    return new ADD (nodePtr);
  }
  
  public Iterator cubeIterator (int nodePtr)
  {
    return new CubeIterator (nodePtr);
  }
  

  public class ADD
  {
    int nodePtr;
    
    protected ADD (int _nodePtr)
    {
      nodePtr = _nodePtr;
    }

    public Iterator cubeIterator ()
    {
      return CuddAdd.this.cubeIterator (getNodePtr ());
    }

    public boolean isConstant ()
    {
      return isConstantPtr (nodePtr);
    }
    
    
    public ADD and (ADD v)
    {
      return createADD (addAndPtr (nodePtr, v.getNodePtr ()));
    }
    
    public ADD or (ADD v)
    {
      return createADD (addOrPtr (nodePtr, v.getNodePtr ()));
    }

    public ADD impl (ADD v)
    {
      return createADD (addImplPtr (nodePtr, v.getNodePtr ()));
    }

    public ADD not ()
    {
      return createADD (addNotPtr (nodePtr));
    }

    public ADD eq (ADD v)
    {
      return createADD (addEqPtr (nodePtr, v.getNodePtr ()));
    }

    public ADD geq (ADD v)
    {
      return createADD (addGeqPtr (nodePtr, v.getNodePtr ()));
    }
    public ADD leq (ADD v)
    {
      return createADD (addLeqPtr (nodePtr, v.getNodePtr ()));
    }
    public ADD plus (ADD v)
    {
      return createADD (addPlusPtr (nodePtr, v.getNodePtr ()));
    }

    public ADD existAbstract (ADD cube)
    {
      return createADD 
	(CuddAdd.this.existAbstract (nodePtr, cube.getNodePtr ()));
    }
    public ADD forallAbstract (ADD cube)
    {
      return createADD (CuddAdd.this.forallAbstract (nodePtr, cube.getNodePtr ()));
    }
    


    
    public ADD cofactor(ADD c)
    {
      return createADD (addCofactorPtr (nodePtr, c.getNodePtr ()));
    }

    public ADD permuteArgs (int[] newArgs)
    {
      return createADD (addPermutePtr (nodePtr, newArgs));
    }
    
      
    public String toString ()
    {
      return getManager ().toString () + "@" + nodePtr;
    }
    
    
    CuddAdd getManager ()
    {
      return CuddAdd.this;
    }
    
    int getNodePtr ()
    {
      return nodePtr;
    }
    
    protected void finalize () throws Throwable
    {
      super.finalize ();
      if (nodePtr != 0)
	recursiveDeref (nodePtr);
    }
  

    public boolean equals (Object o)
    {
      return o != null && o.getClass () == ADD.class && equals ((ADD)o);
    }
    
    public boolean equals (ADD o)
    {
      return nodePtr == o.getNodePtr ();
    }    

    public int getValue ()
    {
      return addGetValue (nodePtr);
    }

    public int dagSize ()
    {
      return CuddAdd.this.dagSize (nodePtr);
    }

    public int hashCode ()
    {
      long hash = nodePtr * edu.toronto.cs.util.Primes.primes [0];
      return (int) (hash >>> 32);
    } 
  }
  
  public class CubeIterator implements Iterator
  {
    int mgrPtr;
    int genPtr;
    int nodePtr;

    CuddAdd addManager;
    
    int[] cube;

    public CubeIterator (int _nodePtr)
    {
      addManager = CuddAdd.this;
      mgrPtr = addManager.getMgrPtr ();
      nodePtr = _nodePtr;

      synchronized (addManager)
	{
	  cube = firstCube ();
	}
    }
    

    public void remove ()
    {
      throw new UnsupportedOperationException ();
    }
    
    public boolean hasNext ()
    {
      return cube != null;
    }
    
    public Object next ()
    {
      int[] cubeToReturn = (int[])cube.clone ();
      synchronized (addManager)
	{
	  cube = nextCube ();
	}
      return cubeToReturn;
    }

    protected void finalize () 
    {
      synchronized (addManager)
	{
	  freeGen ();
	  genPtr = 0;
	}
    }
    
    
    public native int[] firstCube ();
    public native int[] nextCube ();
    public native void freeGen ();
    
  }


}
