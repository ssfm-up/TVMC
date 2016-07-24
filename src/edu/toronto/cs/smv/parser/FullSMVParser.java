// $ANTLR 2.7.6 (2005-12-22): "smv.g" -> "FullSMVParser.java"$
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
import edu.toronto.cs.smv.VariableTable;

public class FullSMVParser extends antlr.LLkParser       implements FullSMVLexerTokenTypes
 {

    VariableTable symbolTable = new VariableTable ();

    public VariableTable getSymbolTable ()
    { return symbolTable; }

protected FullSMVParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public FullSMVParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected FullSMVParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public FullSMVParser(TokenStream lexer) {
  this(lexer,1);
}

public FullSMVParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void simpleExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleExpr_AST = null;
		
		simpleExprPrivate(false);
		astFactory.addASTChild(currentAST, returnAST);
		simpleExpr_AST = (AST)currentAST.root;
		returnAST = simpleExpr_AST;
	}
	
	protected final void simpleExprPrivate(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleExprPrivate_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		case NEG:
		case VARNAME:
		case NUMBER:
		case NEXT:
		case TRUE:
		case FALSE:
		{
			implExpr(isNext);
			astFactory.addASTChild(currentAST, returnAST);
			simpleExprPrivate_AST = (AST)currentAST.root;
			break;
		}
		case LBRACE:
		{
			setExpr();
			astFactory.addASTChild(currentAST, returnAST);
			simpleExprPrivate_AST = (AST)currentAST.root;
			break;
		}
		case CASE:
		{
			caseExpr(isNext);
			astFactory.addASTChild(currentAST, returnAST);
			simpleExprPrivate_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = simpleExprPrivate_AST;
	}
	
	public final void implExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implExpr_AST = null;
		
		iffExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop40:
		do {
			if ((LA(1)==IMPLIES)) {
				AST tmp1_AST = null;
				tmp1_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp1_AST);
				match(IMPLIES);
				iffExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop40;
			}
			
		} while (true);
		}
		implExpr_AST = (AST)currentAST.root;
		returnAST = implExpr_AST;
	}
	
	public final void setExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setExpr_AST = null;
		
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp2_AST);
		match(LBRACE);
		setElement();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop78:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				setElement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop78;
			}
			
		} while (true);
		}
		match(RBRACE);
		setExpr_AST = (AST)currentAST.root;
		returnAST = setExpr_AST;
	}
	
	public final void caseExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseExpr_AST = null;
		
		AST tmp5_AST = null;
		tmp5_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp5_AST);
		match(CASE);
		{
		int _cnt82=0;
		_loop82:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				caseBody(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt82>=1 ) { break _loop82; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt82++;
		} while (true);
		}
		match(ESAC);
		caseExpr_AST = (AST)currentAST.root;
		returnAST = caseExpr_AST;
	}
	
	public final void iffExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iffExpr_AST = null;
		
		orExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop43:
		do {
			if ((LA(1)==IFF)) {
				AST tmp7_AST = null;
				tmp7_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp7_AST);
				match(IFF);
				orExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop43;
			}
			
		} while (true);
		}
		iffExpr_AST = (AST)currentAST.root;
		returnAST = iffExpr_AST;
	}
	
	public final void orExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpr_AST = null;
		
		andExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop47:
		do {
			if ((LA(1)==OR||LA(1)==XOR)) {
				{
				switch ( LA(1)) {
				case OR:
				{
					AST tmp8_AST = null;
					tmp8_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp8_AST);
					match(OR);
					break;
				}
				case XOR:
				{
					AST tmp9_AST = null;
					tmp9_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp9_AST);
					match(XOR);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				andExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop47;
			}
			
		} while (true);
		}
		orExpr_AST = (AST)currentAST.root;
		returnAST = orExpr_AST;
	}
	
	public final void andExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpr_AST = null;
		
		negExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop50:
		do {
			if ((LA(1)==AND)) {
				AST tmp10_AST = null;
				tmp10_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp10_AST);
				match(AND);
				negExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop50;
			}
			
		} while (true);
		}
		andExpr_AST = (AST)currentAST.root;
		returnAST = andExpr_AST;
	}
	
	public final void negExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case NEG:
		{
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp11_AST);
			match(NEG);
			break;
		}
		case LPAREN:
		case VARNAME:
		case NUMBER:
		case NEXT:
		case TRUE:
		case FALSE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		comparisonExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		negExpr_AST = (AST)currentAST.root;
		returnAST = negExpr_AST;
	}
	
	public final void comparisonExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comparisonExpr_AST = null;
		
		modExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop56:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case EQ:
				{
					AST tmp12_AST = null;
					tmp12_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp12_AST);
					match(EQ);
					break;
				}
				case NEQ:
				{
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp13_AST);
					match(NEQ);
					break;
				}
				case LT:
				{
					AST tmp14_AST = null;
					tmp14_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp14_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp15_AST = null;
					tmp15_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp15_AST);
					match(GT);
					break;
				}
				case LEQ:
				{
					AST tmp16_AST = null;
					tmp16_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp16_AST);
					match(LEQ);
					break;
				}
				case GEQ:
				{
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp17_AST);
					match(GEQ);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				modExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop56;
			}
			
		} while (true);
		}
		comparisonExpr_AST = (AST)currentAST.root;
		returnAST = comparisonExpr_AST;
	}
	
	public final void modExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modExpr_AST = null;
		
		sumExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop59:
		do {
			if ((LA(1)==MOD)) {
				AST tmp18_AST = null;
				tmp18_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp18_AST);
				match(MOD);
				sumExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop59;
			}
			
		} while (true);
		}
		modExpr_AST = (AST)currentAST.root;
		returnAST = modExpr_AST;
	}
	
	public final void sumExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumExpr_AST = null;
		
		multExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop63:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp19_AST = null;
					tmp19_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp19_AST);
					match(PLUS);
					break;
				}
				case MINUS:
				{
					AST tmp20_AST = null;
					tmp20_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp20_AST);
					match(MINUS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				multExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop63;
			}
			
		} while (true);
		}
		sumExpr_AST = (AST)currentAST.root;
		returnAST = sumExpr_AST;
	}
	
	public final void multExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multExpr_AST = null;
		
		basicExpr(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop67:
		do {
			if ((LA(1)==MULT||LA(1)==DIV)) {
				{
				switch ( LA(1)) {
				case MULT:
				{
					AST tmp21_AST = null;
					tmp21_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp21_AST);
					match(MULT);
					break;
				}
				case DIV:
				{
					AST tmp22_AST = null;
					tmp22_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp22_AST);
					match(DIV);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				basicExpr(isNext);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop67;
			}
			
		} while (true);
		}
		multExpr_AST = (AST)currentAST.root;
		returnAST = multExpr_AST;
	}
	
	public final void basicExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basicExpr_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			maybeVarName(isNext);
			astFactory.addASTChild(currentAST, returnAST);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		case NUMBER:
		{
			number();
			astFactory.addASTChild(currentAST, returnAST);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		case TRUE:
		case FALSE:
		{
			boolConstant();
			astFactory.addASTChild(currentAST, returnAST);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			simpleExprPrivate(isNext);
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		case NEXT:
		{
			nextExpr(isNext);
			astFactory.addASTChild(currentAST, returnAST);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = basicExpr_AST;
	}
	
	protected final void maybeVarName(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeVarName_AST = null;
		Token  v = null;
		AST v_AST = null;
		
		v = LT(1);
		v_AST = astFactory.create(v);
		match(VARNAME);
		maybeVarName_AST = (AST)currentAST.root;
		
		// -- if this is a variable, rewrite into next(v)
		if (isNext && symbolTable.getByName (v.getText ()) != null)
		{
		maybeVarName_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NEXT,"next")).add(v_AST));
		}
		else
		// -- not a variable so next(v) == v
		maybeVarName_AST = v_AST;
		
		currentAST.root = maybeVarName_AST;
		currentAST.child = maybeVarName_AST!=null &&maybeVarName_AST.getFirstChild()!=null ?
			maybeVarName_AST.getFirstChild() : maybeVarName_AST;
		currentAST.advanceChildToEnd();
		returnAST = maybeVarName_AST;
	}
	
	public final void number() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST number_AST = null;
		
		AST tmp25_AST = null;
		tmp25_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp25_AST);
		match(NUMBER);
		number_AST = (AST)currentAST.root;
		returnAST = number_AST;
	}
	
	public final void boolConstant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST boolConstant_AST = null;
		
		switch ( LA(1)) {
		case TRUE:
		{
			AST tmp26_AST = null;
			tmp26_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp26_AST);
			match(TRUE);
			boolConstant_AST = (AST)currentAST.root;
			break;
		}
		case FALSE:
		{
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(FALSE);
			boolConstant_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = boolConstant_AST;
	}
	
	public final void nextExpr(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nextExpr_AST = null;
		AST expr_AST = null;
		
		assert !isNext;
		AST tmp28_AST = null;
		tmp28_AST = astFactory.create(LT(1));
		match(NEXT);
		match(LPAREN);
		simpleExprPrivate(true);
		expr_AST = (AST)returnAST;
		match(RPAREN);
		nextExpr_AST = (AST)currentAST.root;
		
		nextExpr_AST = expr_AST;
		
		currentAST.root = nextExpr_AST;
		currentAST.child = nextExpr_AST!=null &&nextExpr_AST.getFirstChild()!=null ?
			nextExpr_AST.getFirstChild() : nextExpr_AST;
		currentAST.advanceChildToEnd();
		returnAST = nextExpr_AST;
	}
	
	protected final String  atomValued() throws RecognitionException, TokenStreamException {
		String val;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomValued_AST = null;
		Token  var = null;
		AST var_AST = null;
		
		var = LT(1);
		var_AST = astFactory.create(var);
		astFactory.addASTChild(currentAST, var_AST);
		match(VARNAME);
		val = var.getText ();
		atomValued_AST = (AST)currentAST.root;
		returnAST = atomValued_AST;
		return val;
	}
	
	protected final String  numberValued() throws RecognitionException, TokenStreamException {
		String val;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numberValued_AST = null;
		Token  num = null;
		AST num_AST = null;
		
		num = LT(1);
		num_AST = astFactory.create(num);
		astFactory.addASTChild(currentAST, num_AST);
		match(NUMBER);
		val = num.getText ();
		numberValued_AST = (AST)currentAST.root;
		returnAST = numberValued_AST;
		return val;
	}
	
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		
		AST tmp31_AST = null;
		tmp31_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp31_AST);
		match(VARNAME);
		atom_AST = (AST)currentAST.root;
		returnAST = atom_AST;
	}
	
	public final void setElement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setElement_AST = null;
		
		textOrNumber();
		astFactory.addASTChild(currentAST, returnAST);
		setElement_AST = (AST)currentAST.root;
		returnAST = setElement_AST;
	}
	
	protected final void textOrNumber() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST textOrNumber_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			textOrNumber_AST = (AST)currentAST.root;
			break;
		}
		case NUMBER:
		{
			number();
			astFactory.addASTChild(currentAST, returnAST);
			textOrNumber_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = textOrNumber_AST;
	}
	
	protected final void caseBody(
		boolean isNext
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseBody_AST = null;
		
		simpleExprPrivate(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp32_AST = null;
		tmp32_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp32_AST);
		match(COL);
		simpleExprPrivate(isNext);
		astFactory.addASTChild(currentAST, returnAST);
		match(SEMI);
		caseBody_AST = (AST)currentAST.root;
		returnAST = caseBody_AST;
	}
	
	public final void varBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varBlock_AST = null;
		
		AST tmp34_AST = null;
		tmp34_AST = astFactory.create(LT(1));
		match(VAR);
		{
		int _cnt86=0;
		_loop86:
		do {
			if ((LA(1)==VARNAME)) {
				varDecl();
			}
			else {
				if ( _cnt86>=1 ) { break _loop86; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt86++;
		} while (true);
		}
		returnAST = varBlock_AST;
	}
	
	public final void varDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varDecl_AST = null;
		String varName; Object varType;
		
		varName=atomValued();
		AST tmp35_AST = null;
		tmp35_AST = astFactory.create(LT(1));
		match(COL);
		varType=type();
		match(SEMI);
		
		if (varType == Boolean.class)
		symbolTable.declarePropositional (varName);
		else if (varType instanceof Collection && 
		((Collection)varType).size () > 1)
		symbolTable.declareEnumerated (varName, (Collection)varType);
		else
		System.out.println ("Could not define variable: " + varName);
		
		returnAST = varDecl_AST;
	}
	
	public final void ivarBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ivarBlock_AST = null;
		
		AST tmp37_AST = null;
		tmp37_AST = astFactory.create(LT(1));
		match(IVAR);
		{
		int _cnt89=0;
		_loop89:
		do {
			if ((LA(1)==VARNAME)) {
				varDecl();
			}
			else {
				if ( _cnt89>=1 ) { break _loop89; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt89++;
		} while (true);
		}
		returnAST = ivarBlock_AST;
	}
	
	public final Object  type() throws RecognitionException, TokenStreamException {
		Object typeObject;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN:
		{
			AST tmp38_AST = null;
			tmp38_AST = astFactory.create(LT(1));
			match(BOOLEAN);
			typeObject = Boolean.class;
			break;
		}
		case LBRACE:
		{
			typeObject=textSet();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = type_AST;
		return typeObject;
	}
	
	public final Set  textSet() throws RecognitionException, TokenStreamException {
		Set set;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST textSet_AST = null;
		String val;
		
		AST tmp39_AST = null;
		tmp39_AST = astFactory.create(LT(1));
		match(LBRACE);
		val=textOrNumberValued();
		set = new HashSet (); set.add (val);
		{
		_loop94:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				val=textOrNumberValued();
				set.add (val);
			}
			else {
				break _loop94;
			}
			
		} while (true);
		}
		match(RBRACE);
		returnAST = textSet_AST;
		return set;
	}
	
	protected final String  textOrNumberValued() throws RecognitionException, TokenStreamException {
		String value;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST textOrNumberValued_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			value=atomValued();
			astFactory.addASTChild(currentAST, returnAST);
			textOrNumberValued_AST = (AST)currentAST.root;
			break;
		}
		case NUMBER:
		{
			value=numberValued();
			astFactory.addASTChild(currentAST, returnAST);
			textOrNumberValued_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = textOrNumberValued_AST;
		return value;
	}
	
	public final void assignBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignBlock_AST = null;
		
		AST tmp42_AST = null;
		tmp42_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp42_AST);
		match(ASSIGN);
		{
		int _cnt99=0;
		_loop99:
		do {
			if ((LA(1)==VARNAME||LA(1)==NEXT||LA(1)==INIT)) {
				assignBody();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt99>=1 ) { break _loop99; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt99++;
		} while (true);
		}
		assignBlock_AST = (AST)currentAST.root;
		returnAST = assignBlock_AST;
	}
	
	public final void assignBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignBody_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp43_AST);
			match(ASSIGNOP);
			simpleExpr();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			assignBody_AST = (AST)currentAST.root;
			break;
		}
		case NEXT:
		case INIT:
		{
			{
			switch ( LA(1)) {
			case INIT:
			{
				AST tmp45_AST = null;
				tmp45_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp45_AST);
				match(INIT);
				break;
			}
			case NEXT:
			{
				AST tmp46_AST = null;
				tmp46_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp46_AST);
				match(NEXT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			match(LPAREN);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			AST tmp49_AST = null;
			tmp49_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp49_AST);
			match(ASSIGNOP);
			simpleExpr();
			astFactory.addASTChild(currentAST, returnAST);
			}
			match(SEMI);
			assignBody_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = assignBody_AST;
	}
	
	public final void transBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST transBlock_AST = null;
		
		AST tmp51_AST = null;
		tmp51_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp51_AST);
		match(TRANS);
		simpleExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMI:
		{
			match(SEMI);
			break;
		}
		case EOF:
		case TRANS:
		case ASSIGN:
		case MODULE:
		case CAPITAL_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		transBlock_AST = (AST)currentAST.root;
		returnAST = transBlock_AST;
	}
	
	public final void initBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST initBlock_AST = null;
		
		AST tmp53_AST = null;
		tmp53_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp53_AST);
		match(CAPITAL_INIT);
		simpleExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMI:
		{
			match(SEMI);
			break;
		}
		case EOF:
		case TRANS:
		case ASSIGN:
		case MODULE:
		case CAPITAL_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		initBlock_AST = (AST)currentAST.root;
		returnAST = initBlock_AST;
	}
	
	public final void invarBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST invarBlock_AST = null;
		
		AST tmp55_AST = null;
		tmp55_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp55_AST);
		match(INVAR);
		simpleExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMI:
		{
			match(SEMI);
			break;
		}
		case EOF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		invarBlock_AST = (AST)currentAST.root;
		returnAST = invarBlock_AST;
	}
	
	public final void defineBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defineBlock_AST = null;
		
		AST tmp57_AST = null;
		tmp57_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp57_AST);
		match(DEFINE);
		{
		int _cnt111=0;
		_loop111:
		do {
			if ((LA(1)==VARNAME)) {
				defineBody();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMI);
			}
			else {
				if ( _cnt111>=1 ) { break _loop111; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt111++;
		} while (true);
		}
		defineBlock_AST = (AST)currentAST.root;
		returnAST = defineBlock_AST;
	}
	
	public final void defineBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defineBody_AST = null;
		
		atom();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp59_AST = null;
		tmp59_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp59_AST);
		match(ASSIGNOP);
		simpleExpr();
		astFactory.addASTChild(currentAST, returnAST);
		defineBody_AST = (AST)currentAST.root;
		returnAST = defineBody_AST;
	}
	
	public final void isaDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST isaDecl_AST = null;
		
		AST tmp60_AST = null;
		tmp60_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp60_AST);
		match(ISA);
		atom();
		astFactory.addASTChild(currentAST, returnAST);
		isaDecl_AST = (AST)currentAST.root;
		returnAST = isaDecl_AST;
	}
	
	public final void moduleDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleDecl_AST = null;
		
		AST tmp61_AST = null;
		tmp61_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp61_AST);
		match(MODULE);
		atom();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case IVAR:
		{
			ivarBlock();
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
		varBlock();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case DEFINE:
		{
			defineBlock();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case TRANS:
		case ASSIGN:
		case CAPITAL_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		int _cnt118=0;
		_loop118:
		do {
			switch ( LA(1)) {
			case ASSIGN:
			{
				assignBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case TRANS:
			{
				transBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CAPITAL_INIT:
			{
				initBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				if ( _cnt118>=1 ) { break _loop118; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt118++;
		} while (true);
		}
		moduleDecl_AST = (AST)currentAST.root;
		returnAST = moduleDecl_AST;
	}
	
	public final void start() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST start_AST = null;
		
		moduleDecl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop121:
		do {
			if ((LA(1)==MODULE)) {
				moduleDecl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop121;
			}
			
		} while (true);
		}
		match(Token.EOF_TYPE);
		start_AST = (AST)currentAST.root;
		returnAST = start_AST;
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 3333973088272L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 1090715534754048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
