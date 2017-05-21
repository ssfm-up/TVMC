// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents a boolean Program. To do so it holds the sourcecode of a c program. The code may be unparsed 
* (UnparsedCode) or be an Operation. When converting to string the operations of the program are abstracted
* inserted into the unparsed code.
* @author Daniel Wonisch
*/
public class BooleanProgram extends Object {
/**
	* Construct..
	*/
public BooleanProgram()
{
implementation = new Long(__c3());
}
private native long __c3();
/**
	* (Java Interface Method) Returns a string representing the boolean Program.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m6(implementation.longValue());
return __retval;
}
private native String __m6(long __imp);
/**
	* Sets whether this process/program is in spotlight or not.
	

This method may <var>not</var> be extended in Java.
*/
public void setInSpotlight(boolean spotlight)
{
__m9(implementation.longValue(), spotlight);
}
private native void __m9(long __imp, boolean spotlight);
// cxxwrap ctor, do not use
public BooleanProgram(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof BooleanProgram)) return false;
  return implementation.equals(((BooleanProgram)o).implementation);
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
