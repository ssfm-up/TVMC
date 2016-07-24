package edu.toronto.cs.util;

import java.util.*;

public abstract class NamingScheme
{
  public abstract Object getName (Object name);
  public abstract Iterator getIterator ();

  public static class ToupleNumberingScheme extends NamingScheme
  {
    Map rootMap;
    int n =0;

    public ToupleNumberingScheme ()
    {
      rootMap = whatMapWeUse ();
    }

    // NB The Iterator iterates over INPUTS -- i.e., the touples named.
    public Iterator getIterator ()
    {
      // XXX Note that this will fail if you've been labelling maps; to get around this, you need to somewhere note how far down you go... Perhaps by subclassing Map. I, however, really can't be bothered... :-)
      Set whatsits; 
      whatsits = new HashSet ();
      getIterator (whatsits, new ArrayList (), rootMap);
      return whatsits.iterator ();
    }

    // XXX Bad method name. It doesn't actually get an iterator, but
    // rather builds a set of all possible input lists (touples), in
    // the variable lists. Recursive, hence the odd collection of
    // variables. Bit ugly, really; there should perhaps be a
    // descriptively named wrapper method.
    protected void getIterator (Set lists, List yourlist, Object map)
    {
      if (!(map instanceof Map))
	{
	  lists.add (yourlist);
	  return;
	}
      Set mapkeys = ((Map)map).keySet ();
      for (Iterator it = mapkeys.iterator (); it.hasNext ();)
	{
	  List tlist = (ArrayList)((ArrayList)yourlist).clone ();
	  Object o = it.next ();
	  tlist.add (o);
	  getIterator (lists, tlist, ((Map)map).get (o));
	}
    } 

    public Object getName (Object name)
    {
      List temp = new ArrayList ();
      temp.add (name);
      return getName (rootMap, temp);
    }

    public Object getName (List target)
    {
      return getName (rootMap, target);
    } 
    
    protected Object getName (Object map, List target)
    {
      // If we run out of one or the other...
      if (target.size () == 0 || !(map instanceof Map))
	return map;
      Object o = target.get (0);
      target.remove (0);
      Object m = ((Map)map).get (o);
      if (m == null)
	{
	  if (target.size () == 0)
	    ((Map)map).put (o, ""+n++);
	  else
	    ((Map)map).put (o, whatMapWeUse ());
	}
      return getName (((Map)map).get (o), target);
    }
    
  }

  // Factory method
  public Map whatMapWeUse ()
  {
    return new HashMap ();
  }
      
  public static class QuickNumberingScheme extends NamingScheme
  {
    int num = 0;
    Map names;
    
    public QuickNumberingScheme ()
    {
      names = new HashMap ();
    }

    public Iterator getIterator ()
    {
      return names.values ().iterator ();
      
    }
    
    
    public Object getName (Object name)
    {
      if (names.get (name) != null)
	return names.get (name);
      int n = num ++;
      names.put (name, new String (""+n));
      return ""+n;
      
    }
  }
}











