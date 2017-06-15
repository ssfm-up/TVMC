package edu.toronto.cs.tlq;

import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.util.IntIterator.*;
import java.util.*;


/**
 * class <code>UpSetAlgebra</code> represents a large lattice of upsets
 * Each element is encoded as an ordered list of integers, each integer
 * corresponding to a minimal element of some lattice.
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */
public class UpSetAlgebra implements DeMorganAlgebra
{
  // -- top, bot and noValue elements
  UpSetValue top;
  UpSetValue bot;
  UpSetValue  noValue;

  UniqueTable uniqueTable;
  
  
  /*** DeMorganAlgebra interface */
  public AlgebraValue noValue ()
  {
    return noValue;
  }
  public AlgebraValue getValue (String name)
  {
    if (name.equals ("T")) return top;
    if (name.equals ("F")) return bot;
    
    return noValue ();
  }
  public AlgebraValue getValue (int id)
  {
    if (id == top.getId ()) return top;
    if (id == bot.getId ()) return bot;

    return uniqueTable.getValueById (id);    
  }
  
  public AlgebraValue meet (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    
    UpSetValue v1 = (UpSetValue)_v1;
    UpSetValue v2 = (UpSetValue)_v2;

    if (v1 == bot || v2 == bot) return bot;
    if (v1 == top) return v2;
    if (v2 == top) return v1;
    
    int[] l = v1.getData ();
    int[] r = v2.getData ();

    int[] product = new int [l.length * r.length];
    
    // -- XXX can we do better?
    for (int i = 0; i < l.length; i++)
      for (int j = 0; j < r.length; j++)
	product [(i * r.length) + j] = latJoin (l [i], r [j]);
    return min (sort (product));
  }

  public AlgebraValue join (AlgebraValue _v1, AlgebraValue _v2)
  {
    if (_v1 == _v2) return _v1;
    
    UpSetValue v1 = (UpSetValue)_v1;
    UpSetValue v2 = (UpSetValue)_v2;

    if (v1 == top || v2 == top) return top;
    if (v1 == bot) return v2;
    if (v2 == bot) return v1;

    
    // XXX needs more work
    return min (v1.getData (), v2.getData ());
  }

  public AlgebraValue neg (AlgebraValue v)
  {
    if (v == top) return bot;
    else if (v == bot) return top;

    throw new UnsupportedOperationException ("Negation is only supported " + 
					     "for the boolean subalgebra");
  }
  public AlgebraValue impl (AlgebraValue v1, AlgebraValue v2)
  {
    throw new UnsupportedOperationException ("-> is not supported");
  }
  public AlgebraValue top ()
  {
    return top;
  }
  public AlgebraValue bot ()
  {
    return bot;
  }
  public AlgebraValue eq (AlgebraValue v1, AlgebraValue v2)
  {
    return v1 == v2 ? top : bot;
  }
  public AlgebraValue leq (AlgebraValue _v1, AlgebraValue _v2)
  {
    UpSetValue v1 = (UpSetValue)_v1;
    UpSetValue v2 = (UpSetValue)_v2;

    if (v1 == v2) return top;
    
    return leq (v1.getData (), v2.getData ());
  }
  public AlgebraValue geq (AlgebraValue v1, AlgebraValue v2)
  {
    return leq (v2, v1);
  }
  public int size ()
  {
    // XXX This is very very wrong but makes MDDMvSetFactory happy
    // XXX Need a better solution
    return 2;
  }
  public IntIterator carrierSetId () 
  {
    //throw new UnsupportedOperationException ("This is not supported");
    // XXX same comment as size ()
    return new RangeIterator (0, 2);
  }
  public Collection carrierSet ()
  {
    throw new UnsupportedOperationException ("This is not supported");
  }
  public AlgebraValue[] joinDecomposition (AlgebraValue v)
  {
    return null;
  }
  
  
  
  public void renew()
  {
    uniqueTable = new UniqueTable (this);
    
    bot = getValue (new int [0]);
    bot.setName ("F");
    top = getValue (new int[] {0});    
    top.setName ("T");
    noValue = new UpSetValue (this);

  }

  /**
   *  <code>UpSetAlgebra</code> 
   * Constructs an UpSet lattice with _mintermSize minterm elements
   *
   */
  public UpSetAlgebra ()
  {
    uniqueTable = null;
    renew();
  }

  // -- returns an upset corresponding to the minterm number 'a'
  public UpSetValue getUpMinTerm (int a)
  {
    assert a < 32 : "Currently we only support up to 32 minterms";
    if (a > 31) throw new UnsupportedOperationException ("enable assertions");
    return getUpSetElement (1 << a);
  }

  public UpSetValue getUpSetElement (int a)
  {
    return getValue (new int[]{a});
  }


  /***
   *** if a[i] | b[j] == a [i]
   *** then every bit that a has off, b has off as well
   *** thus if b has a bit on, then a has the same bit on
   *** that means that (a | b = a) -> a >= b
   *** and so if a < b -> (a | b != a)
   ***/

  public UpSetValue leq (int[] a, int[] b)
  {
    // -- a <= b iff for every a[i] there exits b[j] s.t. a[i] >= b[j]

    for (int i = 0; i < a.length; i++)
      {
	boolean found = false;
	for (int j = 0; j < b.length; j++)
	  if ((a [i] | b [j]) == a [i])
	    {
	      // -- found our b[j], no reason to search further
	      found = true;
	      break;
	    }
	// -- see the comment above why this will work
	  else if (a [i] < b [j]) break;
	
	// -- if found is false then we found an element a[i] which is not
	// -- above any element b[j]
	if (!found) return bot;
      }
    return top;
  }



  /*** Internal use ***/

  UpSetValue getValue (int[] v)
  {
    return getValue (v, v.length);
  }
  UpSetValue getValue (int[] v, int len)
  {
    return uniqueTable.getValue (v, len);
  }  
  

  static int[] sort (int[] a)
  {
    Arrays.sort (a);
    return a;
  }
  
  // -- our own sort routine, to be replaced by the best sort for the job
  static int[] sort (int[] a, int fromIdx, int toIdx)
  {
    // -- this is quicksort which may not be the best choice if most
    // -- of our elements are already sorted
    Arrays.sort (a, fromIdx, toIdx);
    return a;
  }
  

  // -- we don't represent the underlying lattice directly, so we need
  // -- some helper methods for it
  static int latMeet (int a, int b)
  {
    return a & b;
  }
  static int latJoin (int a, int b)
  {
    return a | b;
  }
  
  static boolean latEq (int a, int b)
  {
    return a == b;
  }
  
  static boolean latLeq (int a, int b)
  {
    // -- a <= b iff a /\ b = a
    return (a & b) == a;
  }
  
  static boolean latGeq (int a, int b)
  {
    // -- a >= b iff a \/ b = a
    return (a | b) == a;
  }
  

  // basic optimization equation
  // a < b -> !latGeq (a, b)
  // latGeq (a, b) -> a >= b

  // a > b -> !latLeq (a, b)

  UpSetValue min (int[] data)
  {

    /*** check if we can quickly bail out */
    // -- 1) min of an empty array, is empty array, which is bot
    if (data.length == 0) return bot;
    // -- 2) min of an array of one element is just this array,
    // --    moreover if the element is 0, then this is top
    if (data.length == 1)
      return data [0] == 0 ? top : getValue (data, 1);

    /*** no we have to do the work */

    // -- assume that data is sorted
    int[] result = new int [data.length];
    int len = 1;

    // -- first element always gets copied
    result [0] = data [0];

    // now for the rest elements
    for (int i = 1; i < data.length; i++)
      {
	// -- skip identical elements. (remember, array is sorted)
	if (data [i] == data [i - 1]) continue;
	
	// -- we need to decide if we have to copy data [i] now
	// -- we don't copy it only if we have something in our
	// -- result which is below it
	boolean remove = false;
	for (int j = len - 1; j >= 0; j--)
	  // -- found something below data [i], so skip data [i]
	  if (latLeq (result [j], data [i]))
	    {
	      remove = true;
	      break;
	    }
	// copy the value if it is not removed
	if (!remove)
	  result [len++] = data [i];
      }

//     System.out.println ("Min of " + ArrayUtil.toString (data) + 
// 			" is " + ArrayUtil.toString (result, len));

    // -- our final result is sorted already so no need to resort
    return getValue (result, len);
  }
  

  // -- min of two sorted arrays, this is basically min of a single sorted
  // -- array together with a merge sort
  UpSetValue min (int[] data1, int[] data2)
  {

    // -- check if we have a simple case where no work is needed
    if (data1.length == 0 && data2.length == 0) 
      return bot;
    else if (data1.length == 0)
      return getValue (data2, data2.length);
    else if (data2.length == 0)
      return getValue (data1, data1.length);
    
    
    // -- no, we have to do the work
    int[] result = new int [data1.length + data2.length];
    int len = 0;
    
    int pos1 = 0;
    int pos2 = 0;
    

    // -- while there are unprocessed elements
    while (pos1 < data1.length || pos2 < data2.length)
      {
	int element;
    
	// -- pick the smallest element between data1 and data2
	// -- if either data1 or data2 is exhausted then trivial
	// -- otherwise pick the smallest between the current elements
	if (pos1 == data1.length)
	  element = data2 [pos2++];
	else if (pos2 == data2.length)
	  element = data1 [pos1++];
	else if (data1 [pos1] == data2 [pos2])
	  {
	    // -- if an element belongs to both data1 and data2 
	    // -- use it from data1 and ignore data2.
	    // -- more over in this case we don't need to check 
	    // -- if the element is irredundant, it can be proven that 
	    // -- it is
	    result [len++] = data1 [pos1++];
	    pos2++;
	    continue;
	  }
	else 
	  element = 
	    data1 [pos1] < data2 [pos2] ? data1 [pos1++] : data2 [pos2++];
    
	boolean remove = false;
	// -- check if this element should be part of the irreduntant set
	for (int i = 0; i < len; i++)
	  if (latLeq (result [i], element))
	    {
	      remove = true;
	      break;
	    }
    
	if (!remove) result [len++] = element;
      }


    return getValue (result, len);
  }
  

  public Set getJoinIrredundant (BitSet values)
  {
    // XXX yet another n^2 algorithm
    Set res = new HashSet();
    // --  clone since we are doing a destructive operation
    BitSet result = (BitSet)values.clone ();
    
    int bitLength = values.length ();
    for (int i = 0; i < bitLength; i++)
      {
	// -- skip over empty bits
	if (!values.get (i)) continue;

	// -- got a set bit, compare it to the rest
	for (int j = 0; j < result.length (); j++)
	  // -- if bit 'j' is set, and current value is less then j
	  // -- then current value is redundant and should be removed
	  if (i != j && result.get (j) 
	      && getValue (i).leq (getValue (j)) == top)
	    result.clear (i);
      }
    for (int i=0; i< bitLength; i++)
      if (result.get(i))
	res.add(getValue(i));
    return res;
  }
  
  public Set getMeetIrredundant (BitSet values)
  {
    // XXX yet another n^2 algorithm
    
    Set res = new HashSet ();

    // --  clone since we are doing a destructive operation
    BitSet result = (BitSet)values.clone ();
    
    int bitLength = values.length ();
    for (int i = 0; i < bitLength; i++)
      {
	// -- skip over empty bits
	if (!values.get (i)) continue;

	// -- got a set bit, compare it to the rest
	for (int j = 0; j < result.length (); j++)
	  // -- if bit 'j' is set, and current value is greater then j
	  // -- then current value is redundant and should be removed
	  if (i != j && result.get (j) 
	      && getValue (j).leq (getValue (i)) == top)
	      result.clear (i);
      }

    for (int i=0; i< bitLength; i++)
      if (result.get(i))
	res.add(getValue(i));
    return res;

  }
  

  static class UpSetValue extends AlgebraValue
  {
    int[] data;
    int len;


    public UpSetValue (UpSetAlgebra parentAlgebra, 
		       String name, int id, int[] _data, int _len)
    {
      super (parentAlgebra, name, id);
      data = _data;
      len = _len;
    }
    
    public UpSetValue (IAlgebra algebra)
    {
      super (algebra, "-", -1);
      data = new int [0];
      len = 0;
    }

    public int[] getData ()
    {
      return data;
    }
    public int getLength ()
    {
      return len;
    }
    
    public void setData (int[] v)
    {
      data = v;
    }
    public void setLength (int v)
    {
      len = v;
    }
    
    public boolean equals (Object o)
    {
      if (o == null) return false;
      if (o.getClass () == UpSetValue.class)
	return equals ((UpSetValue) o);
      return false;
    }
    
    public boolean equals (UpSetValue v)
    {
      // -- length must be the same
      if (len != v.len) return false;
      
      for (int i = 0; i < v.len; i++)
	if (data [i] != v.data [i]) return false;
      
      return true;
    }

    public static final long[] primes = Primes.primes;

    public int hashCode ()
    {
      long hash = 0;

      int hashLen = len > primes.length ? primes.length : len;
      
      for (int i = 0; i < hashLen; i++)
	hash += data [i] * primes [i];
      
      return (int)hash;
    }
    
    public String toString ()
    {
//       String result = "";

//       if (getName () != null && getName ().length () > 0)
// 	result = getName () + " - ";

//       return result + ArrayUtil.toString (data, len);
      if (getName () != null && getName ().length () > 0) return getName ();
      return ArrayUtil.toString (data, len);
    }
    
    
  }
  

  class UniqueTable 
  {
    Map map;

    // -- ordered set of elements so that we can 
    // -- get an element by id
    List elements;
    

    UpSetValue lookup;
    UpSetAlgebra algebra;


    public void clear()
    {
      elements.clear();
      map.clear();
      
    }
    
    public UniqueTable (UpSetAlgebra _algebra)
    {
      algebra = _algebra;
      lookup = new UpSetValue (algebra);
      elements = new ArrayList ();
      map = new HashMap ();
      
    }

    UpSetValue getValueById (int id)
    {
      return (UpSetValue)elements.get (id);
    }
    

    int count = 0;
    UpSetValue getValue (int[] data, int len)
    {
      lookup.setData (data);
      lookup.setLength (len);
      
      UpSetValue result = (UpSetValue)map.get (lookup);
      
      if (result == null)
	{
	  //System.out.println ("UpSetAlgebra: new value: " + (++count));
	  int[] newData = new int [len];
	  System.arraycopy (data, 0, newData, 0, len);
	  result = new UpSetValue (algebra, "", elements.size (), 
				   newData, len);
	  elements.add (result);
	  map.put (result, result);
	}
      return result;
    }    
  }  

  /****
   **** Some tester methods
   ****/
  public static void main (String[] args)
  {
    UpSetAlgebra lat = new UpSetAlgebra ();
    System.out.println ("top" + lat.top ());
    System.out.println ("bot: " + lat.bot ());


    UpSetValue a = lat.getUpSetElement (1);
    UpSetValue b = lat.getUpSetElement (2);
    UpSetValue c = lat.getUpSetElement (3);
    UpSetValue r = (UpSetValue)a.join (b);

    System.out.println ("r: " + r);
    r = (UpSetValue)r.meet (c);
    
    System.out.println ("r: " + r);
    
    UpSetValue v = lat.getUpSetElement (5);
    System.out.println ("Asked for 5: got: " + v);
    v = lat.getUpSetElement (5);
    System.out.println ("Asked for 5: got: " + v);
  }

}
