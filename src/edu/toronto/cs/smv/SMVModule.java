package edu.toronto.cs.smv;

import java.util.*;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.mvset.MDDMvSetFactory.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.mdd.*;
import edu.toronto.cs.mdd.ApplyFunctions.*;
import edu.toronto.cs.smv.IntApplyFunctions.*;
import edu.toronto.cs.ctl.*;


/*** Class representing an SMV module */
public abstract class SMVModule implements ModelCompiler
{
  // -- variable separator
  public static final String VAR_SEP = "_";
  public static final String NEXT_SUFFIX = "\'";


  PlusFunction plusFunc;
  LtFunction ltFunc;
  EqualsFunction eqFunc;
  


  // -- the name of the module
  String name;
  DeclaredVariables variables;

  MvSet top;
  MvSet bot;

  // class to create MvSetFactory
  Class mvSetFactoryClass;
  
  // -- factory to create MvSets
  MvSetFactory mvSetFactory = null;
  // -- algebra for MvSets
  IAlgebra algebra;


  //XXX
  MDDManager mddMgr;
  
  public SMVModule (String _name)
  {
    name = _name;
    variables = new DeclaredVariables ();
  }

  public AlgebraVariable declareAlgebraVariable (String name)
  {
    return variables.declareAlgebraVariable (name);
  }

  public IntVariable declareIntVariable (String name, int start, int stop)
  {
    return variables.declareIntVariable (name, start, stop);
  }
  
  public AlgebraDefine declareAlgebraDefine (String name)
  {
    return variables.declareAlgebraDefine (name);
  }
  public IntDefine declareIntDefine (String name)
  {
    return variables.declareIntDefine (name);
  }
  
  
  
  public int getNumDDVars ()
  {
    return variables.getNumDDVars ();
  }
  

  public String getName ()
  {
    return name;
  }

  public void setMvSetFactoryClass (Class v)
  {
    mvSetFactoryClass = v;
  }
  
  public Class getMvSetFactoryClass ()
  {
    return mvSetFactoryClass;
  }

  public MvSetFactory getMvSetFactory ()
  {
    return mvSetFactory;
  }

  public void setAlgebra (IAlgebra v)
  {
    algebra = v;
  }
  
  public IAlgebra getAlgebra ()
  {
    return algebra;
  }
  

  public MvSet intConstant (int i)
  {
    return ((MDDMvSetFactory)mvSetFactory).
      createMvSet (mddMgr.getLeafNode (i));
  }
  
  
  
  // -- computes an MvSet representation of the transition relation
  public abstract MvSet computeTrans ();
  // -- computes an MvSet representation of the initial state
  public abstract MvSet computeInit ();

  public XKripkeStructure compile ()
  {
    // -- we should have all options set so we can create what 
    // -- we need to operate
    seal ();
    
    MvSetMvRelation trans = new MvSetMvRelation
      (computeTrans (),
       mvSetFactory.buildCube 
       (variables.getUnPrimedVariablesIds ()),
       mvSetFactory.buildCube
       (variables.getPrimedVariablesIds ()),
       variables.getPrimeMap (),
       variables.getUnPrimeMap ());
    
    // -- compile the model into a XKripke structure
    return new XKripkeStructure (trans, 
				 computeInit (), 
				 variables.getPrimeMap (), 
				 mvSetFactory.buildCube 
				 (variables.getPrimedVariablesIds ()), 
				 mvSetFactory.buildCube 
				 (variables.getUnPrimedVariablesIds ()),
				 variables.getVarNames (),
				 getAlgebra (), 
				 getNumDDVars (),
				 getNumDDVars (), 
				 new DestructivePropertyFiller (),
				 new SMVStatePresenter ());
  }
  

  /***
   *** Given an MvSet in current state variables creates an invariant
   *** out of it
   ***/
  MvSet invariant (MvSet set)
  {
    return set.and (set.renameArgs (variables.getPrimeMap ()));
  }
  
  MvSet next (MvSet set)
  {
    return set.renameArgs (variables.getPrimeMap ());
  }
  
  
  void seal ()
  {
    try {
      mvSetFactory = (MvSetFactory) ReflectUtil.
	callStaticMethod (mvSetFactoryClass, "newMvSetFactory", 
			  new Class[] {IAlgebra.class, int.class},
			  new Object[] {algebra, 
					new Integer (getNumDDVars ())});

      top = mvSetFactory.top ();
      bot = mvSetFactory.bot ();
      
      if (mvSetFactoryClass == MDDMvSetFactory.class)
	{
	  mddMgr = ((MDDMvSetFactory)mvSetFactory).getMddManager ();
	  assert mddMgr != null;
	}
    }
    catch (Exception ex) {
      ex.printStackTrace ();
      assert false : ex;
    }

    if (mddMgr != null)
      {
	plusFunc = new PlusFunction (mddMgr);
	ltFunc = new LtFunction (mddMgr, algebra.top ().getId (), 
				 algebra.bot ().getId ());
	eqFunc = new EqualsFunction (mddMgr, algebra);
      }

    variables.printOut ();
  }
  


  public AlgebraVariable next (AlgebraVariable v)
  {
    return (AlgebraVariable)v.getNext ();
  }
  public IntVariable next (IntVariable v)
  {
    return (IntVariable)v.getNext ();
  }
  



  public class DestructivePropertyFiller extends CloningRewriter
  {

    public Object visitAtomPropNode (CTLAtomPropNode ctl, Object o)
    {
      // -- already done
      if (ctl.getMvSet () == null)
	{
	  Variable var = variables.get (ctl.getName ());
	  if (var != null)
	    ctl.setMvSet (var.mvSet ());
	  else
	    // -- not a valid variable, but may be a constant
	    {
	      if (algebra.getValue (ctl.getName ()) != algebra.noValue ())
		return CTLFactory.createCTLConstantNode 
		  (algebra. getValue (ctl.getName ()));
	      // -- otherwise we don't do anything since may be 
	      // -- somethone else handles this.
	    }
	}

      return ctl;
    }

    public Object visitEqualsNode (CTLEqualsNode ctl, Object o)
    {
      System.out.println ("Visiting equlas node: " + ctl);
      
      if (ctl.getLeft ().getClass () != CTLAtomPropNode.class ||
	  ctl.getRight ().getClass () != CTLAtomPropNode.class)
	return super.visitEqualsNode (ctl, o);


      
      // -- atom = atom   case, assume variable is always on the left
      CTLAtomPropNode left = (CTLAtomPropNode)ctl.getLeft ();
      CTLAtomPropNode right = (CTLAtomPropNode)ctl.getRight ();

      if (! (variables.get (left.getName ()) instanceof IntVariable))
	return super.visitEqualsNode (ctl, o);


      try {
	IntVariable var = (IntVariable)variables.get (left.getName ());	
	int value = Integer.parseInt (right.getName ());
	
	MvSet result = var.eq (value);
	CTLMvSetNode mvSetNode = CTLFactory.createCTLMvSetNode (result);
	mvSetNode.setName (ctl.toString ());
	return mvSetNode;
      } catch (NumberFormatException ex) {
	assert false :  ex;
      }
      return o;
    } 

    public Object visitPlaceholderNode (CTLPlaceholderNode ctl, Object o)
    {
      CTLAtomPropNode[] props = ctl.getProps ();
      MvSet[][] terms = new MvSet [props.length][];
      
      for (int i = 0; i < props.length; i++)
	terms [i] = buildTerms (props [i]);

      ctl.setTerms (terms);
      return ctl;
    }
    
    private MvSet[] buildTerms (CTLAtomPropNode prop)
    {
      Variable var = (Variable)variables.get (prop.getName ());
      
      if (var.getClass () == AlgebraVariable.class)
	return new MvSet [] { var.mvSet (), ((AlgebraVariable)var).not () };
      
      if (var.getClass () == IntVariable.class)
	{
	  IntVariable intVar = (IntVariable)var;
	  Integer[] values = intVar.getValues ();
	  MvSet[] terms = new MvSet [values.length];

	  for (int i = 0; i < values.length; i++)
	    terms [i] = intVar.eq (values [i].intValue ());
	  return terms;
	}
      assert false : "Unknown variable: " + prop;
      return null;
    }
    
    
  }


  public class SMVStatePresenter implements StatePresenter
  {
    public CTLNode[] toCTL (AlgebraValue[] state)
    {
      List result = new LinkedList ();

      for (Iterator it = variables.getVariables ().iterator (); 
	   it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  // -- skip all primed variables
	  if (var.getNext () == null) continue;

	  CTLNode ctl = var.toCTL (state);
	  if (ctl != null) result.add (ctl);
	}
	  
      
      return (CTLNode[])result.toArray (new CTLNode [result.size ()]);
    }

    public CTLNode[] toCTL (MvSet cube)
    {
      return toCTL ((AlgebraValue[])cube.cubeIterator ().next ());
    }
    
    public CTLNode[][] toCTL (AlgebraValue[][] states)
    {
      CTLNode[][] result = new CTLNode[states.length][];
      for (int i = 0; i < states.length; i++)
	result [i] = toCTL (states [i]);
      return result;
    }    
  }
  

  
  public class DeclaredVariables
  {
    int idCounter = 0;
    
    List variables;
    Map varNames;
    
    public DeclaredVariables ()
    {
      variables = new ArrayList ();
      varNames = new HashMap ();
    }

    void printOut ()
    {
      int count = 0;
      for (Iterator it = variables.iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  if (var instanceof AlgebraVariable ||
	      var instanceof IntVariable)
	    System.out.println ((count++) +  var.getName ());
	  
	}
    }
    

    public List getVariables ()
    {
      return variables;
    }
    
    public AlgebraVariable declareAlgebraVariable (String name)
    {
      
      AlgebraVariable var = new AlgebraVariable (name, getFreeId ());
      AlgebraVariable nextVar = new AlgebraVariable (name + "'", 
						     getFreeId ());
      var.setNext (nextVar);

      variables.add (var);
      varNames.put (var.getName (), var);
      variables.add (nextVar);
      varNames.put (nextVar.getName (), nextVar);

      return var;
    }

    public IntVariable declareIntVariable (String name, int start, int stop)
    {
      IntVariable var = new IntVariable (name, start, stop);
      IntVariable nextVar = new IntVariable (name + "'", start, stop);
      var.setNext (nextVar);
      

      // -- get enough bits for both variables
      int idBlock = getFreeIdBlock (var.bitSize () << 1);
      int[] evenBits = new int [var.bitSize ()];
      for (int i = 0; i < evenBits.length; i++)
	evenBits [i] = idBlock + (i << 1);

      int[] oddBits = new int [var.bitSize ()];
      for (int i = 0; i < oddBits.length; i++)
	oddBits [i] = idBlock + ((i << 1) | 1);      
      

      var.setBits (evenBits);
      nextVar.setBits (oddBits);      

      variables.add (var);
      varNames.put (var.getName (), var);
      variables.add (nextVar);
      varNames.put (nextVar.getName (), nextVar);
      return var;
    }

    public AlgebraDefine declareAlgebraDefine (String name)
    {
      AlgebraDefine def = new AlgebraDefine (name);
      variables.add (def);
      varNames.put (def.getName (), def);
      return def;
    }
    public IntDefine declareIntDefine (String name)
    {
      IntDefine def = new IntDefine (name);
      variables.add (def);
      varNames.put (def.getName (), def);
      return def;
    }
    
    

    public Variable get (String name)
    {
      return (Variable)varNames.get (name);
    }
    
    
    
    int getFreeId ()
    {
      return getFreeIdBlock (1);
    }
    
    // -- allocates a block of size many consequetive ids and returns 
    // -- the first one
    int getFreeIdBlock (int size)
    {
      int firstId = idCounter;
      idCounter += size;
      return firstId;
    }
    
    
    /**
     ** number of declared variables
     **/
    public int size ()
    {
      assert false : "Not implemented";
      return variables.size ();
    }
    
    public int getNumDDVars ()
    {
      int size = 0;
      for (Iterator it = variables.iterator (); it.hasNext (); )
	size += ((Variable)it.next ()).size ();
      return size;
    }
    

    public String[] getVarNames ()
    {
      return null;
    }
    
    public int[] getPrimedVariablesIds ()
    {
      int[] primedVars = new int [getNumDDVars () >> 1];

      int count = 0;
      for (Iterator it = variables.iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  if (var.getNext () != null) continue;
	  
	  if (var.getClass () == AlgebraVariable.class)
	    primedVars [count++] = ((AlgebraVariable)var).getId ();
	  else if (var.getClass () == IntVariable.class)
	    {
	      IntVariable intVar = (IntVariable)var;
	      for (int i = 0; i < intVar.bitSize (); i++)
		primedVars [count++] = intVar.getBitId (i);
	    }
	}
      return primedVars;
    }

    // XXX This is broken!
    public int[] getUnPrimedVariablesIds ()
    {
      int[] unPrimedVars = new int [getNumDDVars () >> 1];

      int count = 0;
      for (Iterator it = variables.iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  if (var.getNext () == null) continue;
	  
	  if (var.getClass () == AlgebraVariable.class)
	    unPrimedVars [count++] = ((AlgebraVariable)var).getId ();
	  else if (var.getClass () == IntVariable.class)
	    {
	      IntVariable intVar = (IntVariable)var;
	      for (int i = 0; i < intVar.bitSize (); i++)
		unPrimedVars [count++] = intVar.getBitId (i);
	    }
	}
      return unPrimedVars;
    }

    public int[] getUnPrimeMap ()
    {
      int[] unPrimeMap = new int [getNumDDVars ()];
      
      for (Iterator it = variables.iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  if (var.getNext () == null) continue;
	  
	  if (var.getClass () == AlgebraVariable.class)
	    {
	      AlgebraVariable nextVar = next ((AlgebraVariable)var);
	      unPrimeMap [((AlgebraVariable)var).getId ()] 
		= ((AlgebraVariable)var).getId ();
	      unPrimeMap [nextVar.getId ()] = ((AlgebraVariable)var).getId ();
	    }
	  else if (var.getClass () == IntVariable.class)
	    {
	      IntVariable intVar = (IntVariable)var;
	      IntVariable nextVar = next (intVar);
	      
	      for (int i = 0; i < intVar.bitSize (); i++)
		{
		  unPrimeMap [intVar.getBitId (i)] = intVar.getBitId (i);
		  unPrimeMap [nextVar.getBitId (i)] = intVar.getBitId (i);
		}
	    }
	}
      return unPrimeMap;
    }
    
    public int[] getPrimeMap ()
    {
      int[] primeMap = new int [getNumDDVars ()];
      
      for (Iterator it = variables.iterator (); it.hasNext ();)
	{
	  Variable var = (Variable)it.next ();
	  if (var.getNext () == null) continue;
	  
	  if (var.getClass () == AlgebraVariable.class)
	    {
	      AlgebraVariable nextVar = next ((AlgebraVariable)var);
	      primeMap [((AlgebraVariable)var).getId ()] = nextVar.getId ();
	      primeMap [nextVar.getId ()] = nextVar.getId ();
	    }
	  else if (var.getClass () == IntVariable.class)
	    {
	      IntVariable intVar = (IntVariable)var;
	      IntVariable nextVar = next (intVar);
	      
	      for (int i = 0; i < intVar.bitSize (); i++)
		{
		  primeMap [intVar.getBitId (i)] = nextVar.getBitId (i);
		  primeMap [nextVar.getBitId (i)] = nextVar.getBitId (i);
		}
	    }
	}
      return primeMap;
    }

    public String toString ()
    {
      StringBuffer sb = new StringBuffer ();
      for (Iterator it = variables.iterator (); it.hasNext ();)
	sb.append (it.next ().toString () + "\n");
      return sb.toString ();
    }
    
  }

  public abstract class Variable
  {
    String name;
    MvSet mvSet;

    Variable next;
    
    public Variable (String _name)
    {
      name = _name;
      mvSet = null;
      next = null;
    }
    public void setMvSet (MvSet v)
    {
      mvSet = v;
    }
    public MvSet mvSet ()
    {
      if (mvSet == null)
	mvSet = buildMvSet ();
      return mvSet;
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
    
    
    public String toString ()
    {
      return getName ();
    }
    
    abstract MvSet buildMvSet ();
    abstract int size ();

    public MvSet eq (MvSet v)
    {
      return mvSet ().eq (v);
    }
    public MvSet eq (Variable v)
    {
      return eq (v.mvSet ());
    }
    

    public CTLNode toCTL (AlgebraValue[] state)
    {
      return null;
    }
    
    
  }
  

  public class AlgebraDefine extends Variable
  {
     public AlgebraDefine (String _name)
    {
      super (_name);
    }
    

    public int size ()
    {
      return 0;
    }
    
    MvSet buildMvSet ()
    {
      return null;
    }
    public MvSet and (MvSet v)
    {
      return mvSet ().and (v);
    }
    public MvSet or (MvSet v)
    {
      return mvSet ().or (v);
    }
    public MvSet not ()
    {
      return mvSet ().not ();
    }
    
    
    public MvSet and (AlgebraVariable v)
    {
      return mvSet ().and (v.mvSet ());
    }
    public MvSet or (AlgebraVariable v)
    {
      return mvSet ().or (v.mvSet ());
    }    
    
  }

  public class AlgebraVariable extends AlgebraDefine
  {
    int id;

    public AlgebraVariable (String _name, int _id)
    {
      super (_name);
      id = _id;
    }
    
    public int getId ()
    {
      return id;
    }
    

    public int size ()
    {
      return 1;
    }
    
    MvSet buildMvSet ()
    {
      return mvSetFactory.createProjection (getId ());
    }

    public CTLNode toCTL (AlgebraValue[] state)
    {
      if (state [getId ()] == algebra.noValue ()) return null;
      
      CTLNode ctl = CTLFactory.createCTLAtomPropNode (this.getName ());
      return ctl.eq (CTLFactory.createCTLConstantNode (state [getId ()]));
    }
    

  }

  
  public class IntDefine extends Variable
  {

    public IntDefine (String _name)
    {
      super (_name);
    }
    
    public int size ()
    {
      return 0;
    }

    public MvSet buildMvSet ()
    {
      return null;
    }
    
    
    public MvSet plus (int num)
    {
      MDDNode node = ((MDDMvSet)mvSet ()).getMddNode ();
      MDDNode numNode = mddMgr.getLeafNode (num);
      MDDNode result = mddMgr.apply (plusFunc, node, numNode);
      
      return ((MDDMvSetFactory)mvSetFactory).createMvSet (result);

      
    }
    public MvSet minus (int num)
    {
      return plus (-num);
    }

    public MvSet eq (int num)
    {
      MDDNode node = ((MDDMvSet)mvSet ()).getMddNode ();
      MDDNode numNode = mddMgr.getLeafNode (num);
      MDDNode result = mddMgr.apply (eqFunc, node, numNode);
      return ((MDDMvSetFactory)mvSetFactory).createMvSet (result);
    }
    
    public MvSet leq (int num)
    {
      return lt (num).or (eq (num));
    }
    public MvSet geq (int num)
    {
      return gt (num).or (eq (num));
    }
    public MvSet lt (int num)
    {
      MDDNode node = ((MDDMvSet)mvSet ()).getMddNode ();
      MDDNode numNode = mddMgr.getLeafNode (num);
      MDDNode result = mddMgr.apply (ltFunc, node, numNode);
      return ((MDDMvSetFactory)mvSetFactory).createMvSet (result);
    }
    public MvSet gt (int num)
    {
      MDDNode node = ((MDDMvSet)mvSet ()).getMddNode ();
      MDDNode numNode = mddMgr.getLeafNode (num);
      MDDNode result = mddMgr.apply (ltFunc, numNode, node);
      return ((MDDMvSetFactory)mvSetFactory).createMvSet (result);
    }    
    
  }

  public class IntVariable extends IntDefine
  {

    String name;
    Integer[] values;

    EnumType enumType;
    int enumWidth;

    int[] bits;

    public IntVariable (String _name, int start, int stop)
    {
      super (_name);

      values = new Integer [stop - start + 1];
      for (int i = 0; i < values.length; i++)
	values [i] = new Integer (start + i);

      enumType = new EnumType (values);
      enumWidth = enumType.bitSize ();
    }

    public Integer[] getValues ()
    {
      return values;
    }
    

    public int size ()
    {
      return bitSize ();
    }
    
    public int bitSize ()
    {
      return enumWidth;
    }
    

    public void setBits (int[] v)
    {
      bits = v;
    }
    
    
    public MvSet buildMvSet ()
    {
      MDDNode result = mddMgr.getLeafNode (0);

      for (int i = 0; i < values.length; i++)
	result = 
	  mddMgr.apply (plusFunc, result, var (values [i], values [i]));

      for (int i = 0; i < values.length; i++)
	{
	  System.out.println ("value (" + values [i] + ") is " + 
			      ArrayUtil.toString 
			      (enumType.bitValue (values [i])));
	  
	}
      

      return ((MDDMvSetFactory)mvSetFactory).createMvSet (result);
    }
    
    // ---
    // --- builds an MDD corresponding to 
    // --- (getName = varVal) * termVal
    MDDNode var (Integer varVal, Integer termVal)
    {
      
      // -- expand argIdx and call mddMgr.buildPoint 
      int[] ddArgIdx = new int [mddMgr.getNvars ()];
      Arrays.fill (ddArgIdx, MDDManager.NO_VALUE);
      
      int[] enm = enumType.bitValue (varVal);
      for (int i = 0; i < enumWidth; i++)
	{
	  if (enm [i] == -1) continue;
	  ddArgIdx [getBitId (i)] = enm [i];
	}
      

      return mddMgr.buildPoint (ddArgIdx, 
				mddMgr.getLeafNode (termVal.intValue ()), 
				mddMgr.getLeafNode (0));
    }

    int getBitId (int bit)
    {
      return bits [bit];
    }    


    public CTLNode toCTL (AlgebraValue[] state)
    {
      int[] enm = new int [enumWidth];
      Arrays.fill (enm, -1);
      
      for (int i = 0; i < enumWidth; i++)
	{
	  if (state [getBitId (i)] == algebra.top ())
	    enm [i] = 1;
	  else if (state [getBitId (i)] == algebra.bot ())
	    enm [i] = 0;
// 	  else
// 	    Assert.assert ("For int variable " + this.getName () + 
// 			   " bit " + i +
// 			   " of the encoding is " + state [i] + 
// 			   " and not top or bot" );
	}
      
      Object[] enumValues = enumType.enumValues (enm);
      if (enumValues.length == values.length)
	return null;
      
      return ctlForValues (enumValues);
    }

    /**** 
     **** Given a set of enumValues for example a, b, c
     **** builds   var = a \/ var = b \/ var = c
     ****  if enumValues cotains a single element returns
     ****    var = a
     ****/ 
    CTLNode ctlForValues (Object[] enumValues)
    {

      CTLAtomPropNode self = 
	CTLFactory.createCTLAtomPropNode (this.getName ());

      CTLNode result = null;

      for (int i = 0; i < enumValues.length; i++)
	{
	  if (i == 0) 
	    result = self.eq
	      (CTLFactory.createCTLAtomPropNode (enumValues [i].toString ()));
	  else
	    result = result.or (self.eq
				(CTLFactory.createCTLAtomPropNode 
				 (enumValues [i].toString ())));
	}
      return result;
    }
    
  }
  

  public static class CaseStatement
  {

    // -- final result
    MvSet result = null;
    // -- conjunction of negations of prev. conditions
    // -- i.e. !cond1 & !cond2 & !cond3 ... etc
    MvSet condNeg = null;


    public CaseStatement ()
    {
    }
    public CaseStatement addCase (Variable v, MvSet stmt)
    {
      return addCase (v.mvSet (), stmt);
    }
    

    // -- add one case of the form
    //  cond: stmt
    public CaseStatement addCase (MvSet cond, MvSet stmt)
    {
      if (result == null)
	{
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
    public CaseStatement addDefault (MvSet stmt)
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

  public static class BoolCaseStatement
  {

    // -- final result
    MvSet result = null;
    // -- conjunction of negations of prev. conditions
    // -- i.e. !cond1 & !cond2 & !cond3 ... etc
    MvSet condNeg = null;
    MvSet mvBot;

    public BoolCaseStatement (MvSet _mvBot)
    {
      mvBot = _mvBot;
    }

    // -- add one case of the form
    //  cond: stmt
    public BoolCaseStatement addCase (MvSet cond, MvSet stmt)
    {
      if (result == null)
	{
	  result = cond.and (stmt);
	  condNeg = cond.eq (mvBot);
	}
      else
	{
	  result = result.or (condNeg.and (cond.and (stmt)));
	  condNeg = condNeg.and (cond.eq (mvBot));
	}
      return this;
    }

    // -- add the default case
    // 1 : stmt
    public BoolCaseStatement addDefault (MvSet stmt)
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














