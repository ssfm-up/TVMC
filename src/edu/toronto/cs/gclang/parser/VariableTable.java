package edu.toronto.cs.gclang.parser;

// XXX New version of VariableTable from edu.toronto.cs.smv
// XXX Should be merged with the above once we are done
import java.util.*;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.*;



/**
 * Describe class <code>VariableTable</code> here.
 * Symbol table that keeps track of variables. 
 * Since we often need several copies of the same variable, for example, 
 * current and next state, or even next next state, we support arbitrary
 * number of shadow variables.
 *
 * @author <a href="mailto:arie@cs.toronto.edu">Arie Gurfinkel</a>
 * @version 1.0
 */
public class VariableTable 
{

  
  // -- how many variables we have already seen
  int idCounter = 0;

  // -- symbol table to lookup variables by their name
  Map varNames;


  // -- an mv-set factory so that we can create variables as well 
  // -- as keep track of them
  MvSetFactory factory;
  // -- algebra used by the factory
  IAlgebra algebra;

  // -- do we need auxiliary variables
  boolean auxVars;
  
  int shadows;

  /**
   * Creates a new <code>VariableTable</code> instance.
   *
   * @param _shadows an <code>int</code> number of shadow variables 
   */
  public VariableTable (int _shadows)
  {
    shadows = _shadows;
    idCounter = 0;
    varNames = new HashMap ();
  }
  
  public VariableTable ()
  {
    this (2);
  }
  

  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    
    for (Iterator it = varNames.values ().iterator (); it.hasNext ();)
      {
	sb.append (it.next ().toString ());
	sb.append ('\n');
      }
    return sb.toString ();
  }
  

  // -- mv-set factory setter and getter
  public void setMvSetFactory (MvSetFactory _factory)
  {
    factory = _factory;
    setAlgebra (factory.getAlgebra ());
  }

  public MvSetFactory getMvSetFactory()
  {
    return factory;
  }
  

  void setAlgebra (IAlgebra v)
  {
    algebra = v;
  }

  /**
   * Describe <code>declareDefine</code> method here.
   *
   * @param name a <code>String</code> value
   * @param value a <code>MvSet</code> value
   * @return a <code>DefineVariable</code> value
   */
  public DefineVariable declareDefine (String name, MvSet value)
  {
    DefineVariable v = new DefineVariable (name, value);
    varNames.put (v.getName(), v);
    return v;
  }
  
  /**
   * <code>getIdBlock</code> allocates <code>size</code> number of bits
   *
   * @param size an <code>int</code> value
   * @return an <code>int</code> value
   */
  int getIdBlock (int size)
  {
    int result = idCounter;
    idCounter += size;
    return result;
  }
  
  
  /**
   * <code>declareEnumerated</code> declares a variable of enumerated type
   *
   * @param name a <code>String</code> value
   * @param values a <code>Collection</code> values for the enumeration
   * @return an <code>EnumeratedVariable</code> value
   */
  public EnumeratedVariable declareEnumerated (String name, Collection values)
  {
    return declareEnumerated (name, (String[])
			      values.toArray (new String [values.size ()]));
    
  }
  
  
  /**
   * <code>declareEnumerated</code> declares a variable of enumerated type
   *
   * @param name a <code>String</code> value
   * @param values a <code>String[]</code> values for the enumeration
   * @return an <code>EnumeratedVariable</code> value
   */
  public EnumeratedVariable declareEnumerated (String name, String[] values)
  {
    
    // -- allocate an array to hold all of the variables, 
    // -- a variable with index 0 is the real one, the rest are shadows
    List vars = new ArrayList (shadows + 1);
    
    // -- create variables
    for (int i = 0; i < shadows + 1; i++)
      vars.add (new EnumeratedVariable (name, i, values));
    
    // -- first variable is the real one
    EnumeratedVariable var = (EnumeratedVariable) vars.get (0);
    
    var.setShadows ((EnumeratedVariable[]) vars.
		    toArray (new EnumeratedVariable [vars.size ()]));
    
    
    int startBit = getIdBlock (var.bitSize () * (shadows + 1));

    // -- assign even bits from startBit to var, but leave space
    // -- for shadow variables
    int[] bits = new int [var.bitSize ()];
    for (int i = 0; i < bits.length; i++)
      bits [i] = startBit + (i * (shadows + 1));
    var.setBits (bits);

    // -- allocate bits for shadow variables
    for (Iterator it = vars.subList (1, vars.size ()).iterator ();
	 it.hasNext ();)
      {
	EnumeratedVariable shadow = (EnumeratedVariable)it.next ();
	bits = (int[]) bits.clone ();
	for (int i = 0; i < bits.length; i++)
	  bits [i]++;
	shadow.setBits (bits);
      }
    
    // XXX Should we store everything in the hashtable?
    for (Iterator it = vars.iterator (); it.hasNext ();)
      {
	EnumeratedVariable v = (EnumeratedVariable) it.next ();
	varNames.put (v.getName (), v);
      }
    return var;
  }
  
  
  /**
   * Describe <code>declarePropositional</code> method here.
   *
   * @param name a <code>String</code> value
   * @return a <code>StateVariable</code> value
   */
  public StateVariable declarePropositional (String name)
  {
    
    List vars = new ArrayList (shadows + 1);
    
    for (int i = 0; i < shadows + 1; i++)
      vars.add (new StateVariable (name, i, getIdBlock (1)));
    
    StateVariable var = (StateVariable) vars.get (0);
    
    var.setShadows ((StateVariable[]) 
		    vars.toArray (new StateVariable [vars.size ()]));
    

    for (Iterator it = vars.iterator (); it.hasNext ();)
      {
	StateVariable v = (StateVariable) it.next ();
	varNames.put (v.getName (), v);
      }
    return var;
  }

  /* this should return number of bit variables we have */
  public int getNumVars ()
  {
    return idCounter;
  }

  public Variable getByName (String name)
  {
    return (Variable)varNames.get (name);
  }
  
  public Collection getVariables ()
  {
    return varNames.values ();
  }
  
  /**
   * Describe <code>getVarNames</code> method here.
   *
   * currently called to construct a KripkeStructure but not used there.
   * @return a <code>String[]</code> value
   */
  public String[] getVarNames ()
  {
    return null;
  }
  
  
  // -- a helper debug method, dumps this symbol table
  public void dump () 
  {
     System.out.println("Dumping: " + varNames.keySet().size ());

     for (Iterator it = getVariables ().iterator (); it.hasNext ();)
       System.out.println (it.next ());
  }
  
  public CTLNode handleUnknownVariable (String name)
  {

    // -- unknown variable is possibly an algebraic constant
    AlgebraValue value = algebra.getValue (name);
    if (value == algebra.noValue ())
      // -- error handling for unknown variables
      throw new RuntimeException ("Unknown variable: " + name);

    // -- value is valid, so create appropriate CTL for it
    return CTLFactory.createCTLConstantNode (value);
  }
  
  // -- returns a ctl rewriter that resolves variable names in CTL
  public CTLReWriter getCtlReWriter ()
  {
    return new CloningRewriter ()
      {
	public Object visitAtomPropNode (CTLAtomPropNode ctl, Object o)
	{
	  // -- already done
	  if (ctl.getMvSet () == null)
	    {
	      // -- if we seen an atomPropNode it must by a state variable
	      // -- since enumerated variables are handled at '=' level
	      StateVariable var = (StateVariable)getByName (ctl.getName ());
	      if (var == null) return handleUnknownVariable (ctl.getName ());
	      
	      
	      // -- set mv-set
	      ctl.setMvSet (var.getMvSet ());
	    }
	  
	  return ctl;
	}

	public Object visitEqualsNode (CTLEqualsNode ctl, Object o)
	{
	  // -- for equals node we first check if its left 
	  // -- hand side is a variable name, in which case the 
	  // -- right hand side should be a value from an enum type
      
	  if (ctl.getLeft ().getClass () != CTLAtomPropNode.class ||
	      ctl.getRight ().getClass () != CTLAtomPropNode.class)
	    return super.visitEqualsNode (ctl, o);


      
	  // -- atom = atom   case, assume variable is always on the left
	  CTLAtomPropNode left = (CTLAtomPropNode)ctl.getLeft ();
	  CTLAtomPropNode right = (CTLAtomPropNode)ctl.getRight ();

	  if (!(getByName (left.getName ()) instanceof EnumeratedVariable))
	    return super.visitEqualsNode (ctl, o);

	  // -- get variable from symbol table
	  EnumeratedVariable var = 
	    (EnumeratedVariable)getByName (left.getName ());
	  // -- get value from '='
	  String value = right.getName ();
	  // -- build an mv-set
	  MvSet result = var.eq (value);
	  // -- create new CTLNode to represent this equality
	  CTLMvSetNode mvSetNode = CTLFactory.createCTLMvSetNode (result);
	  // -- set the name of this mv-set node so it will print correctly
	  mvSetNode.setName (ctl.toString ());
	  return mvSetNode;
	} 

	// -- expands the placeholder node
	public Object visitPlaceholderNode (CTLPlaceholderNode ctl, Object o)
	{
	  // -- if we aready seen this placeholder node don't build
	  // -- terms for it again
	  if (ctl.getTerms () != null) return ctl;
	  
	  // -- get atomic propositions restricting this placeholder 
	  CTLAtomPropNode[] props = ctl.getProps ();
	  // -- a set of terms per each prop
	  MvSet[][] terms = new MvSet [props.length][];
      
	  // -- build the terms
	  for (int i = 0; i < props.length; i++)
	    terms [i] = buildTerms (props [i]);
	  
	  
	  // -- update the placeholder with terms
	  ctl.setTerms (terms);
	  return ctl;
	}
	
	// -- used by visitPlaceholderNode 
	private MvSet[] buildTerms (CTLAtomPropNode prop)
	{
	  Variable var = getByName (prop.getName ());
      
	  if (var.getClass () == StateVariable.class)
	    return new MvSet [] { ((StateVariable)var).getMvSet (), 
				  ((StateVariable)var).getMvSet ().not () };
	  
	  if (var.getClass () == EnumeratedVariable.class)
	    {
	      EnumeratedVariable enumVar = (EnumeratedVariable)var;
	      String[] values = enumVar.getValues ();
	      
	      MvSet[] terms = new MvSet [values.length];

	      for (int i = 0; i < values.length; i++)
		terms [i] = enumVar.eq (values [i]);
	      return terms;
	    }
	  assert false : "Unknown variable: " + prop;
	  return null;
	}
      };  
  }
  
  // -- returns a state presenter that interpets a value assignment
  // -- with respect to variables in this symbol table
  public StatePresenter getStatePresenter ()
  {
    return new StatePresenter ()
      {
	public CTLNode[] toCTL (AlgebraValue[] state)
	{
	  List result = new LinkedList ();
	  
	  for (Iterator it = getVariables ().iterator (); it.hasNext ();)
	    {
	      Variable var = (Variable)it.next ();
	      // -- skip all shadow
	      if (var.isShadow ()) continue;

	      // -- let the variable handle its toCTL conversion
	      CTLNode ctl = var.toCTL (state);
	      if (ctl != null) result.add (ctl);
	    }
	  
	  return (CTLNode[])result.toArray (new CTLNode [result.size ()]);
	}

	public CTLNode[] toCTL (MvSet cube)
	{
	  return toCTL ((AlgebraValue[])cube.cubeIterator ().next ());
	}
	
	// XXX Don't know if we ever want this at all!
	public CTLNode[][] toCTL (AlgebraValue[][] states)
	{
	  if (true) throw new UnsupportedOperationException ();
	  CTLNode[][] result = new CTLNode[states.length][];
	  for (int i = 0; i < states.length; i++)
	    result [i] = toCTL (states [i]);
	  return result;
	}
      };
  }


  /**
   * Describe <code>variableMap</code> method here.  returns an
   * integer array that maps variables in the 'fromSet' to variables
   * in the 'toSet'. For example, variableMap (0, 1) returns a map
   * that maps 0 variables to 1 variables. The intention is that 0
   * variables are current variables, and 1 variables are next state
   * variables, in which case we get a map from current to next state
   *
   * @param fromSet an <code>int</code> value
   * @param toSet an <code>int</code> value
   * @return an <code>int[]</code> value
   */
  public int[] variableMap (int fromSet, int toSet)
  {
    int[] map = new int [getNumVars ()];
    
    for (Iterator it = getVariables ().iterator (); it.hasNext ();)
      {
	Variable var = (Variable) it.next ();
	// -- skip shadow variables, maybe we should not even 
	// -- keep them in our varNames?
	if (var.isShadow ()) continue;
	
	if (var instanceof StateVariable)
	  {
	    StateVariable[] shadows = (StateVariable[])var.getShadows ();
	    for (int i = 0; i < shadows.length; i++)
	      {
		int k = (i == fromSet ? toSet : i);
		map [shadows [i].getId ()] = shadows [k].getId ();
	      }
	    
	  }
	else if (var instanceof EnumeratedVariable)
	  {
	    
	    EnumeratedVariable[] shadows = 
	      (EnumeratedVariable[])var.getShadows ();

	    for (int i = 0; i < shadows.length; i++)
	      {
		int k = (i == fromSet ? toSet : i);
		for (int j = 0; j < shadows [i].bitSize (); j++)
		  map [shadows [i].getBitId (j)] = shadows [k].getBitId (j);
	      }    
	  }
	else
	  throw 
	    new RuntimeException ("Unknown variable " + var + " of class " +
				  (var == null ? "null" : 
				   var.getClass ().toString ()));
      }
    return map;
  }
  




  /**
   * Describe <code>getVariableIds</code> method here.
   *
   * returns ids of all of the variables in set <code>set</code>
   * @param set an <code>int</code> value
   * @return an <code>int[]</code> value
   */
  public int[] getVariableIds (int set)
  {
    int[] vars = new int [getNumVars () / (shadows + 1)];

    int count = 0;
    for (Iterator it = getVariables ().iterator (); it.hasNext ();)
      {
	Variable var = (Variable)it.next ();
	// -- skip shadow variables
	if (var.isShadow ()) continue;
	
	if (var instanceof StateVariable)
	  vars [count++] = ((StateVariable)var.getShadow (set)).getId ();
	else if (var instanceof EnumeratedVariable)
	  {
	    EnumeratedVariable enumShadow = 
	      (EnumeratedVariable)var.getShadow (set);
	    
	    for (int i = 0; i < enumShadow.bitSize (); i++)
	      vars [count++] = enumShadow.getBitId (i);
	  }
      }
    return vars;
  }
  

  

  public abstract class Variable 
  {
    // -- the name
    String name;
    Variable[] shadows;
    int shadowIdx;

    String computedName = null;
      
    public static final String NAME_SUFFIX = "'";

    public Variable (String _name, int _idx)
    {
      name = _name;
      shadows = null;
      shadowIdx = _idx;
    }
    
    public String getName ()
    {
      if (computedName == null)
	{
	  StringBuffer sb = new StringBuffer ();
	  sb.append (name);
	  for (int i = 0; i < shadowIdx; i++)
	    sb.append (NAME_SUFFIX);
	  computedName = sb.toString ();
	}
      
      return computedName;
    }

    public Variable getShadow (int i)
    {
      assert !isShadow () : " no shadow of a shadow";
      
      return shadows [i];
    }

    public void setShadows (Variable[] v)
    {
      for (int i = 0; i < v.length; i++)
	assert v [i] != null : "shadow " + i + " is null";
      
      shadows = v;
    }
    
    public Variable[] getShadows ()
    {
      return shadows;
    }
    
    public boolean isShadow ()
    {
      return shadowIdx != 0;
    }    

    public String toString ()
    {
      return getName ();
    }

    public abstract MvSet eq (Variable v);
    
    
    // -- returns an mv-set corresponding to var = next (var) in SMV
    public abstract MvSet eqShadow (int i);
    
    public abstract MvSet eq (MvSet v);
    public abstract MvSet eq (String v);

    // -- returns CTL representation of this variable 
    // -- with respect to the state
    public abstract CTLNode toCTL (AlgebraValue[] state);
    public int size ()
    {
      return 0;
    }
    
  }
  
  public class EnumeratedVariable  extends Variable
  {
    // -- the set of allowed values
    String[] values;

    // -- a set of ids covered by this variable
    int[] bits;

    // -- handles enumerated type for us
    EnumType enumType;
    int enumWidth;
    
    public EnumeratedVariable (String _name, int _idx, String[] _values)
    {
      super (_name, _idx);
      values = _values;
      
      enumType = new EnumType (values);
      enumWidth = enumType.bitSize ();
    }
    
    public String[] getValues ()
    {
      return values;
    }
    
    // -- sets ids for this variable.
    public void setBits (int[] v)
    {
      assert v.length == bitSize ();
      
      bits = v;
      
    }
    
    public int size ()
    {
      return bitSize ();
    }
    
    public int bitSize ()
    {
      return enumWidth;
    }
    

    public int getBitId (int bit)
    {
      return bits [bit];
    }

    public MvSet eq (MvSet v)
    {
      // -- if an enumerated variable has values corresponding to 
      // -- true and false it can be compared with an arbitrary mv-set
      // -- as   (this == true /\ (v = true)) \/ 
      //                               (this == false /\ (v = false))

      MvSet top = factory.top ();
      MvSet bot = factory.bot ();
      
      MvSet result = bot;
      
      for (int i = 0; i < values.length; i++)
	{
	  if (values [i].equals ("1") || values [i].equals ("TRUE"))
	    result = result.or (eq (values [i]).and (v.eq (top)));
	  else if (values [i].equals ("0") || values [i].equals ("FALSE"))
	    result = result.or (eq (values [i]).and (v.eq (bot)));
	}
      return result;
    }
    
    public MvSet eq (Variable v)
    {
      assert v instanceof EnumeratedVariable;
      
      if (v instanceof EnumeratedVariable)
	return eq ((EnumeratedVariable)v);
      return null;
    }
    
    public MvSet eq (String v)
    {

      // -- System.out.println ("SymbTable: " + getName () + " = " + v);
	//System.out.println ("Comparing " + getName () + " with " + v);
      MvSet result = factory.top ();

      // XXX move into super class if required
      AlgebraValue top = factory.getAlgebra ().top ();
      AlgebraValue bot = factory.getAlgebra ().bot ();
      
      int[] enm = enumType.bitValue (v);      
      for (int i = 0; i < enumWidth; i++)
	{
	  // -- skip unset bits
	  if (enm [i] == -1) continue;
	  
	  result = factory.var (getBitId (i), 
				enm [i] == 1 ? top : bot,
				factory.getAlgebra ().top ()).and (result);
	}
	  

      return result;
    }
    

    public MvSet eq (EnumeratedVariable v)
    {
      HashSet valuesSet = new HashSet (Arrays.asList (values));
      MvSet result = factory.bot ();

      for (int i = 0; i < v.getValues ().length; i++)
	{
	  String value = v.getValues () [i];
	  if (valuesSet.contains (value))
	    result = result.or (eq (value).and (v.eq (value)));
	}

      return result;
    }
    
    public MvSet eqShadow (int idx)
    {
      assert !isShadow () : "Cannot apply eqShadow to a shadow var";

      
      EnumeratedVariable vNext = (EnumeratedVariable)getShadow (idx);
      MvSet result = factory.bot ();
      
      // -- walk over all values, and build a big disjunction
      for (int i = 0; i < values.length; i++)
	result = result.or (eq (values [i]).and (vNext.eq (values [i])));
      return result;
    }
    

    public CTLNode toCTL (AlgebraValue[] state)
    {
      // -- we start with all bits being unset
      int[] enm = new int [enumWidth];
      Arrays.fill (enm, -1);
      
      // -- set the bits based on the state
      for (int i = 0; i < enumWidth; i++)
	{
	  if (state [getBitId (i)] == algebra.top ())
	    enm [i] = 1;
	  else if (state [getBitId (i)] == algebra.bot ())
	    enm [i] = 0;
	}

      // -- get the list of values based on the bits set
      Object[] enumValues = enumType.enumValues (enm);
      // -- if all values are present than all bits were don't care
      // -- so return nothing
      if (enumValues.length == values.length)
	return null;
      
      // -- There are at least two choices to describe the result as ctl.
      // -- either we can do \/ (var = enumValues [i]) where i ranged over
      // -- enumValues.length -- this gives a formula of size 
      // -- enumValues.length. 
      // -- Or we can invert the result, so we make the choice based 
      // -- on what is smaller
      
//       if (enumValues.length <= values.length - enumValues.length)
	return ctlForValues (enumValues, true);
//       else
// 	{
// 	  // -- take the set difference between two arrays
// 	  enumValues = ArrayUtil.arrayDiff (values, enumValues);
// 	  return ctlForValues (enumValues, false);
// 	}
      
    }
    
    /**** 
     **** Given a set of enumValues for example a, b, c
     **** builds   var = a \/ var = b \/ var = c
     ****  if enumValues cotains a single element returns
     ****    var = a
     ****  positive indicates if we should produce a positive form as 
     ****  described above, or its negation
     ****/ 
    private CTLNode ctlForValues (Object[] enumValues, boolean positive)
    {

      CTLAtomPropNode self = 
	CTLFactory.createCTLAtomPropNode (this.getName ());

      CTLNode result = null;

      for (int i = 0; i < enumValues.length; i++)
	{
	  if (i == 0) 
	    {
	      if (positive)
		result = self.eq
		  (CTLFactory.createCTLAtomPropNode (enumValues [i].
						     toString ()));
	      else 
		result = self.eq
		  (CTLFactory.createCTLAtomPropNode (enumValues [i].
						     toString ())).neg ();
	    }
	  
	  else
	    {
	      if (positive)
		result = result.or (self.eq
				    (CTLFactory.createCTLAtomPropNode 
				     (enumValues [i].toString ())));
	      else
		result = result.and (self.eq
				    (CTLFactory.createCTLAtomPropNode 
				     (enumValues [i].toString ())).neg ());
	    }
	  
	}
      return result;
    }
    
    public String toString ()
    {
      return getName () + " : " + Arrays.asList (values);
    }
    
  }
  
  

  // -- symbol table entry for a state variable
  public class StateVariable  extends Variable
  {
    // -- mv-set representation of the variable
    MvSet mvSet;

    //  Type vtype;
    // -- an ID of the variable
    int id;
  
  
    
    public StateVariable (String name, int _shadowIdx, int _id)
    {
      super (name, _shadowIdx);
      id = _id;
    }
    
    public int size ()
    {
	return 1;
    }
    
    public String toString ()
    {
      return getName () + " : boolean";
    }
    
  
    public int getId ()
    {
      return id;
    }
    
    public MvSet getMvSet ()
    {
      if (mvSet == null)
	mvSet = factory.createProjection (id);
      return mvSet;
    }

    protected void setMvSet (MvSet v)
    {
      mvSet = v;
    }
    

    public MvSet eqShadow (int idx)
    {
      assert !isShadow () : "Cannot apply eqShadow on shadow variables";
      
      return getMvSet ().eq (((StateVariable)getShadow (idx)).getMvSet ());
    }
    

    public MvSet eq (Variable v)
    {
      assert v instanceof StateVariable;
      
      if (v instanceof StateVariable)
	return eq ((StateVariable)v);
      throw new UnsupportedOperationException ();
    }
    

    public MvSet eq (MvSet v)
    {
      return getMvSet ().eq (v);
    }
    
    public MvSet eq (StateVariable v)
    {
      return getMvSet ().eq (v.getMvSet ());
    }

    public MvSet eq (String v) 
    { 
      // -- we can handle this if String is an algebra value
      AlgebraValue value = algebra.getValue (v);
      assert value != null : "Comparing " + getName () + " with " + v;
      
      return getMvSet ().eq (factory.createConstant (value));
    }

  
    public CTLNode toCTL (AlgebraValue[] state)
    {
      // -- check that id of this variable is valid w.r.t. the state
      if (getId () < 0 || getId () >= state.length) return null;

      // -- if state does not assign a value to this variable
      // -- it is ignored
      if (state [getId ()] == algebra.noValue ()) return null;
      
      // -- otherwise we get 'varname = algebra_value'
       CTLNode ctl = CTLFactory.createCTLAtomPropNode (getName ());
       return ctl.eq (CTLFactory.createCTLConstantNode (state [getId ()]));
      // XXX Since for now StateVariable is a boolean variable
      // XXX do var or \neg var

//        CTLNode ctl = CTLFactory.createCTLAtomPropNode (getName ());
//        if (state [getId ()].equals (algebra.bot ()))
//  	ctl = ctl.neg ();
//        return ctl;
    }
    
  }
  

  public class DefineVariable extends StateVariable
  {
    public DefineVariable (String name, MvSet value)
    {
      super (name, -1, -1);
      setMvSet (value);
    }
	
    public int size ()
    { return 0; }    
  }  
}
