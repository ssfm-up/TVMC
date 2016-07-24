package edu.toronto.cs.proof2;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class ProofStepTreeModel implements TreeModel
{
  ProofStep rootNode;
  
  public ProofStepTreeModel (ProofStep _rootNode)
  {
    rootNode = _rootNode;
  }
  
  public void addTreeModelListener (TreeModelListener l)
  {
  }
  
  public Object getChild (Object parent, int index)
  {
    TreeProofStep proofStep = (TreeProofStep)parent;
    return proofStep.getChild (index);

  }
  
  public int getChildCount (Object parent)
  {
    if (parent instanceof LeafProofStep) return 0;
    return ((TreeProofStep)parent).getChildLength ();
  }

  public int getIndexOfChild (Object parent, Object node)
  {
    TreeProofStep step = (TreeProofStep)parent;
    
    for (int i = 0; i < step.getChildLength (); i++)
      if (step.getChild (i) == node) return i;

    return -1;
  }
  
  public Object getRoot ()
  {
    return rootNode;
  }
  
  public boolean isLeaf (Object node)
  {
    return  node instanceof LeafProofStep || 
      ((TreeProofStep)node).getChildLength () == 0;
  }
  
  public void removeTreeModelListener (TreeModelListener l)
  {
  }
  
  public void valueForPathChanged (TreePath path, Object newValue)
  {
  }
}
