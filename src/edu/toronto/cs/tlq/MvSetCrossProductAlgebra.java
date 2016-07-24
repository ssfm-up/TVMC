package edu.toronto.cs.tlq;

// -- an attempt at MvSet based cross product algebra

import java.util.*;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;


public class MvSetCrossProductAlgebra implements DeMorganAlgebra
{
  
  MvSet bot = null;
  MvSet top = null;


  CrossProductValue cpTop = new CrossProductValue (Collections.EMPTY_LIST)
    {
      public boolean equals (Object o)
      {
	return this == o;
      }
      public int hashCode ()
      {
	return System.identityHashCode (this);
      }
      
    };

  CrossProductValue noValue = new CrossProductValue (Collections.EMPTY_LIST)
    {
      public boolean equals (Object o)
      {
	return this == o;
      }
      public int hashCode ()
      {
	return System.identityHashCode (this);
      }
    };
  
  CrossProductValue cpBot;

  Map uniqueTable;

  public MvSetCrossProductAlgebra (MvSetFactory factory)
  {
    this ();
    setMvSetFactory (factory);
  }
  
  protected MvSetCrossProductAlgebra ()
  {
    uniqueTable = new HashMap ();
    cpBot = makeUnique (new ArrayList ());
  }

  public void setMvSetFactory (MvSetFactory factory)
  {
    bot = factory.bot ();
    top = factory.top ();
  }
  
  

  public AlgebraValue getValue (String name)
  {
    throw new UnsupportedOperationException ();
  }
  public AlgebraValue getValue (int id)
  {
    throw new UnsupportedOperationException ();
  }
  
  


  // creates a new value with v at position idx, and bot everywhere else
  public CrossProductValue newValue (int idx, MvSet v)
  {
    List values = new ArrayList ();
    for (int i = 0; i < idx; i++) values.add (bot);
    values.add (v);
    return makeUnique (values);
  }
  

  private CrossProductValue makeUnique (List values)
  {
    return makeUnique (new CrossProductValue (values));
  }
  

  int count = 0;
  private CrossProductValue makeUnique (CrossProductValue v)
  {
    CrossProductValue result = (CrossProductValue)uniqueTable.get (v);
    if (result == null)
      {
	System.out.println ("CPAlgebra: new value: " + (++count));
	result = v;
	uniqueTable.put (result, result);
      }
    
    
    return result;
  }

  public AlgebraValue meet (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    if (_v1 == cpTop) return _v2;
    if (_v2 == cpTop) return _v1;
    
    CrossProductValue v1 = (CrossProductValue)_v1;
    CrossProductValue v2 = (CrossProductValue)_v2;
    
    // -- min (v1.size (), v2.size ())
    int length = v1.size () > v2.size () ? v2.size () : v1.size ();
    
    List values = new ArrayList ();
    for (int i = 0; i < length; i++)
      values.add (v1.getValue (i).and (v2.getValue (i)));
    return makeUnique (values);    
  }
  
  public AlgebraValue join (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    if (_v1 == cpTop || _v2 == cpTop) return cpTop;
    
    CrossProductValue v1 = (CrossProductValue)_v1;
    CrossProductValue v2 = (CrossProductValue)_v2;

    List values = new ArrayList ();
    
    // -- length = max (v1.size (), v2.size ())
    int length = v1.size () > v2.size () ? v1.size () : v2.size ();
    
    for (int i = 0; i < length; i++)
      values.add (v1.getValue (i).or (v2.getValue (i)));
    return makeUnique (values);
  }
  
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return leq (v2, v1);
  }
  
  public AlgebraValue leq (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return cpTop;
    CrossProductValue v1 = (CrossProductValue)_v1;
    CrossProductValue v2 = (CrossProductValue)_v2;
    
    if (v1.size () > v2.size ()) return cpBot;

    int length = v1.size ();

    CrossProductValue result = cpTop;
    for (int i = 0; i < length; i++)
      if (!v1.getValue (i).leq (v2.getValue (i)).equals (top))
	{
	  result = cpBot;
	  break;
	}

    return result;
  }
  
  public AlgebraValue bot ()
  {
    return cpBot;
  }
  public AlgebraValue top ()
  {
    return cpTop;
  }
  public AlgebraValue noValue ()
  {
    return noValue;
  }
  
  public AlgebraValue neg (AlgebraValue v)
  {
    if (v == cpTop) return cpBot;
    else if (v == cpBot) return cpTop;

    throw new UnsupportedOperationException ("Negation is only supported " + 
					     "for the boolean subalgebra");
  }
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    throw new UnsupportedOperationException ("-> is not supported");
  }
  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return v1 == v2 ? cpTop : cpBot;
  }
  

  public int size ()
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  public IntIterator carrierSetId () 
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  public Collection carrierSet ()
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  
  
  public Set getJoinIrredundant (BitSet values)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  
  public Set getMeetIrredundant (BitSet values)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  
  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  

  

  public class CrossProductValue extends AlgebraValue
  {
    List values;

    public CrossProductValue (List _values)
    {
      super (MvSetCrossProductAlgebra.this, "", -1);
      setValues (_values);
    }
    
    private void setValues (List v)
    {
      assert v.isEmpty () ? true : bot != null;
      
      // -- remove all 'bot' values from the end of the list
      for (ListIterator it = v.listIterator (v.size ()); 
	   it.hasPrevious ();)
	{
	  if (it.previous ().equals (bot))
	    it.remove ();
	  else 
	    break;
	}
      
      values = v;
    }
    

    public List getValues ()
    {
      return values;
    }
    
    public int size ()
    {
      return values.size ();
    }
    
    public MvSet getValue (int i)
    {
      if (i >= values.size ()) return bot;
      return (MvSet) values.get (i);
    }
    

    public boolean equals (Object o)
    {
      if (o == null) return false;
      if (o == this) return true;
      if (!(o instanceof CrossProductValue)) return false;
      return equals ((CrossProductValue)o);
    }
    public boolean equals (CrossProductValue v)
    {
      return values.equals (v.getValues ());
    }

    public int hashCode ()
    {
      long hash = 0;
      
      for (int i = 0; i < values.size (); i++)
	hash += (i + values.get (i).hashCode ()) * Primes.getPrime (i);
      
      return (int)hash;
    }
    
    public String toString ()
    {
      if (getName () != null && getName ().length () > 0)
	return getName ();
      return values.toString ();
    }
    
    
    
  }
  


  // -- tester method
  public static void main (String[] args)
  {
    MvSetCrossProductAlgebra algebra = new MvSetCrossProductAlgebra ();
    
  }
  
  
}
