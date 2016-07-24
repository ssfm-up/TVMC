// $ANTLR 2.7.6 (2005-12-22): "smv.g" -> "FullDDBuilder.java"$
 package edu.toronto.cs.smv.parser;
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

import java.util.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.mvset.MDDMvSetFactory.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.smv.*;
import edu.toronto.cs.smv.VariableTable.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.util.ReflectUtil;
import edu.toronto.cs.ctl.*;


public class FullDDBuilder extends antlr.TreeParser       implements FullSMVLexerTokenTypes
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

public FullDDBuilder() {
	tokenNames = _tokenNames;
}

	public final void moduleDecl(AST _t) throws RecognitionException {
		
		AST moduleDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String moduleName;
		
		AST __t123 = _t;
		AST tmp63_AST_in = (AST)_t;
		match(_t,MODULE);
		_t = _t.getFirstChild();
		moduleName=atom(_t);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case DEFINE:
		{
			defineBlock(_t);
			_t = _retTree;
			break;
		}
		case TRANS:
		case ASSIGN:
		case INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		{
		int _cnt126=0;
		_loop126:
		do {
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASSIGN:
			{
				assignBlock(_t);
				_t = _retTree;
				break;
			}
			case TRANS:
			{
				transBlock(_t);
				_t = _retTree;
				break;
			}
			case INIT:
			{
				initBlock(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				if ( _cnt126>=1 ) { break _loop126; } else {throw new NoViableAltException(_t);}
			}
			}
			_cnt126++;
		} while (true);
		}
		_t = __t123;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	protected final String  atom(AST _t) throws RecognitionException {
		String val;
		
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
			val = v.getText ();
			break;
		}
		case NUMBER:
		{
			v2 = (AST)_t;
			match(_t,NUMBER);
			_t = _t.getNextSibling();
			val = v2.getText ();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return val;
	}
	
	public final void defineBlock(AST _t) throws RecognitionException {
		
		AST defineBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t128 = _t;
		AST tmp64_AST_in = (AST)_t;
		match(_t,DEFINE);
		_t = _t.getFirstChild();
		{
		int _cnt130=0;
		_loop130:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==ASSIGNOP)) {
				defineBody(_t);
				_t = _retTree;
			}
			else {
				if ( _cnt130>=1 ) { break _loop130; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt130++;
		} while (true);
		}
		_t = __t128;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void assignBlock(AST _t) throws RecognitionException {
		
		AST assignBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t134 = _t;
		AST tmp65_AST_in = (AST)_t;
		match(_t,ASSIGN);
		_t = _t.getFirstChild();
		{
		int _cnt136=0;
		_loop136:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==ASSIGNOP)) {
				assignBody(_t);
				_t = _retTree;
			}
			else {
				if ( _cnt136>=1 ) { break _loop136; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt136++;
		} while (true);
		}
		_t = __t134;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void transBlock(AST _t) throws RecognitionException {
		
		AST transBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet val;
		
		AST __t147 = _t;
		AST tmp66_AST_in = (AST)_t;
		match(_t,TRANS);
		_t = _t.getFirstChild();
		val=expression(_t,null);
		_t = _retTree;
		_t = __t147;
		_t = _t.getNextSibling();
		trans = trans.and (val);
		_retTree = _t;
	}
	
	public final void initBlock(AST _t) throws RecognitionException {
		
		AST initBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet val;
		
		AST __t149 = _t;
		AST tmp67_AST_in = (AST)_t;
		match(_t,INIT);
		_t = _t.getFirstChild();
		val=expression(_t,null);
		_t = _retTree;
		_t = __t149;
		_t = _t.getNextSibling();
		init = init.and (val);
		_retTree = _t;
	}
	
	public final void defineBody(AST _t) throws RecognitionException {
		
		AST defineBody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		MvSet val; String name;
		AST __t132 = _t;
		AST tmp68_AST_in = (AST)_t;
		match(_t,ASSIGNOP);
		_t = _t.getFirstChild();
		name=atom(_t);
		_t = _retTree;
		val=expression(_t,null);
		_t = _retTree;
		_t = __t132;
		_t = _t.getNextSibling();
		if (!name.equals ("running")) 
		symbolTable.declareDefine (name, val);
		_retTree = _t;
	}
	
	public final MvSet  expression(AST _t,
		Variable var
	) throws RecognitionException {
		MvSet val;
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String ident; Set set; MvSet caseCond; MvSet caseAction; val = null;
		MvSet lhs; MvSet rhs;
		
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		case NUMBER:
		{
			ident=atom(_t);
			_t = _retTree;
			
			
			
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
			
			break;
		}
		case LBRACE:
		{
			AST __t152 = _t;
			AST tmp69_AST_in = (AST)_t;
			match(_t,LBRACE);
			_t = _t.getFirstChild();
			set=enumSet(_t);
			_t = _retTree;
			_t = __t152;
			_t = _t.getNextSibling();
			
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
			
			break;
		}
		case OR:
		{
			AST __t153 = _t;
			AST tmp70_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			lhs=expression(_t,null);
			_t = _retTree;
			rhs=expression(_t,null);
			_t = _retTree;
			_t = __t153;
			_t = _t.getNextSibling();
			val = lhs.or (rhs);
			break;
		}
		case AND:
		{
			AST __t154 = _t;
			AST tmp71_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			lhs=expression(_t,null);
			_t = _retTree;
			rhs=expression(_t,null);
			_t = _retTree;
			_t = __t154;
			_t = _t.getNextSibling();
			val = lhs.and (rhs);
			break;
		}
		case NEG:
		{
			AST __t155 = _t;
			AST tmp72_AST_in = (AST)_t;
			match(_t,NEG);
			_t = _t.getFirstChild();
			rhs=expression(_t,null);
			_t = _retTree;
			_t = __t155;
			_t = _t.getNextSibling();
			val = rhs.not ();
			break;
		}
		case IMPLIES:
		{
			AST __t156 = _t;
			AST tmp73_AST_in = (AST)_t;
			match(_t,IMPLIES);
			_t = _t.getFirstChild();
			lhs=expression(_t,null);
			_t = _retTree;
			rhs=expression(_t,null);
			_t = _retTree;
			_t = __t156;
			_t = _t.getNextSibling();
			val = lhs.not ().or (rhs);
			break;
		}
		case TRUE:
		{
			AST tmp74_AST_in = (AST)_t;
			match(_t,TRUE);
			_t = _t.getNextSibling();
			val = top;
			break;
		}
		case FALSE:
		{
			AST tmp75_AST_in = (AST)_t;
			match(_t,FALSE);
			_t = _t.getNextSibling();
			val = bot;
			break;
		}
		case EQ:
		{
			AST __t157 = _t;
			AST tmp76_AST_in = (AST)_t;
			match(_t,EQ);
			_t = _t.getFirstChild();
			Variable var2;
			var2=variable(_t);
			_t = _retTree;
			val=expression(_t,var2);
			_t = _retTree;
			_t = __t157;
			_t = _t.getNextSibling();
			break;
		}
		case NEXT:
		{
			AST __t158 = _t;
			AST tmp77_AST_in = (AST)_t;
			match(_t,NEXT);
			_t = _t.getFirstChild();
			Variable var2;
			var2=variable(_t);
			_t = _retTree;
			_t = __t158;
			_t = _t.getNextSibling();
			
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
			
			break;
		}
		case CASE:
		{
			AST __t159 = _t;
			AST tmp78_AST_in = (AST)_t;
			match(_t,CASE);
			_t = _t.getFirstChild();
			CaseTranslator kase = new CaseTranslator ();
			{
			int _cnt162=0;
			_loop162:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COL)) {
					AST __t161 = _t;
					AST tmp79_AST_in = (AST)_t;
					match(_t,COL);
					_t = _t.getFirstChild();
					caseCond=expression(_t,null);
					_t = _retTree;
					caseAction=expression(_t,var);
					_t = _retTree;
					_t = __t161;
					_t = _t.getNextSibling();
					
					kase.addCase (caseCond, caseAction);
					
				}
				else {
					if ( _cnt162>=1 ) { break _loop162; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt162++;
			} while (true);
			}
			_t = __t159;
			_t = _t.getNextSibling();
			val = kase.compute (); var = null;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		
		if (var != null) 
		val = var.eq (val); 
		assert val != null : "Val is null after eq with: " + var;
		
		_retTree = _t;
		return val;
	}
	
	public final void assignBody(AST _t) throws RecognitionException {
		
		AST assignBody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Variable var; boolean isInit = false; boolean isInvar = false; 
		MvSet val;
		
		
		
		AST __t138 = _t;
		AST tmp80_AST_in = (AST)_t;
		match(_t,ASSIGNOP);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case INIT:
		{
			AST __t140 = _t;
			AST tmp81_AST_in = (AST)_t;
			match(_t,INIT);
			_t = _t.getFirstChild();
			var=simpleVariable(_t);
			_t = _retTree;
			isInit = true;
			_t = __t140;
			_t = _t.getNextSibling();
			break;
		}
		case NEXT:
		{
			AST __t141 = _t;
			AST tmp82_AST_in = (AST)_t;
			match(_t,NEXT);
			_t = _t.getFirstChild();
			var=simpleVariable(_t);
			_t = _retTree;
			var = var.getNext ();
			_t = __t141;
			_t = _t.getNextSibling();
			break;
		}
		case VARNAME:
		{
			var=simpleVariable(_t);
			_t = _retTree;
			isInvar = true;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		val=expression(_t,var);
		_t = _retTree;
		_t = __t138;
		_t = _t.getNextSibling();
		
		if (isInit)
		init = init.and (val);
		else if (isInvar)
		invar = invar.and (val);
		else
		trans = trans.and (val);
		
		_retTree = _t;
	}
	
	protected final Variable  simpleVariable(AST _t) throws RecognitionException {
		Variable var;
		
		AST simpleVariable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST v = null;
		
		v = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		var = getByName (v.getText ());
		_retTree = _t;
		return var;
	}
	
	protected final Variable  variable(AST _t) throws RecognitionException {
		Variable var;
		
		AST variable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		{
			var=simpleVariable(_t);
			_t = _retTree;
			break;
		}
		case NEXT:
		{
			AST __t144 = _t;
			AST tmp83_AST_in = (AST)_t;
			match(_t,NEXT);
			_t = _t.getFirstChild();
			var=simpleVariable(_t);
			_t = _retTree;
			_t = __t144;
			_t = _t.getNextSibling();
			var = var.getNext ();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return var;
	}
	
	protected final Set  enumSet(AST _t) throws RecognitionException {
		Set set;
		
		AST enumSet_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String v; set = new HashSet ();
		
		{
		int _cnt165=0;
		_loop165:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==VARNAME||_t.getType()==NUMBER)) {
				v=atom(_t);
				_t = _retTree;
				set.add (v);
			}
			else {
				if ( _cnt165>=1 ) { break _loop165; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt165++;
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
		"IMPLIES",
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
		"\"TRANS\"",
		"\"union\"",
		"\"next\"",
		"\"VAR\"",
		"\"IVAR\"",
		"\"ASSIGN\"",
		"\"MODULE\"",
		"\"case\"",
		"\"esac\"",
		"\"init\"",
		"\"INIT\"",
		"\"DEFINE\"",
		"\"TRUE\"",
		"\"FALSE\"",
		"\"boolean\"",
		"IFF",
		"XOR",
		"NEQ",
		"LT",
		"GT",
		"LEQ",
		"GEQ",
		"MOD",
		"INVAR",
		"ISA"
	};
	
	}
	
