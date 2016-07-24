package edu.toronto.cs.util.gui;

import java.awt.event.*;

public class DoubleClickActionAdapter extends MouseAdapter
{
  ActionListener a;
  
  public DoubleClickActionAdapter (ActionListener _a)
  {
    a = _a;
  }
  
  public void mouseClicked(MouseEvent e) 
  {
    //System.out.println (e.getModifiers ());
    if (e.getClickCount() == 2) 
      a.actionPerformed (new ActionEvent (e.getSource (), 
					  ActionEvent.ACTION_PERFORMED,
					  "Element double-clicked",
					  e.getModifiers ()));
  }
}
