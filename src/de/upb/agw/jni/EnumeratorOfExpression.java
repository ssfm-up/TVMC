// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
*	(Java Interface Class)
*	Designed as interface for Java for enumerating vector classes.
*	@author Daniel Wonisch
*/
public class EnumeratorOfExpression extends Object {
/**
	* Resets the Enumerator to the beginning of the list.
	

This method may <var>not</var> be extended in Java.
*/
public void reset()
{
__m4(implementation.longValue());
}
private native void __m4(long __imp);
/**
	* @return true if there is a next element, false otherwise.
	

This method may <var>not</var> be extended in Java.
*/
public boolean hasNext()
{
boolean __retval = false;
__retval = __m5(implementation.longValue());
return __retval;
}
private native boolean __m5(long __imp);
/**
	* Returns the next Element in the list.
	* @return Delete: No if CFGraph, State, Assignment, BooleanProgram; Yes if Expression, Transition;
	

This method may <var>not</var> be extended in Java.
*/
public Expression getNext()
{
long __retval = 0;
__retval = __m6(implementation.longValue());
return (__retval == 0 ? null : new Expression(new Long(__retval)));
}
private native long __m6(long __imp);
/**
	* Returns the total number of elements in the list.
	

This method may <var>not</var> be extended in Java.
*/
public int getNumberofElements()
{
int __retval = 0;
__retval = __m7(implementation.longValue());
return __retval;
}
private native int __m7(long __imp);
// cxxwrap ctor, do not use
public EnumeratorOfExpression(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof EnumeratorOfExpression)) return false;
  return implementation.equals(((EnumeratorOfExpression)o).implementation);
}
// override hashCode() from Object, return the implementation values hashCode()
public int hashCode() { return implementation.hashCode(); }
/**
	* Destruct..
	*/
public void delete()
{

__d(implementation.longValue());
 implementation = null;
}
private native void __d(long __imp);
};
