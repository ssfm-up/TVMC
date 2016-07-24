// $ANTLR 2.7.6 (2005-12-22): "flatsmv.g" -> "DDBuilder.java"$
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


public class DDBuilder extends antlr.TreeParser       implements SMVParserTokenTypes
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

public DDBuilder() {
	tokenNames = _tokenNames;
}

	public final void pgm(AST _t) throws RecognitionException {
		
		AST pgm_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t82 = _t;
		AST tmp50_AST_in = (AST)_t;
		match(_t,MODULE);
		_t = _t.getFirstChild();
		AST tmp51_AST_in = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IVAR:
		{
			ivarblock(_t);
			_t = _retTree;
			break;
		}
		case VAR:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		varblock(_t);
		_t = _retTree;
		{
		int _cnt85=0;
		_loop85:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==ASSIGN)) {
				assignblock(_t);
				_t = _retTree;
			}
			else {
				if ( _cnt85>=1 ) { break _loop85; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt85++;
		} while (true);
		}
		{
		_loop87:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==TRANS)) {
				transblock(_t);
				_t = _retTree;
			}
			else {
				break _loop87;
			}
			
		} while (true);
		}
		_t = __t82;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void ivarblock(AST _t) throws RecognitionException {
		
		AST ivarblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t89 = _t;
		AST tmp52_AST_in = (AST)_t;
		match(_t,IVAR);
		_t = _t.getFirstChild();
		{
		int _cnt91=0;
		_loop91:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COL)) {
				vardecl(_t);
				_t = _retTree;
			}
			else {
				if ( _cnt91>=1 ) { break _loop91; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt91++;
		} while (true);
		}
		_t = __t89;
		_t = _t.getNextSibling();
		if (false) throw new RecognitionException();
		_retTree = _t;
	}
	
	public final void varblock(AST _t) throws RecognitionException {
		
		AST varblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		vt = new VariableTable();
		
		AST __t93 = _t;
		AST tmp53_AST_in = (AST)_t;
		match(_t,VAR);
		_t = _t.getFirstChild();
		{
		int _cnt95=0;
		_loop95:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COL)) {
				vardecl(_t);
				_t = _retTree;
			}
			else {
				if ( _cnt95>=1 ) { break _loop95; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt95++;
		} while (true);
		}
		_t = __t93;
		_t = _t.getNextSibling();
		
		// -- we get here once all vardecl has been seen so
		// -- we know how many variables we have and the symbol
		// -- table has been constructed.
		
		// -- debug dump
		vt.dump();
		// -- initialize mv-set factory
		initialize (vt.getNumVars ());
		System.out.println("Done varblock");
		
		_retTree = _t;
	}
	
	public final void assignblock(AST _t) throws RecognitionException {
		
		AST assignblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet i, n;
		
		AST __t105 = _t;
		AST tmp54_AST_in = (AST)_t;
		match(_t,ASSIGN);
		_t = _t.getFirstChild();
		i=init(_t);
		_t = _retTree;
		init = init.and (i); 
		assert !init.isConstant () : "Mess of an initial condition";
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NEXT:
		{
			n=next(_t);
			_t = _retTree;
			tr = tr.and (n);
			break;
		}
		case 3:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t105;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void transblock(AST _t) throws RecognitionException {
		
		AST transblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet p;
		
		AST __t135 = _t;
		AST tmp55_AST_in = (AST)_t;
		match(_t,TRANS);
		_t = _t.getFirstChild();
		{
		p=predicate(_t);
		_t = _retTree;
		}
		_t = __t135;
		_t = _t.getNextSibling();
		
		tr = tr.and (p);
		
		_retTree = _t;
	}
	
	public final void vardecl(AST _t) throws RecognitionException {
		
		AST vardecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST name = null;
		AST namedType = null;
		List enumType;
		
		AST __t97 = _t;
		AST tmp56_AST_in = (AST)_t;
		match(_t,COL);
		_t = _t.getFirstChild();
		name = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		{
			namedType = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			
				// named type : for now assume boolean
			vt.declarePropositional(name.getText ());
			break;
		}
		case COMMA:
		{
			enumType=set(_t);
			_t = _retTree;
			
				  System.out.println("Declaration of variable " + name.getText () +
			" with enumerated type " + enumType);
			// -- got a variable of enumerated type
				  vt.declareEnumerated(name.getText(), enumType);
				
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t97;
		_t = _t.getNextSibling();
		System.out.println("Done Vardecl");
		_retTree = _t;
	}
	
	public final List  set(AST _t) throws RecognitionException {
		List enumType;
		
		AST set_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST l = null;
		AST r = null;
		AST m = null;
		
		// -- stores prev. result of recursion
			    List lastList;
			    enumType  = new ArrayList ();
			
		
		AST __t100 = _t;
		AST tmp57_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		{
			{
			l = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			r = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			
			enumType.add(l.getText()); 
			enumType.add(r.getText());
			
			}
			break;
		}
		case COMMA:
		{
			{
			lastList=set(_t);
			_t = _retTree;
			m = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			enumType.addAll (lastList); enumType.add (m.getText());
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t100;
		_t = _t.getNextSibling();
		_retTree = _t;
		return enumType;
	}
	
	public final MvSet  init(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST init_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST varName = null;
		pred = null; MvSet p;
		
		AST __t108 = _t;
		AST tmp58_AST_in = (AST)_t;
		match(_t,INIT);
		_t = _t.getFirstChild();
		varName = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		
		// initialize the current-variable object
		System.out.println("Computing init("+varName.getText()+")");
			currentVar = vt.getByName(varName.getText());
			
		p=predicate(_t);
		_t = _retTree;
			  System.out.println("..computed!"); 
		// XXX Oh what a mess!
		if (currentVar instanceof StateVariable)
		pred = currentVar.eq (p);
		else
		pred = p;
			
		_t = __t108;
		_t = _t.getNextSibling();
		_retTree = _t;
		return pred;
	}
	
	public final MvSet  next(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST next_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST v = null;
		MvSet pp; pred = null;
		
		AST __t119 = _t;
		AST tmp59_AST_in = (AST)_t;
		match(_t,NEXT);
		_t = _t.getFirstChild();
		v = (AST)_t;
		match(_t,VARNAME);
		_t = _t.getNextSibling();
		// XXX Get the variable as variable. 
		// XXX We use global variables to pass them to rules.
		currentVar = vt.getByName(v.getText()).getNext();
			
		pp=astmt(_t);
		_t = _retTree;
		_t = __t119;
		_t = _t.getNextSibling();
		pred = pp; 
		//System.out.println("next="+pred.toDaVinci().toString());
		
		_retTree = _t;
		return pred;
	}
	
	public final MvSet  predicate(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST predicate_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST lval = null;
		AST lv2 = null;
		AST rval = null;
		AST rval2 = null;
		AST lprop = null;
		AST rnprop = null;
		AST i = null;
		AST ev = null;
		AST n = null;
		AST m = null;
		pred = null; 
				Variable var;
				MvSet l,r; 
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case OR:
		{
			AST __t110 = _t;
			AST tmp60_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			l=predicate(_t);
			_t = _retTree;
			r=predicate(_t);
			_t = _retTree;
			_t = __t110;
			_t = _t.getNextSibling();
			pred = l.or(r);
			break;
		}
		case AND:
		{
			AST __t111 = _t;
			AST tmp61_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			l=predicate(_t);
			_t = _retTree;
			r=predicate(_t);
			_t = _retTree;
			_t = __t111;
			_t = _t.getNextSibling();
			pred = l.and(r);
			break;
		}
		case NEG:
		{
			AST __t112 = _t;
			AST tmp62_AST_in = (AST)_t;
			match(_t,NEG);
			_t = _t.getFirstChild();
			l=predicate(_t);
			_t = _retTree;
			_t = __t112;
			_t = _t.getNextSibling();
			pred = l.not();
			break;
		}
		case IMPLIES:
		{
			AST __t113 = _t;
			AST tmp63_AST_in = (AST)_t;
			match(_t,IMPLIES);
			_t = _t.getFirstChild();
			l=predicate(_t);
			_t = _retTree;
			r=predicate(_t);
			_t = _retTree;
			_t = __t113;
			_t = _t.getNextSibling();
			pred = l.not ().or (r);
			break;
		}
		case ZERO:
		{
			AST tmp64_AST_in = (AST)_t;
			match(_t,ZERO);
			_t = _t.getNextSibling();
			pred = factory.bot();
			break;
		}
		case ONE:
		{
			AST tmp65_AST_in = (AST)_t;
			match(_t,ONE);
			_t = _t.getNextSibling();
			pred = factory.top(); 	
			break;
		}
		case VALEQ:
		{
			AST __t114 = _t;
			AST tmp66_AST_in = (AST)_t;
			match(_t,VALEQ);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ENUMVAR:
			{
				lval = (AST)_t;
				match(_t,ENUMVAR);
				_t = _t.getNextSibling();
				System.out.println("Enumvar: " + lval.getText ()); var = vt.getByName(lval.getText());
				break;
			}
			case NEXTENUMVAR:
			{
				lv2 = (AST)_t;
				match(_t,NEXTENUMVAR);
				_t = _t.getNextSibling();
				var = vt.getByName(lv2.getText()).getNext();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VALUE:
			{
				rval = (AST)_t;
				match(_t,VALUE);
				_t = _t.getNextSibling();
				
				System.out.println("Valeq Node:"+var);
				pred = var.eq(rval.getText());
				
				break;
			}
			case NEXTENUMVAR:
			{
				rval2 = (AST)_t;
				match(_t,NEXTENUMVAR);
				_t = _t.getNextSibling();
				
				pred = ((EnumeratedVariable)var).eq 
				((EnumeratedVariable)vt.getByName 
				(rval2.getText ()).getNext ());
				
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t114;
			_t = _t.getNextSibling();
			break;
		}
		case BOOLEQ:
		{
			AST __t117 = _t;
			AST tmp67_AST_in = (AST)_t;
			match(_t,BOOLEQ);
			_t = _t.getFirstChild();
			lprop = (AST)_t;
			match(_t,PROPVAR);
			_t = _t.getNextSibling();
			rnprop = (AST)_t;
			match(_t,NEXTPROPVAR);
			_t = _t.getNextSibling();
			_t = __t117;
			_t = _t.getNextSibling();
			
			var = vt.getByName (lprop.getText ());
			pred = ((StateVariable)var).eq ((StateVariable) 
			vt.getByName (rnprop.getText ()).getNext ());
			
			break;
		}
		case VALUE:
		{
			i = (AST)_t;
			match(_t,VALUE);
			_t = _t.getNextSibling();
			
			System.out.println("Value: "+i.getText());
			pred = currentVar.eq(i.getText());
			
			break;
		}
		case ENUMVAR:
		{
			ev = (AST)_t;
			match(_t,ENUMVAR);
			_t = _t.getNextSibling();
			
			System.out.println("setting to ev "+ev.getText());
			pred = ((EnumeratedVariable)currentVar).
			eq(((EnumeratedVariable)vt.getByName(ev.getText())));
			
			break;
		}
		case PROPVAR:
		{
			n = (AST)_t;
			match(_t,PROPVAR);
			_t = _t.getNextSibling();
				
			var = vt.getByName (n.getText ());
			pred = ( (StateVariable) var).getMvSet();
			
			break;
		}
		case NEXTPROPVAR:
		{
			m = (AST)_t;
			match(_t,NEXTPROPVAR);
			_t = _t.getNextSibling();
				
			var = vt.getByName (m.getText ()).getNext();
			pred = ( (StateVariable) var).getMvSet();
			
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return pred;
	}
	
	public final MvSet  astmt(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST astmt_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet pp = null; pred = null;
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VALUE:
		case PROPVAR:
		case ENUMVAR:
		case ZERO:
		case ONE:
		case NEXTPROPVAR:
		case OR:
		case AND:
		case NEG:
		case BOOLEQ:
		case VALEQ:
		case IMPLIES:
		{
			pp=predicate(_t);
			_t = _retTree;
			break;
		}
		case CASE:
		{
			pp=kase(_t);
			_t = _retTree;
			break;
		}
		case COMMA:
		{
			pp=aset(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		pred = pp;
		_retTree = _t;
		return pred;
	}
	
	public final MvSet  kase(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST kase_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		MvSet condition = null;
			  MvSet effect = null;
			  pred = null;
		
		AST __t129 = _t;
		AST tmp68_AST_in = (AST)_t;
		match(_t,CASE);
		_t = _t.getFirstChild();
		
			      CaseTranslator ct = new CaseTranslator ();
			
		{
		int _cnt132=0;
		_loop132:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==COL)) {
				AST __t131 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,COL);
				_t = _t.getFirstChild();
				condition=predicate(_t);
				_t = _retTree;
				effect=astmt(_t);
				_t = _retTree;
				
				// default case
						if (condition == factory.top ())
				ct.addDefault (effect);		
					    else 
				ct.addCase(condition, effect);
					
				_t = __t131;
				_t = _t.getNextSibling();
			}
			else {
				if ( _cnt132>=1 ) { break _loop132; } else {throw new NoViableAltException(_t);}
			}
			
			_cnt132++;
		} while (true);
		}
		pred = ct.compute ();
		_t = __t129;
		_t = _t.getNextSibling();
		_retTree = _t;
		return pred;
	}
	
	public final MvSet  aset(AST _t) throws RecognitionException {
		MvSet pred;
		
		AST aset_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST v = null;
		AST w = null;
		
			pred = null;
		MvSet old;
			
		
		AST __t123 = _t;
		AST tmp70_AST_in = (AST)_t;
		match(_t,COMMA);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VARNAME:
		{
			v = (AST)_t;
			match(_t,VARNAME);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VARNAME:
			{
				w = (AST)_t;
				match(_t,VARNAME);
				_t = _t.getNextSibling();
				old = currentVar.eq(w.getText());
				break;
			}
			case COMMA:
			{
				old=aset(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			pred = currentVar.eq(v.getText()).or(old);
			break;
		}
		case ZERO:
		{
			{
			AST tmp71_AST_in = (AST)_t;
			match(_t,ZERO);
			_t = _t.getNextSibling();
			AST tmp72_AST_in = (AST)_t;
			match(_t,ONE);
			_t = _t.getNextSibling();
			}
			pred = factory.top();
			break;
		}
		case ONE:
		{
			{
			AST tmp73_AST_in = (AST)_t;
			match(_t,ONE);
			_t = _t.getNextSibling();
			AST tmp74_AST_in = (AST)_t;
			match(_t,ZERO);
			_t = _t.getNextSibling();
			}
			pred=factory.top();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t123;
		_t = _t.getNextSibling();
		_retTree = _t;
		return pred;
	}
	
	public final void defineblock(AST _t) throws RecognitionException {
		
		AST defineblock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (false) throw new RecognitionException();
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"TRANS\"",
		"VALUE",
		"PROPVAR",
		"ENUMVAR",
		"ZERO",
		"ONE",
		"NEXTPROPVAR",
		"NEXTENUMVAR",
		"OR",
		"AND",
		"NEG",
		"BOOLEQ",
		"VALEQ",
		"\"union\"",
		"\"next\"",
		"\"VAR\"",
		"\"IVAR\"",
		"\"ASSIGN\"",
		"\"MODULE\"",
		"\"case\"",
		"\"esac\"",
		"\"init\"",
		"\"DEFINE\"",
		"VARNAME",
		"COL",
		"SEMI",
		"LBRACE",
		"RBRACE",
		"COMMA",
		"ASSIGNOP",
		"IMPLIES",
		"EQ",
		"LPAREN",
		"RPAREN",
		"IDENT",
		"COMMENT",
		"WS",
		"NEWLINE"
	};
	
	}
	
