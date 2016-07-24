package edu.toronto.cs.util;

import java.util.*;
import java.io.*;

/**
 ** This class does some macro expansion on Strings. It also can
 ** perform some macro compression, but at a very simple-minded level.
 **/
public class StringMacroManager
{

  NaryTree macroTree, expansionTree;

  /**
   ** Constructs a new macro expander.
   **/
  public StringMacroManager ()
  {
    macroTree = new NaryTree ();
    expansionTree = new NaryTree ();
  }

  /**
   ** Defines a new macro. If a macro with such a name already exists
   ** it gets overwritten. (There is no support for defining macroes
   ** containing other macroes, i.e. it may or may not work)
   **/
  public void defineMacro (String name, String expansion)
  {
    // using either null or empty string as a macro is not allowed
    if (name == null || name.equals ("")) return;
    // for expanding macroes to text
    record (name, expansion, macroTree);
    // for compressing text to macroes
    record (expansion, name, expansionTree);
  }

  /**
   ** Expands all the macroes present in the text.
   **
   ** @return -- text WITHOUT marcoes
   **/
  public String expandMacro (String text)
  {
    if (macroTree.getNumChildren () == 0) return text; // no macroes defined
    else return expand (text, macroTree);
  }
  
  /**
   ** Compresses all the macroes it can find in the text.
   **
   ** @return -- text WITH macroes (if any were found)
   **/
  public String compressMacro (String text)
  {
    if (expansionTree.getNumChildren () == 0) return text; // can't compress
    else return expand (text, expansionTree);
  }

  /**
   ** The core macro recorder.
   **/
  private void record (String macro, String expansion, NaryTree macroes)
  {
    StringReader it;
    NaryTree cur;

    cur = macroes;
    
    try
      {
	it = new StringReader (macro);
	// First record the macro
	for (int c = it.read (); c != -1; c = it.read ())
	  {
	    Character child = new Character ((char) c);
	    // if such a child already exists, keep going
	    if (cur.existChild (child))
	      {
		cur = cur.getChild (child);
	      }
	    else // no such child? create one!
	      {
		NaryTree temp = new NaryTree ();
		cur.setChild (child, temp);
		cur = temp;
	      }
	  }
	cur.setData (expansion);
      }
    catch (Exception e)
      {
	throw new NestedRuntimeException ("Couldn't read the macro. ", e);
      }    
  }

  /**
   ** The core macro expander.
   **/
  private String expand (String text, NaryTree macroes)
  {
    String result = "";
    StringReader str;
    NaryTree cur;

    cur = macroes;
    boolean matching = false;
    
    try
      {
	str = new StringReader (text);
	// go through the text looking for macroes
	do
	  {
	    // mark the position in the string ('processed')
	    if (!matching) str.mark (0);

	    // read the next character
	    int c = str.read ();
	    if (c == -1) break;
	    Character child = new Character ((char) c);
		
	    // if such a macro prefix exists
	    if (cur.existChild (child))
	      {
		matching = true;
		cur = cur.getChild (child);
		// check for completed macroes
		if (cur.getData () != null) // found a match!
		  {
		    result += cur.getData (); // substitute the macro
		    matching = false;
		    cur = macroes;
		  }
	      }
	    else // no such macro prefix?
	      {
		if (matching) // if were matching, reset the position
		  {
		    matching = false;
		    str.reset ();
		    child = new Character ((char) str.read ());
		    cur = macroes; // back to square one
		  }
		result += child;
	      }
	  }
	while (true);
      }
    catch (Exception e)
      {
	throw new NestedRuntimeException ("Couldn't read the macro. ", e);
      }

    return result;
  }

}
