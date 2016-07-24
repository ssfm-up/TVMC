header { package edu.toronto.cs.smv.parser;}
{
import java.util.*;
}

class FullSMVLexer extends Lexer;

options {k=6; filter=true; }

LPAREN   : '(';
RPAREN   : ')';


ASSIGNOP : ":=";

IMPLIES  : "->";

EQ       : '=';

OR       : '|';

AND      : '&';

NEG      : '!' ;

LBRACE   : '{';

RBRACE   : '}';

COMMA    : ',';

PLUS     : '+';

MINUS    : '-';

MULT     : '*';

DIV      : '/';

VARNAME  : ATOM ('.' ATOM)*;

protected ATOM    : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\'|'$'| '#'| '-')*;

protected DIGIT : '0'..'9';

NUMBER  : DIGIT (DIGIT)*;

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
import edu.toronto.cs.smv.VariableTable;
}

class FullSMVParser extends Parser;

options { buildAST=true; defaultErrorHandler = false;}

tokens {
    TRANS = "TRANS";
    UNION = "union";
    NEXT = "next";
    VAR = "VAR";
    IVAR = "IVAR";
    ASSIGN = "ASSIGN";
    MODULE = "MODULE";
    CASE = "case";
    ESAC = "esac";
    INIT = "init";
    CAPITAL_INIT = "INIT";
    DEFINE = "DEFINE";
    TRUE = "TRUE";
    FALSE = "FALSE";
    BOOLEAN = "boolean";
    
}

{
    VariableTable symbolTable = new VariableTable ();

    public VariableTable getSymbolTable ()
    { return symbolTable; }
}

simpleExpr: simpleExprPrivate[false];

protected simpleExprPrivate [boolean isNext]: 
           implExpr[isNext] | setExpr | caseExpr[isNext];

implExpr [boolean isNext]: iffExpr[isNext] (IMPLIES^ iffExpr[isNext])*;
iffExpr [boolean isNext]: orExpr[isNext] (IFF^ orExpr[isNext])*;
orExpr [boolean isNext]: andExpr[isNext] ((OR^ | XOR^) andExpr[isNext])*;
andExpr [boolean isNext]: negExpr[isNext] (AND^ negExpr[isNext])*;
negExpr [boolean isNext]: (NEG^)? comparisonExpr[isNext];
comparisonExpr [boolean isNext]: 
   modExpr[isNext]( (EQ^ | NEQ^ | LT^ | GT^ | LEQ^ | GEQ^) modExpr[isNext])*;
modExpr [boolean isNext]: sumExpr[isNext] (MOD^ sumExpr[isNext])*;
sumExpr [boolean isNext]: 
        multExpr[isNext] ((PLUS^ | MINUS^) multExpr[isNext])*;
multExpr [boolean isNext]: 
        basicExpr[isNext] ((MULT^ | DIV^) basicExpr[isNext])*;

basicExpr [boolean isNext]: maybeVarName[isNext] | number | boolConstant 
            | LPAREN! simpleExprPrivate[isNext] RPAREN! 
            // -- we include the next here but we have to be careful 
            // -- at the second path of parsing to make sure it is 
            // -- only used as allowed!
            | nextExpr[isNext];

nextExpr![boolean isNext]: {assert !isNext;}
        NEXT^ LPAREN! expr:simpleExprPrivate[true] RPAREN!
        {
            #nextExpr = #expr;
        };

protected maybeVarName! [boolean isNext] :
        v:VARNAME
        { 
            // -- if this is a variable, rewrite into next(v)
            if (isNext && symbolTable.getByName (v.getText ()) != null)
              {
               #maybeVarName = #([NEXT, "next"], v);
              }
            else
            // -- not a variable so next(v) == v
               #maybeVarName = #v;
        };
protected atomValued  returns [String val]: 
        var:VARNAME {val = var.getText ();};
protected numberValued returns [String val]: 
        num:NUMBER {val = num.getText ();};
atom: VARNAME;
number: NUMBER;



boolConstant: TRUE | FALSE;


setExpr: LBRACE^ setElement (COMMA! setElement)* RBRACE! ;
//          simpleExpr IN simpleExpr |
//          simpleExpr UNION simpleExpr;

//setElement: simpleExpr;
setElement: textOrNumber;


caseExpr [boolean isNext]: CASE^ (caseBody[isNext])+  ESAC!;
protected caseBody [boolean isNext]: 
        simpleExprPrivate[isNext] COL^ simpleExprPrivate[isNext] SEMI!;

varBlock!: VAR^ (varDecl)+;
ivarBlock!: IVAR^ (varDecl)+;


varDecl! {String varName; Object varType;}: 
        varName=atomValued COL varType=type SEMI! 
        {
            if (varType == Boolean.class)
              symbolTable.declarePropositional (varName);
            else if (varType instanceof Collection && 
                     ((Collection)varType).size () > 1)
              symbolTable.declareEnumerated (varName, (Collection)varType);
            else
              System.out.println ("Could not define variable: " + varName);
        }
    ; // -- add variable to SymbolTable

type! returns [Object typeObject] : BOOLEAN { typeObject = Boolean.class; }
    | typeObject=textSet;
   //| number DOT DOT^ number;  // -- this has to be resolved somehow

textSet! returns [Set set] {String val;}: 
        LBRACE^ val=textOrNumberValued 
        {set = new HashSet (); set.add (val);}
        (COMMA! val=textOrNumberValued {set.add (val);})*  
        RBRACE!; 

protected textOrNumber: atom | number;
protected textOrNumberValued returns [String value]: 
        value=atomValued | value=numberValued;


assignBlock: ASSIGN^ (assignBody)+;

assignBody: atom ASSIGNOP^ simpleExpr SEMI!
 |   (INIT^ | NEXT^) (LPAREN! atom  RPAREN! ASSIGNOP^ simpleExpr) SEMI!;

transBlock: TRANS^ simpleExpr (SEMI!)?;

initBlock: CAPITAL_INIT^ simpleExpr (SEMI!)?;
    
invarBlock: INVAR^ simpleExpr (SEMI!)?;

defineBlock: DEFINE^ (defineBody SEMI!)+;
defineBody: atom ASSIGNOP^ simpleExpr;

isaDecl: ISA^ atom;

moduleDecl: MODULE^ atom (ivarBlock)? varBlock (defineBlock)? 
                            (assignBlock | transBlock | initBlock)+; // and a lot more

start: moduleDecl (moduleDecl)* EOF!;



// -- the dd builder
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

class FullDDBuilder extends TreeParser;
options { defaultErrorHandler = false; }
{
    MvSet trans;
    MvSet init;
    MvSet invar;
    MvSet bot;
    MvSet top;
    MvSetFactory factory;
    VariableTable symbolTable;

    public FullDDBuilder (MvSetFactory _factory, VariableTable _symbolTable)
    {
        this ();
        factory = _factory;
        symbolTable = _symbolTable;
        
        top = factory.top ();
        bot = factory.bot ();
        
        init = top;
        trans = top;
        invar = top;
    }
                          
            

    Variable getByName (String name)
    {
        return symbolTable.getByName (name);
    }

    MvSet prime (MvSet set)
    {
       return set.renameArgs (symbolTable.getPrimeMap ());
    }

    MvSet getTrans ()
    { return trans; }
    MvSet getInit ()
    { return init; }
    MvSet getInvar ()
    { return invar; }

    Variable getPreState (Variable var)
    {
      if (var.isPreState ()) return var;
      
      String name = var.getName ();
      name = name.substring (0, name.length () - 1);
      return getByName (name);
    }

}

moduleDecl {String moduleName; } : 
        #(MODULE moduleName=atom (defineBlock)? (assignBlock | transBlock | initBlock)+);

defineBlock: #(DEFINE (defineBody)+);


defineBody: {MvSet val; String name;} 
      #(ASSIGNOP name=atom
                 val=expression[null])
                      { if (!name.equals ("running")) 
                            symbolTable.declareDefine (name, val);};


assignBlock : #(ASSIGN (assignBody)+);


assignBody {Variable var; boolean isInit = false; boolean isInvar = false; 
    MvSet val;

           }:
        #(ASSIGNOP 
            (#(INIT var=simpleVariable {isInit = true; })
            |
             #(NEXT var=simpleVariable {var = var.getNext ();})
            |
             var=simpleVariable {isInvar = true;}
            )
            val = expression[var])
            {
                if (isInit)
                    init = init.and (val);
                else if (isInvar)
                    invar = invar.and (val);
                  else
                    trans = trans.and (val);
            };

protected simpleVariable returns [Variable var] : 
        v:VARNAME {var = getByName (v.getText ()); };

protected variable returns [Variable var]:
       var = simpleVariable 
       |
       #(NEXT var = simpleVariable) { var = var.getNext ();};

protected atom returns [String val]: 
  v:VARNAME { val = v.getText ();} |
  v2:NUMBER {val = v2.getText ();};


transBlock {MvSet val;}: #(TRANS val=expression[null])
        { trans = trans.and (val); };


initBlock {MvSet val;}: #(INIT val = expression[null])
        { init = init.and (val);};

 

expression [Variable var] returns [MvSet val] 
  {String ident; Set set; MvSet caseCond; MvSet caseAction; val = null;
   MvSet lhs; MvSet rhs;
  }:
  (ident = atom 
   { 

          
     if (ident.equals ("running"))
      {
         if (var == null) val = top;
         else val = var.eq (top);
      }
     // -- an identifier can be a name of a variable
     // -- or a value of an enumerated type (i.e. constant) 
     // -- so we check for all cases here
     else if (getByName (ident) != null)
      {
        if (var != null)
          val = var.eq (getByName (ident));
        else
          val = ((StateVariable)getByName (ident)).getMvSet ();
      }
     else if (var instanceof EnumeratedVariable)
      val = ((EnumeratedVariable)var).eq (ident); 
     else if (var instanceof StateVariable)
      {
        val = ((StateVariable)var).getMvSet ();
        if (ident.equals ("0")) val = val.not ();
      }
     else if (var == null)
     {
       // -- must be a constant from the logic
       if (ident.equals ("1")) val = top;
       else val = bot;
     }
  
      // -- eaten up var
      var = null;
   } 
  |
  #(LBRACE set=enumSet) 
   {
     if (var instanceof StateVariable)
     {
        // -- the only possiblity here is var = {0,1}
        // -- val = ((StateVariable)var).getMvSet ();
        val = top;
     }
     else if (var instanceof EnumeratedVariable)
      {
         val = bot;
         EnumeratedVariable enumVar = (EnumeratedVariable)var;
         for (Iterator it = set.iterator (); it.hasNext ();)
           val = val.or (enumVar.eq (it.next ().toString ()));
      }
      var = null;
   } 
  |
 #(OR lhs = expression[null] rhs = expression[null]) { val = lhs.or (rhs);} 
 |
 #(AND lhs = expression[null] rhs = expression[null]) { val = lhs.and (rhs);}
 |
 #(NEG rhs = expression[null]) { val = rhs.not (); }
 |
 #(IMPLIES lhs = expression[null] rhs = expression[null]) 
                                   { val = lhs.not ().or (rhs); }
 |
 TRUE { val = top; } | FALSE { val = bot; } 
 |
 #(EQ {Variable var2;} var2 = variable val=expression[var2]) 
 |
// -- we rewrite next so that the only possible occurence is next(varname)
 #(NEXT {Variable var2;} var2 = variable) 
   { 
     var2 = var2.getNext ();
     // -- either we are in an assignement mode
     // -- or we have next(boolean_variable) as part of some expression
     if (var != null) 
      {
         val = var.eq (var2);
         var = null;
      }
     else
      {
         val = ((StateVariable)var2).getMvSet ();
      }
   } 
  |
  #(CASE { CaseTranslator kase = new CaseTranslator (); }
      (#(COL caseCond = expression[null] caseAction=expression[var]) 
        {
          kase.addCase (caseCond, caseAction);
        }
      )+)
   { val = kase.compute (); var = null;}) 
   { 
     if (var != null) 
       val = var.eq (val); 
     assert val != null : "Val is null after eq with: " + var;
   };
      


protected enumSet returns [Set set] {String v; set = new HashSet ();}:
        (v = atom {set.add (v);})+;
