// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents an assignment like "x := y+5". The left side (ident) is represented by a single string while the right
* side is represented by an Expression.
* @author Daniel Wonisch
*/
public class Assignment extends Code {
/**
	* Computes the weakest assumption of a given expression with respect to the assignment.
	

This method may <var>not</var> be extended in Java.
*/
public Expression computeWeakestPrecondition(Expression expression)
{
long __retval = 0;
__retval = __m5(implementation.longValue(), (expression == null ? 0 : expression.getCxxwrapImpl().longValue()));
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m5(long __imp, long expression);
/**
	* (Java Interface Method) Returns a string representing the assignment.
	

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
	* (Java Interface Method) Returns the left side ident as cstring.
	

This method may <var>not</var> be extended in Java.
*/
public String getIdent()
{
String __retval = null;
__retval = __m8(implementation.longValue());
return __retval;
}
private native String __m8(long __imp);
/**
	* (Java Interface Method) Returns the right side expression as Expression. 
	

This method may <var>not</var> be extended in Java.
*/
public Expression getAssignmentExpression()
{
long __retval = 0;
__retval = __m9(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m9(long __imp);
/**
	* (Java Interface Method) Returns the right side expression as PartialExpression. Does not check if 
	* the right side is really a PartialExpression.
	

This method may <var>not</var> be extended in Java.
*/
public PartialExpression getAssignmentPExpression()
{
long __retval = 0;
__retval = __m10(implementation.longValue());
return (__retval == 0 ? null : new PartialExpression(new Long(__retval)));
}
private native long __m10(long __imp);
// cxxwrap ctor, do not use
public Assignment(Long __imp) { super(__imp); }
/**
	* Destruct..
	*/
public void delete() { super.delete(); }
};
