// $ANTLR 2.7.6 (2005-12-22): "CTL.g" -> "CTLParser.java"$
 package edu.toronto.cs.ctl.antlr;
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

public class CTLParser extends antlr.LLkParser       implements CTLLexerTokenTypes
 {

protected CTLParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CTLParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected CTLParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CTLParser(TokenStream lexer) {
  this(lexer,1);
}

public CTLParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void topLevel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST topLevel_AST = null;
		
		ctlExpression();
		astFactory.addASTChild(currentAST, returnAST);
		match(Token.EOF_TYPE);
		topLevel_AST = (AST)currentAST.root;
		returnAST = topLevel_AST;
	}
	
/** Boolean expressions hierarchy */
	public final void ctlExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ctlExpression_AST = null;
		
		impliesExpression();
		astFactory.addASTChild(currentAST, returnAST);
		ctlExpression_AST = (AST)currentAST.root;
		returnAST = ctlExpression_AST;
	}
	
	public final void impliesExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST impliesExpression_AST = null;
		
		orExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop41:
		do {
			if ((LA(1)==IMPLIES)) {
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp26_AST);
				match(IMPLIES);
				orExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop41;
			}
			
		} while (true);
		}
		impliesExpression_AST = (AST)currentAST.root;
		returnAST = impliesExpression_AST;
	}
	
	public final void orExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpression_AST = null;
		
		andExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop44:
		do {
			if ((LA(1)==OR)) {
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp27_AST);
				match(OR);
				andExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop44;
			}
			
		} while (true);
		}
		orExpression_AST = (AST)currentAST.root;
		returnAST = orExpression_AST;
	}
	
	public final void andExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpression_AST = null;
		
		notExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop47:
		do {
			if ((LA(1)==AND)) {
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp28_AST);
				match(AND);
				notExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop47;
			}
			
		} while (true);
		}
		andExpression_AST = (AST)currentAST.root;
		returnAST = andExpression_AST;
	}
	
	public final void notExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST notExpression_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		case NUMBER:
		case PLACEHOLDER:
		case IDENT:
		case FORALL:
		case FORSOME:
		case AX:
		case EX:
		case EF:
		case EG:
		case AF:
		case AG:
		{
			modalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			notExpression_AST = (AST)currentAST.root;
			break;
		}
		case NEG:
		{
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp29_AST);
			match(NEG);
			notExpression();
			astFactory.addASTChild(currentAST, returnAST);
			notExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = notExpression_AST;
	}
	
/** Modal expression and atomics */
	public final void modalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modalExpression_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		case NUMBER:
		case PLACEHOLDER:
		case IDENT:
		{
			comparissonExpression();
			astFactory.addASTChild(currentAST, returnAST);
			modalExpression_AST = (AST)currentAST.root;
			break;
		}
		case AX:
		case EX:
		case EF:
		case EG:
		case AF:
		case AG:
		{
			{
			switch ( LA(1)) {
			case AX:
			{
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp30_AST);
				match(AX);
				break;
			}
			case EX:
			{
				AST tmp31_AST = null;
				tmp31_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp31_AST);
				match(EX);
				break;
			}
			case EF:
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp32_AST);
				match(EF);
				break;
			}
			case EG:
			{
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp33_AST);
				match(EG);
				break;
			}
			case AF:
			{
				AST tmp34_AST = null;
				tmp34_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp34_AST);
				match(AF);
				break;
			}
			case AG:
			{
				AST tmp35_AST = null;
				tmp35_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp35_AST);
				match(AG);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			notExpression();
			astFactory.addASTChild(currentAST, returnAST);
			modalExpression_AST = (AST)currentAST.root;
			break;
		}
		case FORALL:
		case FORSOME:
		{
			{
			switch ( LA(1)) {
			case FORALL:
			{
				AST tmp36_AST = null;
				tmp36_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp36_AST);
				match(FORALL);
				break;
			}
			case FORSOME:
			{
				AST tmp37_AST = null;
				tmp37_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp37_AST);
				match(FORSOME);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LSQPAREN);
			untilExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RSQPAREN);
			modalExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = modalExpression_AST;
	}
	
	public final void comparissonExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comparissonExpression_AST = null;
		
		basicExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case EQ:
		case GEQ:
		case LEQ:
		case NEQ:
		{
			{
			switch ( LA(1)) {
			case EQ:
			{
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp40_AST);
				match(EQ);
				break;
			}
			case LEQ:
			{
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp41_AST);
				match(LEQ);
				break;
			}
			case GEQ:
			{
				AST tmp42_AST = null;
				tmp42_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp42_AST);
				match(GEQ);
				break;
			}
			case NEQ:
			{
				AST tmp43_AST = null;
				tmp43_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp43_AST);
				match(NEQ);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			basicExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case RPAREN:
		case RSQPAREN:
		case IMPLIES:
		case OR:
		case AND:
		case UNTIL:
		case RELEASE:
		case WEAK:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		comparissonExpression_AST = (AST)currentAST.root;
		returnAST = comparissonExpression_AST;
	}
	
	public final void untilExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST untilExpression_AST = null;
		
		ctlExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case UNTIL:
		{
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp44_AST);
			match(UNTIL);
			break;
		}
		case RELEASE:
		{
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp45_AST);
			match(RELEASE);
			break;
		}
		case WEAK:
		{
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp46_AST);
			match(WEAK);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		ctlExpression();
		astFactory.addASTChild(currentAST, returnAST);
		untilExpression_AST = (AST)currentAST.root;
		returnAST = untilExpression_AST;
	}
	
	public final void basicExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basicExpression_AST = null;
		
		switch ( LA(1)) {
		case NUMBER:
		case IDENT:
		{
			atomic();
			astFactory.addASTChild(currentAST, returnAST);
			basicExpression_AST = (AST)currentAST.root;
			break;
		}
		case PLACEHOLDER:
		{
			placeholder();
			astFactory.addASTChild(currentAST, returnAST);
			basicExpression_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			ctlExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			basicExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = basicExpression_AST;
	}
	
	public final void atomic() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomic_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			AST tmp49_AST = null;
			tmp49_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp49_AST);
			match(IDENT);
			atomic_AST = (AST)currentAST.root;
			break;
		}
		case NUMBER:
		{
			AST tmp50_AST = null;
			tmp50_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp50_AST);
			match(NUMBER);
			atomic_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = atomic_AST;
	}
	
	public final void placeholder() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST placeholder_AST = null;
		
		AST tmp51_AST = null;
		tmp51_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp51_AST);
		match(PLACEHOLDER);
		atomic();
		astFactory.addASTChild(currentAST, returnAST);
		match(LBRACE);
		atomicSet();
		astFactory.addASTChild(currentAST, returnAST);
		match(RBRACE);
		placeholder_AST = (AST)currentAST.root;
		returnAST = placeholder_AST;
	}
	
	public final void atomicSet() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomicSet_AST = null;
		
		atomic();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop61:
		do {
			if ((LA(1)==COMMA)) {
				AST tmp54_AST = null;
				tmp54_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp54_AST);
				match(COMMA);
				atomic();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop61;
			}
			
		} while (true);
		}
		atomicSet_AST = (AST)currentAST.root;
		returnAST = atomicSet_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LPAREN",
		"RPAREN",
		"LBRACE",
		"RBRACE",
		"LSQPAREN",
		"RSQPAREN",
		"IMPLIES",
		"IMPLIED",
		"IFF",
		"EQ",
		"GEQ",
		"LEQ",
		"NEQ",
		"OR",
		"AND",
		"NEG",
		"NUMBER",
		"DIGIT",
		"CAPITAL",
		"LOWER",
		"COMMA",
		"PLACEHOLDER",
		"IDENT",
		"COL",
		"SEMI",
		"WS",
		"NEWLINE",
		"\"A\"",
		"\"E\"",
		"\"U\"",
		"\"R\"",
		"\"W\"",
		"\"AX\"",
		"\"EX\"",
		"\"EF\"",
		"\"EG\"",
		"\"AF\"",
		"\"AG\""
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	
	}
