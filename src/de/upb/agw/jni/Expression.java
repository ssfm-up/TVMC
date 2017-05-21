// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
*	Represents an expression like "x > 0". Internally the expression is saved in prefix-notation ("(> x 0)"). 
*	The class holds only a single string attribute representing the expression string. 
*	@author Daniel Wonisch
*/
public class Expression extends Code {
/**
	* Enum representing the different possible operators in an expression.
	*/
public final static int OR = 0;
public final static int XOR = OR + 1;
public final static int AND = XOR + 1;
public final static int EQ = AND + 1;
public final static int LESS = EQ + 1;
public final static int LEQ = LESS + 1;
public final static int GREATER = LEQ + 1;
public final static int GEQ = GREATER + 1;
public final static int PLUS = GEQ + 1;
public final static int MINUS = PLUS + 1;
public final static int MULT = MINUS + 1;
public final static int DIV = MULT + 1;
public final static int MOD = DIV + 1;
public final static int NOT = MOD + 1;
/**
	* Constructs the object using a prefix cstring
	*/
public Expression(String expr)
{
super((Long) null);
implementation = new Long(__c12(expr));
}
private native long __c12(String expr);
/**
	* Constructs the object using a prefix (infix=false) or infix (infex=true) cstring
	*/
public Expression(String expr, boolean infix)
{
super((Long) null);
implementation = new Long(__c13(expr, infix));
}
private native long __c13(String expr, boolean infix);
/**
	* Returns the internal representation of the expr as cstring
	

This method may <var>not</var> be extended in Java.
*/
public String getExpressionCString()
{
String __retval = null;
__retval = __m21(implementation.longValue());
return __retval;
}
private native String __m21(long __imp);
/**
	* expr := (not expr)
	

This method may <var>not</var> be extended in Java.
*/
public void negate()
{
__m22(implementation.longValue());
}
private native void __m22(long __imp);
/**
	* (Java Interface Method) Checks if this expr is contained in a given enumeration of expressions.
	

This method may <var>not</var> be extended in Java.
*/
public boolean isPredicate(EnumeratorOfExpression enumerator)
{
boolean __retval = false;
__retval = __m23(implementation.longValue(), (enumerator == null ? 0 : enumerator.getCxxwrapImpl().longValue()));
return __retval;
}
private native boolean __m23(long __imp, long enumerator);
/**
	* (Java Interface Method) Returns the first op of the expr. For example the first op of "(= x 5)" is EQ.
	

This method may <var>not</var> be extended in Java.
*/
public int getFirstOp()
{
int __retval = 0;
__retval = __m24(implementation.longValue());
return __retval;
}
private native int __m24(long __imp);
/**
	* (Java Interface Method) Returns the left operand of the first operator.
	

This method may <var>not</var> be extended in Java.
*/
public Expression getLeftSubExpression()
{
long __retval = 0;
__retval = __m25(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m25(long __imp);
/**
	* (Java Interface Method) Returns the right operand of the first operator.
	

This method may <var>not</var> be extended in Java.
*/
public Expression getRightSubExpression()
{
long __retval = 0;
__retval = __m26(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m26(long __imp);
/**
	* (Java Interface Method) Returns expr as cstring in infix notation.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m27(implementation.longValue());
return __retval;
}
private native String __m27(long __imp);
/**

This method may <var>not</var> be extended in Java.
*/
public void swap(Expression right)
{
__m28(implementation.longValue(), (right == null ? 0 : right.getCxxwrapImpl().longValue()));
}
private native void __m28(long __imp, long right);
// cxxwrap ctor, do not use
public Expression(Long __imp) { super(__imp); }
};
