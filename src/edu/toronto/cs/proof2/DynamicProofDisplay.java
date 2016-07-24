package edu.toronto.cs.proof2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Describe class DynamicProofDisplay here.
 *
 *
 * Created: Wed Jun 23 01:27:20 2004
 *
 * @author <a href="mailto:arie@localhost.localdomain">Arie Gurfinkel</a>
 * @version 1.0
 */
public class DynamicProofDisplay 
{

  /**
   * Creates a new <code>DynamicProofDisplay</code> instance.
   *
   */
  public DynamicProofDisplay ()
  {

  }


  public static void showProof (CTLProver prover, ProofStep step)
  {
    // XXX GARBAGE BELOW
    NewProofTreeModel ptm = new NewProofTreeModel (step);
    JTree ptree = new JTree (ptm);
    ptree.setCellRenderer (new FormulaRenderer ());
    
    ptree.addTreeSelectionListener (new ProofTreeSelectionListener (prover));
    
				   
    JFrame jfr = new JFrame ();
    Container content = jfr.getContentPane ();
    content.add (new JScrollPane (ptree), BorderLayout.CENTER);
    jfr.pack ();
    jfr.setVisible (true);

  }
  
  static class ProofTreeSelectionListener implements TreeSelectionListener
  {
    CTLProver prover;

    ProofTreeSelectionListener (CTLProver v)
    {
      prover = v;
    }

    public void valueChanged (TreeSelectionEvent evt)
    {
      JTree thetree = (JTree) evt.getSource();
      Object tgt = thetree.getLastSelectedPathComponent();
      if (tgt == null)
	return;

      ProofStep clickedNode = (ProofStep) tgt;
      prover.expand (clickedNode);
    }
  }
}
