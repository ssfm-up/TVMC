package edu.toronto.cs.proof2;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;

public class ProofStepFactory

{
  public static AlgebraValue[] getStateAsArray(Formula f)
    {
      
      AlgebraValue[] stateArray =  
	(AlgebraValue []) 
	f.getState ().getPreImageArray (f.getValue ().getParentAlgebra().top()).iterator().next();
      
      return stateArray;
    } 

  
  //function to add nodes to the parent node 

  public static void dynamicTree (ProofStep parent, ProofStep children)
  {

    if (children == null)
      return;

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)DynamicTree.getRootNode ();
    
    boolean repeat = true;

    ProofStep initial = parent;

    LinkedList listOfParent = new LinkedList ();

    // create a list of path from root to the parent node

    while (!(initial.equals ((ProofStep)node.getUserObject ())))
      {
	listOfParent.addFirst (initial);
	initial = initial.getParent ();
      }
  
    for (int i =1;i <= listOfParent.size (); i++){
      if (((ProofStep)node.getNextNode ().getUserObject ()).equals ((ProofStep)listOfParent.get (i-1)))
	node = node.getNextNode ();
      else
	node = node.getNextNode ().getNextSibling ();
    }
    
    // add child object to the intial tree.
    DynamicTree.addObject (node, children, true);
    
  }
}


