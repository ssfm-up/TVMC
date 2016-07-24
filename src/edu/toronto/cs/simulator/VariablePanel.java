package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

//import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;

/**
 ** Interface to be used for the displaying of variables.
 **/
public abstract class VariablePanel extends JPanel
{
    /**
     ** Displays the variables from the StateInfo.
     ** @param si - state info containing the variable information to
     ** display.
     **/
    public abstract void show (XCTraceState s);

    /**
     ** Clears the variable panel view.
     **/
    public abstract void clear ();
}
