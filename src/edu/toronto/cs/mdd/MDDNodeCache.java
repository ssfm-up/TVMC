package edu.toronto.cs.mdd;

//import edu.toronto.cs.MDD.MDDCacheEntry;

/** This interface defines a cache for MDDNode operation results.
    I can't specify it here, but all operations should be
    <b>synchronized.</b> */
public interface MDDNodeCache 
{
  

  MDDNode find (Object key, MDDNode m1, MDDNode m2);
  MDDNode find (Object key, MDDNode m);
  
  void insert (Object key, MDDNode m1, MDDNode m2, MDDNode answer);
  void insert (Object key, MDDNode m, MDDNode answer);

  /** explicitly tell cache to clean up old entries. */
  void cleanup();
  //  void resetStats ();
}
