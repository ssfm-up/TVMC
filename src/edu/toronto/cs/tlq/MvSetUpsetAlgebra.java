package edu.toronto.cs.tlq;

import java.util.*;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.util.IntIterator.*;
import edu.toronto.cs.tlq.MvSetCrossProductAlgebra.*;


public class MvSetUpsetAlgebra implements IAlgebra
{
  // -- factory to create mv sets if we need any
  MvSetFactory mvSetFactory = null;

  // -- a cross-product algebra on top of which we build
  // -- this upset lattice
  MvSetCrossProductAlgebra cpAlgebra;
  

  // -- top of this algebra
  MvSetUpsetValue top;
  // -- bot of this algebra
  MvSetUpsetValue bot;
  // -- noValue of this algebra
  MvSetUpsetValue noValue;


  // -- unique table to keep track of our elements
  UniqueTable uniqueTable;
  

  /**
   * Creates a new <code>MvSetUpsetAlgebra</code> instance.
   *
   * @param _mvSetFactory a <code>MvSetFactory</code> value
   */
  public MvSetUpsetAlgebra ()
  {
    renew ();
  }

  public void setMvSetFactory (MvSetFactory v)
  {
    if (mvSetFactory == null)
      {
	mvSetFactory = v;
	cpAlgebra.setMvSetFactory (v);
      }
    
    assert v == mvSetFactory;
  }


  public void renew ()
  {
    uniqueTable = new UniqueTable ();
    // -- create cross-product algebra
    cpAlgebra = new MvSetCrossProductAlgebra ();

    // -- set our top, bot and noValue elements
    noValue = new MvSetUpsetValue (this);

    bot = uniqueTable.getValue (new MinUpSet ());
    bot.setName ("F");    
    top = getJoinIrreducible ((CrossProductValue)cpAlgebra.top ());
    top.setName ("T");
    mvSetFactory = null;
  }
  
  

  /**
   * Returns underlying cross-product algebra
   *
   * @return a <code>MvSetCrossProductAlgebra</code> value
   */
  public MvSetCrossProductAlgebra getCrossProductAlgebra ()
  {
    return cpAlgebra;
  }
  
  /**
   * Convinience method to produce an join irreducible element 
   * of this algebra without creating a cross-product element first.
   * This is equivalent to 
   * getJoinIrreducible (getCrossProductAlgebra ().newValue (i, mvSet))
   *
   * @param i an <code>int</code> value
   * @param mvSet a <code>MvSet</code> value
   * @return a <code>MvSetUpsetValue</code> value
   */
  public MvSetUpsetValue getJoinIrreducible (int i, MvSet mvSet)
  {
    return getJoinIrreducible (cpAlgebra.newValue (i, mvSet));
  }
  
  /**
   * Returns a join irreducible element of this algebra which is 
   * equivalent to upset (v)
   *
   * @param v a <code>CrossProductValue</code> value
   * @return a <code>MvSetUpsetValue</code> value
   */
  public MvSetUpsetValue getJoinIrreducible (CrossProductValue v)
  {
    MinUpSet set = new MinUpSet ();
    set.add (v);
    return uniqueTable.getValue (set);
  }
  

  /**
   * Returns an AlgebraValue based on id
   *
   * @param id an <code>int</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue getValue (int id)
  {
    if (id == top.getId ()) return top;
    if (id == bot.getId ()) return bot;
    return uniqueTable.getValueById (id);
  }
  
  /**
   * Returns an algebra value based on name
   *
   * @param name a <code>String</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue getValue (String name)
  {
    if (name.equals ("T")) return top;
    if (name.equals ("F")) return bot;
    return noValue ();
  }

  /**
   *
   * @return Top of this algebra
   */
  public AlgebraValue top ()
  {
    return top;
  }
  
  /**
   *
   * @return Bottom element of this algebra
   */
  public AlgebraValue bot ()
  {
    return bot;
  }
  
  /**
   *
   * @return noValue element
   */
  public AlgebraValue noValue ()
  {
    return noValue;
  }

  /**
   * Algebraic meet
   *
   * @param _v1 an <code>AlgebraValue</code> value
   * @param _v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue meet (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    
    MvSetUpsetValue v1 = (MvSetUpsetValue)_v1;
    MvSetUpsetValue v2 = (MvSetUpsetValue)_v2;

    if (v1 == bot || v2 == bot) return bot;
    if (v1 == top) return v2;
    if (v2 == top) return v1;

    MinUpSet result = new MinUpSet ();
    
    for (Iterator it = v1.getValues ().iterator (); it.hasNext ();)
      {
	CrossProductValue cpVal1 = (CrossProductValue)it.next ();
	for (Iterator jt = v2.getValues ().iterator (); jt.hasNext ();)
	  {
	    CrossProductValue cpVal2 = (CrossProductValue)jt.next ();
	    result.add (cpAlgebra.join (cpVal1, cpVal2));
	  }
      }
    return uniqueTable.getValue (result);
  }

  /**
   * Algebraic join
   *
   * @param _v1 an <code>AlgebraValue</code> value
   * @param _v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue join (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    
    MvSetUpsetValue v1 = (MvSetUpsetValue)_v1;
    MvSetUpsetValue v2 = (MvSetUpsetValue)_v2;

    if (v1 == top || v2 == top) return top;
    if (v1 == bot) return v2;
    if (v2 == bot) return v1;

    MinUpSet s = new MinUpSet ();
    s.addAll (v1.getValues ());
    s.addAll (v2.getValues ());
    return uniqueTable.getValue (s);
  }

  /**
   * Negation. Only defined if v == top (), or v == bot ()
   *
   * @param v an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue neg (AlgebraValue v)
  {
    if (v == top) return bot;
    else if (v == bot) return top;

    throw new UnsupportedOperationException ("Negation is only supported " + 
					     "for the boolean subalgebra");
  }

  /**
   * Not implemented
   *
   * @param v1 an <code>AlgebraValue</code> value
   * @param v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    throw new UnsupportedOperationException ("-> is not supported");
  }

  /**
   * Algebraic equivalence
   *
   * @param v1 an <code>AlgebraValue</code> value
   * @param v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return v1 == v2 ? top : bot;
  }
  
  /**
   * Algebraic less-than-or-equals
   *
   * @param _v1 an <code>AlgebraValue</code> value
   * @param _v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue leq (AlgebraValue _v1, AlgebraValue _v2)
  {
    MvSetUpsetValue v1 = (MvSetUpsetValue)_v1;
    MvSetUpsetValue v2 = (MvSetUpsetValue)_v2;

    if (v1 == v2) return top;

    return v2.getValues ().isSubSet (v1.getValues ()) ? top : bot;
  }

  
  
  /**
   * same as leq (v2, v1)
   *
   * @param v1 an <code>AlgebraValue</code> value
   * @param v2 an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue</code> value
   */
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return leq (v2, v1);
  }


  /**
   * The size of the carrier set -- not implemented
   *
   * @return an <code>int</code> value
   */
  public int size ()
  {
    // XXX This is very very wrong but makes MDDMvSetFactory happy
    // XXX Need a better solution
    return 2;
  }
  
  /**
   * IntIterator over id's of the carrier set
   *
   * @return an <code>IntIterator</code> value
   */
  public IntIterator carrierSetId () 
  {
    //throw new UnsupportedOperationException ("This is not supported");
    // XXX same comment as size ()
    return new RangeIterator (0, 2);
  }
  
  /**
   * Not implemented
   *
   * @return a <code>Collection</code> value
   */
  public Collection carrierSet ()
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  
  
  /**
   * Not implemented
   *
   * @param values a <code>BitSet</code> value
   * @return a <code>Set</code> value
   */
  public Set getJoinIrredundant (BitSet values)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }

  /**
   * Not implemented
   *
   * @param values a <code>BitSet</code> value
   * @return a <code>Set</code> value
   */  
  public Set getMeetIrredundant (BitSet values)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  
  /**
   * Not implemented
   *
   * @param v an <code>AlgebraValue</code> value
   * @return an <code>AlgebraValue[]</code> value
   */
  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  

  /**
   * A single element of MvSetUpsetAlgebra
   *
   */
  public class MvSetUpsetValue extends AlgebraValue
  {
    // -- a set of values represented by an upset
    MinUpSet values;
    
    public MvSetUpsetValue (MvSetUpsetAlgebra parentAlgebra,
			    String name, int id, MinUpSet _values)
    {
      super (parentAlgebra, name, id);
      values = _values;
    }
    
    public MvSetUpsetValue (MvSetUpsetAlgebra parentAlgebra)
    {
      super (parentAlgebra, "-", -1);
      values = null;
    }

    public MinUpSet getValues ()
    {
      return values;
    }
    
    protected void setValues (MinUpSet v)
    {
      values = v;
    }

    public boolean equals (Object o)
    {
      if (o == null) return false;
      if (o == this) return true;
      if (o.getClass () != MvSetUpsetValue.class) return false;
      return values.equals (((MvSetUpsetValue)o).getValues ());
    }

    public int hashCode ()
    {
      long hash = 0;

      for (Iterator it = values.iterator (); it.hasNext ();)
	hash += System.identityHashCode (it.next ());
      
      
      hash *= Primes.getPrime (0);
      return (int)hash;
    }
    
    
    public String toString ()
    {
      if (getName () != null && getName ().length () > 0)
	return getName ();
      return values.toString ();
    }    
  }
  

  class UniqueTable
  {
    Map table;
    List elements;

    MvSetUpsetValue lookupValue;

    public UniqueTable ()
    {
      elements = new ArrayList ();
      table = new HashMap ();
      lookupValue = new MvSetUpsetValue (MvSetUpsetAlgebra.this);
    }
    
    
    MvSetUpsetValue getValueById (int id)
    {
      if (id < 0) return (MvSetUpsetValue)noValue ();
      assert id < elements.size () : "Unknown element with id: " + id;

      return (MvSetUpsetValue)elements.get (id);
    }

    
    /**
     * Constructs a new MvSetUpsetValue for a Set 
     *
     * @param v a <code>Set</code> value
     * @return a <code>MvSetUpsetValue</code> value
     */
    int count = 0;
    MvSetUpsetValue getValue (MinUpSet v)
    {
      lookupValue.setValues (v);
      
      MvSetUpsetValue result = (MvSetUpsetValue)table.get (lookupValue);
      if (result == null)
	{
	  System.out.println ("MvSetUpsetAlgebra: new value: " + (++count));
	  result = new MvSetUpsetValue (MvSetUpsetAlgebra.this, 
					"", elements.size (), v);
	  elements.add (result);
	  table.put (result, result);

	  if (elements.size () < 0)
	    {
	      System.out.println ("Too many lattice values");
	      MvSetUpsetValue v1 = (MvSetUpsetValue)elements.get (48);
	      MvSetUpsetValue v2 = (MvSetUpsetValue)elements.get (49);
	      System.out.println ("Comparing 48 and 49: " + 
				  v1.equals ((Object)v2));
	      
	      System.out.println ("Identity: " + (v1 == v2));
	      
	      System.out.println ("Their hash codes are: " + v1.hashCode ()
				  + ", " + v2.hashCode ());
	      
	      MinUpSet m1 = v1.getValues ();
	      MinUpSet m2 = v2.getValues ();

	      System.out.println ("min-up-sets: " + m1 + ", " + m2);
	      System.out.println ("identity: " + (m1 == m2));
	      System.out.println ("equals: " + m1.equals (m2));
	      System.out.println ("hashcodes: " + m1.hashCode () + ", " + 
				  m2.hashCode ());
	      

	      CrossProductValue cp1 = 
		(CrossProductValue)m1.iterator ().next ();
	      CrossProductValue cp2 =
		(CrossProductValue)m2.iterator ().next ();
	      System.out.println ("The insider: " + cp1 + ", " + cp2);
	      System.out.println ("equals: " + cp1.equals (cp2));
	      System.out.println ("identity: " + (cp1 == cp2));
	      System.out.println ("hashCode: " + cp1.hashCode () + ", " + 
				  cp2.hashCode ());
	      
	      MvSet mv1 = cp1.getValue (0);
	      MvSet mv2 = cp2.getValue (0);
	      System.out.println ("mvsets: " + mv1 + ", " + mv2);
	      System.out.println ("equals: " + mv1.equals (mv2));
	      System.out.println ("identity: " + (mv1 == mv2));
	      System.out.println ("hashCode: " + mv1.hashCode () + ", " + 
				  mv2.hashCode ());
	      
	      
	      System.out.println (elements);
	      System.exit (0);
	    }
	  
	}
      
      return result;
    }    
  }
}
