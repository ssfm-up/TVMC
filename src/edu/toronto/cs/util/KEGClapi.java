package edu.toronto.cs.util;

import java.util.*;
import java.io.*;
import edu.toronto.cs.util.Clapi.*;

/**
 ** This is the utility that parses the command line arguements for
 ** the counter example generator.
 **/
public class KEGClapi
{

  public KEGClapi ()
  {
  }

  /**
   ** Parses the arguments producing a Map for the options and their
   ** values. The map is of the form (option, value).
   **
   ** @param opts -- the arguements to be parsed
   **
   ** @return -- Map of all the options
   **/
  public static Map parseOpts (String[] opts)
  {
    OptParser parser = new OptParser ().startClass (KEGClapi.class);

    parser.programName ("Multi Valued Counterexample Generator");
    parser.version ("0.1");
    parser.webPage ("http://www.cs.toronto.edu/~annie/FMGroup");
    parser.author ("Formal Methods Group");
    parser.copyrightNotice ("(c) 2001 University of Toronto");
    parser.programDescription 
      ("A command line implementation of the counterexample generator.");

    parser.quote 
      ("\"Knowing is not enough; we must apply. Willing is not "+
       "enough; we must do.\" -Johann Wolfgang von Goethe (1749-1832)");
    parser.quote 
      ("\"I never did a day's work in my life; it was all fun.\" "+
       "-Thomas Edison (1847-1931)");
    parser.quote 
      ("\"If you would not be forgotten as soon as you are gone, "+
       "either write things worth reading or do things worth writing.\""+
       " -Benjamin Franklin (1706-1790)");
    parser.quote 
      ("\"I cannot judge my work while I am doing it. I have to do as "+
       "painters do, stand back and view it from a distance, but not too "+
       "great a distance.\" -Blaise Pascal (1623-1662)");
    parser.quote 
      ("\"Those who write against vanity want the glory of having written "+
       "well, and their readers the glory of reading well, and I who write "+
       "this have the same desire, as perhaps those who read this have "+
       "also.\" -Blaise Pascal (1623-1662)");    

    /** gives the --heuristic [string] option **/
    /** specifies the heuristics for KEG **/
    StringOpt heuristicOpt = parser.opt ().
      longName ("heuristic").
      description ("Textual heuristics").
      group ("KEG Options").
      asString ();
    /** gives the --prefix [string] option **/
    /** specifies the prefix for output files **/
    StringOpt prefixOpt = parser.opt ().
      longName("prefix").shortName ('p').
      description("Specify prefix for output file").
      group("KEG Options").
      asString ();
    prefixOpt.defaultValue("KEG");
    /** gives the --input [strings] option **/
    /** specifies input files for checking **/
    /** it is the required field **/
    /** used as default if no option tag is specified **/
    FileNameOpt inFilesOpt = parser.opt ().
      longName ("input").
      nameless ().suffix (".xml").
      description ("File for checking").
      required ().
      list ().
      group ("File Specification Options").
      asFileName ();
    /** gives the --output [string] option **/
    /** specifies output file for KEG results **/
    FileNameOpt outFilesOpt = parser.opt ().
      longName ("output").
      suffix (".daVinci").
      description ("Save results to file").
      group ("File Specification Options").
      asFileName ();
    /** gives the --statenames option **/
    /** specifies the state name user wishes to check against **/
    BooleanOpt statenamesOpt = parser.opt ().
      longName ("statenames").shortName ('S').
      description ("Add state names (default = false)").
      group ("Model Options").
      asBoolean ();
    statenamesOpt.defaultValue (false);
    BooleanOpt smvOpt = parser.opt ().
      longName("smv").description ("SMV").
      group ("Model Options").
      asBoolean ();
    smvOpt.defaultValue (false);
    
    /** parses the options **/
    OptResult result = parser.run (opts);
    
    /** retrieves the option values from parser **/
    String heuristicValue = heuristicOpt.getString (result);
    String prefixValue = prefixOpt.getString(result);
    String[] inputValues = inFilesOpt.getStringArray (result);
    String outputValue = outFilesOpt.getString(result);
    boolean statenamesValue = statenamesOpt.getBoolean (result);
    boolean smvValue = smvOpt.getBoolean (result);
    

    /** creates a map of (option, value) **/
    Map m = new HashMap();
    m.put("heuristic", heuristicValue);
    m.put("prefix", prefixValue);
    m.put("output", outputValue);
    m.put("input", Arrays.asList (inputValues));
    m.put ("statenames", new Boolean (statenamesValue));
    m.put ("smv", new Boolean (smvValue));

    return m;
  }
    
  /**
   ** ParserHelper driver: it will print the options map.
   **/  
  public static void main (String[] opts)
  {
    Map p = parseOpts(opts);
    Iterator i = p.keySet().iterator();
    System.out.println("The (key,value) pairs are:");
	
    while(i.hasNext())
      {
	String k = (String)i.next();
	System.out.println(k+" = "+p.get(k));
      }
  }
    
	
	
	
}


