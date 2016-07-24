package edu.toronto.cs.util;

import javax.swing.event.*;

/**
 ** An adapter class for the TreeModelListener interface. Does
 ** absolutely nothing unless overridden.
 **/
public class TreeModelAdapter implements TreeModelListener
{
    public TreeModelAdapter () {}

    public void treeNodesChanged (TreeModelEvent e) {}
    public void treeNodesInserted (TreeModelEvent e) {}
    public void treeNodesRemoved (TreeModelEvent e) {}
    public void treeStructureChanged (TreeModelEvent e) {}
}
