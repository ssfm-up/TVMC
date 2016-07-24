package edu.toronto.cs.simulator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

//import edu.toronto.cs.util.*;
import edu.toronto.cs.util.gui.*;
//import edu.toronto.cs.ctl.*;

public class ConditionPanel extends JPanel
{
    // enabled checkbox
    JCheckBox enabled;

    // how the simulation conditions are specified
    JToggleButton dummy, user, script;

    public ConditionPanel (ActionMap map)
    {
	super ();

	ButtonGroup siminput = new ButtonGroup ();
	dummy = new JToggleButton
	    ((Action) map.get ("Get states randomly"));
	dummy.setSelected (true);
	siminput.add (dummy);
	user = new JToggleButton
	    ((Action) map.get ("Specify simulation conditions by hand"));
	siminput.add (user);	
	script = new JToggleButton
	    ((Action) map.get ("Use an xsim script"));
	siminput.add (script);

	setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));

	add (dummy);
	add (user);
	add (script);

	// align things properly
	GUIUtil.alignAllX (getComponents (), JComponent.CENTER_ALIGNMENT);
    }
}
