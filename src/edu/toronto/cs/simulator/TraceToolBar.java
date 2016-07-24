package edu.toronto.cs.simulator;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;

/**
 ** A tool bar for the trace viewer.
 **/
public class TraceToolBar extends JToolBar
{
    // buttons:
    // a condition that must be satisfied needs to be
    // specified for every step (will prompt user for input)
    JButton goodcond;
    // this must never happen (specified once, used universally
    // throughout the trace unless changed or cancelled)
    JButton badcond;
    // specify a breaking condition
    // when this condition is true - stop expanding
    JButton breakcond;

    public TraceToolBar (ActionMap actionMap)
    {
	super ();

	goodcond = new JButton ((Action)actionMap.get
				("Prompt for condition"));
	goodcond.setText ("Ask condition");

	badcond = new JButton ((Action)actionMap.get
			       ("Ignore condition"));
	badcond.setText ("Ignore");

	breakcond = new JButton ((Action)actionMap.get
				 ("Break condition"));
	breakcond.setText ("Break");

	// create the toolbar
	add (goodcond);
	add (badcond);
	add (breakcond);

	// turn off dragging
	setFloatable (false);
    }
}
