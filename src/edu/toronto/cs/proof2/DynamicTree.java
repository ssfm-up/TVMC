package edu.toronto.cs.proof2;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

public class DynamicTree extends JPanel 
{
  static protected DefaultMutableTreeNode rootNode;
  static protected DefaultTreeModel treeModel;
  static protected JTree tree;
  static private Toolkit toolkit = Toolkit.getDefaultToolkit();

  static protected Color[] palette = new Color[32];
  static protected int currentColor = 0;
  static protected Map statesToColors = new HashMap();
  
  
  public DynamicTree (ProofStep proofStep) 
  {
    rootNode = new DefaultMutableTreeNode (proofStep);
    rootNode.setUserObject (proofStep);
    
    treeModel = new DefaultTreeModel (rootNode);
    treeModel.addTreeModelListener 
      (new TreeModelListener ()
	{
	  public void treeNodesChanged(TreeModelEvent e) 
	  {
	    DefaultMutableTreeNode node =  (DefaultMutableTreeNode)
	      e.getTreePath ().getLastPathComponent ();

	    /*
	     * If the event lists children, then the changed
	     * node is the child of the node we've already
	     * gotten.  Otherwise, the changed node and the
	     * specified node are the same.
	     */
	    try {
	      int index = e.getChildIndices () [0];
	      node = (DefaultMutableTreeNode)node.getChildAt (index);
	    } catch (NullPointerException exc) {}

	    System.out.println("The user has finished editing the node.");
	    System.out.println("New value: " + node.getUserObject());
	  }
	  public void treeNodesInserted(TreeModelEvent e) 
	  {}
	  public void treeNodesRemoved(TreeModelEvent e) 
	  {}
	  public void treeStructureChanged(TreeModelEvent e) 
	  {}
	});

    tree = new JTree (treeModel);
    tree.setEditable (true);
    tree.getSelectionModel ().setSelectionMode 
      (TreeSelectionModel.SINGLE_TREE_SELECTION);
    // BD: added rendered
    tree.setCellRenderer(new FormulaRenderer());
    
    tree.setShowsRootHandles (true);

    // -- Do we need GridLayout here?!
    setLayout(new GridLayout(1,0));
    add(new JScrollPane (tree));
	
	
    //add listener
    tree.addTreeSelectionListener (new TreeSelectionListener ()
      {
	public void valueChanged (TreeSelectionEvent evt)
	{
	  // what is this doing here? -BD
	  ProofStep selectedStep ;
	  
	  DefaultMutableTreeNode node = 
	    (DefaultMutableTreeNode)tree.getLastSelectedPathComponent ();
	  
	  if (node == null || node.getLevel () == 0)
	    return;

	  ProofStep clickedNode = (ProofStep)node.getUserObject ();
	  System.out.println("I am expanding a node of type "+clickedNode.getClass());
	  //CTLProver.expandFully (clickedNode);
	  throw new RuntimeException ("THIS IS DEAD CODE");
	}
      });
	

  }

  /** Remove all nodes except the root node. */
  public void clear () 
  {
    rootNode.removeAllChildren ();
    treeModel.reload ();
  }

  
  static public DefaultMutableTreeNode getRootNode ()
  {
    return rootNode;
  }
  


  /** Remove the currently selected node. */
  static public void removeCurrentNode() 
  {
    TreePath currentSelection = tree.getSelectionPath ();
    if (currentSelection != null) 
      {
	DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
	  currentSelection.getLastPathComponent ();
	if (currentNode.getParent () != null) 
	  {
	    treeModel.removeNodeFromParent (currentNode);
	    return;
	  }
      }
    // Either there was no selection, or the root was selected.
    toolkit.beep();
  }

    

  /** Add child to the currently selected node. */
  public static DefaultMutableTreeNode addObject (Object child) 
  {

    TreePath parentPath = tree.getSelectionPath ();
    DefaultMutableTreeNode parentNode;

    if (parentPath == null) 
      parentNode = getRootNode ();
    else 
      parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

    return addObject(parentNode, child, true);
  }

  public static DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
						 Object child) 
  {
    return addObject(parent, child, false);
  }

  public static DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
						 Object child, 
						 boolean shouldBeVisible) 
  {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode (child);

    if (parent == null) 
      parent = rootNode;

    treeModel.insertNodeInto (childNode, parent, parent.getChildCount());

    // Make sure the user can see the lovely new node.
    // XXX This causes erratic jumping of the tree so something 
    // XXX has to be done about it.
    if (shouldBeVisible) 
      tree.scrollPathToVisible (new TreePath (childNode.getPath ()));
    
    return childNode;
  }

}
