package edu.toronto.cs.proof2;


import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class FormulaRenderer extends DefaultTreeCellRenderer 
{
 
  public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
    super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
    //System.out.println ("FR: Getting renderer for "+value);
    
    if (value instanceof TreeProofStep)
      {
	TreeProofStep ps = (TreeProofStep) value;
	
	TreeModel tr = tree.getModel();
	if (tr instanceof NewProofTreeModel)
	  {
	    NewProofTreeModel pt = (NewProofTreeModel) tr;
	    Color whichc = pt.getColourForState(ps.getFormula().getStateName());
	    
	    setTextNonSelectionColor (whichc);
	  }
	
	else
	  setTextNonSelectionColor (Color.BLACK);
	
      }
    else setTextNonSelectionColor(Color.BLACK);
    
    
//     System.out.println("Icon dimensions: "+(getLeafIcon().getIconHeight())+
// 		       ","+(getLeafIcon().getIconWidth()));
    
    
    return this;
  }
  
  protected boolean printNode(Object v)
  {
    
    //    DefaultMutableTreeNode node =
    //      (DefaultMutableTreeNode)v;
    
    //     Object obj =  node.getUserObject();
     if (v instanceof TreeProofStep)
       {
	 ProofRule pr = ((TreeProofStep)v).getProofRule();
	 if (pr != null) {
	   System.out.println("Proof rule: "+pr);
	   return (pr instanceof Axiomatic);
	 }
	 
       }
     return false;
     
  }
  
  
}
