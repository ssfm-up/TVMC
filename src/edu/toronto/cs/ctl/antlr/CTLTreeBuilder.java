// $ANTLR 2.7.6 (2005-12-22): "CTL.g" -> "CTLTreeBuilder.java"$
 package edu.toronto.cs.ctl.antlr;
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

    import edu.toronto.cs.ctl.*;


public class CTLTreeBuilder extends antlr.TreeParser       implements CTLLexerTokenTypes
 {

    CTLNode[] fairness = CTLAbstractNode.EMPTY_ARRAY;
    
    public CTLTreeBuilder (CTLNode[] _fairness)
    {
        this ();
        if (_fairness != null) fairness = _fairness;
    }
public CTLTreeBuilder() {
	tokenNames = _tokenNames;
}

	public final CTLNode  ctlTree(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST ctlTree_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NUMBER:
		case IDENT:
		{
			node=atomic(_t);
			_t = _retTree;
			break;
		}
		case NEG:
		{
			node=notExpression(_t);
			_t = _retTree;
			break;
		}
		case IMPLIES:
		{
			node=impliesExpression(_t);
			_t = _retTree;
			break;
		}
		case OR:
		{
			node=orExpression(_t);
			_t = _retTree;
			break;
		}
		case AND:
		{
			node=andExpression(_t);
			_t = _retTree;
			break;
		}
		case FORALL:
		case FORSOME:
		case AX:
		case EX:
		case EF:
		case EG:
		case AF:
		case AG:
		{
			node=modalExpression(_t);
			_t = _retTree;
			break;
		}
		case EQ:
		case GEQ:
		case LEQ:
		case NEQ:
		{
			node=comparissonExpression(_t);
			_t = _retTree;
			break;
		}
		case PLACEHOLDER:
		{
			node=placeholder(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return node;
	}
	
	public final CTLAtomPropNode  atomic(AST _t) throws RecognitionException {
		CTLAtomPropNode node;
		
		AST atomic_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST name = null;
		AST number = null;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IDENT:
		{
			name = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			node = CTLFactory.createCTLAtomPropNode (name.getText ());
			break;
		}
		case NUMBER:
		{
			number = (AST)_t;
			match(_t,NUMBER);
			_t = _t.getNextSibling();
			node = CTLFactory.createCTLAtomPropNode (number.getText ());
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  notExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST notExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode left;
		
		AST __t71 = _t;
		AST tmp1_AST_in = (AST)_t;
		match(_t,NEG);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		_t = __t71;
		_t = _t.getNextSibling();
		node = left.neg ();
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  impliesExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST impliesExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t65 = _t;
		AST tmp2_AST_in = (AST)_t;
		match(_t,IMPLIES);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t65;
		_t = _t.getNextSibling();
		node = left.implies (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  orExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST orExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t67 = _t;
		AST tmp3_AST_in = (AST)_t;
		match(_t,OR);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t67;
		_t = _t.getNextSibling();
		node = left.or (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  andExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST andExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t69 = _t;
		AST tmp4_AST_in = (AST)_t;
		match(_t,AND);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t69;
		_t = _t.getNextSibling();
		node = left.and (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  modalExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST modalExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case AX:
		{
			node=axExpression(_t);
			_t = _retTree;
			break;
		}
		case EX:
		{
			node=exExpression(_t);
			_t = _retTree;
			break;
		}
		case EF:
		{
			node=efExpression(_t);
			_t = _retTree;
			break;
		}
		case EG:
		{
			node=egExpression(_t);
			_t = _retTree;
			break;
		}
		case AG:
		{
			node=agExpression(_t);
			_t = _retTree;
			break;
		}
		case AF:
		{
			node=afExpression(_t);
			_t = _retTree;
			break;
		}
		case FORALL:
		case FORSOME:
		{
			node=untillReleaseExpression(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  comparissonExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST comparissonExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NEQ:
		{
			node=neqExpression(_t);
			_t = _retTree;
			break;
		}
		case EQ:
		{
			node=eqExpression(_t);
			_t = _retTree;
			break;
		}
		case LEQ:
		{
			node=leqExpression(_t);
			_t = _retTree;
			break;
		}
		case GEQ:
		{
			node=geqExpression(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  placeholder(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST placeholder_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLAtomPropNode name;
		Set set;
		
		
		AST __t108 = _t;
		AST tmp5_AST_in = (AST)_t;
		match(_t,PLACEHOLDER);
		_t = _t.getFirstChild();
		name=atomic(_t);
		_t = _retTree;
		set=atomicSet(_t);
		_t = _retTree;
		_t = __t108;
		_t = _t.getNextSibling();
		node = CTLFactory.createCTLPlaceholderNode (name.getName (),
		(CTLAtomPropNode[])set.toArray (new CTLAtomPropNode [set.size ()]));
		
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  neqExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST neqExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t74 = _t;
		AST tmp6_AST_in = (AST)_t;
		match(_t,NEQ);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t74;
		_t = _t.getNextSibling();
		node = null;
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  eqExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST eqExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t76 = _t;
		AST tmp7_AST_in = (AST)_t;
		match(_t,EQ);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t76;
		_t = _t.getNextSibling();
		node = left.eq (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  leqExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST leqExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t78 = _t;
		AST tmp8_AST_in = (AST)_t;
		match(_t,LEQ);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t78;
		_t = _t.getNextSibling();
		node = left.under (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  geqExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST geqExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t80 = _t;
		AST tmp9_AST_in = (AST)_t;
		match(_t,GEQ);
		_t = _t.getFirstChild();
		left=ctlTree(_t);
		_t = _retTree;
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t80;
		_t = _t.getNextSibling();
		node = left.over (right);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  axExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST axExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t83 = _t;
		AST tmp10_AST_in = (AST)_t;
		match(_t,AX);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t83;
		_t = _t.getNextSibling();
		node = right.ax ();
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  exExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST exExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t85 = _t;
		AST tmp11_AST_in = (AST)_t;
		match(_t,EX);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t85;
		_t = _t.getNextSibling();
		node = right.ex ();
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  efExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST efExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t89 = _t;
		AST tmp12_AST_in = (AST)_t;
		match(_t,EF);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t89;
		_t = _t.getNextSibling();
		node = right.ef ();
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  egExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST egExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t93 = _t;
		AST tmp13_AST_in = (AST)_t;
		match(_t,EG);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t93;
		_t = _t.getNextSibling();
		node = right.eg (fairness);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  agExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST agExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t91 = _t;
		AST tmp14_AST_in = (AST)_t;
		match(_t,AG);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t91;
		_t = _t.getNextSibling();
		node = right.ag (fairness);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  afExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST afExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		CTLNode right;
		
		AST __t87 = _t;
		AST tmp15_AST_in = (AST)_t;
		match(_t,AF);
		_t = _t.getFirstChild();
		right=ctlTree(_t);
		_t = _retTree;
		_t = __t87;
		_t = _t.getNextSibling();
		node = right.af (fairness);
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  untillReleaseExpression(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST untillReleaseExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case FORALL:
		{
			node=forallUntil(_t);
			_t = _retTree;
			break;
		}
		case FORSOME:
		{
			node=forsomeUntil(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  forallUntil(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST forallUntil_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t96 = _t;
		AST tmp16_AST_in = (AST)_t;
		match(_t,FORALL);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case UNTIL:
		{
			AST __t98 = _t;
			AST tmp17_AST_in = (AST)_t;
			match(_t,UNTIL);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t98;
			_t = _t.getNextSibling();
			node = left.au (right);
			break;
		}
		case RELEASE:
		{
			AST __t99 = _t;
			AST tmp18_AST_in = (AST)_t;
			match(_t,RELEASE);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t99;
			_t = _t.getNextSibling();
			node = left.ar (right);
			break;
		}
		case WEAK:
		{
			AST __t100 = _t;
			AST tmp19_AST_in = (AST)_t;
			match(_t,WEAK);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t100;
			_t = _t.getNextSibling();
			node = left.aw (right);
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t96;
		_t = _t.getNextSibling();
		_retTree = _t;
		return node;
	}
	
	public final CTLNode  forsomeUntil(AST _t) throws RecognitionException {
		CTLNode node;
		
		AST forsomeUntil_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLNode left;
		CTLNode right;
		
		
		AST __t102 = _t;
		AST tmp20_AST_in = (AST)_t;
		match(_t,FORSOME);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case UNTIL:
		{
			AST __t104 = _t;
			AST tmp21_AST_in = (AST)_t;
			match(_t,UNTIL);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t104;
			_t = _t.getNextSibling();
			node = left.eu (right);
			break;
		}
		case RELEASE:
		{
			AST __t105 = _t;
			AST tmp22_AST_in = (AST)_t;
			match(_t,RELEASE);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t105;
			_t = _t.getNextSibling();
			node = left.er (right);
			break;
		}
		case WEAK:
		{
			AST __t106 = _t;
			AST tmp23_AST_in = (AST)_t;
			match(_t,WEAK);
			_t = _t.getFirstChild();
			left=ctlTree(_t);
			_t = _retTree;
			right=ctlTree(_t);
			_t = _retTree;
			_t = __t106;
			_t = _t.getNextSibling();
			node = left.ew (right);
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t102;
		_t = _t.getNextSibling();
		_retTree = _t;
		return node;
	}
	
	public final Set  atomicSet(AST _t) throws RecognitionException {
		Set result;
		
		AST atomicSet_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		CTLAtomPropNode node;
		Set temp;
		
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case NUMBER:
		case IDENT:
		{
			node=atomic(_t);
			_t = _retTree;
			result = new HashSet (); result.add (node);
			break;
		}
		case COMMA:
		{
			AST __t110 = _t;
			AST tmp24_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getFirstChild();
			temp=atomicSet(_t);
			_t = _retTree;
			node=atomic(_t);
			_t = _retTree;
			_t = __t110;
			_t = _t.getNextSibling();
			temp.add (node); result = temp;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return result;
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
	
	}
	
