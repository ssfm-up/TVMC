package edu.toronto.cs.util;

import java.util.*;
import java.io.*;
import java.lang.*;

import edu.toronto.cs.util.Clapi.*;

/**
 ** This is the utility that parses the command line arguements for
 ** the model-checker.
 **/

/** Notes on adding a new option:

    1. In ParseOpts(), add using parser.opt()....
    2. Set a defulat value immediately after adding.
    3. In the "retrieves options values" section, get the value from
          result
    4. m.put("name", value)
**/
public class McClapi
{
  public McClapi ()
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
    OptParser parser = new OptParser ().startClass (McClapi.class);

    parser.programName ("Multi Valued Model Checker");
    parser.version ("0.1");
    parser.webPage ("http://www.cs.toronto.edu/~annie/FMGroup");
    parser.author ("Formal Methods Group");
    parser.copyrightNotice ("(c) 2001 University of Toronto");
    parser.programDescription 
      ("A command line implementation of the model checker.");

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
    parser.quote
      ("\"Anybody who thinks we can have practical things without "+
       "theoretical designs can go and quarrel with the nearest engineer "+
       " or architect for drawing thin lines on thin paper.\""+
       " -G.K. Chesterton (1874-1936)");
    
    

    /** gives the --verbose [int] option **/
    /** specifies the verbosity level **/
    /** default verbosity level is 5 **/
    int d = 2;
    IntOpt verboseOpt = parser.opt ().
      longName ("verbose").shortName ('v').
      description ("Verbose level (default = " + d + ")").
      group ("Model Options").
      asInt ();
    verboseOpt.defaultValue (d);
    /** gives the --mvset [string] option **/
    /** specifies the type of MvSets to be used (join or mdd) **/
    StringOpt mvsetOpt = parser.opt ().
      longName ("mvset").
      description ("Type of MvSet to use: join = JMvSet, " +
		   "mdd = MDDMvSet, jbdd = JBDDMvSet, " +
		   "cubdd = CUBDDMvSet, cuadd = CUADDMvSet, " + 
		   "jadd = JADDMvSet, cubiadd = CUBiADDMvSet").
      group ("Model Options").
      asString ();
    mvsetOpt.defaultValue ("mdd");
    /** gives the --state [string] option **/
    /** specifies the initial state **/
    StringOpt initstateOpt = parser.opt ().
      longName ("initstate").shortName ('s').
      description ("The name of the initial state").
      group ("Model Options").
      asString ();
    initstateOpt.defaultValue (null);
    /** gives the --statenames option **/
    /** specifies the state name user wishes to check against **/
    BooleanOpt statenamesOpt = parser.opt ().
      longName ("statenames").shortName ('S').
      description ("Add state names (default = false)").
      group ("Model Options").
      asBoolean ();
    statenamesOpt.defaultValue (false);
    BooleanOpt smvOpt = parser.opt ().
      longName ("smv").description ("Enables SMVModule").
      group ("Model Options").asBoolean ();
    smvOpt.defaultValue (false);
    
    /** gives the --bdd option for loading using DDUnDumper **/
    
    BooleanOpt bddOpt = parser.opt().
      longName ("bdd").description("Use DDUnDumper").
      group("Model Options").asBoolean();
    bddOpt.defaultValue(false);
    
    
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
    /** specifies output file for mc results **/
    FileNameOpt outFilesOpt = parser.opt ().
      longName ("output").
      suffix (".daVinci"). suffix (".txt").
      description ("Save results to file (default mdd.daVinci").
      group ("File Specification Options").
      asFileName ();
    outFilesOpt.defaultValue ("mdd.daVinci");
    OptResult result = parser.run (opts);
    
    /** retrieves the option values from parser **/
    int verboseValue = verboseOpt.getInt (result);
    String mvsetValue = mvsetOpt.getString (result);
    String initstateValue = initstateOpt.getString (result);
    boolean statenamesValue = statenamesOpt.getBoolean (result);
    boolean smvValue = smvOpt.getBoolean (result);
    boolean bddValue = bddOpt.getBoolean(result);
    
    String[] inputValues = inFilesOpt.getStringArray (result);
    String outputValue = outFilesOpt.getString (result);

    /** creates a map of (option, value) **/
    Map m = new HashMap();
    
    m.put ("verbose", new Integer (verboseValue));
    m.put ("mvset", mvsetValue);
    m.put ("initstate", initstateValue);
    m.put ("statenames", new Boolean (statenamesValue));
    m.put ("smv", new Boolean (smvValue));
    m.put ("bdd", new Boolean (bddValue));
    m.put ("output", outputValue);
    m.put ("input", Arrays.asList (inputValues));

    return m;
  }

  /**
   ** ParserHelper driver: it will print the options map.
   **/
  public static void main (String[] opts)
  {
    Map p = parseOpts(opts);
    Iterator i = p.keySet().iterator();
    System.out.println("The (key,value) pairs are: ");
    while(i.hasNext())
      {
	String k = (String)i.next();
	System.out.println(k+" = "+p.get(k));
      }
  }
       
}






