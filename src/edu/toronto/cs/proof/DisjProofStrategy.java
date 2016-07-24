package edu.toronto.cs.proof;

// this class is responsible, given a set of disjuncts (and their values)
//  for choosing which disjuncts absolutely must be followed.


// the trivial case obviously is two logic values, two disjuncts
//  (a \/ b) where a evaluates to true and b to false; then, clearly,
//  the only decision is to evaluate a.
// If both are true, only one needs to be followed; a simple strategy
//  can just pick the first arbitrarily, but a more sophisticated one
//  can use heuristics based on the structure of a and b.

import java.util.Set;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;

public interface DisjProofStrategy extends ProofStrategy
{
 
}
