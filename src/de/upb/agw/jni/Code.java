// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents Code in a c program. Abstract class.
* @author Daniel Wonsich
*/
public class Code extends Object {
/**
	* Construct..
	

This constructor produces a object in which some methods may be extended in java.
*/
public Code()
{
implementation = new Long(__c1());
extensible = true;
}
private native long __c1();
/**
	* (Java Interface Method) 
	* Returns a string representing the boolean Program.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m3(implementation.longValue());
return __retval;
}
private native String __m3(long __imp);
// cxxwrap ctor, do not use
public Code(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof Code)) return false;
  return implementation.equals(((Code)o).implementation);
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
