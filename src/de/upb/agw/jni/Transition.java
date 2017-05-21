// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Simple class representing a transition in an CFGraph.
* @author Daniel Wonisch
*/
public class Transition extends Object {
/**
	* (Java Interface Method)
	* Returns the source state id.
	

This method may <var>not</var> be extended in Java.
*/
public int getSource()
{
int __retval = 0;
__retval = __m2(implementation.longValue());
return __retval;
}
private native int __m2(long __imp);
/**
	* (Java Interface Method)
	* Returns the destination state id.
	

This method may <var>not</var> be extended in Java.
*/
public int getDestination()
{
int __retval = 0;
__retval = __m3(implementation.longValue());
return __retval;
}
private native int __m3(long __imp);
/**
	* (Java Interface Method)
	* Returns the Operation of the Transition.
	* @return Delete: No
	

This method may <var>not</var> be extended in Java.
*/
public Operation getOperation()
{
long __retval = 0;
__retval = __m4(implementation.longValue());
return (__retval == 0 ? null : new Operation(new Long(__retval)));
}
private native long __m4(long __imp);
public Transition() {

implementation = new Long(__cdefault());

}
private native long __cdefault();
// cxxwrap ctor, do not use
public Transition(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof Transition)) return false;
  return implementation.equals(((Transition)o).implementation);
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
