header { package edu.toronto.cs.gclang.parser;}
{
import java.util.*;
}

class GCLangLexer extends Lexer;

options {k=6; filter=true; }

LPAREN   : '(';
RPAREN   : ')';


ASSIGNOP : ":=";

CHOICE : "||";

IMPLIES  : "->";
IFF      : "<->";

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
import edu.toronto.cs.gclang.parser.VariableTable;
import edu.toronto.cs.gclang.parser.VariableTable.*;
}

class GCLangParser extends Parser;

options { k = 2; buildAST=true; defaultErrorHandler = false;}

tokens {
    VAR = "VAR";
    INIT = "INIT";
    RULES = "RULES";
    BOOLEAN = "boolean";
    TRUE = "true";
    FALSE = "false";
    SKIP = "skip";
    NAME = "NAME";
    IF = "if";
    THEN = "then";
    ELSE = "else";
    FI = "fi";
}

{
    VariableTable symbolTable = new VariableTable (2);

    public VariableTable getSymbolTable ()
    { return symbolTable; }
}

// -- an expression, right now we combine boolean and non-boolean expressions
// -- but this may need to be changed
expr:  implExpr | setExpr;

implExpr : iffExpr (IMPLIES^ iffExpr)*;
iffExpr : orExpr (IFF^ orExpr)*;
orExpr : andExpr ((OR^ | XOR^) andExpr)*;
andExpr : negExpr (AND^ negExpr)*;
negExpr : (NEG^)? comparisonExpr;
comparisonExpr : 
   modExpr ( (EQ^ | NEQ^ | LT^ | GT^ | LEQ^ | GEQ^) modExpr)*;
modExpr : sumExpr (MOD^ sumExpr)*;
sumExpr : 
        multExpr ((PLUS^ | MINUS^) multExpr)*;
multExpr : 
        basicExpr ((MULT^ | DIV^) basicExpr)*;
basicExpr : v:VARNAME | number | boolConstant 
            | LPAREN! expr RPAREN! ;


protected atomValued  returns [String val]: 
        var:VARNAME {val = var.getText ();};
protected numberValued returns [String val]: 
        num:NUMBER {val = num.getText ();};
atom: VARNAME;
number: NUMBER;
boolConstant: TRUE | FALSE;

setExpr: LBRACE^ setElement (COMMA! setElement)* RBRACE! ;
setElement: textOrNumber;

// -- program blocks start here

// -- variable declaration block, note that we do not put
// -- variables into the output tree
varBlock!: VAR^ (varDecl)+;

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

type! returns [Object typeObject] : 
        BOOLEAN { typeObject = Boolean.class; } |
        typeObject=textSet;
   //| number DOT DOT^ number;  // -- this has to be resolved somehow

textSet! returns [Set set] {String val;}: 
        LBRACE^ val=textOrNumberValued 
        {set = new HashSet (); set.add (val);}
        (COMMA! val=textOrNumberValued {set.add (val);})*  
        RBRACE!; 

protected textOrNumber: atom | number;
protected textOrNumberValued returns [String value]: 
        value=atomValued | value=numberValued;


// -- command 
command: sequenceCommand;
sequenceCommand: choiceCommand (SEMI^ choiceCommand)*;
choiceCommand: atomicCommand (CHOICE^ atomicCommand)*;
atomicCommand: assign | skip | ite | LPAREN! command RPAREN!;

assign!: one:atom ASSIGNOP^ two:expr 
        {
            String varName = #one.getText ();
            Variable var = symbolTable.getByName (varName);
            if (var != null && var instanceof EnumeratedVariable)
                #assign = #([ASSIGNOPSPEC, ":=="], #one, #two); 
            else
                #assign = #(ASSIGNOP, #one, #two);
        };
skip : SKIP;
ite: IF^ LPAREN! expr RPAREN! (THEN!)? iteBody;
iteBody: command ELSE^ command FI!;

protected guard : expr;

guardedCommand: guard COL^ command;

rulesBlock: RULES^ (guardedCommand)+ ;

// -- Initializer block, needs more work
initBlock: INIT^ expr (SEMI!)?;

start: NAME^ atom varBlock initBlock rulesBlock EOF!;

{
    import edu.toronto.cs.gclang.parser.VariableTable.*;
    import edu.toronto.cs.mvset.*;
    import edu.toronto.cs.algebra.*;
    import edu.toronto.cs.util.*;

    import java.util.Set;
    import java.util.HashSet;
    import java.util.Iterator;
    
}
class GCLangBuilder extends TreeParser;
options {defaultErrorHandler = false; }
{
    MvSet trans;
    MvSet init;
    MvSet invar;
    MvSet bot;
    MvSet top;
    MvSetFactory factory;
    VariableTable symbolTable;

    // -- cube of all auxillary variables
    MvSet auxCube;

    String name;

    public GCLangBuilder (MvSetFactory _factory, VariableTable _symbolTable)
    {
        this ();
        factory = _factory;
        symbolTable = _symbolTable;
        
        top = factory.top ();
        bot = factory.bot ();
        
        init = top;
        trans = bot;
        invar = top;
      
        auxCube = factory.buildCube (symbolTable.getVariableIds (2));
    }
                          
            
    public String getName () { return name; }

    Variable getByName (String name)
    {
        return symbolTable.getByName (name);
    }

    MvSet prime (MvSet set)
    {
       return set.renameArgs (symbolTable.variableMap (0, 1));
    }

    /**
    * map current state variables to auxillary variables
    */
    MvSet currToAux (MvSet set)
    {
        return set.renameArgs (symbolTable.variableMap (0, 2));
    }
    /**
     * map next state variables to auxillary variables 
     */
    MvSet nextToAux (MvSet set)
    {
        return set.renameArgs (symbolTable.variableMap (1, 2));
    }

    /**
    * Dependent (sequential) composition 
    */

    MvSet composeStmts (MvSet stmt1, MvSet stmt2)
    {
        return 
          nextToAux (stmt1).and (currToAux (stmt2)).existAbstract (auxCube);
    }

    MvSet choiceStmts (MvSet stmt1, MvSet stmt2)
    {
        return stmt1.or (stmt2);
    }

    MvSet doOk (Variable var)
    {
        MvSet result = top;

        for (Iterator it = symbolTable.getVariables ().iterator (); 
            it.hasNext ();)
        {
            Variable v = (Variable) it.next ();
            if (!v.isShadow () && v != var)
              result = result.and (v.eqShadow (1));
        }
        return result;
    }


    MvSet doSkip () 
    {
        MvSet skip = top;

        for (Iterator it = symbolTable.getVariables ().iterator (); 
            it.hasNext ();)
        {
            Variable var = (Variable) it.next ();
            if (!var.isShadow ())
              skip = skip.and (var.eqShadow (1));

        }
        return skip;
    }

    MvSet doAssign (Variable var, MvSet val)
    {
        
        return var.getShadow (1).eq (val).and (doOk (var));
    }

    MvSet doAssign (Variable var, String val)
    {	
	MvSet result;
	if (symbolTable.getByName (val) != null)
	   result = var.getShadow (1).eq (symbolTable.getByName (val));
	else
	   result = var.getShadow (1).eq (val);
	return result.and (doOk (var));
    }

    MvSet doIf (MvSet cond, MvSet thenClause, MvSet elseClause)
    {
        return (cond.and (thenClause)).or (cond.not ().and (elseClause));
    }

    MvSet getTrans ()
    { return trans; }
    MvSet getInit ()
    { return init; }
    MvSet getInvar ()
    { return invar; }


}

program :
        #(NAME name=atom initBlock rulesBlock);

protected atom returns [String s] : 
        v:VARNAME { s = v.getText (); } |
        v2:NUMBER {s = v2.getText ();};

initBlock : #(INIT init=expression);

rulesBlock {MvSet gCmd;} : 
        #(RULES (gCmd=guardedCommand { trans = trans.or (gCmd);})+);

guardedCommand  returns [MvSet v] {MvSet guard; MvSet cmd; } :
        #(COL guard=guard cmd=command) 
        {
            // -- guarded command is a guard /\ cmd
            v = guard.and (cmd);
        };

protected guard returns [MvSet v] : v=expression;

command returns [MvSet v] {MvSet lhs; MvSet rhs;} :
        v=atomicCommand |
        #(SEMI lhs=command rhs=command) { v = composeStmts (lhs, rhs); } |
        #(CHOICE lhs=command rhs=command) { v = choiceStmts (lhs, rhs); };
        
atomicCommand returns [MvSet v] : 
        v=assign | v=skip | v=ite;

assign  returns [MvSet r]
    {Variable var; MvSet val; String s;} : 
        #(ASSIGNOP var=variable val=expression)
        {
            r = doAssign (var, val);
        } |
        // XXX don't know a good way to handle this for now
        #(ASSIGNOPSPEC var=variable s=atom) 
        { r = doAssign (var, s); };

skip returns [MvSet v] : 
        SKIP { v = doSkip (); };

ite returns [MvSet v] {MvSet cond; MvSet thenCmd; MvSet elseCmd;} :
        #(IF cond=expression #(ELSE thenCmd=command elseCmd=command))
        {
            v = doIf (cond, thenCmd, elseCmd);
        };

protected variable returns [Variable var] :
        v:VARNAME 
        { 
            var = getByName (v.getText ()); 
            if (var == null) 
                throw new SemanticException ("Undeclared variable: " + v);
              //throw new SemanticException ("Undeclared variable: "  + v, 
              //"", v.getLine (), v.getColumn ());
        };


expression returns [MvSet val] 
{String ident; 
    Set set; 
    MvSet caseCond; 
    MvSet caseAction; 
    MvSet lhs; 
    MvSet rhs;
    Variable var;
    String s;
    val = null;
}:
        (ident = atom 
            { 
                // XXX THIS NEEDS WORK
                // -- an identifier can be a name of a variable
                // -- or a value of an enumerated type (i.e. constant) 
                // -- so we check for all cases here
                if (getByName (ident) != null)
                {
                    val = ((StateVariable)getByName (ident)).getMvSet ();
                    if (val == null) 
                      throw 
                        new RecognitionException ("Unknwon atom: " + ident);
                }
  
            } |
            #(OR lhs = expression rhs = expression) { val = lhs.or (rhs);}  |

            #(AND lhs = expression rhs = expression) { val = lhs.and (rhs);} |

            #(NEG rhs = expression) { val = rhs.not (); } |

            #(IMPLIES lhs = expression rhs = expression) 
            { val = lhs.not ().or (rhs); } |

            #(IFF lhs=expression rhs=expression) 
            { val = (lhs.and (rhs)).or (lhs.not ().and (rhs.not ()));  } |

            #(EQ var=variable s=atom) { val = var.eq (s); } |
            TRUE { val = top; } | FALSE { val = bot; } );


      
protected enumSet returns [Set set] {String v; set = new HashSet ();}:
        (v = atom {set.add (v);})+;
