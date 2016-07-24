package edu.toronto.cs.xkripke;

import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;

import java.util.*;
import java.lang.reflect.Array;

// XXX This should move away from smv package
import edu.toronto.cs.smv.VariableTable;


/**
 * A XKripke structure
 * A XKripke structure consists of a set of states, and a set of transitions
 * Each state has:
 *   -- name
 *   -- indicator if it is an initial state
 *   -- a set of propositions
 * Each transition has:
 *   -- a src state
 *   -- a dst state
 *   -- a transition value
 * Each proposition has
 *   -- name
 *   -- value
 */
public class XKripke
{

  // -- lattice to interpret values of the XKripke model

  IAlgebra algebra;
  
  // -- a set of states
  Map states;
  // -- a set of transitions
  Set transitions;

  XKripkeState[] initialStates = null;

  // -- variable declarations
  VariableTable symbolTable;

  public XKripke ()
  {
    states = new HashMap ();
    transitions = new HashSet ();
    symbolTable = null;
  }
  

  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  public void setAlgebra (IAlgebra v)
  {
    algebra = v;
  }
  
  public VariableTable getSymbolTable ()
  {
    if (symbolTable == null)
      symbolTable = buildSymbolTable ();
    
    return symbolTable;
  }

  private VariableTable buildSymbolTable ()
  {
    VariableTable vt = new VariableTable ();
    // -- loop through variable names and construct the symbol table
    for (Iterator it = getPropNamesAsSet ().iterator (); it.hasNext ();)
      vt.declarePropositional ((String)it.next ());
    return vt;
  }
  

  // -- returns the list of names of all of the atomic propositions used
  // -- in this XKripke structure
  public String[] getPropNames ()
  {
    // -- we assume that everything has been normalized and therefore, 
    // -- every state has exactly the same atomic props, and there is 
    // -- at least one state
    XKripkeState state = (XKripkeState)states.values ().iterator ().next ();
    return state.getPropNames ();
  }
  /**
   ** A useful modification. (Victor)
   **/
  public Set getPropNamesAsSet ()
  {
    XKripkeState state = (XKripkeState)states.values ().iterator ().next ();
    return state.getPropNamesAsSet ();
  }
  
  // Returns number of variables
  public int getNumVars ()
    {
      return Array.getLength (getPropNames ());
    };
  
  public XKripkeState[] getInitialStates ()
  {
    if (initialStates == null)
      {
	List initStates = new ArrayList ();
	
	for (Iterator it = getStates ().values ().iterator (); it.hasNext ();)
	  {
	    XKripkeState state = (XKripkeState)it.next ();
	    if (state.isInitial ())
	      initStates.add (state);
	  }
	initialStates = (XKripkeState[])initStates.toArray 
	  (new XKripkeState [initStates.size ()]);
	
	
      }
    
    return initialStates;
  }

  public void labeliseStates ()
  {
    // !
    String name;

    for (Iterator it = states.values ().iterator (); it.hasNext (); )
      {
	XKripkeState st = (XKripkeState) it.next ();
	name = st.getName ();
	//log.debug ("Setting up var for: "+name);
	for (Iterator iter = states.values ().iterator (); iter.hasNext ();)
	  {
	    // -- iterate over states and add the state name variable,
	    // -- making it true in exactly one state.
	    // XXX This is a big waste of state variables!
	    XKripkeState state = (XKripkeState) iter.next ();
	    if (state == st)
	      state.addProp 
		(new XKripkeProp ("_" + name, algebra.top ().toString ()));
	    else
	      state.addProp 
		(new XKripkeProp ("_" + name, algebra.bot ().toString ()));
	  }
      }
  }
  
  
  public Map getStates ()
  {
    return states;
  }
  public void setStates (Map v)
  {
    states = v;
  }

  public XKripkeState getState (String name)
  {
    return (XKripkeState)states.get (name);
  }
  
  public XKripkeTransition getTransition (XKripkeState src, XKripkeState dst)
  {
    return null;
  }
  
  public Set getTransitions ()
  {
    return transitions;
  }
  public void setTransitions (Set v)
  {
    transitions = v;
  }

  public void addTransition (String src, String dst, String value)
  {
    XKripkeState srcState = getState (src);
    XKripkeState dstState = getState (dst);
    
    addTransition (new XKripkeTransition (srcState, dstState, value));
  }
  
  public void addTransition (XKripkeTransition t)
  {
    transitions.add (t);
  }

  public void addState (XKripkeState state)
  {
    states.put (state.getName (), state);
    state.setXKripke (this);
  }
  

  public static class XKripkeTransition
  {
    XKripkeState src;
    XKripkeState dst;
    String value;
    
    public XKripkeTransition (XKripkeState _src, XKripkeState _dst, 
			      String _value)
    {
//       log.debug ("Initialising XKT to: ");
//       log.debug (StringUtil.doEscapes(""+_src.getName ()));
//       log.debug (" and "+StringUtil.doEscapes(""+_dst.getName ()));
//       log.debug (" and "+StringUtil.doEscapes(_value));
      setSrc (_src);
      setDst (_dst);
      setValue (_value);
    }
    
    
    public XKripkeState getSrc ()
    {
      return src;
    }
    public void setSrc (XKripkeState v)
    {
      src = v;
    }
    public XKripkeState getDst ()
    {
      return dst;
    }
    public void setDst (XKripkeState v)
    {
      dst = v;
    }
    public void setValue (String v)
    {
      value = v;
    }
    public String getValue ()
    {
      return value;
    }

    public String toString ()
    {
      return getSrc ().getName () + " -" + getValue () + 
	"-> " + getDst ().getName ();
    }
    
  }
  
  public static class XKripkeState
  {
    String name;
    Map props;
    XKripke xk;
    
    // -- true if this is an initial state
    boolean initial = false;
    
    public XKripkeState (String _name)
    {
      //      log.debug ("Initialising state "+_name);
      xk = null;
      setName (_name);
      props = new HashMap ();
    }
    
    public XKripkeState (String _name, Map _props)
    {
      setName (_name);
      setProps (_props);
    }
    
    public void setXKripke (XKripke _xk)
    {
      xk = _xk;
    }

    public XKripke getXKripke ()
    {
      return xk;
    }
    
    
    public boolean isInitial ()
    {
      return initial;
    }
    public void setInitial (boolean v)
    {
      initial = v;
    }
    
    public String getName ()
    {
      return name;
    }
    public void setName (String v)
    {
      name = v;
    }
    public Map getProps ()
    {
      return props;
    }

    /**
     ** Gets the names of the propositions used by the xkripke
     ** structure in String [] format.
     **/
    public String[] getPropNames ()
    {
      Collection propNames = getProps ().keySet ();
      return (String[])propNames.toArray (new String [propNames.size ()]);
    }
    /**
     ** A useful modification. (Victor)
     **/
    public Set getPropNamesAsSet ()
    {
      return getProps ().keySet ();
    }

    public void setProps (Map v)
    {
      props = v;
    }
    
    public void addProp (XKripkeProp prop)
    {
      props.put (prop.getName (), prop);
    }
    public XKripkeProp getProp (String name)
    {
      return (XKripkeProp)props.get (name);
    }
    
    public String toString ()
    {
      StringBuffer sb = new StringBuffer ();
      sb.append (getName () + "\n");
      for (Iterator it = getProps ().values ().iterator (); it.hasNext ();)
	sb.append ("\t" + it.next () + "\n");
      return sb.toString ();
    }
    
  }
  
  public static class XKripkeProp
  {
    String name;
    String value;
    
    public XKripkeProp (String _name, String _value)
    {
      setName (_name);
      setValue (_value);
    }
    
    public String getName ()
    {
      return name;
    }
    public void setName (String v)
    {
      name = v;
    }
    public String getValue ()
    {
      return value;
    }
    public void setValue (String v)
    {
      value = v;
    }

    public String toString ()
    {
      return getName () + " = " + getValue ();
    }
    
  }

  public static class XKripkeVisitor
  {
    public void visitXKripke (XKripkeState state)
    {
    }
  }
  
  
  
}
