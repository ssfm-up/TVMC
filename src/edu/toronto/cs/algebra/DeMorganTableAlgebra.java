package edu.toronto.cs.algebra;

import java.util.*;
import java.io.*;


public class DeMorganTableAlgebra extends AbstractTableAlgebra 
  implements DeMorganAlgebra
{


  // -- negation table
  int[] negationTable;


  // -- cross product XXX Should not be here
  public DeMorganTableAlgebra (DeMorganTableAlgebra alg1, 
			       DeMorganTableAlgebra alg2)
  {
    super (alg1, alg2);
    buildNegationTable (alg1, alg2);
  }


  public DeMorganTableAlgebra (Collection elements, Collection aboveRelation,
			       Collection negRelation)
  {
    super (elements, aboveRelation);
    buildNegationTable (negRelation);
  }
  

  public AlgebraValue neg (AlgebraValue v)
  {
    return getValue (negationTable [v.getId ()]);
  }
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    // -- in de Morgan algebras v1 -> v2 is equivalent to -v1 \/ v2
    return v1.neg ().join (v2);
  }
  

  public int[] getNegTable ()
  {
    return negationTable;
  }


  // XXX REWRITE
  void buildNegationTable (DeMorganTableAlgebra alg1, 
			   DeMorganTableAlgebra alg2)
  {
    negationTable = new int [size ()];
    
    AlgebraValue[] elements1 = alg1.getValues ();
    AlgebraValue[] elements2 = alg2.getValues ();

    for (int i = 0; i < elements1.length; i++)
      {
	AlgebraValue neg1 = elements1 [i].neg ();
	
	for (int j = 0; j < elements2.length; j++)
	  {
	    AlgebraValue neg2 = elements2 [j].neg ();
	    AlgebraValue elem =
	      getValue (elements1 [i].getName () + elements2 [j].getName ());

	    negationTable [elem.getId ()] = 
	      getValue (neg1.getName () + neg2.getName ()).getId ();	    
	  }
      }
    
    
  }
  
  void buildNegationTable (Collection negRelation)
  {
    negationTable = new int [size ()];
    
    for (Iterator it = negRelation.iterator (); it.hasNext ();)
      {
	String[] neg = (String[])it.next ();

	// -- ~neg[0] = neg[1]
	negationTable [getValue (neg [0]).getId ()] = 
	  getValue (neg [1]).getId ();
      }
  }


  

  /**** 
   **** Debug and tester methods
   **** 
   ****/
  
  public void dumpTables (PrintWriter out)
  {
    out.println ("Dumping negation table");
    dumpNegation (out);
    super.dumpTables (out);
  }
  
  void dumpNegation (PrintWriter out)
  {
    out.println ("Negation: ");
    for (int i = 0; i < negationTable.length; i++)
      out.println ("~" + getValue (i) + " = " + 
		   getValue (negationTable [i]));
  }


  public Set getJoinIrredundant(BitSet interested)
  {
    Set res = new HashSet();
    
    BitSet irredundant = (BitSet)interested.clone ();

    for (int i = 0; i < irredundant.length (); i++)
      {
	// -- skip empty bits
	if (!irredundant.get(i)) continue;
	  
	for (int j = 0; j < irredundant.length (); j++)
	  if (irredundant.get(j) && (joinTable[j][i] == i) && i!=j)
	      irredundant.clear(j);
      }
    
    for (int i=0; i<irredundant.length(); i++)
      if (irredundant.get(i))
	res.add(getValue(i));
    
    return res;
  }

  
  public Set getMeetIrredundant(BitSet interested)
  {
    Set res = new HashSet();
    
    BitSet irredundant = (BitSet)interested.clone ();

    for (int i = 0; i < irredundant.length (); i++)
      {
	// -- skip empty bits
	if (!irredundant.get(i)) continue;
	  
	for (int j = 0; j < irredundant.length (); j++)
	  if (irredundant.get(j) && (meetTable[j][i] == i) && i!=j)
	      irredundant.clear(j);
      }
    
    for (int i=0; i<irredundant.length(); i++)
      if (irredundant.get(i))
	res.add(getValue(i));
    
    return res;
  }
  
 

  public static void main (String[] args)
  {
    List elem = new ArrayList ();
    elem.add ("T");
    elem.add ("S");
    elem.add ("N");
    elem.add ("C");
    elem.add ("U");
    elem.add ("F");
    
    List above = new ArrayList ();
    above.add (tuple ("T", "S"));
    above.add (tuple ("S", "N"));
    above.add (tuple ("S", "C"));
    above.add (tuple ("N", "U"));
    above.add (tuple ("C", "U"));
    above.add (tuple ("U", "F"));

    List neg = new ArrayList ();
    neg.add (tuple ("T", "F"));
    neg.add (tuple ("S", "U"));
    neg.add (tuple ("N", "N"));
    neg.add (tuple ("C", "C"));
    neg.add (tuple ("U", "S"));
    neg.add (tuple ("F", "T"));
    
    
    
    DeMorganTableAlgebra alg = new DeMorganTableAlgebra (elem, above, neg);
    alg.dumpTables (new PrintWriter (System.out, true));
    
  }
  

  
}

