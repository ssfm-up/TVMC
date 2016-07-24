package edu.toronto.cs.ctl;

import java.util.*; // we *still* love java.util

/** A CTLTemplate
 */
public class CTLTemplate 
{
  CTLNode node;
  Set match; // and game?

  public CTLTemplate (CTLNode _node, Set _match)
  {
    node = _node;
    match = new HashSet(_match);
  }
  
  public Map matchCTLNode(CTLNode _node)
  {
    Map mymap = new HashMap();
    if (matchInternal(mymap, _node, node))
      return mymap;
    else
      return null;
  }

  public boolean matchInternal(Map map, CTLNode _node, CTLNode matchnode)
  {
    System.out.println("Checking "+_node.toString()+
		       " against "+matchnode.toString()+" map="+map.toString());
    
    // handle atom prop nodes specially
    if (matchnode.getClass() == CTLAtomPropNode.class)
      {
	CTLAtomPropNode mnode = (CTLAtomPropNode) matchnode;
	String wildcard = mnode.getName();
	
	if (match.contains(wildcard))
	  // it is a node to be matched
	  if (map.containsKey(wildcard))
	    // it has already been matched
	    return (_node == (CTLNode) map.get(wildcard));
	
	  else
	    // match it, put it in the map, and move on
	    {
	      map.put(wildcard, _node);
	      return true;
	    }
      } // end if matchnode is a CTLAtomPropNode
    
    if (matchnode.getClass().equals(_node.getClass())) {
	if (matchnode instanceof CTLBinaryNode)
	  return (matchInternal(map, _node.getLeft(), matchnode.getLeft()) &&
		  matchInternal(map, _node.getRight(), matchnode.getRight()));
	
	if (matchnode instanceof CTLUnaryNode)
	  return matchInternal(map, _node.getRight(), matchnode.getRight());
	
    }
    else
      return false;
    
    assert false : "Improper CTL check"; // we should never get here
    
    return false;
    
  }
  
  
  
  
  
}
