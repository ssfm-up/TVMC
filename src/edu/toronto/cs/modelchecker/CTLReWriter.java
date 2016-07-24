package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.*;

// -- rewrites a CTL tree
public interface CTLReWriter
{
  CTLNode rewrite (CTLNode ctl);
}
