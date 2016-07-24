// $ANTLR 2.7.6 (2005-12-22): "gclang.g" -> "GCLangParser.java"$
 package edu.toronto.cs.gclang.parser;
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
import edu.toronto.cs.gclang.parser.VariableTable;
import edu.toronto.cs.gclang.parser.VariableTable.*;

public class GCLangParser extends antlr.LLkParser       implements GCLangLexerTokenTypes
 {

    VariableTable symbolTable = new VariableTable (2);

    public VariableTable getSymbolTable ()
    { return symbolTable; }

protected GCLangParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public GCLangParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected GCLangParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public GCLangParser(TokenStream lexer) {
  this(lexer,2);
}

public GCLangParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void expr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expr_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		case NEG:
		case VARNAME:
		case NUMBER:
		case TRUE:
		case FALSE:
		{
			implExpr();
			astFactory.addASTChild(currentAST, returnAST);
			expr_AST = (AST)currentAST.root;
			break;
		}
		case LBRACE:
		{
			setExpr();
			astFactory.addASTChild(currentAST, returnAST);
			expr_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = expr_AST;
	}
	
	public final void implExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implExpr_AST = null;
		
		iffExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop41:
		do {
			if ((LA(1)==IMPLIES)) {
				AST tmp20_AST = null;
				tmp20_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp20_AST);
				match(IMPLIES);
				iffExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop41;
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
		
		AST tmp21_AST = null;
		tmp21_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp21_AST);
		match(LBRACE);
		setElement();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop77:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				setElement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop77;
			}
			
		} while (true);
		}
		match(RBRACE);
		setExpr_AST = (AST)currentAST.root;
		returnAST = setExpr_AST;
	}
	
	public final void iffExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iffExpr_AST = null;
		
		orExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop44:
		do {
			if ((LA(1)==IFF)) {
				AST tmp24_AST = null;
				tmp24_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp24_AST);
				match(IFF);
				orExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop44;
			}
			
		} while (true);
		}
		iffExpr_AST = (AST)currentAST.root;
		returnAST = iffExpr_AST;
	}
	
	public final void orExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpr_AST = null;
		
		andExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop48:
		do {
			if ((LA(1)==OR||LA(1)==XOR)) {
				{
				switch ( LA(1)) {
				case OR:
				{
					AST tmp25_AST = null;
					tmp25_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp25_AST);
					match(OR);
					break;
				}
				case XOR:
				{
					AST tmp26_AST = null;
					tmp26_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp26_AST);
					match(XOR);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				andExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop48;
			}
			
		} while (true);
		}
		orExpr_AST = (AST)currentAST.root;
		returnAST = orExpr_AST;
	}
	
	public final void andExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpr_AST = null;
		
		negExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop51:
		do {
			if ((LA(1)==AND)) {
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp27_AST);
				match(AND);
				negExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop51;
			}
			
		} while (true);
		}
		andExpr_AST = (AST)currentAST.root;
		returnAST = andExpr_AST;
	}
	
	public final void negExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case NEG:
		{
			AST tmp28_AST = null;
			tmp28_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp28_AST);
			match(NEG);
			break;
		}
		case LPAREN:
		case VARNAME:
		case NUMBER:
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
		comparisonExpr();
		astFactory.addASTChild(currentAST, returnAST);
		negExpr_AST = (AST)currentAST.root;
		returnAST = negExpr_AST;
	}
	
	public final void comparisonExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comparisonExpr_AST = null;
		
		modExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop57:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case EQ:
				{
					AST tmp29_AST = null;
					tmp29_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp29_AST);
					match(EQ);
					break;
				}
				case NEQ:
				{
					AST tmp30_AST = null;
					tmp30_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp30_AST);
					match(NEQ);
					break;
				}
				case LT:
				{
					AST tmp31_AST = null;
					tmp31_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp31_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp32_AST = null;
					tmp32_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp32_AST);
					match(GT);
					break;
				}
				case LEQ:
				{
					AST tmp33_AST = null;
					tmp33_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp33_AST);
					match(LEQ);
					break;
				}
				case GEQ:
				{
					AST tmp34_AST = null;
					tmp34_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp34_AST);
					match(GEQ);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				modExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop57;
			}
			
		} while (true);
		}
		comparisonExpr_AST = (AST)currentAST.root;
		returnAST = comparisonExpr_AST;
	}
	
	public final void modExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modExpr_AST = null;
		
		sumExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop60:
		do {
			if ((LA(1)==MOD)) {
				AST tmp35_AST = null;
				tmp35_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp35_AST);
				match(MOD);
				sumExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop60;
			}
			
		} while (true);
		}
		modExpr_AST = (AST)currentAST.root;
		returnAST = modExpr_AST;
	}
	
	public final void sumExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumExpr_AST = null;
		
		multExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop64:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp36_AST = null;
					tmp36_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp36_AST);
					match(PLUS);
					break;
				}
				case MINUS:
				{
					AST tmp37_AST = null;
					tmp37_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp37_AST);
					match(MINUS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				multExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop64;
			}
			
		} while (true);
		}
		sumExpr_AST = (AST)currentAST.root;
		returnAST = sumExpr_AST;
	}
	
	public final void multExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multExpr_AST = null;
		
		basicExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop68:
		do {
			if ((LA(1)==MULT||LA(1)==DIV)) {
				{
				switch ( LA(1)) {
				case MULT:
				{
					AST tmp38_AST = null;
					tmp38_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp38_AST);
					match(MULT);
					break;
				}
				case DIV:
				{
					AST tmp39_AST = null;
					tmp39_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp39_AST);
					match(DIV);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				basicExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop68;
			}
			
		} while (true);
		}
		multExpr_AST = (AST)currentAST.root;
		returnAST = multExpr_AST;
	}
	
	public final void basicExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basicExpr_AST = null;
		Token  v = null;
		AST v_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			v = LT(1);
			v_AST = astFactory.create(v);
			astFactory.addASTChild(currentAST, v_AST);
			match(VARNAME);
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
			expr();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
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
	
	public final void number() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST number_AST = null;
		
		AST tmp42_AST = null;
		tmp42_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp42_AST);
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
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp43_AST);
			match(TRUE);
			boolConstant_AST = (AST)currentAST.root;
			break;
		}
		case FALSE:
		{
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp44_AST);
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
		
		AST tmp45_AST = null;
		tmp45_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp45_AST);
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
	
	public final void varBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varBlock_AST = null;
		
		AST tmp46_AST = null;
		tmp46_AST = astFactory.create(LT(1));
		match(VAR);
		{
		int _cnt81=0;
		_loop81:
		do {
			if ((LA(1)==VARNAME)) {
				varDecl();
			}
			else {
				if ( _cnt81>=1 ) { break _loop81; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt81++;
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
		AST tmp47_AST = null;
		tmp47_AST = astFactory.create(LT(1));
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
	
	public final Object  type() throws RecognitionException, TokenStreamException {
		Object typeObject;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN:
		{
			AST tmp49_AST = null;
			tmp49_AST = astFactory.create(LT(1));
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
		
		AST tmp50_AST = null;
		tmp50_AST = astFactory.create(LT(1));
		match(LBRACE);
		val=textOrNumberValued();
		set = new HashSet (); set.add (val);
		{
		_loop86:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				val=textOrNumberValued();
				set.add (val);
			}
			else {
				break _loop86;
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
	
	public final void command() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST command_AST = null;
		
		sequenceCommand();
		astFactory.addASTChild(currentAST, returnAST);
		command_AST = (AST)currentAST.root;
		returnAST = command_AST;
	}
	
	public final void sequenceCommand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sequenceCommand_AST = null;
		
		choiceCommand();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop92:
		do {
			if ((LA(1)==SEMI)) {
				AST tmp53_AST = null;
				tmp53_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp53_AST);
				match(SEMI);
				choiceCommand();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop92;
			}
			
		} while (true);
		}
		sequenceCommand_AST = (AST)currentAST.root;
		returnAST = sequenceCommand_AST;
	}
	
	public final void choiceCommand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST choiceCommand_AST = null;
		
		atomicCommand();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop95:
		do {
			if ((LA(1)==CHOICE)) {
				AST tmp54_AST = null;
				tmp54_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp54_AST);
				match(CHOICE);
				atomicCommand();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop95;
			}
			
		} while (true);
		}
		choiceCommand_AST = (AST)currentAST.root;
		returnAST = choiceCommand_AST;
	}
	
	public final void atomicCommand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomicCommand_AST = null;
		
		switch ( LA(1)) {
		case VARNAME:
		{
			assign();
			astFactory.addASTChild(currentAST, returnAST);
			atomicCommand_AST = (AST)currentAST.root;
			break;
		}
		case SKIP:
		{
			skip();
			astFactory.addASTChild(currentAST, returnAST);
			atomicCommand_AST = (AST)currentAST.root;
			break;
		}
		case IF:
		{
			ite();
			astFactory.addASTChild(currentAST, returnAST);
			atomicCommand_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			command();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			atomicCommand_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = atomicCommand_AST;
	}
	
	public final void assign() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assign_AST = null;
		AST one_AST = null;
		AST two_AST = null;
		
		atom();
		one_AST = (AST)returnAST;
		AST tmp57_AST = null;
		tmp57_AST = astFactory.create(LT(1));
		match(ASSIGNOP);
		expr();
		two_AST = (AST)returnAST;
		assign_AST = (AST)currentAST.root;
		
		String varName = one_AST.getText ();
		Variable var = symbolTable.getByName (varName);
		if (var != null && var instanceof EnumeratedVariable)
		assign_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGNOPSPEC,":==")).add(one_AST).add(two_AST)); 
		else
		assign_AST = (AST)astFactory.make( (new ASTArray(3)).add(tmp57_AST).add(one_AST).add(two_AST));
		
		currentAST.root = assign_AST;
		currentAST.child = assign_AST!=null &&assign_AST.getFirstChild()!=null ?
			assign_AST.getFirstChild() : assign_AST;
		currentAST.advanceChildToEnd();
		returnAST = assign_AST;
	}
	
	public final void skip() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST skip_AST = null;
		
		AST tmp58_AST = null;
		tmp58_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp58_AST);
		match(SKIP);
		skip_AST = (AST)currentAST.root;
		returnAST = skip_AST;
	}
	
	public final void ite() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ite_AST = null;
		
		AST tmp59_AST = null;
		tmp59_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp59_AST);
		match(IF);
		match(LPAREN);
		expr();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		{
		switch ( LA(1)) {
		case THEN:
		{
			match(THEN);
			break;
		}
		case LPAREN:
		case VARNAME:
		case SKIP:
		case IF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		iteBody();
		astFactory.addASTChild(currentAST, returnAST);
		ite_AST = (AST)currentAST.root;
		returnAST = ite_AST;
	}
	
	public final void iteBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteBody_AST = null;
		
		command();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp63_AST = null;
		tmp63_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp63_AST);
		match(ELSE);
		command();
		astFactory.addASTChild(currentAST, returnAST);
		match(FI);
		iteBody_AST = (AST)currentAST.root;
		returnAST = iteBody_AST;
	}
	
	protected final void guard() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST guard_AST = null;
		
		expr();
		astFactory.addASTChild(currentAST, returnAST);
		guard_AST = (AST)currentAST.root;
		returnAST = guard_AST;
	}
	
	public final void guardedCommand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST guardedCommand_AST = null;
		
		guard();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp65_AST = null;
		tmp65_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp65_AST);
		match(COL);
		command();
		astFactory.addASTChild(currentAST, returnAST);
		guardedCommand_AST = (AST)currentAST.root;
		returnAST = guardedCommand_AST;
	}
	
	public final void rulesBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rulesBlock_AST = null;
		
		AST tmp66_AST = null;
		tmp66_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp66_AST);
		match(RULES);
		{
		int _cnt106=0;
		_loop106:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				guardedCommand();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt106>=1 ) { break _loop106; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt106++;
		} while (true);
		}
		rulesBlock_AST = (AST)currentAST.root;
		returnAST = rulesBlock_AST;
	}
	
	public final void initBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST initBlock_AST = null;
		
		AST tmp67_AST = null;
		tmp67_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp67_AST);
		match(INIT);
		expr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMI:
		{
			match(SEMI);
			break;
		}
		case RULES:
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
	
	public final void start() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST start_AST = null;
		
		AST tmp69_AST = null;
		tmp69_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp69_AST);
		match(NAME);
		atom();
		astFactory.addASTChild(currentAST, returnAST);
		varBlock();
		astFactory.addASTChild(currentAST, returnAST);
		initBlock();
		astFactory.addASTChild(currentAST, returnAST);
		rulesBlock();
		astFactory.addASTChild(currentAST, returnAST);
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 272678883689472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 51558506512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
