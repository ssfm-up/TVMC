/*  
 * Clapi.java  
 *  
 *  
 */  
package edu.toronto.cs.util;  
  
import java.io.*;  
import java.util.*;  
  
/**  
 * <p>Clapi - the Command Line API.</p>  
 *   
 * <p>A fancy option parser.</p>  
 *  
 * <p>Clapi has a webpage at  
 *    http://www.svincent.com/shawn/software/Clapi/</p>  
 *  
 * <p>Needs lots more features, some of them might be:</p>  
 *  
 *   <ul>  
 *     <li>Some sort of algorithmic option type (pass in a class which  
 *         gets executed to determine whether to match an option, and  
 *         then maybe another method that gets called to parse the  
 *         arguments...)</li>  
 *     <li>If no short options are specified, should allow long  
 *         options to be specified using single dash. (this is mostly done --   
 *         just need to deal with the default help messages)</li>  
 *     <li>Print only summary usage for -?, -h: long usage for --help.</li>  
 *   </ul>  
 *  
 * <p>XXX bug: suffix, prefix, equals specifiers print out  
 * improperly.</p>  
 *  
 * <p>XXX bug: should be error if more than one arg arity < 0 specified.</p>  
 *  
 * <p>Here's a simple example of how to use it.</p>  
 * <pre>  
  
    import edu.toronto.cs.util.Clapi.*;  
  
    OptParser optParser = new OptParser ().startClass (MyClass.class);  
    FileNameOpt inputFileOpt = optParser.opt ().nameless ().required ().  
      description ("input file").asFileName ();  
    OptResult optResult = optParser.run (args);  
    String infileName = inputFileOpt.getString (optResult);  
  
 * </pre>  
 *  
 * <p>Options in Clapi are specified using a builder API.</p>  
 *  
 * <p>If you have never worked with a builder API before, it might  
 * be a little mysterious.  The trick is that every setter method  
 * returns a reference to the <code>this</code> pointer, so that  
 * further setter methods can be directly chained.</p>  
 *  
 * <p>For example, in a typical use of OptSpec:</p>  
 * <pre>  
 *    // --- get an option parser  
 *    OptParser optParser = new OptParser ();  
 *      
 *    // --- make an option.  
 *    StringOpt stringOpt = optParser.opt ().longName ("stringOpt").  
 *                          shortName ('s').asString ();  
 * </pre>  
 *  
 * <p>In this example, the call to <code>opt()</code> returns an  
 * instance of OptSpec, which then gets  
 * <code>longName()</code> called upon it.  
 * <code>longName()</code> adds a long name and returns the option  
 * specifier again, so that <code>shortName()</code> can be  
 * called.</p>  
 *  
 * <p><code>shortName()</code> adds a short name, and returns the  
 * option specifier again.  At this point, <code>asString ()</code>  
 * is called, which wraps the OptSpec in a StringOpt, adds the  
 * StringOpt to the parser, and finally, returns the StringOpt.</p>  
 *  
 * <p>Builder APIs provide a very compact way of specifying  
 * algorithms.  If we hadn't used a builder API, the above code  
 * would have looked like this:</p>  
 *  
 * <pre>  
 *    // --- get an option parser  
 *    OptParser optParser = new OptParser ();  
 *      
 *    // --- make an option.  
 *    OptSpec stringOptSpec = optParser.opt ();  
 *    stringOptSpec.longName ("stringOpt");  
 *    stringOptSpec.shortName ('s');  
 *  
 *    StringOpt stringOpt = stringOptSpec.asString ();  
 * </pre>  
 *  
 * <p>As you can see, the code is considerably bulkier, and for many  
 * sorts of data structures (particularly those you're building by  
 * hand all the time), a builder API can be a very succinct way of  
 * representing it.</p>  
 *  
 * <p>Note that builder APIs are not without their drawbacks.  
 * Particularly in a statically typed language, it is often  
 * difficult to combine builder APIs with inheritance hierarchies.  
 * Often, an intermediate 'specification' object must be used.  That  
 * approach has been taken with Clapi.</p>  
 **/  
public class Clapi {  
  
  // -------------------------------------------------------------------------  
  // ---- Option Parser                                                   ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  /**  
   * <p>The Option Parser.</p>  
   *  
   * <p>Responsible for creating options and parsing String arrays  
   * into OptResults.</p>  
   *  
   * <p>There are two ways to invoke the compiler.  The first,  
   * <code>run()</code> does everything for you: it prints usage  
   * information, calls <code>System.exit()</code> if there is any  
   * errors, and mostly goes about making itself useful.</p>  
   *  
   * <p>For second mode of running, call <code>parse()</code>.  If  
   * there is any error, this method throws a subclass of  
   * <code>OptParseException</code>.  You can deal with this, if you  
   * like, by calling <code>printUsage</code>, and then taking  
   * whichever steps you'd like to deal with the error condition.</p>  
   *  
   * <p>If there is any problem with the specification of the options  
   * by you, the programmer, the runtime exception  
   * <code>OptCompilationFailedException</code> is thrown.  This  
   * exception should probably not be caught and handled.  As much as  
   * possible, static typing has been used to remove sources of errors  
   * in the specification.</p>  
   *  
   * <p><code>parse</code> or <code>run</code> may be called multiple  
   * times, and new options may be added to the parser between  
   * runs.</p>  
   *  
   * <p>The parser, on construction, automatically adds the options  
   * <code>--help</code>, <code>-h</code>, and <code>-?</code>.  These  
   * options all print usage information.  There is currently no way  
   * of supressing this behavior.</p>  
   *  
   * @see #opt()  
   * @see #run(String[])  
   * @see #parse(String[])  
   **/  
  public static class OptParser {  
    String startClassName;  
    String programName;  
    String version;  
    String author;  
    String webPage;  
    String copyrightNotice;  
    String programDescription;  
  
    List randomQuotes = new ArrayList ();  
  
    List opts = new ArrayList ();  
  
    /**  
     * Compiled state.  A boolean to remember if we've been compiled,  
     * and a bunch of data structures that make the parsing more  
     * straightforward.  
     **/  
    boolean compiled = false;  
    Map longOpts;  
    Map shortOpts;  
    Map equalsOpts;  
    Map prefixOpts;  
    Map suffixOpts;  
    Opt namelessOpt = null;  
    Map optsByGroup;  
  
    // -----------------------------------------------------------------------  
    // ---- Construction & Specification API ---------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * Make a new option parser.  
     **/  
    public OptParser ()  
    {  
      // --- specify the help option.  
      opt ().  
	longName ("help").shortName ('h').shortName ('?').argumentArity (0).  
	description ("Print usage information").  
        group ("Help Options").  
	asHelp ();  
    }  
  
    /** Specify the startup class, for usage information. **/  
    public OptParser startClass (Class v)   
    { startClassName = v.getName (); return this; }  
  
    /** Specify the name of the startup class, for usage information. **/  
    public OptParser startClassName (String v)   
    { startClassName = v; return this; }  
  
    /** Specify the name of the program (banner information) **/  
    public OptParser programName (String v) { programName = v; return this; }  
  
    /** Specify the version of the program (banner information) **/  
    public OptParser version (String v) { version = v; return this; }  
  
    /** Specify the name of the program's author (banner information) **/  
    public OptParser author (String v) { author = v; return this; }  
  
    /** Specify the name of the program's webpage (banner information) **/  
    public OptParser webPage (String v) { webPage = v; return this; }  
  
    /** Specify the program's copyright notice (banner information) **/  
    public OptParser copyrightNotice (String v)   
    { copyrightNotice = v; return this; }  
  
    /** Specify the program's description (banner information) **/  
    public OptParser programDescription (String v)   
    { programDescription = v; return this; }  
  
    /**   
     * Add a random quote to the set of quotes that can be randomly  
     * printed with the usage information.   
     **/  
    public OptParser quote (String quote)  
    {   
      if (quote == null) throw new NullPointerException ();  
      compiled = false;  
      randomQuotes.add (quote); return this;   
    }  
  
    /**  
     * <p>Start making a new Option. </p>  
     *  
     * <p>This method returns an <code>OptSpec</code> instance, which  
     * has a builder API upon it with which you specify things about  
     * the option you want to build (i.e. - long & short names,  
     * description, min/max arities, etc).</p>  
  
     * <p>When you're done, call one of the  
     * <code>as<em>Type</em>()</code> (i.e. - <code>asInt()</code>,  
     * <code>asString()</code>, etc) methods to get an  
     * <code>Opt</code> instance of the appropriate type.</p>  
     **/  
    public OptSpec opt () { return new OptSpec (this); }  
  
    /**  
     * <p>Adds a new option to this OptParser.  Normally, you do not  
     * call this method directly, instead calling <code>opt</code> and  
     * using the builder API provided.</p>  
     **/  
    public Opt addOpt (Opt opt)   
    {   
      opts.add (opt);   
      // when we've added an option, we need to recompile to   
      compiled = false;  
      return opt;   
    }  
  
    // -----------------------------------------------------------------------  
    // ---- Invoking the parser ----------------------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * <p>Defined as running and returning the result of  
     * <code>parse()</code> with the given tokens. </p>  
     *  
     * <p>If there is an <code>OptParseException</code> during the  
     * execution of <code>parse()</code>, it is caught, usage  
     * information is printed to <code>System.out</code>, and  
     * <code>System.exit(1)</code> is called.</p>  
     *  
     * @param tokens   The tokens to parse  
     * @return         The parse result data structure  
     *  
     * @exception OptHelpRequestedException   
     *                 If there is an error in the specified options  
     *  
     * @see #parse(String[])  
     * @see #printUsage(PrintWriter,OptParseException)  
     **/  
    public OptResult run (String[] tokens)  
    {  
      try {  
	return parse (tokens);  
      } catch (OptParseException ex) {  
	printUsage (new PrintWriter (System.out, true), ex);  
	System.exit (1);  
	return null;  
      }  
    }  
  
    /**  
     * <p>Parses the given token array, returning the result of the  
     * parse.</p>  
     *  
     * <p>If one of the constraints specified when building the  
     * options is violated, an instance of  
     * <code>OptParseException</code> is thrown.</p>  
     *  
     * @param tokens   The tokens to parse  
     * @return         The parse result data structure  
     *  
     * @exception OptParseException  
     *                 If there is an error whilst parsing the tokens.  
     * @exception OptHelpRequestedException   
     *                 If there is an error in the specified options  
     *  
     * @see #parse(String[])  
     * @see #printUsage(PrintWriter,OptParseException)  
     **/  
    public OptResult parse (String[] _tokens) throws OptParseException  
    {  
      compile ();  
  
      String[] tokens = preprocessTokens (_tokens);  
  
      return parse (new TokenStream (tokens));  
    }  

    private String isTerminal (String token)
    {
      if (token.startsWith ("--"))
	{
	  try
	    {
	      if (getLongOpt (token.substring (2)).isTerminal ())
		return token.substring (2);
	      if (shortOpts.isEmpty () && token.startsWith ("-"))  
		if (getLongOpt (token.substring (2)).isTerminal ())
		  return token.substring (1);
	    }
	  catch (Exception e)
	    {
	    }
	}
      return null;
    }
  
    private String isLongName (String token)  
    {  
      if (token.startsWith ("--")) return token.substring (2);  
      if (shortOpts.isEmpty () && token.startsWith ("-"))  
        return token.substring (1);  
      return null;  
    }  
  
    private String isShortName (String token)  
    {  
      if (shortOpts.isEmpty ()) return null;  
      if (token.startsWith ("-")) return token.substring (1);  
      return null;  
    }  
  
    private String isNamelessToken (String token)  
    {  
      if (!token.startsWith ("-") || token.equals ("-")) return token;  
      return null;  
    }  
  
    private OptResult parse (TokenStream in) throws OptParseException  
    {  
      OptResult result = new OptResult (this);  
  
      while (in.hasNext ())  
	{  
	  String token = in.nextToken ();  
  
          String name;
  
	  // --- things that don't have nice option names...  
	  if ((name = isNamelessToken (token)) != null)  
	    {  
	      // XXX prefix, suffix, nameless  
	      Opt opt = matchNamelessToken (name);  
	      if (opt == null)  
		throw new OptParseException   
		  ("Cannot match nameless option '"+token+"'");  
              in.pushback (token);  
	      opt.parseArguments (in, result);  
	    }  
	  // --- long options
	  else if ((name = isLongName (token)) != null)  
	    {  
	      String longName = name;  
	      Opt opt = getLongOpt (longName);  
	      if (opt == null)  
                {  
                  int eqIndex = longName.indexOf ('=');  
                  // --- maybe it's in '=' form?  
                  if (eqIndex != -1)  
                    {  
                      String realName = longName.substring (0, eqIndex);  
                      String attribute = longName.substring (eqIndex+1);  
                      longName = realName;  
                        
                      opt = getLongOpt (longName);  
                      if (opt != null) in.pushback (attribute);  
                    }  
                }
	      
	      if (opt == null)  
		throw new OptParseException ("Unknown opt '--"+longName+"'");  
	      opt.parseArguments (in, result);  
	    }  
	  // --- short options  
	  else if ((name = isShortName (token)) != null)  
	    {  
	      char[] chars = token.substring (1).toCharArray ();  
	      for (int j=0; j<chars.length; j++)  
		{  
		  String shortName = String.valueOf (chars[j]);  
		  Opt opt = (Opt)shortOpts.get (shortName);  
		  if (opt == null)  
		    throw new OptParseException   
		      ("Unknown opt '-"+shortName+"'");  
		  opt.parseArguments (in, result);  
		}  
	    }  
	  else  
	    {  
              throw new OptParseException   
                ("Internal error: For token '"+token+  
                 "', got to a point in the code that should "+  
                 "never have been executed.");  
	    }  
	}  
  
      // --- then, check to ensure that all minimum required arities are met.  
      Iterator i = opts.iterator ();  
      while (i.hasNext ())  
	{  
	  Opt opt = (Opt)i.next ();  
	  opt.checkArity (result);  
	}        
  
      return result;  
    }  
  
    // -----------------------------------------------------------------------  
    // ---- Usage printing APIs ----------------------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * <p>Prints usage information for a program based on the  
     * specified options and program information. </p>  
     *  
     * <p>Also prints the message from the exception before printing  
     * usage.</p>  
     *  
     * @param out     The PrintWriter to write the usage info to.  
     * @param ex      The throwable which caused the usage information   
     *                to be printed..  
     **/  
    public void printUsage (PrintWriter out, OptParseException ex)  
    {  
      out.println (ex.getMessage ());  
      // XXX maybe nested exception stuff??  
  
      printUsage (out);  
    }  
  
    /**  
     * <p>Prints usage information for a program based on the  
     * specified options and program information. </p>  
     *  
     * <p>Prints a banner, summary usage, and option descriptions.</p>  
     *  
     * @param out     The PrintWriter to write the usage info to.  
     **/  
    public void printUsage (PrintWriter out)  
    {  
      compile ();  
  
      printBanner (out);  
      out.println ();  
      out.println ("Usage:");  
      printSummaryUsage (out);  
      out.println ();  
      printOptDescriptions (out);  
    }  
  
    /**  
     * <p>Prints a pleasant banner describing the program.  The  
     * information used for this banner is specified by calls to the  
     * OptParser object.</p>  
     *  
     * @param out     The PrintWriter to write the usage info to.  
     **/  
    public void printBanner (PrintWriter out)  
    {  
      boolean outputted = false;  
      if (programName != null)   
	{   
	  out.print (programName);   
	  if (version != null) out.print (' '+version);  
	  out.println ();  
	}  
      if (webPage != null)   
	{ out.println (webPage); }  
      if (author != null)   
	{ out.println (author); }  
      if (copyrightNotice != null)   
	{ out.println (copyrightNotice); }  
  
      if (programDescription != null) wordWrap (out, programDescription, 5, 5);  
  
      if (!randomQuotes.isEmpty ())  
        {  
          out.println ();  
  
          int r = new Random ().nextInt (randomQuotes.size ());  
          String quote = (String)randomQuotes.get (r);  
  
          wordWrap (out, quote, 5, 5);  
        }  
    }  
  
    /**  
     * <p>Prints summary usage information to the given PrintWriter. </p>  
     *  
     * @param out     The PrintWriter to write the usage info to.  
     **/  
    public void printSummaryUsage (PrintWriter _out)  
    {  
      StringBuffer out = new StringBuffer ();  
      out.append ("java ");  
      if (startClassName != null) out.append (startClassName);  
      else out.append ("<startup class>");  
  
      Iterator i = opts.iterator ();  
      while (i.hasNext ())  
        {  
          out.append (' ');  
          Opt opt = (Opt)i.next ();  
  
          boolean required = opt.getSpec ().isRequired ();  
          boolean list = opt.getSpec ().isList ();  
  
          if (!required) out.append ('[');  
          if (list) out.append ('(');  
          out.append (opt.tag ());  
          if (list) out.append (")*");  
          if (!required) out.append (']');  
        }  
  
      wordWrap (_out, out.toString (), 5, 5);  
    }  
      
    /**  
     * <p>Prints descriptions of all the options to the given  
     * writer. </p>  
     *  
     * @param out     The PrintWriter to write the usage info to.  
     **/  
    public void printOptDescriptions (PrintWriter out)  
    {  
      // --- calculate the longest option  
      int maxOptSpecLength = 0;  
      Iterator it = opts.iterator ();  
      while (it.hasNext ())  
	{  
	  Opt opt = (Opt)it.next ();  
  
	  List optionSpecifiers = opt.getOptionSpecifiers ();  
	  Iterator j = optionSpecifiers.iterator ();  
	  while (j.hasNext ())  
	    {  
	      String optSpec = (String)j.next ();  
	      maxOptSpecLength =   
		Math.max (maxOptSpecLength, optSpec.length ());  
	    }  
	}  
  
      // --- calculate the width of all the columns, based on various stuff.  
      int screenWidth = 78;  
      int rightMargin = 4;  
      int col1Width = maxOptSpecLength+1+1; // COMMMA + SPACE!  
      int col2Width = screenWidth - col1Width - rightMargin;   
  
      // --- print out all the options, divided into groups.  
      it = optsByGroup.keySet ().iterator ();  
      while (it.hasNext ())  
        {  
          String groupName = (String)it.next ();  
          List opts = (List)optsByGroup.get (groupName);  
  
          out.println ();  
          out.println (groupName == null ? "Miscellaneous" : groupName);  
          for (int i=0; i<opts.size (); i++)  
            {  
              Opt opt = (Opt)opts.get (i);  
              List optionSpecs = opt.getOptionSpecifiers ();  
              String description = opt.getSpec ().getDescription ();  
              List descriptionWords = breakIntoWords (description);  
  
              printTwoColumns (out,   
                               optionSpecs, descriptionWords,   
                               rightMargin, col1Width, col2Width, true, false);  
            }  
        }  
    }  
  
    // -----------------------------------------------------------------------  
    // ---- Utility methods for implementation -------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * <p>Initializes internal data structures and keys for the  
     * options, in preparation for parsing and/or printing usage  
     * information.</p>  
     *  
     * <p>This method only rebuilds these data structures if the  
     * option specifications are 'dirty': that is, if they have been  
     * changed.</p>  
     **/  
    protected void compile ()  
    {  
      if (compiled) return;  
  
      longOpts = new HashMap ();  
      shortOpts = new HashMap ();  
      equalsOpts = new HashMap ();  
      prefixOpts = new HashMap ();  
      suffixOpts = new HashMap ();  
      namelessOpt = null;  
  
      optsByGroup = new HashMap ();  
  
      // --- loop through the options.  For each one, add keys and  
      //   - cross references to the appropriate data structures...  
      Iterator i = opts.iterator ();  
      while (i.hasNext ())  
	{  
	  // XXX check for duplicates.  
	  Opt opt = (Opt)i.next ();  
  
	  List longNames = opt.getSpec ().getLongNames ();  
          Iterator j = longNames.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
              processOption (name, longOpts, opt, "--"+name);  
            }  
  
	  List shortNames = opt.getSpec ().getShortNames ();  
          j = shortNames.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
              processOption (name, shortOpts, opt, "-"+name);  
            }  
  
	  List equalses = opt.getSpec ().getEqualses ();  
          j = equalses.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
              processOption (name, equalsOpts, opt, name);  
            }  
  
	  List prefixes = opt.getSpec ().getPrefixes ();  
          j = prefixes.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
              processOption (name, prefixOpts, opt, name+"*");  
            }  
  
	  List suffixes = opt.getSpec ().getSuffixes ();  
          j = suffixes.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
              processOption (name, suffixOpts, opt, "*"+name);  
            }  
  
	  if (opt.getSpec ().getNameless ())   
	    if (namelessOpt != null)  
	      throw new OptCompilationFailedException   
		("Only one nameless option allowed at a time.");  
	    else  
	      namelessOpt = opt;  
  
          // --- process groups.  
	  List groups = opt.getSpec ().getGroups ();  
          if (groups.isEmpty ())  
            {  
              groups = new ArrayList ();  
              groups.add (null);  
            }  
          j = groups.iterator ();  
          while (j.hasNext ())  
            {  
              String name = (String)j.next ();  
  
              List groupList = (List)optsByGroup.get (name);  
              if (groupList == null)  
                {  
                  groupList = new ArrayList ();  
                  optsByGroup.put (name, groupList);  
                }  
  
              groupList.add (opt);  
            }  
  
	}  
      compiled = true;  
    }  
  
    /**  
     * Utility method for use by compile method.  
     **/  
    private void processOption (String name, Map set, Opt opt,   
                                String debugName)  
    {  
      if (set.containsKey (name))  
        throw new OptCompilationFailedException   
          (debugName+" specified more than once!");  
      set.put (name, opt);  
    }  
  
    /**  
     * <p>Process '@' options, loading sets of options from named  
     * files. </p>  
     *  
     * @param tokens    The tokens to process  
     * @return          The given tokens, with @ options expanded.  
     *  
     * @exception OptParseException  
     *                  Thrown if there is an error loading or parsing an  
     *                  @-option file.  
     **/  
    protected String[] preprocessTokens (String[] tokens)   
      throws OptParseException  
    {  
      List result = new ArrayList ();  
      for (int i=0; i<tokens.length; i++)  
	{  
	  String token = tokens[i];  
	  if (token.startsWith ("@"))  
            try {  
              result.addAll (loadTokenFile (token.substring (1)));  
            } catch (IOException ex) {  
              throw new OptParseException   
                ("I/O exception parsing option file", ex);  
            }  
	  else  
	    result.add (token);  
	}  
  
      return (String[])result.toArray (new String[result.size ()]);  
    }  
  
    protected Opt getLongOpt (String longName) throws OptParseException  
    {  
      Opt longOpt = (Opt)longOpts.get (longName);  
  
      // --- try to find a maximally unique prefix...  
      if (longOpt == null)  
        {  
          String matchedLongName = null;  
          Iterator i = longOpts.keySet ().iterator ();  
          while (i.hasNext ())  
            {  
              String possibleMatch = (String)i.next ();  
              if (possibleMatch.startsWith (longName))  
                {  
                  if (longOpt != null)  
                    {  
                      throw new OptParseException   
                        ("Ambiguous specification of long name:  "+  
                         "You specified '"+longName+"', and there exist "+  
                         "long names '"+matchedLongName+"' and '"+  
                         possibleMatch+"'");  
                    }  
                  else  
                    {  
                      longOpt = (Opt)longOpts.get (possibleMatch);  
                      matchedLongName = possibleMatch;  
                    }  
                }  
            }  
        }  
  
      return longOpt;  
    }  
  
    /**  
     * <p>Attempts to match the given token against all suffix,  
     * prefix, equals, or nameless rules. </p>  
     *  
     * @param token     The token to try to match  
     * @return          The matched option, or <code>null</code>, if   
     *                  there is no match.  
     **/  
    protected Opt matchNamelessToken (String token)  
    {  
      Iterator i;  
  
      i = equalsOpts.keySet ().iterator ();  
      while (i.hasNext ())  
	{  
	  String equals = (String)i.next ();  
	  if (token.equals (equals)) return (Opt)equalsOpts.get (equals);  
	}  
  
      i = prefixOpts.keySet ().iterator ();  
      while (i.hasNext ())  
	{  
	  String prefix = (String)i.next ();  
	  if (token.startsWith (prefix)) return (Opt)prefixOpts.get (prefix);  
	}  
  
      i = suffixOpts.keySet ().iterator ();  
      while (i.hasNext ())  
	{  
	  String suffix = (String)i.next ();  
	  if (token.endsWith (suffix)) return (Opt)suffixOpts.get (suffix);  
	}  
  
      return namelessOpt;  
    }  
  
    // -----------------------------------------------------------------------  
    // ---- Read a token file ------------------------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * <p>Load the named @-option file into a List. </p>  
     *  
     * @param fileName    The name of the file to load.  
     * @return            A List containing the options loaded from the file.  
     *  
     * @exception OptParseException  
     *                    Thrown if there is an error loading or parsing   
     *                    the file.  
     **/  
    protected List loadTokenFile (String fileName)   
      throws OptParseException, IOException  
    {  
      List result = new ArrayList ();  
  
  
      StreamTokenizer in =   
        new StreamTokenizer (new FileReader (fileName));  
      in.resetSyntax ();  
      in.wordChars ('A', 'Z');  
      in.wordChars ('a', 'z');  
      in.wordChars ('\u0000', '\uffff');  
      in.whitespaceChars ('\u0000', '\u0020');  
      in.commentChar ('/');  
      in.quoteChar ('\'');  
      in.quoteChar ('"');  
      in.slashSlashComments (true);  
      in.slashStarComments (true);  
  
      int token = in.nextToken ();  
      while (token != StreamTokenizer.TT_EOF)  
        {  
          switch (token)  
            {  
            case '"': case '\'': case StreamTokenizer.TT_WORD:  
              result.add (in.sval);  
              break;  
            default:  
              throw new OptParseException   
                (fileName+":"+in.lineno ()+": unexpected token type "+token);  
            }  
  
          token = in.nextToken ();  
        }  
  
      return result;  
    }  
  
    // -----------------------------------------------------------------------  
    // ---- Word-wrap algorithms ---------------------------------------------  
    // -----------------------------------------------------------------------  
  
    /**  
     * <p>Word-wrap the specified text to an 80-column screen.</p>  
     *  
     * @see #wordWrap(PrintWriter,String,int,int,int)  
     **/  
    public static void wordWrap (PrintWriter out,  
                                 String t, int leftMargin, int rightMargin)  
    { wordWrap (out, t, leftMargin, rightMargin, 80); }  
  
    /**  
     * <p>Word-wrap the specified text to a PrintWriter.</p>  
     *  
     * <p>The algorithm used here is very expensive, and so has  
     * limited applicability.  For use in a usage printing scenario,  
     * this is fine.  Simplicity and straightforwardness matters more  
     * to me here than screaming performance while printing how to use  
     * grep.</p>  
     *  
     * @param out         the PrintWriter used to wrap words  
     * @param text        the text to print.  
     * @param leftMargin  the left margin (in characters)  
     * @param rightMargin the right margin (in characters)  
     * @param pageWidth   the width of the page (in characters)  
     **/  
    public static void wordWrap (PrintWriter out,  
                                 String text, int leftMargin, int rightMargin,  
                                 int pageWidth)  
    {  
      // --- note that the -1 factor in the 'col 1 width' calculation is  
      //   - to avoid characters being printed in the last column, which  
      //   - on many displays forces an implicit linefeed.  
      List words = breakIntoWords (text);  
      printTwoColumns (out,   
                       words, // col 1 words  
                       new ArrayList (), // col 2 words  
                       leftMargin, // left margin  
                       pageWidth - rightMargin - 1, // col 1 width  
                       0, // col 2 width  
                       false, // no commas col 1  
                       false); // no commas col 2  
    }  
  
    /**  
     * Breaks the given string into words.  Used in support of  
     * <code>wordWrap</code> and friends.  
     **/  
    public static List breakIntoWords (String s)  
    {  
      List words = new ArrayList ();  
      StringTokenizer tokenizer = new StringTokenizer (s, " ");  
      while (tokenizer.hasMoreTokens ())  
	{  
	  String word = tokenizer.nextToken ();  
	  words.add (word);  
	}  
      return words;  
    }  
  
    /**  
     * Given 2 lists of strings, lay them out in two columns, pleasantly.  
     **/  
    private static void printTwoColumns (PrintWriter out,   
                                         List firstCol, List secondCol,  
                                         int rightMargin,  
                                         int maxCol1Width, int maxCol2Width,  
                                         boolean col1Commas,   
                                         boolean col2Commas)  
    {  
      ListIterator first = firstCol.listIterator ();  
      ListIterator second = secondCol.listIterator ();  
  
      while (first.hasNext () || second.hasNext ())  
	{  
	  for (int i=0; i<rightMargin; i++) out.print (' ');  
  
	  // --- first column  
	  printColumn (out, first, maxCol1Width, col1Commas);  
  
	  // --- second column  
	  printColumn (out, second, maxCol2Width, col2Commas);  
  
	  out.println ();  
	}  
    }  
  
    /** 
     * Support method for <code>printColumn</code>
    **/
    public static int contains (String val, String subject, boolean cases)
    {
      int i;
      for (i = 0; i <= subject.length () - val.length (); i++)
	if (subject.regionMatches (cases, i, val, 0, val.length ()))
	  return i;
      return -1;
    }

    /**  
     * Support method for <code>printTwoColumns</code>  
     **/  
    private static void printColumn (PrintWriter out, ListIterator words,   
                                     int maxWidth, boolean commas)  
    {  
      int pos = 0;  
  
      // --- print words until we run out of space.  
      while (words.hasNext () && pos < maxWidth)  
	{  
	  String word = (String)words.next ();  
	  if (commas && words.hasNext ()) word += ',';  
	  int wordLength = word.length ();  
	  
	  if (contains ("\n", word, true) != -1)
	    {
	      String ourHalf = word.substring (0, contains 
						 ("\n", word, true));
	      if (contains ("\n", word, true) < word.length ())
		{
		  words.add (word.substring (contains
					     ("\n", word, true)+1,
					     word.length ()));
		  words.previous ();
		}
	      out.print (ourHalf);
	      pos += ourHalf.length ();
	      break;
	    }
  
	  // --- done.  
	  if (pos + wordLength >= maxWidth && wordLength < maxWidth)  
	    { words.previous (); break; }  
  
	  out.print (word);  
	  pos += wordLength;  
  
	  if (pos+1 < maxWidth)  
	    {  
	      out.print (' ');  
	      pos++;  
	    }  
	}  
  
      // --- print spaces to fill out the rest of the available space.  
      while (pos < maxWidth)  
	{  
	  out.print (' ');  
	  pos++;  
	}  
    }  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Option specification                                            ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  /**  
   * <p>An option specifier provides a builder API for specifying the  
   * features of an option.</p>  
   **/  
  public static class OptSpec {  
    OptParser parser;  
  
    List longNames = new ArrayList ();  
    List shortNames = new ArrayList ();  
    List equalses = new ArrayList ();  
    List prefixes = new ArrayList ();  
    List suffixes = new ArrayList ();  
    boolean nameless = false;  
    boolean terminal = false;

    List groups = new ArrayList ();  
  
    String description = null;  
    boolean required = false;  
  
    int minArity = 0;  
    int maxArity = 1;  
  
    int argumentArity = 1;  
  
    public OptSpec (OptParser _parser) { parser = _parser; }  
  
    /** Wraps this specification as a Help option, adds it to the  
        parser, and returns the Help option. */  
    public HelpOpt asHelp ()   
    { return (HelpOpt)parser.addOpt (new HelpOpt (this)); }  
  
    /** Wraps this specification as a Boolean option, adds it to the  
        parser, and returns the Boolean option. */  
    public BooleanOpt asBoolean ()   
    { return (BooleanOpt)parser.addOpt (new BooleanOpt (this)); }  
  
    /** Wraps this specification as a String option, adds it to the  
        parser, and returns the String option. */  
    public StringOpt asString ()   
    { return (StringOpt)parser.addOpt (new StringOpt (this)); }  
  
    /** Wraps this specification as a Int option, adds it to the  
        parser, and returns the Int option. */  
    public IntOpt asInt ()   
    { return (IntOpt)parser.addOpt (new IntOpt (this)); }  
  
    /** Wraps this specification as a FileName option, adds it to the  
        parser, and returns the FileName option. */  
    public FileNameOpt asFileName ()   
    { return (FileNameOpt)parser.addOpt (new FileNameOpt (this)); }  
      
    /** Adds a long name to this option spec. */  
    public OptSpec longName (String v) { longNames.add (v); return this; }  
    /** Adds a short name to this option spec. */  
    public OptSpec shortName (char v)   
    { shortNames.add (String.valueOf (v)); return this; }  
    /**   
     * <p>Adds a equals specifier to this option spec.</p>  
     *  
     * <p>An equals specifier says that if a particular string is  
     * found on the command line, it is to be processed by a  
     * particular option.</p>  
     **/  
    public OptSpec equals (String v) { equalses.add (v); return this; }  
  
    /**   
     * <p>Adds a prefix specifier to this option spec.</p>  
     *  
     * <p>An prefix specifier says that if a string with a particular  
     * prefix is found on the command line, it is to be processed by a  
     * particular option.</p>  
     **/  
    public OptSpec prefix (String v) { prefixes.add (v); return this; }  
  
    /**   
     * <p>Adds a suffix specifier to this option spec.</p>  
     *  
     * <p>An suffix specifier says that if a string with a particular  
     * suffix is found on the command line, it is to be processed by a  
     * particular option.</p>  
     **/  
    public OptSpec suffix (String v) { suffixes.add (v); return this; }  
  
    /**   
     * <p>Specifies that this option will be nameless.</p>  
     *  
     * <p>There may only be one nameless option.  If an option is  
     * encountered which has no name (no short name or long name), and  
     * does not match any of the suffix, prefix, or equals rules),  
     * then it is processed by the nameless option, if it is  
     * available.  Nameless options are often used for utilities which  
     * process a number of filenames, which may be arbitrary  
     * strings.</p>  
     **/  
    public OptSpec nameless () { nameless = true; return this; }  

    /** 
     * <p>Specifies that this option should end the parsing of the tokens,
     * and swallow all remaining tokens whole as its option value list. </p>
     **/
    public OptSpec terminal () 
    {
      list ();
      terminal = true;
      return this;
    }
  
    /**   
     * <p>Specifies this option's minimum arity.  It is a parse error  
     * for the option <code>opt</code> to be specified fewer than  
     * <code>opt.getMinArity()</code> times. </p>  
     *  
     * <p>Some min arities of note are 0 (which means that the option  
     * is optional), and 1 (which means that the option is  
     * required).</p>  
     *  
     * <p>The default minArity of any option is 0, meaning that it is  
     * optional.</p>  
     *  
     * @see #required  
     * @see #optional  
     **/  
    public OptSpec minArity (int v) { minArity = v; return this; }  
  
    /**  
     * <p>Specifies this option's maximum arity.  It is a parse error  
     * for the option <code>opt</code> to be specified more than  
     * <code>opt.getMaxArity()</code> times. </p>  
     *  
     * <p>There is also special handling for values less than 0.  If  
     * the maximuum arity is -1 or lower, then the option may be  
     * specified as many times as the user likes, and there will be no  
     * error.</p>  
     *  
     * <p>Some min arities of note are 0 (which means that the option  
     * cannot be specified -- this is a stupid thing to set max arity  
     * to), 1 (which means that the option can only be specified once  
     * -- it is a singleton), and -1 (which means that the option can  
     * be specified as many times as the user would like.)</p>  
     *  
     * <p>The default maxArity of any option is 1, meaning that it  
     * cannot be specified more than once.</p>  
     *  
     * @see #list  
     **/  
    public OptSpec maxArity (int v) { maxArity = v; return this; }  
  
    /**  
     * <p>Specifies the number of arguments this parameter should receive.</p>  
     *  
     * <p>If 0 arguments are specified, </p>  
     **/  
    public OptSpec argumentArity (int v) { argumentArity = v; return this; }  
  
    /** Specifies this option's description: used in usage information. */  
    public OptSpec description (String v) { description = v; return this; }  
  
    /**   
     * Specifies this option's string group name.  When usage  
     * information is printed out, options are grouped into these  
     * groups, and the group names are used as headers.  
     **/  
    public OptSpec group (String groupName)   
    { groups.add (groupName); return this; }  
  
    /** alias for minArity (0) */  
    public OptSpec optional () { return minArity (0); }  
    /** alias for minArity (1) */  
    public OptSpec required () { return minArity (1); }  
    /** alias for maxArity (-1) */  
    public OptSpec list () { return maxArity (-1); }  
  
    public List getLongNames () { return longNames; }  
    public List getShortNames () { return shortNames; }  
    public List getEqualses () { return equalses; }  
    public List getPrefixes () { return prefixes; }  
    public List getSuffixes () { return suffixes; }  
    public boolean getNameless () { return nameless; }  
    public String getDescription () { return description; }  
    public boolean getRequired () { return minArity > 0; }  
    public int getMinArity () { return minArity; }  
    public int getMaxArity () { return maxArity; }  
    public int getArgumentArity () { return argumentArity; }  
  
    /** Return true if this option must be specified. */  
    public boolean isRequired () { return getMinArity () > 0; }  
  
    /** Return true if this option may be specified more than once. */  
    public boolean isList ()   
    { return getMaxArity () > 1 || getMaxArity () < 0; }  
  
    /** Return the list of groups this option is a member of. */  
    public List getGroups () { return groups; }  
  }  
  
  public abstract static class ArgumentParser {  
    Opt opt;  
  
    public ArgumentParser (Opt _opt) { opt = _opt; }  
  
    public Opt getOpt () { return opt; }  
    public OptSpec getSpec () { return opt.getSpec (); }  
  
    protected abstract Object parseArguments (TokenStream in)  
      throws OptParseException;  
  
    public String getTypeName () { return "argument"; }  
  
    public void printArgumentsSpec (PrintWriter out)  
    {  
      if (getSpec ().getArgumentArity () < 0)  
        out.print ("<"+getTypeName ()+">...");  
      else  
        {  
          for (int i=0; i<getSpec ().getArgumentArity (); i++)  
            {  
              if (i > 0) out.print (' ');  
              out.print ("<"+getTypeName ()+">");  
            }  
        }  
    }  
  
    /**  
     * Grab the given token.  If the token specified is outside of the  
     * token stream, throw a parse exception.  
     **/  
    protected String getToken (TokenStream in) throws OptParseException  
    {  
      if (!in.hasNext ())   
	throw new OptParseException   
	  ("Option '"+getOpt ().tag ()+  
           "' expected argument, got end of options!");  
      return in.nextToken ();  
    }  
  }  
  
  public static class HelpArgumentParser extends ArgumentParser {  
    public HelpArgumentParser (Opt _opt) { super (_opt); }  
  
    protected Object parseArguments (TokenStream in) throws OptParseException  
    { throw new OptHelpRequestedException ("Help requested"); }  
  }  
  
  public static class StringArgumentParser extends ArgumentParser {  
  
    public StringArgumentParser (Opt _opt) { super (_opt); }  
  
    protected Object parseArguments (TokenStream in) throws OptParseException  
    {  
      int argumentArity = getSpec ().getArgumentArity ();  
  
      Object result;  
      if (argumentArity < 0)  
        {  
          List list = new ArrayList ();  
          while (in.hasNext ())  
            {  
              String token = getToken (in);  
              list.add (token);  
            }  
          result = (String[])list.toArray (new String[list.size ()]);  
        }  
      else if (argumentArity == 1)  
        {  
          String token = getToken (in);  
          result = token;  
        }  
      else  
        {  
          List list = new ArrayList ();  
          for (int i=0; i<argumentArity; i++)  
            {  
              String token = getToken (in);  
              list.add (token);  
            }  
          result = (String[])list.toArray (new String[list.size ()]);  
        }  
      return result;  
    }  
  
    public String getTypeName () { return "string"; };  
  }  
  
  public abstract static class FilterArgumentParser extends ArgumentParser {  
    StringArgumentParser base;  
  
    public FilterArgumentParser (Opt _opt, StringArgumentParser _base)   
    { super (_opt); base = _base; }  
  }  
  
  public static class IntArgumentParser extends FilterArgumentParser {  
  
    public IntArgumentParser (Opt _opt, StringArgumentParser _base)   
    { super (_opt, _base); }  
    
    protected Object parseArguments (TokenStream in) throws OptParseException  
    {  
      Object _result = base.parseArguments (in);  
      Object result;  
      int argumentArity = getSpec ().getArgumentArity ();  
      if (argumentArity < 0 || argumentArity > 1)  
        {  
          String[] list = (String[])_result;  
          int[] intArray = new int[list.length];  
          for (int i=0; i<list.length; i++)  
            {  
              String token = list[i];  
              intArray[i] = parseInt (token);  
            }  
          result = intArray;  
        }  
      else if (argumentArity == 0)  
        {  
          // XXX ???  
          result = new Integer (1);  
        }  
      else // (argumentArity == 1)  
        {  
          result = new Integer (parseInt ((String)_result));  
        }  
  
      return result;  
    }      
  
    int parseInt (String token) throws OptParseException  
    {  
      try {  
        // XXX radix?  
        return Integer.parseInt (token);  
      } catch (NumberFormatException ex) {  
        throw new OptParseException   
          ("Expected integer argument for "+getOpt ().tag ()+  
           ", instead got "+token);  
      }  
    }  
  
    public String getTypeName () { return "integer"; };  
  }  

public static class EatAllParser extends ArgumentParser
{
  public EatAllParser (Opt opt)
  {
    super (opt);
    //super (_opt, _base);
  }
  
  protected Object parseArguments (TokenStream in) throws OptParseException
  {
    List result = new ArrayList ();
    for (; in.hasNext ();)
      result.add (in.nextToken ());
    //return (String []) result.toArray (new String [result.size ()]);
    return result;
    //return result;
  }

  public String getTypeName () { return "String"; };  
}

  
  public static class BooleanArgumentParser extends FilterArgumentParser {  
  
    public BooleanArgumentParser (Opt _opt, StringArgumentParser _base)   
    { super (_opt, _base); }  
    
    protected Object parseArguments (TokenStream in) throws OptParseException  
    {  
      Object _result = base.parseArguments (in);  
      Object result;  
      int argumentArity = getSpec ().getArgumentArity ();  
      if (argumentArity < 0 || argumentArity > 1)  
        {  
          String[] list = (String[])_result;  
          boolean[] booleanArray = new boolean[list.length];  
          for (int i=0; i<list.length; i++)  
            {  
              String token = list[i];  
              booleanArray[i] = parseBoolean (token);  
            }  
          result = booleanArray;  
        }  
      else if (argumentArity == 0)  
        {  
          // XXX ???  
          result = Boolean.TRUE;  
        }  
      else // (argumentArity == 1)  
        {  
          result =   
            parseBoolean ((String)_result) ? Boolean.TRUE : Boolean.FALSE;  
        }  
  
      return result;  
    }      
  
    boolean parseBoolean (String token) throws OptParseException  
    {  
      char firstChar = Character.toLowerCase (token.charAt (0));  
      if (firstChar == 't' || firstChar == '1' || firstChar == 'y')  
        return true;  
      if (firstChar == 'f' || firstChar == '0' || firstChar == 'n')  
        return false;  
  
      throw new OptParseException   
        ("Expected boolean argument for "+getOpt ().tag ()+  
         ", instead got "+token);  
    }  
  
    public String getTypeName () { return "boolean"; };  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Option                                                          ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  /**  
   * <p>Represents a command-line option.</p>  
   *  
   * <p>This is the main deal: users of Clapi create instances of  
   * Opt subclasses, and ask the parser to parse.  The subclasses of  
   * Opt are responsible for parsing their arguments and constructing  
   * the result data structure.</p>  
   **/  
  public abstract static class Opt {  
  
    OptSpec spec;  


    public boolean isTerminal ()
    {
      return spec.terminal;
    }
    
  
    public Opt (OptSpec _spec) { spec = _spec; }  
  
    public OptSpec getSpec () { return spec; }  
  
    public abstract ArgumentParser getArgumentParser ();  
  
    /**  
     * <p>Parses the arguments for this option, adding them to the  
     * OptResult data structure passed in.</p>  
     *   
     * <p>Consumes tokens from the input stream.</p>  
     **/  
    protected void parseArguments (TokenStream in, OptResult optResult)  
      throws OptParseException  
    {  
      Object result;
      if (!isTerminal ())
	{
	 result = getArgumentParser ().parseArguments (in);  
	 optResult.add (this, result);  
	}
      else
	{
	  result = new EatAllParser (this).parseArguments (in);
	  for (Iterator it = ((List)result).iterator (); it.hasNext ();)
	    optResult.add (this, it.next ());
	}
    }  
  
    /**  
     * Grab the given token.  If the token specified is outside of the  
     * token stream, throw a parse exception.  
     **/  
    protected String getToken (TokenStream in) throws OptParseException  
    {  
      if (!in.hasNext ())   
	throw new OptParseException   
	  ("Option '"+tag ()+"' expected token, got end of options!");  
      return in.nextToken ();  
    }  
  
    protected void checkArity (OptResult result) throws OptParseException  
    {  
      Object value = result.get (this);  
  
      int minArity = getSpec ().getMinArity ();  
      if (minArity > 0 && !result.containsKey (this))   
	throw new OptParseException ("Required opt "+tag ()+" not specified");  
  
      if (minArity > 1)  
	{  
	  // --- it's a list.  
	  if (!(value instanceof List))  
	    throw new OptParseException ("For opt "+tag ()+" minimum arity "+  
					 "is greater than 1, and somehow we "+  
					 "got a non-list!!");  
  
	  List list = (List)value;  
  
	  if (list.size () < minArity)  
	    throw new OptParseException ("Required at least "+minArity+  
					 " repetitions of opt "+tag ()+  
					 ", got "+list.size ()+" repetitions");  
	}  
    }  
  
    public Object get (OptResult result)  
    {  
      return result.get (this);  
    }  
  
    public List getList (OptResult result)  
    {  
      // XXX error if maxArity <= 1??  
      // System.err.println (result.get (this).getClass ());
      //Object [] o = (Object [])result.get (this);
      //for (int i=0; i<o.length; i++)
      //System.err.println (o [i].getClass ());
      
      return (List)result.get (this);  
    }  
  
    public Object[] getObjectArray (OptResult result)  
    {  
      List l = getList (result);  
      if (l == null) return null;  
      return l.toArray (new Object[l.size ()]);  
    }  
  
    public String toString () { return tag (); }
  
    public String tag ()  
    {  
      List optionSpecifiers = getOptionSpecifiers ();  
      if (optionSpecifiers.isEmpty ()) return "<Unspecifiable Option>";  
      else return (String)optionSpecifiers.get (0);  
    }  
  
    /**  
     * Returns a list of strings containing human-readable  
     * descriptions of all the forms of this option.  
     **/  
    public List getOptionSpecifiers ()  
    {  
      List result = new ArrayList ();  
  
      Iterator i;  
  
      String argumentsSpec = getArgumentsSpec ();  
      boolean space = argumentsSpec.length () > 0;   
  
      i = getSpec ().getLongNames ().iterator ();  
      while (i.hasNext ())  
	{  
	  String name = (String)i.next ();  
	  result.add ("--"+name+(space?" ":"")+argumentsSpec);  
	}  
  
      i = getSpec ().getShortNames ().iterator ();  
      while (i.hasNext ())  
	{  
	  String name = (String)i.next ();  
	  result.add ("-"+name+(space?" ":"")+argumentsSpec);  
	}  
  
      i = getSpec ().getEqualses ().iterator ();  
      while (i.hasNext ())  
	{  
	  String name = (String)i.next ();  
	  result.add (name);  
	}  
  
      i = getSpec ().getPrefixes ().iterator ();  
      while (i.hasNext ())  
	{  
	  String name = (String)i.next ();  
	  result.add (name+"*");  
	}  
  
      i = getSpec ().getSuffixes ().iterator ();  
      while (i.hasNext ())  
	{  
	  String name = (String)i.next ();  
	  result.add ("*"+name);  
	}  
  
      if (getSpec ().getNameless ()) result.add (argumentsSpec);  
  
      return result;  
    }  
  
    public String getArgumentsSpec ()  
    {  
      StringWriter sout = new StringWriter ();  
      PrintWriter out = new PrintWriter (sout, true);  
      printArgumentsSpec (out);  
      out.flush ();  
      return sout.toString ();  
    }  
  
    public void printArgumentsSpec (PrintWriter out)  
    { getArgumentParser ().printArgumentsSpec (out); }  
  }  
  
  
  // -------------------------------------------------------------------------  
  // ---- Help Option                                                     ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class HelpOpt extends Opt {  
    public HelpOpt (OptSpec _spec) { super (_spec); }  
    public ArgumentParser getArgumentParser ()   
    { return new HelpArgumentParser (this); }  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Boolean Option                                                  ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class BooleanOpt extends Opt {  
  
    boolean defaultValue = false;  
  
    public BooleanOpt (OptSpec _spec) { super (_spec); }  
  
    public BooleanOpt defaultValue (boolean v)   
    { defaultValue = v; return this; }  
  
    public ArgumentParser getArgumentParser ()   
    { return new BooleanArgumentParser (this, new StringArgumentParser (this));}  
  
    protected boolean toBoolean (Boolean v)  
    {  
      if (v == null) return defaultValue;  
      else return v.booleanValue ();  
    }  
  
    public boolean getBoolean (OptResult result)  
    {  
      Boolean b = (Boolean)get (result);  
      return toBoolean (b);  
    }  
  
    public boolean[] getBooleanArray (OptResult result)  
    {  
      List l = getList (result);  
      if (l == null) return null;  
  
      boolean[] retval = new boolean[l.size ()];  
      for (int i=0; i<l.size (); i++)  
	{  
	  Boolean b = (Boolean)l.get (i);  
	  retval[i] = toBoolean (b);  
	}  
  
      return retval;  
    }  
  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- String Option                                                   ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class StringOpt extends Opt {  
  
    public String defaultValue = null;  
  
    public StringOpt (OptSpec _spec) { super (_spec); }  
  
    public StringOpt defaultValue (String v) { defaultValue = v; return this; }  
  
    public ArgumentParser getArgumentParser ()   
    { return new StringArgumentParser (this); }  
  
    protected String toString (String s)  
    {  
      if (s == null) return defaultValue;  
      return s;  
    }  
  
    public String getString (OptResult result)  
    {  
      String s = (String)get (result);  
      return toString (s);  
    }  
  
    public String[] getStringArray (OptResult result)  
    {  
      List l = getList (result);  
      if (l == null) return null;  
  
      String[] retval = new String[l.size ()];
      for (int i=0; i<l.size (); i++)  
	{
	  String s = (String)l.get (i);  
	  retval[i] = toString (s);  
	}  
  
      return retval;  
    }  
  }  
  
  public static class FileNameOpt extends StringOpt {  
  
    public FileNameOpt (OptSpec _spec) { super (_spec); }  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Integer Option                                                  ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class IntOpt extends Opt {  
    int defaultValue;  
  
    public IntOpt (OptSpec _spec) { super (_spec); }  
  
    public IntOpt defaultValue (int v) { defaultValue = v; return this; }  
  
    public ArgumentParser getArgumentParser ()   
    { return new IntArgumentParser (this, new StringArgumentParser (this)); }  
  
    protected int toInt (Integer v)  
    {  
      if (v == null) return defaultValue;  
      else return v.intValue ();  
    }  
  
    public int getInt (OptResult result)  
    {  
      Integer s = (Integer)get (result);  
      return toInt (s);  
    }  
  
    public int[] getIntArray (OptResult result)  
    {  
      List l = getList (result);  
  
      if (l == null) return null;  
  
      int[] retval = new int[l.size ()];  
      for (int i=0; i<l.size (); i++)  
	{  
	  Integer s = (Integer)l.get (i);  
	  retval[i] = toInt (s);  
	}  
  
      return retval;  
    }  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Option Parsing Result                                           ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class OptResult {  
    OptParser parser;  
    Map results;  
  
    public OptResult (OptParser _parser)  
    { parser = _parser; results = new HashMap (); }  
  
    public void add (Opt key, Object value) throws OptParseException  
    {  
      int maxArity = key.getSpec ().getMaxArity ();  
  
      // --- bad case: 0??  
      if (maxArity == 0)  
	{  
	  throw new OptParseException ("Cannot specify opt "+key.tag ()+".");  
	}  
  
      // --- list  
      else if (key.getSpec ().isList ())  
	{  
	  List list = (List)results.get (key);  
	  if (list == null)   
	    { list = new ArrayList (); results.put (key, list); }  
	  list.add (value);  
	}  
  
      // --- normal: non-list (maxArity == 1)  
      else  
	{  
	  // --- cannot have more than 1: must not already be there.  
	  if (results.containsKey (key))  
	    throw new OptParseException ("Cannot specify opt "+key.tag ()+  
					 " more than once");  
  
	  results.put (key, value);  
	}  
    }  
  
    public boolean containsKey (Opt key) { return results.containsKey (key); }  
    public Object get (Opt key) { return results.get (key); }  
  
    public String toString () { return results.toString (); }  
  }  
  
  static class TokenStream   
  {  
    LinkedList tokens;  
  
    TokenStream (String[] _tokens)   
    { this (new LinkedList (Arrays.asList (_tokens))); }  
    TokenStream (LinkedList _tokens) { tokens = _tokens; }  
    boolean hasNext () { return !tokens.isEmpty (); }  
    String nextToken () throws OptParseException   
    {  
      if (tokens.isEmpty ())   
        throw new OptParseException  
          ("Expected another token, got end of input");  
      return (String)tokens.removeFirst ();   
    }  
    void pushback (String v) { tokens.addFirst (v); }  
  }  
  
  // -------------------------------------------------------------------------  
  // ---- Exceptions                                                      ----  
  // ----                                                                 ----  
  // -------------------------------------------------------------------------  
  
  public static class OptCompilationFailedException extends RuntimeException {  
    Throwable nested = null;  
  
    public OptCompilationFailedException () { super (); }  
    public OptCompilationFailedException (String msg) { super (msg); }  
    public OptCompilationFailedException (String msg, Throwable ex)   
    { super (msg); nested = ex; }  
  
    public Throwable getNestedException () { return nested; }  
  
    /** Print the stack trace of this exception to the given writer. */  
    public void printStackTrace (PrintWriter out)  
    {  
      super.printStackTrace (out);  
      if (nested != null)  
        {out.println ("---- Nested Exception"); nested.printStackTrace (out);}  
      else   
        out.println ("--- No Nested Exception ---");  
    }  
  
    public void printStackTrace (PrintStream out)  
    { printStackTrace (new PrintWriter (out, true)); }  
  }  
  
  public static class OptParseException extends Exception {  
    Throwable nested = null;  
      
    public OptParseException () { super (); }  
    public OptParseException (String msg) { super (msg); }  
    public OptParseException (String msg, Throwable ex)   
    { super (msg); nested = ex; }  
  
  
    public Throwable getNestedException () { return nested; }  
  
    /** Print the stack trace of this exception to the given writer. */  
    public void printStackTrace (PrintWriter out)  
    {  
      super.printStackTrace (out);  
      if (nested != null)  
        {out.println ("---- Nested Exception"); nested.printStackTrace (out);}  
      else   
        out.println ("--- No Nested Exception ---");  
    }  
  
    public void printStackTrace (PrintStream out)  
    { printStackTrace (new PrintWriter (out, true)); }  
  }  
  
  public static class OptHelpRequestedException extends OptParseException {  
    public OptHelpRequestedException () { super (); }  
    public OptHelpRequestedException (String msg) { super (msg); }  
    public OptHelpRequestedException (String msg, Throwable ex)   
    { super (msg, ex); }  
  }  
  
  public static class OptShortHelpRequestedException   
    extends OptHelpRequestedException   
  {  
    public OptShortHelpRequestedException () { super (); }  
    public OptShortHelpRequestedException (String msg) { super (msg); }  
    public OptShortHelpRequestedException (String msg, Throwable ex)   
    { super (msg, ex); }  
  }  
  
}
