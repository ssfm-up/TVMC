// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents an Operation consiting of an "assume" part and a parallel assignment.
* @author Daniel Wonisch
*/
public class Operation extends Code {
/**
	* Constructs an empty Operation. Also known as "skip"-Operation.
	*/
public Operation()
{
super((Long) null);
implementation = new Long(__c5());
}
private native long __c5();
/**
	* @return true if both conditional and assignment part are empty, false otherwise.
	

This method may <var>not</var> be extended in Java.
*/
public boolean isSkipOperation()
{
boolean __retval = false;
__retval = __m11(implementation.longValue());
return __retval;
}
private native boolean __m11(long __imp);
/**
	* (Java Interface Method)
	* Returns the "skipped" Assignments. That are Assignments like "(> x 0) := choice((> x 0), (not (> x 0))" which 
	* basicly do nothing and are therefore skipped in the parallal assignment vector. Still these assignments are needed
	* for compiling the MDD in the Java Project.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfAssignment getSkippedAssignments()
{
long __retval = 0;
__retval = __m12(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfAssignment(new Long(__retval)));
}
private native long __m12(long __imp);
/**
	* (Java Interface Method)
	* Returns an Enumerator for the parallel assignment part.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfAssignment getAssignments()
{
long __retval = 0;
__retval = __m13(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfAssignment(new Long(__retval)));
}
private native long __m13(long __imp);
/**
	* (Java Interface Method)
	* Returns the conditional "assume" part of the Operation as an Expression.
	* @return Delete: No
	

This method may <var>not</var> be extended in Java.
*/
public Expression getCondExpr()
{
long __retval = 0;
__retval = __m14(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m14(long __imp);
/**
	* (Java Interface Method)
	* Returns the conditional "assume" part of the Operation as an PartialExpression.  
	* Does not check if the right side is really a PartialExpression.
	* @return Delete: No
	

This method may <var>not</var> be extended in Java.
*/
public PartialExpression getCondPExpr()
{
long __retval = 0;
__retval = __m15(implementation.longValue());
return (__retval == 0 ? null : new PartialExpression(new Long(__retval)));
}
private native long __m15(long __imp);
/**
	* (Java Interface Method)
	* Returns a string representing the Operation.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m16(implementation.longValue());
return __retval;
}
private native String __m16(long __imp);
/**
	* Adds a "skipped" Assignment. Typically skipped assignments are generated while abstracting an Operation though.
	

This method may <var>not</var> be extended in Java.
*/
public void addSkippedAssignment(Expression ident)
{
__m21(implementation.longValue(), (ident == null ? 0 : ident.getCxxwrapImpl().longValue()));
}
private native void __m21(long __imp, long ident);
// cxxwrap ctor, do not use
public Operation(Long __imp) { super(__imp); }
/**
	* Destruct..
	*/
public void delete() { super.delete(); }
};
