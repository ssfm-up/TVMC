// $ANTLR 2.7.6 (2005-12-22): "flatsmv.g" -> "SMVParser.java"$
 package edu.toronto.cs.smv.parser;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

import java.util.*;

public class SMVParser extends antlr.LLkParser       implements SMVParserTokenTypes
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

protected SMVParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public SMVParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected SMVParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public SMVParser(TokenStream lexer) {
  this(lexer,1);
}

public SMVParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void pgm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pgm_AST = null;
		
		mainmodule();
		astFactory.addASTChild(currentAST, returnAST);
		match(Token.EOF_TYPE);
		pgm_AST = (AST)currentAST.root;
		returnAST = pgm_AST;
	}
	
	public final void mainmodule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mainmodule_AST = null;
		Token  a = null;
		AST a_AST = null;
		
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp2_AST);
		match(MODULE);
		a = LT(1);
		a_AST = astFactory.create(a);
		astFactory.addASTChild(currentAST, a_AST);
		match(VARNAME);
		System.out.println("Read module "+a);
		{
		switch ( LA(1)) {
		case IVAR:
		{
			ivarblock();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case VAR:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		varblock();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop5:
		do {
			if ((LA(1)==DEFINE)) {
				defineblock();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop5;
			}
			
		} while (true);
		}
		System.out.println("Read defines");
		{
		int _cnt7=0;
		_loop7:
		do {
			if ((LA(1)==ASSIGN)) {
				assignblock();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt7>=1 ) { break _loop7; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt7++;
		} while (true);
		}
		{
		_loop9:
		do {
			if ((LA(1)==TRANS)) {
				transblock();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop9;
			}
			
		} while (true);
		}
		mainmodule_AST = (AST)currentAST.root;
		returnAST = mainmodule_AST;
	}
	
	public final void ivarblock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ivarblock_AST = null;
		
		AST tmp3_AST = null;
		tmp3_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp3_AST);
		match(IVAR);
		System.out.println("Slurped ivar");
		{
		int _cnt12=0;
		_loop12:
		do {
			if ((LA(1)==VARNAME)) {
				vardecl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt12>=1 ) { break _loop12; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt12++;
		} while (true);
		}
		ivarblock_AST = (AST)currentAST.root;
		returnAST = ivarblock_AST;
	}
	
	public final void varblock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varblock_AST = null;
		
		AST tmp4_AST = null;
		tmp4_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp4_AST);
		match(VAR);
		{
		int _cnt24=0;
		_loop24:
		do {
			if ((LA(1)==VARNAME)) {
				vardecl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt24>=1 ) { break _loop24; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt24++;
		} while (true);
		}
		varblock_AST = (AST)currentAST.root;
		returnAST = varblock_AST;
	}
	
	public final void defineblock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defineblock_AST = null;
		
		AST tmp5_AST = null;
		tmp5_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp5_AST);
		match(DEFINE);
		{
		int _cnt27=0;
		_loop27:
		do {
			if ((LA(1)==VARNAME)) {
				definition();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt27>=1 ) { break _loop27; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt27++;
		} while (true);
		}
		System.out.println ("Slurped defblock");
		defineblock_AST = (AST)currentAST.root;
		returnAST = defineblock_AST;
	}
	
	public final void assignblock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignblock_AST = null;
		
		AST tmp6_AST = null;
		tmp6_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp6_AST);
		match(ASSIGN);
		System.out.println ("Reading ASSIGN");
		init();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case NEXT:
		{
			next();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case TRANS:
		case ASSIGN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		assignblock_AST = (AST)currentAST.root;
		returnAST = assignblock_AST;
	}
	
	public final void transblock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST transblock_AST = null;
		
		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp7_AST);
		match(TRANS);
		predicate();
		astFactory.addASTChild(currentAST, returnAST);
		transblock_AST = (AST)currentAST.root;
		returnAST = transblock_AST;
	}
	
	public final void vardecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST vardecl_AST = null;
		Token  vname = null;
		AST vname_AST = null;
		Token  typename = null;
		AST typename_AST = null;
		
		vname = LT(1);
		vname_AST = astFactory.create(vname);
		astFactory.addASTChild(currentAST, vname_AST);
		match(VARNAME);
		varnames.add(vname.getText());
		AST tmp8_AST = null;
		tmp8_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp8_AST);
		match(COL);
		{
		switch ( LA(1)) {
		case VARNAME:
		{
			typename = LT(1);
			typename_AST = astFactory.create(typename);
			astFactory.addASTChild(currentAST, typename_AST);
			match(VARNAME);
			propVars.add(vname.getText());
			break;
		}
		case LBRACE:
		{
			set();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		vardecl_AST = (AST)currentAST.root;
		returnAST = vardecl_AST;
	}
	
	public final void set() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST set_AST = null;
		
		match(LBRACE);
		
			      setList = new LinkedList();
		elements();
		astFactory.addASTChild(currentAST, returnAST);
		match(RBRACE);
		set_AST = (AST)currentAST.root;
		returnAST = set_AST;
	}
	
	public final void elements() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elements_AST = null;
		Token  v = null;
		AST v_AST = null;
		Token  w = null;
		AST w_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			{
			v = LT(1);
			v_AST = astFactory.create(v);
			astFactory.addASTChild(currentAST, v_AST);
			match(VARNAME);
			{
			_loop19:
			do {
				if ((LA(1)==COMMA)) {
					AST tmp12_AST = null;
					tmp12_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp12_AST);
					match(COMMA);
					w = LT(1);
					w_AST = astFactory.create(w);
					astFactory.addASTChild(currentAST, w_AST);
					match(VARNAME);
					setList.add(w);
				}
				else {
					break _loop19;
				}
				
			} while (true);
			}
			setList.add(v);
				System.out.println(setList);
			}
			elements_AST = (AST)currentAST.root;
			break;
		}
		case ZERO:
		{
			edu.toronto.cs.smv.parser.ExprAST tmp13_AST = null;
			tmp13_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.addASTChild(currentAST, tmp13_AST);
			match(ZERO);
			{
			switch ( LA(1)) {
			case COMMA:
			{
				AST tmp14_AST = null;
				tmp14_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp14_AST);
				match(COMMA);
				edu.toronto.cs.smv.parser.ExprAST tmp15_AST = null;
				tmp15_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
				astFactory.addASTChild(currentAST, tmp15_AST);
				match(ONE);
				break;
			}
			case RBRACE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			elements_AST = (AST)currentAST.root;
			break;
		}
		case ONE:
		{
			edu.toronto.cs.smv.parser.ExprAST tmp16_AST = null;
			tmp16_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.addASTChild(currentAST, tmp16_AST);
			match(ONE);
			{
			AST tmp17_AST = null;
			tmp17_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp17_AST);
			match(COMMA);
			edu.toronto.cs.smv.parser.ExprAST tmp18_AST = null;
			tmp18_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.addASTChild(currentAST, tmp18_AST);
			match(ZERO);
			}
			elements_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = elements_AST;
	}
	
	public final void definition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definition_AST = null;
		Token  v = null;
		AST v_AST = null;
		
		v = LT(1);
		v_AST = astFactory.create(v);
		astFactory.addASTChild(currentAST, v_AST);
		match(VARNAME);
		AST tmp19_AST = null;
		tmp19_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp19_AST);
		match(ASSIGNOP);
		predicate();
		astFactory.addASTChild(currentAST, returnAST);
		match(SEMI);
		System.out.println("Definition of "+v.getText());
		definition_AST = (AST)currentAST.root;
		returnAST = definition_AST;
	}
	
	public final void predicate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST predicate_AST = null;
		
		impPred();
		astFactory.addASTChild(currentAST, returnAST);
		predicate_AST = (AST)currentAST.root;
		returnAST = predicate_AST;
	}
	
	public final void pred() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pred_AST = null;
		
		predicate();
		astFactory.addASTChild(currentAST, returnAST);
		match(SEMI);
		System.out.println("Done.");
		pred_AST = (AST)currentAST.root;
		returnAST = pred_AST;
	}
	
	public final void impPred() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST impPred_AST = null;
		
		orPred();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case IMPLIES:
		{
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(IMPLIES);
			System.out.println ("Read ->");
			orPred();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case TRANS:
		case COL:
		case SEMI:
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		impPred_AST = (AST)currentAST.root;
		returnAST = impPred_AST;
	}
	
	public final void orPred() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orPred_AST = null;
		
		andPred();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop35:
		do {
			if ((LA(1)==OR)) {
				edu.toronto.cs.smv.parser.ExprAST tmp23_AST = null;
				tmp23_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
				astFactory.makeASTRoot(currentAST, tmp23_AST);
				match(OR);
				andPred();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop35;
			}
			
		} while (true);
		}
		orPred_AST = (AST)currentAST.root;
		returnAST = orPred_AST;
	}
	
	public final void andPred() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andPred_AST = null;
		
		eqPred();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop38:
		do {
			if ((LA(1)==AND)) {
				edu.toronto.cs.smv.parser.ExprAST tmp24_AST = null;
				tmp24_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
				astFactory.makeASTRoot(currentAST, tmp24_AST);
				match(AND);
				eqPred();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop38;
			}
			
		} while (true);
		}
		andPred_AST = (AST)currentAST.root;
		returnAST = andPred_AST;
	}
	
	public final void eqPred() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST eqPred_AST = null;
		AST lhs_AST = null;
		AST rhs_AST = null;
		
		atom();
		lhs_AST = (AST)returnAST;
		eqPred_AST = (AST)currentAST.root;
		
		
		System.out.println("LHS: "+lhs_AST.toStringList());
		eqPred_AST = lhs_AST;
		
		currentAST.root = eqPred_AST;
		currentAST.child = eqPred_AST!=null &&eqPred_AST.getFirstChild()!=null ?
			eqPred_AST.getFirstChild() : eqPred_AST;
		currentAST.advanceChildToEnd();
		{
		switch ( LA(1)) {
		case EQ:
		{
			match(EQ);
			atom();
			rhs_AST = (AST)returnAST;
			eqPred_AST = (AST)currentAST.root;
			
			System.out.println("Handling equality.");
			if (((ExprAST)rhs_AST).isEnum())
			{
			System.out.println("LHS: "+lhs_AST.toStringList());
			System.out.println("in between");
			System.out.println("RHS "+rhs_AST.toStringList()+" is enum");
			try {
			//#eqPred = #[VALEQ, "=="];
			eqPred_AST = (AST)astFactory.make( (new ASTArray(3)).add((edu.toronto.cs.smv.parser.ExprAST)astFactory.create(VALEQ,"==","edu.toronto.cs.smv.parser.ExprAST")).add(lhs_AST).add(rhs_AST));
			
			}catch (Exception e) { e.printStackTrace();}
			}
			else {
			eqPred_AST = (AST)astFactory.make( (new ASTArray(3)).add((edu.toronto.cs.smv.parser.ExprAST)astFactory.create(BOOLEQ,"<=>","edu.toronto.cs.smv.parser.ExprAST")).add(lhs_AST).add(rhs_AST));
			}
			
			currentAST.root = eqPred_AST;
			currentAST.child = eqPred_AST!=null &&eqPred_AST.getFirstChild()!=null ?
				eqPred_AST.getFirstChild() : eqPred_AST;
			currentAST.advanceChildToEnd();
			break;
		}
		case EOF:
		case TRANS:
		case OR:
		case AND:
		case COL:
		case SEMI:
		case IMPLIES:
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		returnAST = eqPred_AST;
	}
	
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		Token  symbol = null;
		AST symbol_AST = null;
		Token  vn = null;
		AST vn_AST = null;
		
		switch ( LA(1)) {
		case ZERO:
		{
			edu.toronto.cs.smv.parser.ExprAST tmp26_AST = null;
			tmp26_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.addASTChild(currentAST, tmp26_AST);
			match(ZERO);
			atom_AST = (AST)currentAST.root;
			((ExprAST) atom_AST).setProp();
			atom_AST = (AST)currentAST.root;
			break;
		}
		case ONE:
		{
			edu.toronto.cs.smv.parser.ExprAST tmp27_AST = null;
			tmp27_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(ONE);
			atom_AST = (AST)currentAST.root;
			((ExprAST) atom_AST).setProp();
			atom_AST = (AST)currentAST.root;
			break;
		}
		case VARNAME:
		{
			symbol = LT(1);
			symbol_AST = astFactory.create(symbol);
			astFactory.addASTChild(currentAST, symbol_AST);
			match(VARNAME);
			atom_AST = (AST)currentAST.root;
			
			String sym = symbol.getText();
			// several cases to cope with!
			// 1. is it TRUE?
			if (sym.equals("TRUE"))
			{
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(ONE,"","edu.toronto.cs.smv.parser.ExprAST");
			((ExprAST) atom_AST).setProp();
			}
			// 2. FALSE?
			else if (sym.equals("FALSE")) {
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(ZERO,"","edu.toronto.cs.smv.parser.ExprAST");
			((ExprAST) atom_AST).setProp();
			}
			// 3. is it a propositional variable?
			else if (isPropVar(sym)) {
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(PROPVAR,sym,"edu.toronto.cs.smv.parser.ExprAST");
			((ExprAST) atom_AST).setProp();
			}
			// 4. is it enumerated?
			else if (isVarName(sym)) {
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(ENUMVAR,sym,"edu.toronto.cs.smv.parser.ExprAST");
			((ExprAST) atom_AST).setEnum(null);
			}
			else // 5. it's a value. 
			{
			System.out.println("Returning value atom: "+sym);
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(VALUE,sym,"edu.toronto.cs.smv.parser.ExprAST");    
			((ExprAST) atom_AST).setEnum(null);  
			}
			
			currentAST.root = atom_AST;
			currentAST.child = atom_AST!=null &&atom_AST.getFirstChild()!=null ?
				atom_AST.getFirstChild() : atom_AST;
			currentAST.advanceChildToEnd();
			atom_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			predicate();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			atom_AST = (AST)currentAST.root;
			break;
		}
		case NEXT:
		{
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp30_AST);
			match(NEXT);
			match(LPAREN);
			vn = LT(1);
			vn_AST = astFactory.create(vn);
			astFactory.addASTChild(currentAST, vn_AST);
			match(VARNAME);
			match(RPAREN);
			atom_AST = (AST)currentAST.root;
			String pn =
			new String(vn.getText()+"'");
			if (isPropVar(vn.getText())) {
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(NEXTPROPVAR,vn.getText(),"edu.toronto.cs.smv.parser.ExprAST"); 
			((ExprAST) atom_AST).setProp();
			}
			
			else {
			atom_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(NEXTENUMVAR,vn.getText(),"edu.toronto.cs.smv.parser.ExprAST");
			((ExprAST) atom_AST).setEnum(null);  }
			
			currentAST.root = atom_AST;
			currentAST.child = atom_AST!=null &&atom_AST.getFirstChild()!=null ?
				atom_AST.getFirstChild() : atom_AST;
			currentAST.advanceChildToEnd();
			atom_AST = (AST)currentAST.root;
			break;
		}
		case NEG:
		{
			edu.toronto.cs.smv.parser.ExprAST tmp33_AST = null;
			tmp33_AST = (edu.toronto.cs.smv.parser.ExprAST)astFactory.create(LT(1),"edu.toronto.cs.smv.parser.ExprAST");
			astFactory.makeASTRoot(currentAST, tmp33_AST);
			match(NEG);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			atom_AST = (AST)currentAST.root;
			((ExprAST) atom_AST).setProp();
			atom_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = atom_AST;
	}
	
	public final void init() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST init_AST = null;
		Token  v = null;
		AST v_AST = null;
		
		AST tmp34_AST = null;
		tmp34_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp34_AST);
		match(INIT);
		match(LPAREN);
		v = LT(1);
		v_AST = astFactory.create(v);
		astFactory.addASTChild(currentAST, v_AST);
		match(VARNAME);
		match(RPAREN);
		match(ASSIGNOP);
		predicate();
		astFactory.addASTChild(currentAST, returnAST);
		match(SEMI);
		System.out.println ("init of "+v.getText());
		init_AST = (AST)currentAST.root;
		returnAST = init_AST;
	}
	
	public final void next() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST next_AST = null;
		
		AST tmp39_AST = null;
		tmp39_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp39_AST);
		match(NEXT);
		match(LPAREN);
		AST tmp41_AST = null;
		tmp41_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp41_AST);
		match(VARNAME);
		match(RPAREN);
		match(ASSIGNOP);
		astmt();
		astFactory.addASTChild(currentAST, returnAST);
		next_AST = (AST)currentAST.root;
		returnAST = next_AST;
	}
	
	public final void astmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST astmt_AST = null;
		
		switch ( LA(1)) {
		case ZERO:
		case ONE:
		case NEG:
		case NEXT:
		case VARNAME:
		case LPAREN:
		{
			predicate();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			astmt_AST = (AST)currentAST.root;
			break;
		}
		case CASE:
		{
			kase();
			astFactory.addASTChild(currentAST, returnAST);
			astmt_AST = (AST)currentAST.root;
			break;
		}
		case LBRACE:
		{
			set();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			astmt_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = astmt_AST;
	}
	
	public final void kase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kase_AST = null;
		
		AST tmp46_AST = null;
		tmp46_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp46_AST);
		match(CASE);
		System.out.println("Reading a case statement");
		{
		int _cnt49=0;
		_loop49:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				cases();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt49>=1 ) { break _loop49; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt49++;
		} while (true);
		}
		match(ESAC);
		match(SEMI);
		kase_AST = (AST)currentAST.root;
		returnAST = kase_AST;
	}
	
	public final void cases() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cases_AST = null;
		
		predicate();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp49_AST = null;
		tmp49_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp49_AST);
		match(COL);
		astmt();
		astFactory.addASTChild(currentAST, returnAST);
		cases_AST = (AST)currentAST.root;
		returnAST = cases_AST;
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap = new Hashtable();
		tokenTypeToASTClassMap.put(new Integer(5), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(6), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(7), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(8), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(9), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(10), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(11), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(12), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(13), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(14), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(15), edu.toronto.cs.smv.parser.ExprAST.class);
		tokenTypeToASTClassMap.put(new Integer(16), edu.toronto.cs.smv.parser.ExprAST.class);
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 68853973760L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
