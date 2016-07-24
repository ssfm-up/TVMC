header { package edu.toronto.cs.ctl.antlr;}
{
import java.util.*;
}


class CTLLexer extends Lexer;

options {k = 3; filter=true; }

LPAREN   : '(';
RPAREN   : ')';
LBRACE   : '{';
RBRACE   : '}';
LSQPAREN : '[';
RSQPAREN : ']';


IMPLIES  : "->" | '\u21D2';
IMPLIED  : "<-";
IFF      : "<->";

EQ       : '=';
GEQ      : ">=" | '\u2291';
LEQ      : "<=" | '\u2292';
NEQ      : ('!' '=') => "!=" |
           '!' {$setType (NEG);} |
           '\u2260';

OR       : '|' | "\\/" | '\u2228';
AND      : '&' | "/\\" | '\u2227';
NEG      : '~' | '\u00AC';

NUMBER   : DIGIT (DIGIT)*;
protected DIGIT : '0'..'9';
protected CAPITAL: 'A'..'Z';
protected LOWER : 'a'..'z';

COMMA    : ',';

PLACEHOLDER : '?';

IDENT : (LOWER | CAPITAL |'_') (LOWER | CAPITAL |'_'|'-'| '.' | DIGIT)*;

COL      : ':';
SEMI	: ';';


WS      : (' ' | '\r' | '\t')+ { $setType(Token.SKIP); };
	
NEWLINE : '\n'
	{
      newline();
	  $setType(Token.SKIP);
	};


class CTLParser extends Parser;
options { buildAST = true; defaultErrorHandler = false;}
tokens 
{
    FORALL = "A";
    FORSOME = "E";
    UNTIL = "U";
    RELEASE = "R";
    WEAK = "W";
    AX = "AX";
    EX = "EX";
    EF = "EF";
    EG = "EG";
    AF = "AF";
    AG = "AG";
}

topLevel: ctlExpression EOF!;

/** Boolean expressions hierarchy */

ctlExpression: impliesExpression;

impliesExpression: orExpression (IMPLIES^ orExpression)*;

orExpression: andExpression (OR^ andExpression)*;

andExpression: notExpression (AND^ notExpression)*;

notExpression: modalExpression |
               NEG^ notExpression;


/** Modal expression and atomics */

modalExpression: comparissonExpression | 
                 (AX^|EX^|EF^|EG^|AF^|AG^) notExpression |
                 (FORALL^ | FORSOME^) LSQPAREN! untilExpression RSQPAREN!;

comparissonExpression: basicExpression 
                ((EQ^ | LEQ^ | GEQ^ | NEQ^) basicExpression)?;



basicExpression: atomic |
                 placeholder |
                 LPAREN! ctlExpression RPAREN!;

untilExpression: ctlExpression (UNTIL^| RELEASE^ | WEAK^) ctlExpression;

placeholder : PLACEHOLDER^ atomic LBRACE! atomicSet RBRACE!;

atomicSet: atomic (COMMA^ atomic)*;

atomic : IDENT | NUMBER;

{
    import java.util.*;

    import edu.toronto.cs.ctl.*;
}

class CTLTreeBuilder extends TreeParser;
options { defaultErrorHandler = false;}

{
    CTLNode[] fairness = CTLAbstractNode.EMPTY_ARRAY;
    
    public CTLTreeBuilder (CTLNode[] _fairness)
    {
        this ();
        if (_fairness != null) fairness = _fairness;
    }
}

// ctlTree: impliesExpression | orExpression | andExpression | modalExpression | atomic | comparissonExpression | notExpression;

ctlTree returns [CTLNode node] : 
        node = atomic | 
        node = notExpression | 
        node = impliesExpression | 
        node = orExpression | 
        node = andExpression | 
        node = modalExpression |
        node = comparissonExpression | 
        node = placeholder;

impliesExpression returns [CTLNode node] 
  {
    CTLNode left;
    CTLNode right;
  }
  : #(IMPLIES left = ctlTree right = ctlTree)
  { node = left.implies (right); };
orExpression returns [CTLNode node] 
  {
    CTLNode left;
    CTLNode right;
  }
  : #(OR left = ctlTree right = ctlTree)
  { node = left.or (right); };
andExpression returns [CTLNode node]
  { 
    CTLNode left;
    CTLNode right;
  }
  : #(AND left = ctlTree right = ctlTree) 
  { node = left.and (right); };

notExpression returns [CTLNode node]
  { CTLNode left; }  : #(NEG left = ctlTree) { node = left.neg ();};

comparissonExpression returns [CTLNode node]:   
        node = neqExpression | 
        node = eqExpression |
        node = leqExpression | 
        node = geqExpression;

neqExpression returns [CTLNode node] 
  { 
    CTLNode left;
    CTLNode right;
  }
  : #(NEQ left = ctlTree right = ctlTree)
  { node = null;};

eqExpression returns [CTLNode node]
  { 
    CTLNode left;
    CTLNode right;
  }
  : #(EQ left = ctlTree right = ctlTree)
  { node = left.eq (right);  };

leqExpression returns [CTLNode node]
  { 
    CTLNode left;
    CTLNode right;
  }
  : #(LEQ left = ctlTree right = ctlTree)
  { node = left.under (right); };
geqExpression returns [CTLNode node]
  {
    CTLNode left;
    CTLNode right;
  }
  : #(GEQ left = ctlTree right = ctlTree)
  { node = left.over (right); };

modalExpression returns [CTLNode node] : 
        node = axExpression | 
        node = exExpression | 
        node = efExpression | 
        node = egExpression | 
        node = agExpression | 
        node = afExpression | 
        node = untillReleaseExpression;

axExpression returns [CTLNode node] { CTLNode right;}: #(AX right = ctlTree) 
  {node = right.ax ();};
exExpression returns [CTLNode node] { CTLNode right;}: #(EX right = ctlTree) 
  {node = right.ex ();};
afExpression returns [CTLNode node] { CTLNode right;}: #(AF right = ctlTree) 
  {node = right.af (fairness);};
efExpression returns [CTLNode node] { CTLNode right;}: #(EF right = ctlTree) 
  {node = right.ef ();};
agExpression returns [CTLNode node] { CTLNode right;}: #(AG right = ctlTree) 
  {node = right.ag (fairness);};
egExpression returns [CTLNode node] { CTLNode right;}: #(EG right = ctlTree) 
  {node = right.eg (fairness);};

untillReleaseExpression returns [CTLNode node] : node = forallUntil | 
                                                 node = forsomeUntil;

forallUntil returns [CTLNode node] 
 {
   CTLNode left;
   CTLNode right;
 }
    : #(FORALL (#(UNTIL left = ctlTree right = ctlTree) 
                            { node = left.au (right); } 
    |
                #(RELEASE left = ctlTree right = ctlTree) 
                {node = left.ar (right);}
    |           
                #(WEAK left = ctlTree right = ctlTree)
                {node = left.aw (right);})
                )
    ;

forsomeUntil returns [CTLNode node] 
 {
   CTLNode left;
   CTLNode right;
 }
 : #(FORSOME (#(UNTIL left = ctlTree right = ctlTree) 
   { node = left.eu (right); }
 |
   #(RELEASE left = ctlTree right = ctlTree) {node = left.er (right);}
 |
   #(WEAK left = ctlTree right = ctlTree) { node = left.ew (right);}))
 ;


placeholder returns [CTLNode node]
 { 
   CTLAtomPropNode name;
   Set set;
 }
 : #(PLACEHOLDER name = atomic set = atomicSet) 
 { node = CTLFactory.createCTLPlaceholderNode (name.getName (),
        (CTLAtomPropNode[])set.toArray (new CTLAtomPropNode [set.size ()]));
 };

atomicSet returns [Set result]
 {
    CTLAtomPropNode node;
    Set temp;
 }
 : node = atomic { result = new HashSet (); result.add (node); }
 | #(COMMA temp = atomicSet node = atomic) { temp.add (node); result = temp;};

atomic returns [CTLAtomPropNode node]: name:IDENT 
      { node = CTLFactory.createCTLAtomPropNode (name.getText ());}
      | number:NUMBER 
      { node = CTLFactory.createCTLAtomPropNode (number.getText ());};
