package edu.toronto.cs.gui;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.*;
import javax.swing.tree.TreePath;

import edu.toronto.cs.util.gui.*;
import edu.toronto.cs.proof2.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;

import edu.toronto.cs.grappa.*;
import java.util.*;



public class ProofTreeFrame extends JFrame
{
  ProofStep proofStep;
  StatePresenter statePresenter;

  JTree tree;
  JList stateView;
  MvSetModelChecker mc;
  int count = 1;
  int countLevel = 100;
  int finalCount;
  int level;
  int index;
  boolean expand = true;
  // to store expanded nodes
  HashMap hashMap = new HashMap ();
  static boolean firstTime = true;
  String name;
  boolean expandNode = false;

  java.util.List levelList = new LinkedList ();
  java.util.List treeNodeList = new LinkedList ();
  java.util.List listTreeNode = new LinkedList ();
  

  JComboBox choiceToExpand;
  DefaultMutableTreeNode top1;
  
  public ProofTreeFrame (ProofStep _proofStep, StatePresenter _statePresenter
			 , MvSetModelChecker _mc)
  {
    super ("Proof Window");
    proofStep = _proofStep;
    statePresenter = _statePresenter;
    mc = _mc;
    
    initFrame ();
  }

  public void setProofStep (ProofStep v)
  {
    proofStep = v;
  }
  public void setStatePresenter (StatePresenter v)
  { statePresenter = v; }


  // function to create the whole tree 
  // also sets the corresponding user object.
  
  private void createNode (ProofStep proofStep, DefaultMutableTreeNode top1)
  {
    Formula formula;
    
    if (proofStep.getClass () == TreeProofStep.class)
      {
	for (int i =0; i < ((TreeProofStep)proofStep).getChildLength (); i++){
	  DefaultMutableTreeNode next = new DefaultMutableTreeNode (((TreeProofStep)proofStep).getChild (i));
	  next.setUserObject(((TreeProofStep)proofStep).getChild (i));
	  top1.add (next);
	  createNode (((TreeProofStep)proofStep).getChild (i), next);
	}
      }
  }

  public boolean checkEntry (Object key)
  {
    if (hashMap.get (key) == null)
      return false;
    else 
      return true;
  }
  
  
  public void putInHashMap (Object key)
  {
    hashMap.put (key, new String ("expanded"));
  }
  


  void initFrame ()
  {
    final ProofStepListModel listModel = 
      new ProofStepListModel (statePresenter);

    listModel.setProofStep (proofStep);

    tree = new JTree (new ProofStepTreeModel (proofStep));
    
    top1 = new DefaultMutableTreeNode (proofStep);
    
    // create the whole tree.
    createNode (proofStep,top1);
    
    final JTree proofTree = new JTree (top1);
    

    // -- update the state view whenever new node in a tree is selected
    


    stateView = new JList (listModel);

    String[] possibleValues = { "Expand 2 steps", "Expand 4 steps", "Expand All" };
    
    choiceToExpand = new JComboBox(possibleValues);

    JPanel treePanel = new JPanel ();
    treePanel.setLayout (new BoxLayout (treePanel, BoxLayout.Y_AXIS));
    
    treePanel.add (choiceToExpand);
    treePanel.add (new JScrollPane (proofTree));
    treePanel.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), "Proof"));




    proofTree.addTreeSelectionListener (new TreeSelectionListener ()
      {
	public void valueChanged (TreeSelectionEvent evt)
	{
	  ProofStep selectedStep ;
	  
	  DefaultMutableTreeNode node = (DefaultMutableTreeNode)proofTree.getLastSelectedPathComponent();
	  TreePath selectedPath = proofTree.getSelectionPath ();

	  int maxDepth = node.getDepth ();
	  
	  if (node == null) return;
	  
	  name = node.toString ();
	  // debug -- BD
	  System.out.println("valueChanged - "+node);
	  
	  // just a formula to expand n levels
	  
	  countLevel = maxDepth;
	  
	  if (choiceToExpand.getSelectedIndex ()==0)
	    countLevel = 2;
	  else if (choiceToExpand.getSelectedIndex ()==1)
	    countLevel = 4;
	  
	  // get how many level down this node is
	  level = node.getLevel ();
	  // get at what index does this node occurs among its siblings
	  if (level==0)
	    index = 1;
	  else
	    index = node.getParent ().getIndex (node);
	  
	  count = 0;
	  
	  // till how many levels down to expand when clicked, in this case countLevel
	  finalCount = level + countLevel;
	  
 
	  // find if there is a level down the expanding node
	  // where an Or node is present.
	  findOrLevel (level, index, countLevel, selectedPath, top1);
	  
	  
	  TreeNode root = (TreeNode)proofTree.getModel().getRoot();
	  
	  // Traverse tree from root
	  expandAll(proofTree, new TreePath(root), true);
	}
	


	// function used to find the level of CTLOrNode (if exists)  
	/* param
	   level - at what level down from root the clicked node is
	   index - number among its siblings
	   countLevel - till what level down to expand the node
	   selectedPath - path from root to the clicked node
	   top1         - the initial tree . needed to add the new expanded nodes to the initial tree
	*/

	public void findOrLevel (int level, int index, int countLevel, TreePath selectedPath, DefaultMutableTreeNode top1)
	{
	  Object[] path = selectedPath.getPath ();
	  DefaultMutableTreeNode top = top1;
	  ProofStep orNode;

	  // first find the defaultmutabletree node which is clicked.

	  for (int i=0; i< level; i++){
	    if (top.toString ().equals (path[i].toString ())) 
	      top = top.getNextNode ();
	    else 
	      top = top.getNextSibling ().getNextNode ();
	  }
	  
	  // top now represents the clicked node 

	  if (!top.toString ().equals (path[level].toString ()))
	    top = top.getNextSibling ();


	  // add to list model to get the state view

	  listModel.setProofStep ((ProofStep)top.getUserObject ());

	  callFunction (countLevel -1, top, level, countLevel);
	  
	}
	
	
	public void callFunction (int countLevel, DefaultMutableTreeNode top, int level, int initialCountLevel)
	{
	  DefaultMutableTreeNode top1 = top;
	  int count = countLevel;
	  int childCount = top1.getChildCount ();
	  
	  if (countLevel == 0)
	    return;
	  else {
	    top1 = top1.getNextNode ();
	    if (!(top1 == null)){
	      for (int i = 0; i<childCount; i++) {
		
		if (((ProofStep)top1.getUserObject ()).getFormula ().getConsequent () instanceof CTLOrNode)
		  {
		    // cud not find a better way to get the node :(
		    TreeNode n = top1.getFirstChild ().getParent ();
		    int level1 =  level + (initialCountLevel - countLevel);
		    levelList.add (new Integer (level1));
		    treeNodeList.add (n);
		  }
		else 
		  callFunction (count-1, top1, level, initialCountLevel);
		
		if (childCount > 1)
		  top1 = top1.getNextSibling ();
	      }
	    } 
	  }
	}

  
	// takes a node and a list and return true if the list contains the node
	    
	public boolean checkExist (TreeNode node, java.util.List listTreeNode)
	{
	  TreeNode temp;
	  boolean result = false;
	  for (Iterator i = listTreeNode.iterator (); i.hasNext ();){
	    temp = (TreeNode)i.next ();
	    if (temp.equals (node)) {
	      result = true;
	      break;
	    }
	  }
	  return result;
	}
	
	
	// expands the selected node to 'n' levels down
	// very complicated function ..cant help :(


	private void expandAll(JTree tree, TreePath parent, boolean expand) {
	  System.out.println("expandAll() invoked");
	  
	  if (firstTime) {
	    TreeNode node =(TreeNode) parent.getLastPathComponent ();
	    putInHashMap (node);
	    firstTime = false;
	  }
	  

	  if (count == level - 1) {
	    TreeNode node = (TreeNode) parent.getLastPathComponent ();
		    
	    if (node.getChildCount () >=0) {
	      int i = 0;	
	      for (Enumeration e = node.children (); e.hasMoreElements ();) {
		TreeNode n = (TreeNode)e.nextElement ();
		if ((i == index) && (n.toString ().equals (name))) {
		  index = -100;
		  expandNode = true;
		  if (!checkEntry (node))
		    putInHashMap(node);
		  TreePath path = parent.pathByAddingChild (n);
		  count = count + 1;
		  expandAll (tree,path,true);
		  count = count -1;
		  tree.expandPath (parent);
		  i = i+1 ;
		}
		else {
		  i = i + 1;
		  expandNode = false;
		  TreePath path = parent.pathByAddingChild (n);
		  if (checkEntry (n)) {
		    count = count + 1;
		    expandAll (tree, path, true);
		    count = count - 1;
		  }
		  tree.expandPath (parent);
		}
	      }
	    }
	  }
	  else {
	    int temp = 0;
	    int k;
	    if (count >= level){
	      for (Iterator i = levelList.iterator (); i.hasNext ();){
		k = ((Integer)i.next ()).intValue ();
		if (k == count)
		  listTreeNode.add ((TreeNode)treeNodeList.get (temp));
		temp++;
	      }
	      		
	      if (count != finalCount){
		TreeNode node = (TreeNode) parent.getLastPathComponent ();
		if (node.getChildCount () >= 0) {
		  if (!checkEntry (node) && expandNode) 
		    putInHashMap (node);
		    
		  if (checkEntry (node)) {
		    for (Enumeration e = node.children (); e.hasMoreElements ();) {
		      TreeNode n = (TreeNode)e.nextElement ();
		      TreePath path = parent.pathByAddingChild (n);
		      if (!checkExist (node, listTreeNode)) {
			count = count + 1;
			expandAll (tree,path,true);
			count = count -1;
		      }
		      tree.expandPath (parent);
		    }
		  }
		  
		}
	      }
	    }
	    else {
	      TreeNode node = (TreeNode) parent.getLastPathComponent ();
	      if (node.getChildCount () >= 0) {
		if (checkEntry (node)) {
		  for (Enumeration e = node.children (); e.hasMoreElements ();) {
		    TreeNode n = (TreeNode)e.nextElement ();
		    TreePath path = parent.pathByAddingChild (n);
		    count = count + 1;
		    expandAll (tree,path,true);
		    count = count -1;     
		    tree.expandPath (parent); 
		  }
		}
		
	      }
	    }
	  }
	}
	
      });
    



    JPanel leftPanel = new JPanel ();
    leftPanel.setLayout (new BoxLayout (leftPanel, BoxLayout.Y_AXIS));

    JPanel stateViewPanel = new JPanel ();
    JScrollPane scroll = new JScrollPane (stateView);
    scroll.setBorder (BorderFactory.createTitledBorder
			 (BorderFactory.createCompoundBorder
			  (BorderFactory.createEtchedBorder (),
			   StandardFiller.makeEmptyBorder ()), "Current State"));
    leftPanel.add (scroll);
    leftPanel.add (StandardFiller.makeLongVstrut ());

    JButton daVinci = new JButton ("daVinci");
    leftPanel.add (daVinci);

    JButton grappaBut= new JButton ("Grappa");
    leftPanel.add (grappaBut);

    JButton grappaBut2= new JButton ("Grappa State");
    leftPanel.add (grappaBut2);


    JPanel top = new JPanel ();
    
    top.setLayout (new BoxLayout (top, BoxLayout.X_AXIS));
    top.add (treePanel);
    top.add (leftPanel);
    top.setBorder (StandardFiller.makeWideEmptyBorder ());

    getContentPane ().add (top);
  
    grappaBut2.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  try {
	    // -- build a graph from the proof and decorate it a bit
	    GrappaGraph graph = ProofToGrappa.toGrappaState (proofStep, mc).
	      orientation ("landscape").
	      center (true).
	      size ("8.5,11").
	      editable (true).
	      errorWriter (System.err);
	    // -- open a frame to show the graph
	    GrappaFrame frame = new GrappaFrame (graph.getGraph ());
	    // -- lay out the graph first
	    if (frame == null)
	      System.out.println(" frame is null");
	    else {
		
	    frame.doGraphLayout ();
	    // -- show the window
	    frame.setVisible (true);
	      }
	      
	  } 
	  catch (Exception e) 
	    {  
	      XChekGUI.showException (ProofTreeFrame.this, 
				      "Error running Grappa", 
				      "Could not run Grappa", e);
	    }
	}
      });  
    
    grappaBut.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent evt)
	{
	  try {
	    // -- build a graph from the proof and decorate it a bit
	    GrappaGraph graph = ProofToGrappa.toGrappa (proofStep, mc).
	      orientation ("landscape").
	      center (true).
	      size ("8.5,11").
	      editable (true).
	      errorWriter (System.err);
	    // -- open a frame to show the graph
	    GrappaFrame frame = new GrappaFrame (graph.getGraph ());
	    // -- lay out the graph first
	    frame.doGraphLayout ();
	    // -- show the window
	    frame.setVisible (true);
	    
	  } 
	  catch (Exception e) 
	    {  
	      XChekGUI.showException (ProofTreeFrame.this, 
				      "Error running Grappa", 
				      "Could not run Grappa", e);
	    }
	}
      });  
  }
}

