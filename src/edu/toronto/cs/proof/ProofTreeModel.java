package edu.toronto.cs.proof;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class ProofTreeModel implements TreeModel
{
  ProofStep rootNode;
  
  public ProofTreeModel (ProofStep _rootNode)
  {
    rootNode = _rootNode;
  }
  
  public void addTreeModelListener (TreeModelListener l)
  {
  }
  
  public Object getChild (Object parent, int index)
  {
    ProofStep proofStep = (ProofStep)parent;
    return proofStep.getAntecedents ().get (index);
  }
  
  public int getChildCount (Object parent)
  {
    return ((ProofStep)parent).getAntecedents ().size ();
  }

  public int getIndexOfChild (Object parent, Object node)
  {
    return ((ProofStep)parent).getAntecedents ().indexOf (node);
  }
  
  public Object getRoot ()
  {
    return rootNode;
  }
  
  public boolean isLeaf (Object node)
  {
    return ((ProofStep)node).getAntecedents ().isEmpty ();
  }
  
  public void removeTreeModelListener (TreeModelListener l)
  {
  }
  
  public void valueForPathChanged (TreePath path, Object newValue)
  {
  }
  
  
  
  
}
