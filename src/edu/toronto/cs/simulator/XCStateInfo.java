package edu.toronto.cs.simulator;

import edu.toronto.cs.ctl.*;
import java.util.*;
import edu.toronto.cs.mvset.*;

/**
 ** XCStateInfo is the class used to store the state info in the trace.
 **/
public class XCStateInfo
{
    // some additional label (potentially different from the state name)
    String label;
    // state variable values
    MvSet variables;
    // transition into the state
    Object transition;
    
    /**
     ** Constructs an empty XCStateInfo object.
     **/
    public XCStateInfo ()
    {
	label = null;
	variables = null;
	transition = "";
    }

    /**
     ** Constructs the state info from the MvSet identifying the state. 
     **
     ** @param vars - variables identifying the state.
     **/
    public XCStateInfo (MvSet vars)
    {
	label = null;
	variables = vars;
	transition = "";
    }
    
    /**
     ** Constructs the state info from the MvSet identifying the state
     ** and a label.
     **
     ** @param l - state label (not necessarily same as name)
     ** @param vars - variables identifying the state.
     **/
    public XCStateInfo (String l, MvSet vars)
    {
	label = l;
	variables = vars;
	transition = "";
    }

    /**
     ** If the object compared to is of type XCStateInfo and has
     ** <i>equal</i> label and variables, returns true; otherwise
     ** returs false.
     **
     ** @param o - Object to compare.
     **
     ** @return true if the label and variables of XCStateInfo objects
     ** are <i>equal</i> and false otherwise.
     **/
    public boolean equals (Object o)
    {
	XCStateInfo si;
	
	// XXX: need to fix the "null" bug....
	if (o instanceof XCStateInfo)
	    {
		si = (XCStateInfo) o;
		return ((label == null ? si.label == null :
			 label.equals (((XCStateInfo) o).label)) &&
			(variables == null ? si.variables == null :
			 variables.equals (((XCStateInfo) o).variables)));
	    }
	else return false;
    }

    /**
     ** Retrieves the state label.
     **
     ** @return state label.
     **/
    public String getLabel ()
    {
	return label;
    }

    /**
     ** Sets the state label.
     **
     ** @parent newlabel - new value of the label.
     **/
    public void setLabel (String newlabel)
    {
	label = newlabel;
    }

    /**
     ** Retrieves the state variables.
     **
     ** @return state variables.
     **/
    public MvSet getVariables ()
    {
	return variables;
    }

    /**
     ** Sets the state variables.
     **
     ** @parent newvariables - new value of the variables
     **
    public void setVariables (MvSet newvariables)
    {
	variables = newvariables;
    }
    
    /**
     ** Retrieves the value of the transition into the state.
     **
     ** @return the value of the transition into the state.
     **/
    public Object getTransition ()
    {
	return transition;
    }

    /**
     ** Sets the transition into the state.
     **
     ** @parent newtransition - new value of the transition.
     **
    public void setTrransition (Object newtransition)
    {
	transition = newtransition;
    }
    /**
     ** Gives the string representation of the XCStateInfo.
     **
     ** @return the string representation of the XCStateInfo.
     **/
    public String toString ()
    {
	return ("Label: " + getLabel () + 
		"\nVariables:\n" + getVariables () + 
		"\nTransition: " + getTransition () + "\n");
    }
}
