package edu.toronto.cs.smv;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.toronto.cs.algebra.AlgebraValue;
import edu.toronto.cs.algebra.IAlgebra;
import edu.toronto.cs.ctl.CTLAtomPropNode;
import edu.toronto.cs.ctl.CTLEqualsNode;
import edu.toronto.cs.ctl.CTLFactory;
import edu.toronto.cs.ctl.CTLMvSetNode;
import edu.toronto.cs.ctl.CTLNode;
import edu.toronto.cs.ctl.CTLPlaceholderNode;
import edu.toronto.cs.modelchecker.CTLReWriter;
import edu.toronto.cs.modelchecker.CloningRewriter;
import edu.toronto.cs.modelchecker.StatePresenter;
import edu.toronto.cs.mvset.MvSet;
import edu.toronto.cs.mvset.MvSetFactory;
import edu.toronto.cs.util.EnumType;



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

  public VariableTable()
  {
    idCounter = 0;
    varNames = new HashMap ();
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

  public DefineVariable declareDefine (String name, MvSet value)
  {
      System.out.println ("DEFINE: " + name);
    DefineVariable v = new DefineVariable (name, value);
    varNames.put (v.getName(), v);
    return v;
    
  }
  
  int getIdBlock (int size)
  {
    int result = idCounter;
    idCounter += size;
    return result;
  }
  
  
  public EnumeratedVariable declareEnumerated (String name, Collection values)
  {
    return declareEnumerated (name, (String[])
			      values.toArray (new String [values.size ()]));
    
  }
  
  public EnumeratedVariable declareEnumerated (String name, String[] values)
  {
    
    EnumeratedVariable var = 
      new EnumeratedVariable (name, values);
    
    EnumeratedVariable primed =
      new EnumeratedVariable (name + "'", values);

    var.setNext (primed);

    int startBit = getIdBlock (var.bitSize () * 2);

    // -- assign even bits from startBit to var, and odd bits to primed
    int[] bits = new int [var.bitSize ()];
    for (int i = 0; i < bits.length; i++)
      bits [i] = startBit + (i * 2);
    var.setBits (bits);
    bits = (int[]) bits.clone ();
    for (int i = 0; i < bits.length; i++)
      bits [i] ++;
    primed.setBits (bits);
    
    varNames.put (var.getName (), var);
    varNames.put (primed.getName (), primed);
    return var;
  }
  
  // -- declare a propositional (boolean) variable
  public StateVariable declarePropositional (String name)
  {
    String primed = name + "'";
    StateVariable v = new StateVariable (name, getIdBlock (1));
    StateVariable nextv = new StateVariable (primed, getIdBlock (1));

    v.setNext (nextv);

    varNames.put (v.getName (), v);
    varNames.put (nextv.getName (), nextv);

    return v;
  }
  
  public StateVariable declareParentlessPropositional(String name) {
	  name = name.replaceAll("\\(", "");
	  name = name.replaceAll("\\)", "");
	  return declarePropositional(name);
  }

  public int getNumVars ()
  {
    return idCounter;
  }

  public int getNumDDVars ()
  {
    int size = 0;
    for (Iterator it = varNames.values ().iterator (); it.hasNext (); )
      size += ((Variable)it.next ()).size ();
    return size;
  }
  
  
  public Variable getByParentlessName (String name)
  {
	  name = name.replaceAll("\\(", "");
	  name = name.replaceAll("\\)", "");
	  return getByName(name);
  }

  public Variable getByName (String name)
  {
    return (Variable)varNames.get (name);
  }

  public String[] getVarNames()
  {
	  String[] names = new String[getNumDDVars()];
    for(Object obj : getVariables()) {
    	StateVariable var = (StateVariable) obj;
    	names[var.getId()] = var.getName();
    }
    
    return names;
    
  }
  
  public Collection getVariables ()
  {
    return varNames.values ();
  }
  
  
  // -- a helper debug method, dumps this symbol table
  public void dump () 
  {
     System.out.println("Dumping: " + varNames.keySet().size ());

//     if (!variables.isEmpty ())
//       System.out.println ("more coming!");
    
     for (Iterator it = varNames.keySet().iterator(); it.hasNext(); )
       {
	 Variable v = (Variable) (varNames.get(it.next ()));
	 

     System.out.println(v);
     
       }
    
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
	      // -- skip all post-state variables
	      if (var.isPostState ()) continue;

	      // -- let the variable handle its toCTL conversion
	      CTLNode ctl = var.toCTL (state);
	      if (ctl != null) result.add (ctl);
	    }
	  
	  return (CTLNode[])result.toArray (new CTLNode [result.size ()]);
	}
	
	public CTLNode[] toCTL (MvSet cube)
	{
	  return toCTL ((AlgebraValue[]) cube.cubeIterator ().next ());
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
  
  // -- returns a mapping that maps all unprimed variables to primed versions
  public int[] getPrimeMap ()
  {
      int[] primeMap = new int [getNumVars ()];
      
      // -- loop over all the variables
      for (Iterator it = getVariables ().iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  // -- skip all post-state (i.e. primed) variables
	  if (var.isPostState ()) continue;

	  
	  if (var.getClass () == StateVariable.class)
	    {
	      StateVariable nextVar = (StateVariable)var.getNext ();
	      primeMap [((StateVariable)var).getId ()] = nextVar.getId ();
	      primeMap [nextVar.getId ()] = nextVar.getId ();
	    }
	  else if (var.getClass () == EnumeratedVariable.class)
	    {
	      EnumeratedVariable enumVar = (EnumeratedVariable)var;
	      EnumeratedVariable nextVar 
		= (EnumeratedVariable)enumVar.getNext ();
	      
	      for (int i = 0; i < enumVar.bitSize (); i++)
		{
		  primeMap [enumVar.getBitId (i)] = nextVar.getBitId (i);
		  primeMap [nextVar.getBitId (i)] = nextVar.getBitId (i);
		}
	    }
	}
      return primeMap;
  }
  

  // -- returns a mapping from unprimed to primed variables
  // XXX We should merge getUnPrimeMap and getPrimeMap somehow since
  // XXX they do exactly the same thing
  public int[] getUnPrimeMap ()
  {
      int[] unPrimeMap = new int [getNumVars ()];
      
      // -- loop over all the variables
      for (Iterator it = getVariables ().iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  // -- skip all post-state (i.e. primed) variables
	  if (var.isPostState ()) continue;

	  
	  if (var.getClass () == StateVariable.class)
	    {
	      StateVariable nextVar = (StateVariable)var.getNext ();
	      unPrimeMap [((StateVariable)var).getId ()] 
		= ((StateVariable)var).getId ();
	      unPrimeMap [nextVar.getId ()] 
		= ((StateVariable)var).getId ();
	    }
	  else if (var.getClass () == EnumeratedVariable.class)
	    {
	      EnumeratedVariable enumVar = (EnumeratedVariable)var;
	      EnumeratedVariable nextVar 
		= (EnumeratedVariable)enumVar.getNext ();
	      
	      for (int i = 0; i < enumVar.bitSize (); i++)
		{
		  unPrimeMap [enumVar.getBitId (i)] = enumVar.getBitId (i);
		  unPrimeMap [nextVar.getBitId (i)] = enumVar.getBitId (i);
		}
	    }
	}
      return unPrimeMap;
  }



  // -- returns ids of all of the varialbes
  // -- that are either preState (unprimed), or postState (primed)
  int[] getVariableIds (boolean preState)
  {
    // -- half of the variables we have are pre-state, the other is post-state
    // -- so >> 1 is division by 2 :)
    int[] vars = new int [getNumVars () >> 1];
    
    int count = 0;
    for (Iterator it = getVariables ().iterator (); it.hasNext ();)
      {
	Variable var = (Variable)it.next ();
	  if (var.isPreState () != preState) continue;
	  
	  if (var.getClass () == StateVariable.class)
	    vars [count++] = ((StateVariable)var).getId ();
	  else if (var.getClass () == EnumeratedVariable.class)
	    {
	      EnumeratedVariable enumVar = (EnumeratedVariable)var;
	      for (int i = 0; i < enumVar.bitSize (); i++)
		vars [count++] = enumVar.getBitId (i);
	    }
      }
    return vars;
  }
  

  public int[] getPrimedVariablesIds ()
  {
    return getVariableIds (false);
  }

  public int[] getUnPrimedVariablesIds ()
  {
    return getVariableIds (true);
  }
  
  

  public abstract class Variable 
  {
    // -- the name
    String name;
    Variable next;

    public Variable (String _name)
    {
      name = _name;
      next = null;
    }
    
    public String getName ()
    {
      return name;
    }
    public Variable getNext ()
    {
      return next;
    }
    public void setNext (Variable v)
    {
      next = v;
    }

    public boolean isPostState ()
    {
      return !isPreState ();
    }

    public boolean isPreState ()
    {
      return next != null;
    }

    public String toString ()
    {
      return name;
    }

    public abstract MvSet eq (Variable v);
    
    
    // -- returns an mv-set corresponding to var = next (var) in SMV
    public abstract MvSet eqNext ();
    
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
    
    public EnumeratedVariable (String _name, String[] _values)
    {
      super (_name);
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
    
    public MvSet __eq (EnumeratedVariable v)
    {
      assert this.isPostState () || v.isPostState () 
	: "this: " + this + " v: " + v;
      
      // -- v' == v is equivalent to v == '
      if (v.isPreState ()) return v.eq (this);
      if (v.isPostState () && v == getNext ()) return eqNext ();
      
     
      throw new UnsupportedOperationException ("Cannot compare " + 
					       getName () + 
					       " to " + v.getName ());
    }
    

    public MvSet eqNext ()
    {
      assert isPreState () : "Cannot apply eqNext on post-state var";
      
      EnumeratedVariable vNext = (EnumeratedVariable)getNext ();
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
  
  
    
    public StateVariable (String name, int _id)
    {
      super (name);
      id = _id;
    }
    public StateVariable (String name, MvSet _mvSet)
    {
      super (name);
      mvSet = _mvSet;
      id = -1;
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

    public MvSet eqNext ()
    {
      assert isPreState () : "Cannot apply eqNext on post-state variables";
      
      return getMvSet ().eq (((StateVariable)getNext ()).getMvSet ());
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
	    super (name, value);
	}
	
	public int size ()
	{ return 0; }
    }


  // -- helper class to build SMV case statements
  public static class CaseTranslator
  {

    // -- final result
    MvSet result = null;
    // -- conjunction of negations of prev. conditions
    // -- i.e. !cond1 & !cond2 & !cond3 ... etc
    MvSet condNeg = null;
    
    // -- add one case of the form
    //  cond: stmt
    public CaseTranslator addCase (MvSet cond, MvSet stmt)
    {
      // first case
      if (result == null)
	{
	  if (cond == null)
	    System.out.println("Cond null!");
	  if (stmt == null)
	    System.out.println("Stmt null!");
// 	  System.out.println("Cond="+cond.toDaVinci().toString()+
// 			     ",\n eff="+stmt.toDaVinci().toString());
	  
	  result = cond.and (stmt);
	  condNeg = cond.not ();
	}
      else
	{
	  result = result.or (condNeg.and (cond.and (stmt)));
	  condNeg = condNeg.and (cond.not ());
	}
      return this;
    }

    // -- add the default case
    // 1 : stmt
    public CaseTranslator addDefault (MvSet stmt)
    {
      result = result.or (condNeg.and (stmt));
      condNeg = null;
      return this;
    }
    
    public MvSet compute ()
    {
      return result;
    }    
    
  }
  
}
