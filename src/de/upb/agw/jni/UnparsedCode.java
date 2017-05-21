// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Unparsed code represented as string.
* @author Daniel Wonisch
*/
public class UnparsedCode extends Code {
/**
	* Returns an empty UnparsedCode object.
	*/
public UnparsedCode()
{
super((Long) null);
implementation = new Long(__c2());
}
private native long __c2();
/**
	* (Java Interface Method) 
	* Returns unparsed code as cstring.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m4(implementation.longValue());
return __retval;
}
private native String __m4(long __imp);
/**
	* Appends a cstring to the unparsed code string.
	

This method may <var>not</var> be extended in Java.
*/
public void addCode(String code)
{
__m5(implementation.longValue(), code);
}
private native void __m5(long __imp, String code);
/**
	* Clears current unparsed code string.
	

This method may <var>not</var> be extended in Java.
*/
public void clear()
{
__m6(implementation.longValue());
}
private native void __m6(long __imp);
// cxxwrap ctor, do not use
public UnparsedCode(Long __imp) { super(__imp); }
/**
	* Destruct..
	*/
public void delete() { super.delete(); }
};
