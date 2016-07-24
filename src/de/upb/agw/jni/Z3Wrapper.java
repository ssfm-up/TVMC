// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Singleton wrapper class for the theorem prover Z3.
* @author Daniel Wonisch
*/
public class Z3Wrapper extends Object {
/**
	* Enum representing the different possible types of variables currently supported.
	*/
public final static int BOOL = 0;
public final static int INT = BOOL + 1;
public final static int MUTEX = INT + 1;
/**
	* Returns an instance of this class. Creates a new one if there is none yet, else returns the already created instance.
	

This method may <var>not</var> be extended in Java.
*/
public static Z3Wrapper getInstance()
{
long __retval = 0;
__retval = __m10(0);
return (__retval == 0 ? null : new Z3Wrapper(new Long(__retval)));
}
private native static long __m10(long __imp);
/**
	* Tells Z3 to try to prove an implication expr1 => expr2. Caches the results.
	* @return true if expr1 => expr2 holds, false if it does not hold or no result could be derived.
	

This method may <var>not</var> be extended in Java.
*/
public boolean proveImplication(Expression expr1, Expression expr2)
{
boolean __retval = false;
__retval = __m13(implementation.longValue(), (expr1 == null ? 0 : expr1.getCxxwrapImpl().longValue()), (expr2 == null ? 0 : expr2.getCxxwrapImpl().longValue()));
return __retval;
}
private native boolean __m13(long __imp, long expr1, long expr2);
// cxxwrap ctor, do not use
public Z3Wrapper(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof Z3Wrapper)) return false;
  return implementation.equals(((Z3Wrapper)o).implementation);
}
// override hashCode() from Object, return the implementation values hashCode()
public int hashCode() { return implementation.hashCode(); }
};
