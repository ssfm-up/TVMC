package edu.toronto.cs.proof2;

import edu.toronto.cs.ctl.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.Color;

public class NewProofTreeModel implements TreeModel
{
  ProofStep root;
  List listeners;

  
  Color palette[];
  int nextColourToUse;
  
  Map colourMap;
  
  NewProofTreeModel()
  {
  }
  
  // methods to deal with colours
  // XXX belong in util!
  public Color getColourForState(String s)
  {
    // if already mapped, return it
    if (colourMap.containsKey(s))
      
	return (Color) colourMap.get(s);
    else
      {
	Color c = palette[nextColourToUse++];
	if (nextColourToUse == 4)
	  nextColourToUse = 0;
	
	colourMap.put(s, c);
	return c;
      }
    
    
  }
  
  public NewProofTreeModel(ProofStep ps)
  {
    root = ps;
    listeners = new LinkedList();
    palette = new Color[4];
    palette[0] = Color.BLUE;
    palette[1] = Color.CYAN;
    palette[2] = Color.RED;
    palette[3] = Color.MAGENTA;
   
    nextColourToUse = 0;
    colourMap = new HashMap();
  }
  
    /**
     ** Adds a listener for the TreeModelEvent posted after the tree changes.
     **/
    public void addTreeModelListener (TreeModelListener l)
    {
	listeners.add (l);
    }
    
    /**
     ** Returns the child of parent at index index in the parent's
     ** child array.
     **/     
    public Object getChild (Object parent, int index)
    {
	if (parent instanceof TreeProofStep)
	    return ((TreeProofStep) parent).getChild (index);
	else return null;
    }

    /**
     ** Returns the number of children of parent.
     **/     
    public int getChildCount (Object parent)
    {
	if (parent instanceof TreeProofStep)
	    return ((TreeProofStep) parent).getChildLength ();
	else return -1;
    }

    /**
     ** Returns the index of child in parent.
     **/
  public int getIndexOfChild (Object parent, Object child)
  {
    int k=-1;
    
    if (parent instanceof TreeProofStep &&
	child instanceof TreeProofStep){
      TreeProofStep pps = (TreeProofStep) parent;
      for (int i=0; i<pps.getChildLength(); i++)
	if (pps.getChild(i).equals(child))
	  k = i;
    }
    return k;
  }

    /**
     ** Returns the root of the tree.
     **/
    public Object getRoot ()
    {
	return root;
    }

    /**
     ** Returns true if node is a leaf.
     **/
    public boolean isLeaf (Object node)
    {
	if (node instanceof TreeProofStep)
	    return ((TreeProofStep) node).getChildLength () == 0;
	else return (node instanceof LeafProofStep);
	
    }
    
    /**
     ** Removes a listener previously added with addTreeModelListener.
     **/      
    public void removeTreeModelListener (TreeModelListener l)
    {
	listeners.remove (l);
    }
  
    /**
     ** Messaged when the user has altered the value for the item
     ** identified by path to newValue.
     **/     
    public void valueForPathChanged (TreePath path, Object newValue)
    {
	// Victor: at the moment I'm not sure what goes here...
    }

}
