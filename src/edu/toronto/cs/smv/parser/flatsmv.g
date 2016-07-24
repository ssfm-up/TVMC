/* Old deprecated parser */
header { package edu.toronto.cs.smv.parser;}
{
import java.util.*;
}

class SMVParser extends Parser;

options { buildAST=true; defaultErrorHandler=false;}

tokens {
  TRANS = "TRANS";
  VALUE<AST=edu.toronto.cs.smv.parser.ExprAST>;
  PROPVAR<AST=edu.toronto.cs.smv.parser.ExprAST>;
  ENUMVAR<AST=edu.toronto.cs.smv.parser.ExprAST>;
    ZERO<AST=edu.toronto.cs.smv.parser.ExprAST>;
    ONE<AST=edu.toronto.cs.smv.parser.ExprAST>;
  NEXTPROPVAR<AST=edu.toronto.cs.smv.parser.ExprAST>;
  NEXTENUMVAR<AST=edu.toronto.cs.smv.parser.ExprAST>;
  OR<AST=edu.toronto.cs.smv.parser.ExprAST>;
  AND<AST=edu.toronto.cs.smv.parser.ExprAST>;
  NEG<AST=edu.toronto.cs.smv.parser.ExprAST>;
  BOOLEQ<AST=edu.toronto.cs.smv.parser.ExprAST>; // "if and only if"
  VALEQ<AST=edu.toronto.cs.smv.parser.ExprAST>;  // "lhs = rhs"
   // separate token for primed variables, to
                // decouple from actual lexical distinction
    UNION = "union";
    NEXT = "next";
    VAR = "VAR";
    IVAR = "IVAR";
    ASSIGN = "ASSIGN";
    MODULE = "MODULE";
    CASE = "case";
    ESAC = "esac";
    INIT = "init";
    DEFINE = "DEFINE";
    
}
{
	public List setList;
  public Set varnames = new HashSet();
  public Set propVars = new HashSet();
  
  public void addVarName(String s)
  {
    varnames.add(s);
  }

  public void setVarProp(String s)
  {
    propVars.add(s);
  }

  public boolean isVarName(String s) 
  {
    return varnames.contains(s);
  }

  public boolean isPropVar(String s)
  {
    return propVars.contains(s);
  }
}

pgm : mainmodule EOF!;
mainmodule : MODULE^ a:VARNAME { System.out.println("Read module "+a); } (ivarblock)? varblock (defineblock)* 
	{System.out.println("Read defines");} (assignblock)+ (transblock)*;
ivarblock : IVAR^ {System.out.println("Slurped ivar");} (vardecl)+;
vardecl: vname:VARNAME { varnames.add(vname.getText()); }
    COL^ (typename:VARNAME { propVars.add(vname.getText()); }
    | set) SEMI!;
set : LBRACE! { 
	      setList = new LinkedList();} elements RBRACE!;
elements : (v:VARNAME (COMMA^ w:VARNAME { setList.add(w);})* {setList.add(v);
	System.out.println(setList);}) |
	ZERO (COMMA^ ONE)? | ONE (COMMA^ ZERO)
	;
varblock: VAR^ (vardecl)+;
defineblock: DEFINE^ (definition)+ { System.out.println ("Slurped defblock");};
definition: v:VARNAME ASSIGNOP^ predicate SEMI! {System.out.println("Definition of "+v.getText());};
// temporary debuggage
pred: predicate SEMI! {System.out.println("Done."); };
predicate: impPred;
impPred: orPred (IMPLIES^ { System.out.println ("Read ->"); } orPred)?;
orPred : andPred (OR^ andPred)*;
andPred : eqPred (AND^ eqPred)*;
eqPred! : lhs:atom {
      
          System.out.println("LHS: "+#lhs.toStringList());
      #eqPred = #lhs;
    } (EQ! rhs:atom {
        System.out.println("Handling equality.");
        if (((ExprAST)#rhs).isEnum())
        {
          System.out.println("LHS: "+#lhs.toStringList());
          System.out.println("in between");
          System.out.println("RHS "+#rhs.toStringList()+" is enum");
          try {
            //#eqPred = #[VALEQ, "=="];
            #eqPred = #(#[VALEQ, "=="], #lhs, #rhs);
            
          }catch (Exception e) { e.printStackTrace();}
        }
        else {
          #eqPred = #(#[BOOLEQ, "<=>"], lhs, rhs);
       }
      })?;
atom: ZERO { ((ExprAST) #atom).setProp(); }
  | ONE { ((ExprAST) #atom).setProp(); }
  | symbol:VARNAME {
      String sym = symbol.getText();
      // several cases to cope with!
      // 1. is it TRUE?
      if (sym.equals("TRUE"))
      {
        #atom = #[ONE];
        ((ExprAST) #atom).setProp();
      }
      // 2. FALSE?
      else if (sym.equals("FALSE")) {
        #atom = #[ZERO];
        ((ExprAST) #atom).setProp();
      }
      // 3. is it a propositional variable?
      else if (isPropVar(sym)) {
        #atom = #[PROPVAR, sym];
        ((ExprAST) #atom).setProp();
      }
      // 4. is it enumerated?
      else if (isVarName(sym)) {
        #atom = #[ENUMVAR, sym];
         ((ExprAST) #atom).setEnum(null);
      }
      else // 5. it's a value. 
        {
        System.out.println("Returning value atom: "+sym);
          #atom = #[VALUE, sym];    
          ((ExprAST) #atom).setEnum(null);  
        }
      }
  | LPAREN! predicate RPAREN! 
  |	NEXT LPAREN! vn:VARNAME RPAREN! { String pn =
      new String(vn.getText()+"'");
      if (isPropVar(vn.getText())) {
        #atom = #[NEXTPROPVAR, vn.getText()]; 
        ((ExprAST) #atom).setProp();
      }
      
      else {
        #atom = #[NEXTENUMVAR, vn.getText()];
        ((ExprAST) #atom).setEnum(null);  }
    }
    
  |	NEG^ atom { ((ExprAST) #atom).setProp(); }
  ;
assignblock: ASSIGN^ {System.out.println ("Reading ASSIGN");} init (next)?;
init : INIT^ LPAREN! v:VARNAME RPAREN! ASSIGNOP! predicate SEMI!
	{ System.out.println ("init of "+v.getText());};
next : NEXT^ LPAREN! VARNAME RPAREN! ASSIGNOP! astmt;
astmt: predicate SEMI! | kase | set SEMI!;
kase : CASE^ {System.out.println("Reading a case statement"); } (cases)+ ESAC! SEMI!;
cases : predicate COL^ astmt;
transblock: TRANS^ predicate;

class SMVLexer extends Lexer;

options {k=6; filter=true; }

LPAREN   : '(';
RPAREN   : ')';


ASSIGNOP : ":=";

IMPLIES  : "->";

EQ       : '=';

OR       : '|';

AND      : '&';

NEG      : '!' ;

ZERO     : '0';

ONE:        '1';

LBRACE   : '{';

RBRACE   : '}';

COMMA    : ',';

VARNAME  : IDENT ('.' IDENT)*;

protected IDENT    : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

COL      : ':';

SEMI	: ';';

COMMENT : "--" (options {greedy=true;}:~'\n')*
	{ 	$setType(Token.SKIP); } ;

WS      : (' ' | '\r' | '\t')+ { 
	$setType(Token.SKIP); };
	
NEWLINE : '\n'
	{
	newline();
	$setType(Token.SKIP);
	};

{
import java.util.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.mvset.MDDMvSetFactory.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.smv.*;
import edu.toronto.cs.smv.VariableTable.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.ReflectUtil;
import edu.toronto.cs.ctl.*;
}

class DDBuilder extends TreeParser;
options {defaultErrorHandler=false;}
{
    // -- mv-set factory class used to create the factory
	Class mvSetFactoryClass;
	MvSetFactory factory = null;

    // -- the transition relation we build
	MvSet tr;
    // -- the initial state
	MvSet init;

    // -- helper class to compile case statements
	CaseTranslator ct;

    // XXX current variable?!
    // XXX For enumerated types we want it to be something like
    // XXX a StateVariable
	Variable currentVar;
	
    // -- our working algebra
	IAlgebra algebra;
    
    // -- the symbol table
	VariableTable vt;


    public DDBuilder(Class _mvSetFactoryClass, IAlgebra _alg)
	{
        this ();
        algebra = _alg;
        mvSetFactoryClass = _mvSetFactoryClass;
	};


    // -- initialize mv-set factory once we know how many variables
    // -- we need
	public void initialize (int nvars)
	{
        try {
            factory = (MvSetFactory) ReflectUtil.callStaticMethod(
                mvSetFactoryClass, "newMvSetFactory",
                    new Class[] {IAlgebra.class, int.class},
                    new Object[] { algebra, new Integer (nvars)});
        }
        catch (Exception ex) {
            ex.printStackTrace ();
            // XXX figure good error handling mechanism at some point
            throw new RuntimeException (ex);
        }
        
        // -- set our factory
        vt.setMvSetFactory (factory);
        
        // -- we start with transition relation and initial constraint
        // -- being \top
        tr = factory.top();
        init = factory.top();
	}

	public MvSet getInit () { return init; }
    public MvSet getTrans () { return tr; }
    public VariableTable getSymbolTable () { return vt; }

}

// -- program rule -- the top level rule
// -- sets transition relation 'tr' and intial state 'init'
// -- pgm is a tree rooted at MODULE with children as described by the
// -- expression.
pgm : #(MODULE VARNAME (ivarblock)? varblock (assignblock)+
      (transblock)*)
//     { System.out.println("init: "+init.toDaVinci().toString()+"\n"+
//       "tr: "+tr.toDaVinci().toString()); }
;

// -- ignore ivarblock for now, due to antlr difficulties it looks like that
ivarblock : #(IVAR (vardecl)+) 
    {if (false) throw new RecognitionException();};

// -- variable declaration -- we collect all variables and initialize 
// -- everything.
varblock  { vt = new VariableTable(); } : #(VAR (vardecl)+) 
        {
            // -- we get here once all vardecl has been seen so
            // -- we know how many variables we have and the symbol
            // -- table has been constructed.
            
            // -- debug dump
            vt.dump();
            // -- initialize mv-set factory
            initialize (vt.getNumVars ());
      System.out.println("Done varblock");
        };  

// -- for each variable declaration, create an entry in symbol table
vardecl { List enumType; }: #(COL name:VARNAME (namedType:VARNAME {
	// named type : for now assume boolean
    vt.declarePropositional(name.getText ()); } 
    // -- NOTICE THE OR!            
    | 
    enumType = set 
    {
	  System.out.println("Declaration of variable " + name.getText () +
                         " with enumerated type " + enumType);
      // -- got a variable of enumerated type
	  vt.declareEnumerated(name.getText(), enumType);
	})) { System.out.println("Done Vardecl"); };

set returns [List enumType] 
    { 
        // -- stores prev. result of recursion
  	    List lastList;
  	    enumType  = new ArrayList ();
  	} : 
    // -- either we have 2 element set, or a set followed by an element
    #(COMMA ((l: VARNAME r:VARNAME { 
            enumType.add(l.getText()); 
            enumType.add(r.getText());
          }) 
    |
    (lastList=set m:VARNAME {enumType.addAll (lastList); enumType.add (m.getText()); })));

assignblock {MvSet i, n;} : 
    #(ASSIGN  i = init {init = init.and (i); 
       assert !init.isConstant () : "Mess of an initial condition";
     } 	
      (n = next { tr = tr.and (n); })?);

// -- init(var) := value;
init returns [MvSet pred] { pred = null; MvSet p; } : 
    #(INIT varName:VARNAME
	{
    // initialize the current-variable object
        System.out.println("Computing init("+varName.getText()+")");
	currentVar = vt.getByName(varName.getText());
	}
	p = predicate 
    {	  System.out.println("..computed!"); 
          // XXX Oh what a mess!
          if (currentVar instanceof StateVariable)
            pred = currentVar.eq (p);
          else
            pred = p;
	});

//  enumexpr returns [List values] { values = new ArrayList(); }
//      :
//        var:ENUMVAR |
//        nvar:NEXTENUMVAR |
//        val:VALUE |
//        #(CASE (#(COL predicate enumexpr))+ ESAC);

predicate returns [MvSet pred] { pred = null; 
		Variable var;
		MvSet l,r; 
} : 
        // or, and, not are the same regardless of type
	#(OR l=predicate r=predicate) { pred = l.or(r); } |
	#(AND l=predicate r=predicate) { pred = l.and(r);} | 	
	#(NEG l=predicate) { pred = l.not(); } |
    #(IMPLIES l=predicate r=predicate) { pred = l.not ().or (r); } |
	ZERO { pred = factory.bot();} | 
    ONE {pred = factory.top(); 	} | 
        // (some variable)=(some constant) 
    #(VALEQ 
      (lval:ENUMVAR { System.out.println("Enumvar: " + lval.getText ()); var = vt.getByName(lval.getText()); } |
        lv2:NEXTENUMVAR { var = vt.getByName(lv2.getText()).getNext(); }
      ) (rval:VALUE {
          System.out.println("Valeq Node:"+var);
          pred = var.eq(rval.getText());
        } 
        |
         rval2:NEXTENUMVAR 
                {
                    pred = ((EnumeratedVariable)var).eq 
                           ((EnumeratedVariable)vt.getByName 
                                   (rval2.getText ()).getNext ());
                }
            ) )
    | 
    #(BOOLEQ lprop:PROPVAR rnprop:NEXTPROPVAR) 
    { 
      var = vt.getByName (lprop.getText ());
      pred = ((StateVariable)var).eq ((StateVariable) 
                       vt.getByName (rnprop.getText ()).getNext ());
    } 
    |   
    i:VALUE { 
      System.out.println("Value: "+i.getText());
        pred = currentVar.eq(i.getText());
    } |
    ev:ENUMVAR {
      System.out.println("setting to ev "+ev.getText());
      pred = ((EnumeratedVariable)currentVar).
      eq(((EnumeratedVariable)vt.getByName(ev.getText())));
    } |
    n:PROPVAR {	
      var = vt.getByName (n.getText ());
      pred = ( (StateVariable) var).getMvSet();
    } |
    m:NEXTPROPVAR {	
      var = vt.getByName (m.getText ()).getNext();
      pred = ( (StateVariable) var).getMvSet();
    };

// -- next(var) := val;
next returns [MvSet pred] { MvSet pp; pred = null; } : 
    #(NEXT v:VARNAME 
         { // XXX Get the variable as variable. 
           // XXX We use global variables to pass them to rules.
           currentVar = vt.getByName(v.getText()).getNext();
	}
      pp = astmt) { pred = pp; 
            //System.out.println("next="+pred.toDaVinci().toString());
    };
      

astmt returns [MvSet pred] { MvSet pp = null; pred = null; } : 
    (pp = predicate  | pp = kase | pp = aset )
	{ pred = pp; };

aset returns [MvSet pred] {
	pred = null;
    MvSet old;
	} : #(COMMA (v:VARNAME 
        (w:VARNAME 
          { old = currentVar.eq(w.getText()); }
        | old=aset ) { pred = currentVar.eq(v.getText()).or(old); }
    | (ZERO ONE) {pred = factory.top();}
    | (ONE ZERO)
          	{pred=factory.top();}));

kase returns [MvSet pred] 
    { MvSet condition = null;
	  MvSet effect = null;
	  pred = null; }  :
	#(CASE 
        {
	      CaseTranslator ct = new CaseTranslator ();
	    } 
      (#(COL condition = predicate effect = astmt 
    	{
            // default case
		if (condition == factory.top ())
           ct.addDefault (effect);		
	    else 
           ct.addCase(condition, effect);
	    }))+ 
    { pred = ct.compute (); }) ;


// XXX Not done yet
defineblock : {if (false) throw new RecognitionException();};

transblock {MvSet p;} : #(TRANS (p = predicate))
        {
            tr = tr.and (p);
        }

    ;
