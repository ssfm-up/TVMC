// created by cxxwrap -- DO NOT EDIT
package de.upb.agw.jni;
/**
* (Java Interface Class) Gives access to the c program parser.
* @author Daniel Wonisch
*/
public class CParser extends Object {
/**
	*	Parses a given filename and stores the parsed information in the object.
	*	@param const char* path to a c file
	*/
public CParser(String filename)
{
implementation = new Long(__c2(filename));
}
private native long __c2(String filename);
/**
	* Returns Enumerator containing the parsed CFGs.
	* @return Delete: Yes
	

This method may <var>not</var> be extended in Java.
*/
public EnumeratorOfCFGraph getCFGEnumerator()
{
long __retval = 0;
__retval = __m4(implementation.longValue());
return (__retval == 0 ? null : new EnumeratorOfCFGraph(new Long(__retval)));
}
private native long __m4(long __imp);
/**
    * Returns the initialiser CFG.
	* @return Delete: No
	

This method may <var>not</var> be extended in Java.
*/
public CFGraph getInitCFG()
{
long __retval = 0;
__retval = __m5(implementation.longValue());
return (__retval == 0 ? null : new CFGraph(new Long(__retval)));
}
private native long __m5(long __imp);
// cxxwrap ctor, do not use
public CParser(Long __imp) { implementation = __imp; }
protected Long implementation = null;
protected boolean extensible = false;
public Long getCxxwrapImpl() { return implementation; }
// override equals() from Object, compare the implementation value
public boolean equals(Object o) {
  if (!(o instanceof CParser)) return false;
  return implementation.equals(((CParser)o).implementation);
}
// override hashCode() from Object, return the implementation values hashCode()
public int hashCode() { return implementation.hashCode(); }
/**
	*	Note: Delete may also destroy cfgs
	*/
public void delete()
{

__d(implementation.longValue());
 implementation = null;
}
private native void __d(long __imp);
};
