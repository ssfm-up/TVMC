// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents an partial expression like "choice(x > 0, false)". The left and right operand of choice are represented as
* Expressions. 
* @author Daniel Wonisch
*/
public class PartialExpression extends Expression {
/**
	* Constructs a partial expression using given left and right operands.
	*/
public PartialExpression(Expression left, Expression right)
{
super((Long) null);
implementation = new Long(__c3((left == null ? 0 : left.getCxxwrapImpl().longValue()), (right == null ? 0 : right.getCxxwrapImpl().longValue())));
}
private native long __c3(long left, long right);
/**
	* Returns left operand.
	* @return Delete - No
	

This method may <var>not</var> be extended in Java.
*/
public Expression getLeftExpression()
{
long __retval = 0;
__retval = __m5(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m5(long __imp);
/**
	* Returns right operand.
	* @return Delete - No
	

This method may <var>not</var> be extended in Java.
*/
public Expression getRightExpression()
{
long __retval = 0;
__retval = __m6(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m6(long __imp);
/**
	* (Java Interface Method)
	* Returns a string representing the partial expression.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m7(implementation.longValue());
return __retval;
}
private native String __m7(long __imp);
/**
	* Negates left and right operand.
	

This method may <var>not</var> be extended in Java.
*/
public void negate()
{
__m9(implementation.longValue());
}
private native void __m9(long __imp);
// cxxwrap ctor, do not use
public PartialExpression(Long __imp) { super(__imp); }
/**
	* Destruct..
	*/
public void delete() { super.delete(); }
};
