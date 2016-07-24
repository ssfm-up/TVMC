// $ANTLR 2.7.6 (2005-12-22): "gclang.g" -> "GCLangBuilder.java"$
 package edu.toronto.cs.gclang.parser;
import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

    import edu.toronto.cs.gclang.parser.VariableTable.*;
    import edu.toronto.cs.mvset.*;
    import edu.toronto.cs.algebra.*;
    import edu.toronto.cs.util.*;

    import java.util.Set;
    import java.util.HashSet;
    import java.util.Iterator;
    


public class GCLangBuilder extends antlr.TreeParser       implements GCLangLexerTokenTypes
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


public GCLangBuilder() {
	tokenNames = _tokenNames;
}

	public final void program(AST _t) throws RecognitionException {
		
		AST program_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t111 = _t;
		AST tmp1_AST_in = (AST)_t;
		match(_t,NAME);
		_t = _t.getFirstChild();
		name=atom(_t);
		_t = _retTree;
		initBlock(_t);
		_t = _retTree;
		rulesBlock(_t);
		_t = _retTree;
		_t = __t111;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	protected final String  atom(AST _t) throws RecognitionException {
		String s;
		
		AST atom_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST v = null;
		AST v2 = null;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		{
			v = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			s = v.getText ();
			break;
		}
		case NUMBER:
		{
			v2 = (AST)_t;
			match(_t,NUMBER);
			_t = _t.getNextSibling();
			s = v2.getText ();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return s;
	}
	
	public final void initBlock(AST _t) throws RecognitionException {
		
		AST initBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t114 = _t;
		AST tmp2_AST_in = (AST)_t;
		match(_t,INIT);
		_t = _t.getFirstChild();
		init=expression(_t);
		_t = _retTree;
		_t = __t114;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void rulesBlock(AST _t) throws RecognitionException {
		
		AST rulesBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet gCmd;
		
		AST __t116 = _t;
		AST tmp3_AST_in = (AST)_t;
		match(_t,RULES);
		_t = _t.getFirstChild();
		{
		int _cnt118=0;
		_loop118:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COL)) {
				gCmd=guardedCommand(_t);
				_t = _retTree;
				trans = trans.or (gCmd);
			}
			else {
				if ( _cnt118>=1 ) { break _loop118; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt118++;
		} while (true);
		}
		_t = __t116;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final MvSet  expression(AST _t) throws RecognitionException {
		MvSet val;
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String ident; 
		Set set; 
		MvSet caseCond; 
		MvSet caseAction; 
		MvSet lhs; 
		MvSet rhs;
		Variable var;
		String s;
		val = null;
		
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		case NUMBER:
		{
			ident=atom(_t);
			_t = _retTree;
			
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
			
			
			break;
		}
		case OR:
		{
			AST __t136 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			lhs=expression(_t);
			_t = _retTree;
			rhs=expression(_t);
			_t = _retTree;
			_t = __t136;
			_t = _t.getNextSibling();
			val = lhs.or (rhs);
			break;
		}
		case AND:
		{
			AST __t137 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			lhs=expression(_t);
			_t = _retTree;
			rhs=expression(_t);
			_t = _retTree;
			_t = __t137;
			_t = _t.getNextSibling();
			val = lhs.and (rhs);
			break;
		}
		case NEG:
		{
			AST __t138 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,NEG);
			_t = _t.getFirstChild();
			rhs=expression(_t);
			_t = _retTree;
			_t = __t138;
			_t = _t.getNextSibling();
			val = rhs.not ();
			break;
		}
		case IMPLIES:
		{
			AST __t139 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,IMPLIES);
			_t = _t.getFirstChild();
			lhs=expression(_t);
			_t = _retTree;
			rhs=expression(_t);
			_t = _retTree;
			_t = __t139;
			_t = _t.getNextSibling();
			val = lhs.not ().or (rhs);
			break;
		}
		case IFF:
		{
			AST __t140 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,IFF);
			_t = _t.getFirstChild();
			lhs=expression(_t);
			_t = _retTree;
			rhs=expression(_t);
			_t = _retTree;
			_t = __t140;
			_t = _t.getNextSibling();
			val = (lhs.and (rhs)).or (lhs.not ().and (rhs.not ()));
			break;
		}
		case EQ:
		{
			AST __t141 = _t;
			AST tmp9_AST_in = (AST)_t;
			match(_t,EQ);
			_t = _t.getFirstChild();
			var=variable(_t);
			_t = _retTree;
			s=atom(_t);
			_t = _retTree;
			_t = __t141;
			_t = _t.getNextSibling();
			val = var.eq (s);
			break;
		}
		case TRUE:
		{
			AST tmp10_AST_in = (AST)_t;
			match(_t,TRUE);
			_t = _t.getNextSibling();
			val = top;
			break;
		}
		case FALSE:
		{
			AST tmp11_AST_in = (AST)_t;
			match(_t,FALSE);
			_t = _t.getNextSibling();
			val = bot;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_retTree = _t;
		return val;
	}
	
	public final MvSet  guardedCommand(AST _t) throws RecognitionException {
		MvSet v;
		
		AST guardedCommand_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet guard; MvSet cmd;
		
		AST __t120 = _t;
		AST tmp12_AST_in = (AST)_t;
		match(_t,COL);
		_t = _t.getFirstChild();
		guard=guard(_t);
		_t = _retTree;
		cmd=command(_t);
		_t = _retTree;
		_t = __t120;
		_t = _t.getNextSibling();
		
		// -- guarded command is a guard /\ cmd
		v = guard.and (cmd);
		
		_retTree = _t;
		return v;
	}
	
	protected final MvSet  guard(AST _t) throws RecognitionException {
		MvSet v;
		
		AST guard_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		v=expression(_t);
		_t = _retTree;
		_retTree = _t;
		return v;
	}
	
	public final MvSet  command(AST _t) throws RecognitionException {
		MvSet v;
		
		AST command_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet lhs; MvSet rhs;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ASSIGNOP:
		case SKIP:
		case IF:
		case ASSIGNOPSPEC:
		{
			v=atomicCommand(_t);
			_t = _retTree;
			break;
		}
		case SEMI:
		{
			AST __t123 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getFirstChild();
			lhs=command(_t);
			_t = _retTree;
			rhs=command(_t);
			_t = _retTree;
			_t = __t123;
			_t = _t.getNextSibling();
			v = composeStmts (lhs, rhs);
			break;
		}
		case CHOICE:
		{
			AST __t124 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,CHOICE);
			_t = _t.getFirstChild();
			lhs=command(_t);
			_t = _retTree;
			rhs=command(_t);
			_t = _retTree;
			_t = __t124;
			_t = _t.getNextSibling();
			v = choiceStmts (lhs, rhs);
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return v;
	}
	
	public final MvSet  atomicCommand(AST _t) throws RecognitionException {
		MvSet v;
		
		AST atomicCommand_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ASSIGNOP:
		case ASSIGNOPSPEC:
		{
			v=assign(_t);
			_t = _retTree;
			break;
		}
		case SKIP:
		{
			v=skip(_t);
			_t = _retTree;
			break;
		}
		case IF:
		{
			v=ite(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return v;
	}
	
	public final MvSet  assign(AST _t) throws RecognitionException {
		MvSet r;
		
		AST assign_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Variable var; MvSet val; String s;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ASSIGNOP:
		{
			AST __t127 = _t;
			AST tmp15_AST_in = (AST)_t;
			match(_t,ASSIGNOP);
			_t = _t.getFirstChild();
			var=variable(_t);
			_t = _retTree;
			val=expression(_t);
			_t = _retTree;
			_t = __t127;
			_t = _t.getNextSibling();
			
			r = doAssign (var, val);
			
			break;
		}
		case ASSIGNOPSPEC:
		{
			AST __t128 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,ASSIGNOPSPEC);
			_t = _t.getFirstChild();
			var=variable(_t);
			_t = _retTree;
			s=atom(_t);
			_t = _retTree;
			_t = __t128;
			_t = _t.getNextSibling();
			r = doAssign (var, s);
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return r;
	}
	
	public final MvSet  skip(AST _t) throws RecognitionException {
		MvSet v;
		
		AST skip_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST tmp17_AST_in = (AST)_t;
		match(_t,SKIP);
		_t = _t.getNextSibling();
		v = doSkip ();
		_retTree = _t;
		return v;
	}
	
	public final MvSet  ite(AST _t) throws RecognitionException {
		MvSet v;
		
		AST ite_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet cond; MvSet thenCmd; MvSet elseCmd;
		
		AST __t131 = _t;
		AST tmp18_AST_in = (AST)_t;
		match(_t,IF);
		_t = _t.getFirstChild();
		cond=expression(_t);
		_t = _retTree;
		AST __t132 = _t;
		AST tmp19_AST_in = (AST)_t;
		match(_t,ELSE);
		_t = _t.getFirstChild();
		thenCmd=command(_t);
		_t = _retTree;
		elseCmd=command(_t);
		_t = _retTree;
		_t = __t132;
		_t = _t.getNextSibling();
		_t = __t131;
		_t = _t.getNextSibling();
		
		v = doIf (cond, thenCmd, elseCmd);
		
		_retTree = _t;
		return v;
	}
	
	protected final Variable  variable(AST _t) throws RecognitionException {
		Variable var;
		
		AST variable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST v = null;
		
		v = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		
		var = getByName (v.getText ()); 
		if (var == null) 
		throw new SemanticException ("Undeclared variable: " + v);
		//throw new SemanticException ("Undeclared variable: "  + v, 
		//"", v.getLine (), v.getColumn ());
		
		_retTree = _t;
		return var;
	}
	
	protected final Set  enumSet(AST _t) throws RecognitionException {
		Set set;
		
		AST enumSet_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String v; set = new HashSet ();
		
		{
		int _cnt144=0;
		_loop144:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==VARNAME||_t.getType()==NUMBER)) {
				v=atom(_t);
				_t = _retTree;
				set.add (v);
			}
			else {
				if ( _cnt144>=1 ) { break _loop144; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt144++;
		} while (true);
		}
		_retTree = _t;
		return set;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LPAREN",
		"RPAREN",
		"ASSIGNOP",
		"CHOICE",
		"IMPLIES",
		"IFF",
		"EQ",
		"OR",
		"AND",
		"NEG",
		"LBRACE",
		"RBRACE",
		"COMMA",
		"PLUS",
		"MINUS",
		"MULT",
		"DIV",
		"VARNAME",
		"ATOM",
		"DIGIT",
		"NUMBER",
		"COL",
		"SEMI",
		"COMMENT",
		"WS",
		"NEWLINE",
		"\"VAR\"",
		"\"INIT\"",
		"\"RULES\"",
		"\"boolean\"",
		"\"true\"",
		"\"false\"",
		"\"skip\"",
		"\"NAME\"",
		"\"if\"",
		"\"then\"",
		"\"else\"",
		"\"fi\"",
		"XOR",
		"NEQ",
		"LT",
		"GT",
		"LEQ",
		"GEQ",
		"MOD",
		"ASSIGNOPSPEC"
	};
	
	}
	
