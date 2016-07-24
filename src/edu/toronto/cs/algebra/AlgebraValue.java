package edu.toronto.cs.algebra;

import java.util.Arrays;

/***
 *** A lattice value
 ***/
public class AlgebraValue 
{
  // -- opaque int id of this lattice element
  int id;
  // -- a name for this lattice element
  String name;
  

  // -- parent lattice to which this element belongs
  IAlgebra parentAlgebra;
  

  public AlgebraValue (IAlgebra _parentAlgebra, String _name, int _id)
  {
    parentAlgebra = _parentAlgebra;
    name = _name;
    id = _id;
    
  }

  public String toString ()
  {
    return (name != null) ? name : super.toString ();
  }


  // --- getter methods
  public int getId ()
  {
    return id;
  }
  public String getName ()
  {
    return name;
  }
  // XXX this should not be public!
  public void setName (String v)
  {
    name = v;
  }
  
  public IAlgebra getParentAlgebra ()
  {
    return parentAlgebra;
  }

  // -- setter methods
  public void setId (int v)
  {
    id = v;
  }
  


  // -- object identity 
  public boolean equals (Object o)
  {
    if (! (o instanceof AlgebraValue)) return false;
    return equals ((AlgebraValue)o);
  }
    
  public boolean equals (AlgebraValue v)
  {
    // -- two AlgebraValues are identical if they come from 
    // -- the same Algebra and have the same id
    return v.getParentAlgebra () == getParentAlgebra () &&
      v.getId () == getId ();
  }


  // -- lattice operations
  public AlgebraValue meet (AlgebraValue v)
  {
    return getParentAlgebra ().meet (this, v);
  }
  public AlgebraValue join (AlgebraValue v)
  {
    return getParentAlgebra ().join (this, v);
  }
  public AlgebraValue neg ()
  {
    return getParentAlgebra ().neg (this);
  }
  public AlgebraValue impl (AlgebraValue v)
  {
    return getParentAlgebra ().impl (this, v);
  }
  
  

  public AlgebraValue eq (AlgebraValue v)
  {
    return getParentAlgebra ().eq (this, v);
  }
  public AlgebraValue leq (AlgebraValue v)
  {
    return getParentAlgebra ().leq (this, v);
  }
  public AlgebraValue geq (AlgebraValue v)
  {
    return getParentAlgebra ().geq (this, v);
  }


  public boolean isTop ()
  {
    return getParentAlgebra ().top ().eq (this) == getParentAlgebra ().top ();
  }
  public boolean isBot ()
  {
    return getParentAlgebra ().bot ().eq (this) == getParentAlgebra ().top ();
  }
  public boolean isNoValue ()
  {
    return getParentAlgebra ().noValue () == this;
  }
  
  

  // -- returns join decomposition of a lattice value
  public AlgebraValue[] joinDecomposition ()
  {
    return getParentAlgebra().joinDecomposition (this);
  }

  // static methods for dealing with arrays
  public static AlgebraValue[] newVector(IAlgebra pa, int l) 
  {
    AlgebraValue r[] = new AlgebraValue[l];
    Arrays.fill(r, pa.noValue());
    return r;
  }

  public static AlgebraValue[] renameArgs(AlgebraValue[] v, int[] rmap)
  {
    IAlgebra a = v[0].getParentAlgebra();
    AlgebraValue rv[] = newVector(a, v.length);
    for (int i=0; i<v.length; i++)
	if (!v[i].equals(a.noValue()))
	  rv[rmap[i]] = v[i];
    
    return rv;
  }
  
  public static AlgebraValue[] delta(AlgebraValue[] x, AlgebraValue[] y)
  {
    if (x.length != y.length) 
      throw new RuntimeException("Lengths must match");
    
    AlgebraValue rv[] = newVector(x[0].getParentAlgebra()
				  ,x.length);
    for (int i=0; i< x.length; i++)
      if (!y[i].equals(x[i]))
	rv[i] = y[i];
    
    return rv;
  }

  public static String toString(AlgebraValue[][] vecs)
  {
    StringBuffer sb = new StringBuffer();

    for (int i=0; i<vecs.length; i++)
      sb.append(toString(vecs[i])+"\n");
    
    return sb.toString();
  }
  
  public static String toString(AlgebraValue[] vec)
  {
    StringBuffer sb = new StringBuffer("{");
    
    for (int i=0; i< vec.length; i++)
      {
	sb.append(vec[i].toString());
	if (i == vec.length-1)
	  sb.append("}");
	else
	  sb.append(";");
      }
    
    return sb.toString();
   
    
  }

  
}
