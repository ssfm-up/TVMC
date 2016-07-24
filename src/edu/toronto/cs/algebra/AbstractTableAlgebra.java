package edu.toronto.cs.algebra;

import java.util.*;
import java.io.*;

import edu.toronto.cs.util.*;
import edu.toronto.cs.util.IntIterator.*;



// -- a lattice implemented by computing the meet and join table
// -- of the elements.
/****
 **** Implementation of a lattice that sorts its elements in topological
 **** order and builds a join table following the algorithms in 
 **** Free Lattices by Ralph Freese
 **** author: Arie Gurfinkel arie@cs.toronto.edu

/****
 **** Implements all kinds of (partially) ordered set operations
 **** namely: topological sorting and computing a join table
 ****/

public abstract class AbstractTableAlgebra implements IAlgebra
{


  AlgebraValue noValue;
  

  // -- name to AlgebraValue map
  Map nameToValue;

  // -- id to AlgebraValue map
  AlgebraValue[] elements;

  // -- upper and lower covers used by topological sorting algorithm
  Map upperCovers;
  Map lowerCovers;
  
  // meet and join tables
  int[][] meetTable;
  int[][] joinTable;



  /*** Construct an ordered set which is a product of set1 and set2 **/
  public AbstractTableAlgebra (AbstractTableAlgebra set1, 
			       AbstractTableAlgebra set2)
  {
    Collection elements = new ArrayList ();
    Collection aboveRelation = new ArrayList ();

    contructProduct (set1, set2, elements, aboveRelation);
    init (elements, aboveRelation);    
    noValue = new AlgebraValue (this, "-", -1);
  }
  
  public AlgebraValue noValue ()
  {
    return noValue;
  }
  
  
  /****
   **** Constructs an ordered set given a collection of elements and 
   **** an above relation (given a set of pairs) */
  public AbstractTableAlgebra (Collection _elements, Collection aboveRelation)
  {
    init (_elements, aboveRelation);
    noValue = new AlgebraValue (this, "-", -1);
  }


  // -- the actual initialization method
  void init (Collection _elements, Collection aboveRelation)
  {
    upperCovers = new HashMap ();
    lowerCovers = new HashMap ();
    // -- map the elements first
    mapElements (_elements);
    // -- construct upper covers
    constructUpperCovers (aboveRelation);
    constructLowerCovers (aboveRelation);
    
    elements = addWithOrder (nameToValue.values ());

    // -- reverse topological sort
    elements = sortElements (nameToValue.values ());

    // -- build join table
    buildJoinTable ();
    buildMeetTable ();
    
  }  
  
  
  
  /*** Create an array of AlgebraValue, populate it with elements, 
   *** and assign to each element an order based on its placement
   *** in the array
   ***/
  AlgebraValue[] addWithOrder (Collection elements)
  {
    AlgebraValue[] result = new AlgebraValue [elements.size ()];
    int idx = 0;
    
    for (Iterator it = elements.iterator (); it.hasNext ();)
      {
	AlgebraValue element = (AlgebraValue)it.next ();
	result [idx] = element;
	element.setId (idx);
	idx++;
      }
    return result;
  }
  
  void constructLowerCovers (Collection aboveRelation)
  {
    for (Iterator it = aboveRelation.iterator (); it.hasNext ();)
      {
	String[] above = (String[])it.next ();
	AlgebraValue a = getValue (above [0]);
	AlgebraValue b = getValue (above [1]);
	// -- a > b so we put b into lower cover of a
	addToLowerCover (a, b);
      }    
  }

  Set getLowerCover (AlgebraValue a)
  {
    Set lowerCover = (Set)lowerCovers.get (a);
    if (lowerCover == null) return Collections.EMPTY_SET;
    return lowerCover;
  }
  void addToLowerCover (AlgebraValue a, AlgebraValue b)
  {
    Set lowerCover = (Set)lowerCovers.get (a);
    if (lowerCover == null)
      {
	lowerCover = new HashSet ();
	lowerCovers.put (a, lowerCover);
      }
    lowerCover.add (b);
  }
  

  void constructUpperCovers (Collection aboveRelation)
  {
    // -- each element in the aboveRelation is of type Object[2]
    // -- such that aboveRelation [0] >= aboveRelation [1]
    for (Iterator it = aboveRelation.iterator (); it.hasNext ();)
      {
	String[] above = (String[])it.next ();
	AlgebraValue a = getValue (above [0]);
	AlgebraValue b = getValue (above [1]);
	// -- a >= b so we put a into upper cover of b
	
	addToUpperCover (b, a);
      }
  }
  
  Set getUpperCover (AlgebraValue a)
  {
    Set upperCover = (Set)upperCovers.get (a);
    if (upperCover == null) return Collections.EMPTY_SET;
    return upperCover;
  }
  void addToUpperCover (AlgebraValue a, AlgebraValue b)
  {
    Set upperCover = (Set)upperCovers.get (a);
    if (upperCover == null)
      {
	upperCover = new HashSet ();
	upperCovers.put (a, upperCover);
      }
    upperCover.add (b);
  }
  

  /** build a map between the lattice elements as view by the user
   ** and our internal representation
   **/
  void mapElements (Collection _elements)
  {
    nameToValue = new HashMap ();
    int count = 0;
    for (Iterator it = _elements.iterator (); it.hasNext ();)
      {
	String name = (String)it.next ();
	nameToValue.put (name, new AlgebraValue (this, name, count++));
      }
  }
  

  // -- sorts elements in reverse toplogical order 
  // -- (also known as linear extension)
  AlgebraValue[] sortElements (Collection col)
  {
    // -- create axulary array filled with '0'
    int[] count = new int [col.size ()];
    Arrays.fill (count, 0);

    // -- fill count with numbers such that
    // -- given an element 'a'
    // -- the value of count [a.getId ()] is exactly the number 
    // -- of occurences of a in an upperCover of other elements
    for (Iterator it = col.iterator (); it.hasNext ();)
      {
	AlgebraValue element = (AlgebraValue)it.next ();
	Set upperCover = getUpperCover (element);
	for (Iterator it2 = upperCover.iterator (); it2.hasNext ();)
	  {
	    AlgebraValue upCoverElement = (AlgebraValue)it2.next ();
	    count [upCoverElement.getId ()]++;
	  }
      }
    
    // -- stack of elements that have to be processed
    Stack needProcessing = new Stack ();
    for (int i = 0; i < count.length; i++)
      if (count [i] == 0)
	needProcessing.push (getValue (i)); 

    // -- set up our result array
    AlgebraValue[] result = new AlgebraValue [count.length];
    int idx = count.length - 1;
    
    // -- while we have elements to process keep adding them to the result
    while (!needProcessing.empty ())
      {
	AlgebraValue element = (AlgebraValue)needProcessing.pop ();

	// -- add this element to the list
	result [idx] = element;
	element.setId (idx--);
	
	for (Iterator it = getUpperCover (element).iterator (); 
	     it.hasNext ();)
	  {
	    AlgebraValue upCoverElement = (AlgebraValue)it.next ();
	    count [upCoverElement.getId ()]--;
	    if (count [upCoverElement.getId ()] == 0)
	      needProcessing.push (upCoverElement);
	  }
      }
    return result;
  }
  
  /***
   *** Builds a join table 
   ***/
  void buildJoinTable ()
  {
    joinTable = new int [elements.length][elements.length];


    for (int i = 0; i < elements.length; i++)
      {	
	joinTable [i][i] = getValue (i).getId ();

	// -- compute join with all of the elements we have processed 
	// -- so far
	for (int j = 0; j < i; j++)
	  {
	    
	    int q = -1;
	    for (Iterator it = getUpperCover (getValue (i)).iterator ();
		 it.hasNext ();)
	      {
		/****
		 **** 
		 **** We need to compute q where q is the maximum of the set
		 **** { e.getId () | e = j \/ x and x \in upperCover[i] }
		 */
		AlgebraValue upCoverElement = (AlgebraValue)it.next ();
		if (joinTable [j][upCoverElement.getId ()] > q)
		  q = joinTable [j][upCoverElement.getId ()];
	      }
	    joinTable [i][j] = q;
	    joinTable [j][i] = q;
	  }
      }
  }
  
  /****
   **** Builds a meet table. This is dual to buildJoinTable and possibly
   **** we can encapsulate both constructions in a single method, but for 
   **** now they are separate
   ****/
  void buildMeetTable ()
  {
    meetTable = new int [elements.length][elements.length];


    // -- counts how many elements we've seen so far
    int idx = 0;
    for (Iterator it = new ArrayIterator (elements, false); 
	 it.hasNext ();)
      {
	AlgebraValue x = (AlgebraValue)it.next ();
	int ordX = x.getId ();

	meetTable [ordX][ordX] = ordX;

	// -- compute join with all of the elements we have processed 
	// -- so far
	//for (int j = 0; j < i; j++)
	for (Iterator it2 = new ArrayIterator (elements, false, idx); 
	     it2.hasNext ();)
	  {
	    AlgebraValue y = (AlgebraValue)it2.next ();
	    int q = size () + 1;
	    for (Iterator it3 = getLowerCover (x).iterator ();
		 it3.hasNext ();)
	      {
		/****
		 **** 
		 **** We need to compute q where q is the minimum of the set
		 **** { e.getId () | e = y /\ z and z \in upperCover[i] }
		 */
		AlgebraValue z = (AlgebraValue)it3.next ();
		if (meetTable [y.getId ()][z.getId ()] < q)
		  q = meetTable [y.getId ()][z.getId ()];
	      }
	    //System.out.println (x + " meet " + y + " = " + q);
	    meetTable [ordX][y.getId ()] = q;
	    meetTable [y.getId ()][ordX] = q;
	  }
	idx++;
      }

  }
  


  


  
  void contructProduct (AbstractTableAlgebra set1, AbstractTableAlgebra set2, 
			 Collection elements, 
			 Collection aboveRelation)
  {
    AlgebraValue[] elements1 = set1.getValues ();
    AlgebraValue[] elements2 = set2.getValues ();

    for (int i = 0; i < elements1.length; i++)
      {
	for (int j = 0; j < elements2.length; j++)
	  {
	    // -- new lattice value is a concatination of the 
	    // -- elements1[i] and elements2[j]
	    String latValue = elements1 [i].getName ().toString () + 
	      elements2 [j].getName ().toString ();
	    // -- add the value to our elements
	    elements.add (latValue);
	    // -- build the above realtions
	    buildAboveRelation (set1, set2, elements1 [i], elements2 [j],
				aboveRelation);
	    
	  }
      }
    
    
  }
  
  void buildAboveRelation (AbstractTableAlgebra set1, AbstractTableAlgebra set2, 
			   AlgebraValue elm1, AlgebraValue elm2, 
			   Collection aboveRelation)
  {
    Set lowerCover1 = set1.getLowerCover (elm1);
    Set lowerCover2 = set2.getLowerCover (elm2);
    
    for (Iterator it = lowerCover2.iterator (); it.hasNext ();)
      {
	AlgebraValue low = (AlgebraValue)it.next ();
	Object[] rel = new Object [2];
	rel [0] = elm1.getName ().toString () + elm2.getName ().toString ();
	rel [1] = elm1.getName ().toString () + low.getName ().toString ();
	aboveRelation.add (rel);
      }

    for (Iterator it = lowerCover1.iterator (); it.hasNext ();)
      {
	AlgebraValue low = (AlgebraValue)it.next ();
	Object[] rel = new Object [2];
	rel [0] = elm1.getName ().toString() + elm2.getName ().toString ();
	rel [1] = low.getName ().toString () + elm2.getName ().toString ();
	aboveRelation.add (rel);
      }

    for (Iterator it = lowerCover1.iterator (); it.hasNext ();)
      {
	AlgebraValue low1 = (AlgebraValue)it.next ();
	for (Iterator it2 = lowerCover2.iterator (); it2.hasNext ();)
	  {
	    AlgebraValue low2 = (AlgebraValue)it2.next ();
	    Object[] rel = new Object [2];
	    rel [0] = elm1.getName ().toString() + 
	      elm2.getName ().toString ();
	    rel [1] = low1.getName ().toString () +
	      low2.getName ().toString ();
	    aboveRelation.add (rel);
	  }
      }
    
  }


  
  // -- helper method for meet/join table lookups
  private int tableLookUp (int[][] table, int idx1, int idx2)
  {
    return (idx1 <= idx2) ? table [idx1][idx2] : table [idx2][idx1];
  }

  

  // -- some getter methods that may be useful
  public int[][] getJoinTable ()
  {
    return joinTable;
  }
  public int[][] getMeetTable ()
  {
    return meetTable;
  }
  AlgebraValue[] getValues ()
  {
    return elements;
  }




  /****
   **** Lattice Interface
   ****/

  // -- get a new lattice value based on its data
  public AlgebraValue getValue (String name)
  {
    // Benet: the BinaryTreeToCTLConverter uses this to check if
    //  a symbol is a constant or an atom; so if something is
    //  not in the nameToValue Map, it needs to return noValue,
    //  rather than null.
    if (nameToValue.containsKey(name))
      return (AlgebraValue)nameToValue.get (name);
    else
      return noValue();
  }
  
  // -- get a new lattice value based on its id
  public AlgebraValue getValue (int id)
  {
    return elements [id];
  }
  
  
  public AlgebraValue meet (AlgebraValue v1, AlgebraValue v2)
  {  
    return getValue (tableLookUp (meetTable, v1.getId (), v2.getId ()));
  }
  
  public AlgebraValue join (AlgebraValue v1, AlgebraValue v2)
  {
    return getValue (tableLookUp (joinTable, v1.getId (), v2.getId ()));
  }

  public abstract AlgebraValue neg (AlgebraValue v);
  public abstract AlgebraValue impl (AlgebraValue v1, AlgebraValue v2);
  

  // -- since elements are sorted getting top and bottom is trivial
  public AlgebraValue top ()
  {
    return elements [0];
  }
  
  public AlgebraValue bot ()
  {
    return elements [elements.length - 1];
  }
  

  

  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return v1 == v2 ? top () : bot ();
  }
  
  public AlgebraValue leq (AlgebraValue v1, AlgebraValue v2)
  {
    return 
      (v1 == v2 || 
       tableLookUp (meetTable, v1.getId (), v2.getId ()) == v1.getId ()) ?
      top () : bot ();
  }
  
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return 
      (v1 == v2 ||
       tableLookUp (joinTable, v1.getId (), v2.getId ()) == v1.getId ()) ?
      top () : bot ();
  }
  


  // -- returns the size of this lattice
  public int size ()
  {
    return nameToValue.size ();
  }
  



  // -- returns ids for the carrier set
  public IntIterator carrierSetId () throws UnsupportedOperationException
  {
    return new RangeIterator (0, size ());
  }
  
  // -- returns elements of the carrier set
  public Collection carrierSet () throws UnsupportedOperationException
  {
    return Arrays.asList (elements);
  }
  

  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {
    throw new UnsupportedOperationException ("join decomposition");
  }
  


  
  /**** 
   **** TESTER method and debug functions 
   **** 
   ****/


  public void dumpTables (PrintWriter out)
  {
    out.println ("Join table: ");
    dumpTable (out, joinTable);
    out.println ("Meet table: ");
    dumpTable (out, meetTable);
  }
  
  public void dumpValueMap (PrintWriter out)
  {
    for (int i = 0; i < elements.length; i++)
      out.println (getValue(i).getName ()+ " = " + i);
  }
  
  void dumpTable (PrintWriter out, int[][] table)
  {
    for (int i = 0; i < table.length; i++)
      {
	for (int j = 0; j < table [i].length; j++)
	  out.print (getValue (table [i][j]) + " ");
	out.println ();
      }
  }

  static void printJoinTable (AbstractTableAlgebra oSet)
  {
    int[][] table = oSet.joinTable;
    printTable (oSet, table);
  }

  static void printMeetTable (AbstractTableAlgebra oSet)
  {
    printTable (oSet, oSet.meetTable);
  }
  
  static void printTable (AbstractTableAlgebra oSet, int[][] table)
  {
    
    System.out.println ("1,1 is " + oSet.getValue (table [1][1]));
    for (int i = 0; i < table.length; i++)
      {
	for (int j = 0; j < table [i].length; j++)
	  System.out.print (oSet.getValue (table [i][j]) + " ");
	System.out.println ();
      }
    
  }

  static String[] tuple (String o1, String o2)
  {
    return new String[] {o1, o2};
  }


  public Set getJoinIrredundant (BitSet subset)
  {
    throw new UnsupportedOperationException ("Not supported");
  }
  

}

