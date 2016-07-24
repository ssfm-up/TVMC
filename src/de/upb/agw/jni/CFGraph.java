// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* Represents a Control Flow Graph. 
* @author Daniel Wonisch
*/
public class CFGraph extends Object {
/**
	*	(Java Interface Method)
	*	Find State using a given label (e.g. findStateByLabel("END") == 5 if state with ID 5 has label "END")
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfint findStateByLabel(String label)
{
long __retval = 0;
__retval = __m8(implementation.longValue(), label);
return (__retval == 0 ? null : new EnumeratorOfint(new Long(__retval)));
}
private native long __m8(long __imp, String label);
/**
	* (Java Interface Method)
	* Returns an Enumerator representing the list of states of the CFG.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfState getStates()
{
long __retval = 0;
__retval = __m9(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfState(new Long(__retval)));
}
private native long __m9(long __imp);
/**
	* (Java Interface Method)
	* Returns an Enumerator containing all registered labels.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfString getRegisteredLabels()
{
long __retval = 0;
__retval = __m10(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfString(new Long(__retval)));
}
private native long __m10(long __imp);
/**
	* (Java Interface Method)
	* Returns the the id of the first state of the CFG.
	

This method may <var>not</var> be extended in Java.
*/
public int getBeginStateId()
{
int __retval = 0;
__retval = __m11(implementation.longValue());
return __retval;
}
private native int __m11(long __imp);
/**
	* (Java Interface Method)
	* Returns a string representing the object.
	

This method may <var>not</var> be extended in Java.
*/
public String __toString()
{
String __retval = null;
__retval = __m12(implementation.longValue());
return __retval;
}
private native String __m12(long __imp);
/**
	* (Java Interface Method)
	* returns the program number of the CFG as initialised in the constructor.
	

This method may <var>not</var> be extended in Java.
*/
public int getProgramNumber()
{
int __retval = 0;
__retval = __m13(implementation.longValue());
return __retval;
}
private native int __m13(long __imp);
/**
	* (Java Interface Method)
	* returns the count of states of the CFG.
	

This method may <var>not</var> be extended in Java.
*/
public int getStateCount()
{
int __retval = 0;
__retval = __m14(implementation.longValue());
return __retval;
}
private native int __m14(long __imp);
/**
	* Optimises the CFG to reduce to number of states/transitions needed. Warning: Ids may be changed
	* after using this method.
	

This method may <var>not</var> be extended in Java.
*/
public void reduce()
{
__m15(implementation.longValue());
}
private native void __m15(long __imp);
/**
	* (Java Interface Method)
	* Returns the Transition from the state identified by sourceId to the state identified by targetId. 
	* UB if there is no such Transition
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public Transition findTransition(int sourceId, int targetId)
{
long __retval = 0;
__retval = __m16(implementation.longValue(), sourceId, targetId);
return (__retval == 0 ? null : new Transition(new Long(__retval)));
}
private native long __m16(long __imp, int sourceId, int targetId);
/**
	* @return true if predicate may be modified by some operation in this CFG, false otherwise.
	

This method may <var>not</var> be extended in Java.
*/
public boolean isPredicateModifiedByGraph(Expression expr)
{
boolean __retval = false;
__retval = __m17(implementation.longValue(), (expr == null ? 0 : expr.getCxxwrapImpl().longValue()));
return __retval;
}
private native boolean __m17(long __imp, long expr);
// cxxwrap ctor, do not use
public CFGraph(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof CFGraph)) return false;
  return implementation.equals(((CFGraph)o).implementation);
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
