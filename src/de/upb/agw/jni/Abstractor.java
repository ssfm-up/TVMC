// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* (Java Interface Class) Util class which takes a list of CFGs, predicates and spotlight and computes the abstracted
* CFGs. Given a InitialiserCFG the class can also compute initial values of given predicates.
* @author Daniel Wonisch
*/
public class Abstractor extends Object {
/**
	* Construct..
	*/
public Abstractor()
{
implementation = new Long(__c6());
}
private native long __c6();
/**
	* Sets a given CFG as initialer CFG used by getPredicateInitValue.
	

This method may <var>not</var> be extended in Java.
*/
public void setInitialiserCFG(CFGraph graph)
{
__m8(implementation.longValue(), (graph == null ? 0 : graph.getCxxwrapImpl().longValue()));
}
private native void __m8(long __imp, long graph);
/**
	* Add a CFG which should be taken into consideration for the abstraction.
	* Same as calling addCFG(graph, true);
	

This method may <var>not</var> be extended in Java.
*/
public void addCFG(CFGraph graph)
{
__m9(implementation.longValue(), (graph == null ? 0 : graph.getCxxwrapImpl().longValue()));
}
private native void __m9(long __imp, long graph);
/**
	* Add a CFG which should be taken into consideration for the abstraction.
	* Also sets the spotlight value of this graph (true for graph is in spotlight, false else).
	

This method may <var>not</var> be extended in Java.
*/
public void addCFG(CFGraph graph, boolean spotlight)
{
__m10(implementation.longValue(), (graph == null ? 0 : graph.getCxxwrapImpl().longValue()), spotlight);
}
private native void __m10(long __imp, long graph, boolean spotlight);
/**
	* Add a predicate..
	

This method may <var>not</var> be extended in Java.
*/
public void addPredicate(Expression predicate)
{
__m11(implementation.longValue(), (predicate == null ? 0 : predicate.getCxxwrapImpl().longValue()));
}
private native void __m11(long __imp, long predicate);
/**
	* Sets the spotlight value for a CFG identified by a given index.
	

This method may <var>not</var> be extended in Java.
*/
public void setSpotlight(int index, boolean spotlight)
{
__m12(implementation.longValue(), index, spotlight);
}
private native void __m12(long __imp, int index, boolean spotlight);
/**
	*	Returns a Enumerator of abstracted CFGs. Also may contain the \bottom-process
	*	Beware: Calling this method may destroy CFGs abstracted before.
	*	@return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfCFGraph getAbstractedCFGs()
{
long __retval = 0;
__retval = __m13(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfCFGraph(new Long(__retval)));
}
private native long __m13(long __imp);
/**
	*	Returns the set of predicates added before as Enumerator
	*	@return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfExpression getPredicates()
{
long __retval = 0;
__retval = __m14(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfExpression(new Long(__retval)));
}
private native long __m14(long __imp);
/**
	*	Computes the initial value of a predicate.
	*	@return 0 for "false", 1 for "maybe", 2 for "true"
	

This method may <var>not</var> be extended in Java.
*/
public int getPredicateInitValue(Expression predicate)
{
int __retval = 0;
__retval = __m15(implementation.longValue(), (predicate == null ? 0 : predicate.getCxxwrapImpl().longValue()));
return __retval;
}
private native int __m15(long __imp, long predicate);
/**
	* Returns a Enumerator of boolean Programs constructed when abstracting.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfBooleanProgram getBooleanProgramEnumerator()
{
long __retval = 0;
__retval = __m16(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfBooleanProgram(new Long(__retval)));
}
private native long __m16(long __imp);
// cxxwrap ctor, do not use
public Abstractor(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof Abstractor)) return false;
  return implementation.equals(((Abstractor)o).implementation);
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
