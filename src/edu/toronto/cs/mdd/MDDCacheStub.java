package edu.toronto.cs.mdd;

import java.util.*;
import edu.toronto.cs.util.*;


public class MDDCacheStub implements MDDNodeCache 
{

  // XXX This assumes that all uses of the cache are synchronized
  MDDCacheKey lookupKey;
  
  // -- the actual cache -- should be made into a soft data structure
  SoftHashMap cache;

 
  /** Constructor. */
  public MDDCacheStub() 
  {
    //cache = new Hashtable();
    cache = new SoftSoftHashMap ();
    lookupKey = new MDDCacheKey ();
  }

  public MDDNode find (Object key, MDDNode node1, MDDNode node2)
  {
    lookupKey.initialize (key, node1, node2);
    return (MDDNode)cache.get (lookupKey);
  }
  public MDDNode find (Object key, MDDNode node1)
  {
    return find (key, node1, null);
  }

  public void insert (Object key, MDDNode m1, MDDNode m2, MDDNode answer)
  {
    MDDCacheKey cacheKey = new MDDCacheKey (key, m1, m2);
    cache.put (cacheKey, answer);
  }
  
  public void insert (Object key, MDDNode m, MDDNode answer)
  {
    insert (key, m, null, answer);
  }

  public void cleanup()
  {
    // eventually do some cleaning-up
    cache = new SoftSoftHashMap ();
  }


  // -- The cache key
 static class MDDCacheKey 
  {

    // -- at most there are two nodes -- i.e. only binary operations
    // -- we should do something to avoid wasting space of an extra pointer
    MDDNode node1;
    MDDNode node2;
    
    // -- the key, should this actually be stored here?
    Object key;
    
    
    

    public  MDDCacheKey ()
    {
      this (null, null, null);
    }
    
    
    public MDDCacheKey (Object key, MDDNode node1)
    {
      this (key, node1, null);
    }
    
    public MDDCacheKey (Object _key, MDDNode _node1, MDDNode _node2)
    {
      initialize (_key, _node1, _node2);
    }    
    
    void initialize (Object _key, MDDNode _node1, MDDNode _node2)
    {
      node1 = _node1;
      node2 = _node2;
      key = _key;
    }
    
    
    /** Equality test, inherited from Object
	@see java.lang.Object#equals */      
    public boolean equals (Object o)
    {
      if (o.getClass () != MDDCacheKey.class) return false;

      return equals ((MDDCacheKey)o);
    }
    
    public boolean equals (MDDCacheKey otherKey)
    {
      return key == otherKey.key && 
	node1 == otherKey.node1 && 
	node2 == otherKey.node2;
    }
    
    public int hashCode ()
    {
      long hash;

      hash = key.hashCode ();
      
      hash += node1.objectHashCode () * MDDNode.primes [0];
      if (node2 != null)
	hash += node2.objectHashCode () * MDDNode.primes [1];

      return (int)(hash >> 32);
    }
  }

}  // end MDDCacheStub







