// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents a state of an CFG.
* @author Daniel Wonisch
*/
public class State extends Object {
/**
	* (Java Interface Method)
	* Returns an Enumerator for the transitions of this state.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfTransition getTransitions()
{
long __retval = 0;
__retval = __m3(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfTransition(new Long(__retval)));
}
private native long __m3(long __imp);
/**
	* (Java Interface Method)
	* Returns a string representing the state.
	

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
	* (Java Interface Method)
	* Returns the Transition from this state to an target state identified by a given targetId. 
	* UB if there is no such Transition.
	* @return Delete: Yes
	*

This method may <var>not</var> be extended in Java.
*/
public Transition findTransition(int targetId)
{
long __retval = 0;
__retval = __m5(implementation.longValue(), targetId);
return (__retval == 0 ? null : new Transition(new Long(__retval)));
}
private native long __m5(long __imp, int targetId);
public State() {

implementation = new Long(__cdefault());

}
private native long __cdefault();
// cxxwrap ctor, do not use
public State(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof State)) return false;
  return implementation.equals(((State)o).implementation);
}
// override hashCode() from Object, return the implementation values hashCode()
public int hashCode() { return implementation.hashCode(); }
public void delete()
{

__d(implementation.longValue());
 implementation = null;
}
private native void __d(long __imp);
};
