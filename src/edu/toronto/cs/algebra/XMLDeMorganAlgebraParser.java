package edu.toronto.cs.algebra;

import edu.toronto.cs.util.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import java.io.File;

/***
 *** Parses XML representation of the lattice
 *** XXX Needs serious rewrite
 ***/
public class XMLDeMorganAlgebraParser
{
  
  // -- tester method
  public static void main (String[] args) throws Exception
  {
    DeMorganTableAlgebra algebra = parse (args [0]);
    System.out.println ("Parsed an algebra:");
    System.out.println ("Dumping tables");
    java.io.PrintWriter out = new java.io.PrintWriter (System.out);
    out.println ("HELLO");
    algebra.dumpTables (out);
    out.println ("Carrier set: ");
    for (IntIterator it = algebra.carrierSetId (); it.hasNext ();)
      out.println (it.nextInt ());
    out.flush ();
  }
  

  public static DeMorganTableAlgebra parse (InputStream uri) 
    throws IOException, ParserConfigurationException, SAXException
  {
    // -- first parse the XML document
    Document document = DOMUtil.parse (uri);
    // -- construct a QBLattice from XML Document
    return buildAlgebra (document);
  }


  public static DeMorganTableAlgebra parse (String uri) 
    throws IOException, ParserConfigurationException, SAXException
  {
    System.out.println ("Parsing: " + uri);
    // -- first parse the XML document
    Document document = DOMUtil.parse (uri);
    // -- construct a QBLattice from XML Document
    return buildAlgebra (document);
  }
  

  public static DeMorganTableAlgebra buildAlgebra (Document document)
  {
    NodeList graphs = document.getElementsByTagName ("graph");
    if (graphs == null || graphs.getLength () != 1)
      throw new RuntimeException 
	("XML Document should contain one graph node");
    
    DeMorganTableAlgebra algebra = buildAlgebra ((Element)graphs.item (0));
    // -- set other algebra arguments, like image and things
    
    if (document.getElementsByTagNameNS ("*", "img").getLength () != 0)
      {
	Element imgElement = 
	  (Element)document.getElementsByTagNameNS ("*", "img").item (0);
	String imagePath = imgElement.getAttribute ("xlink:href");
	File imageFile = new File (imagePath);
// 	if (imageFile.exists ())
// 	  {
// 	    //lattice.setImageFile (imageFile);
// 	  }
      }
    
    return algebra;
  }
  
  /***
   * Given a top level graph node constructs a DeMorganTableAlgebra
   */
  public static DeMorganTableAlgebra buildAlgebra(Element graph) 
  {
    if (!sanityCheck (graph)) return null;

    // -- to construct a DeMorganTableAlgebra we need to know how many elements
    // -- we have in the lattice (= # of node tags)
    // -- and convert the 'above' edges into a list
    // -- and convert the 'neg' edges into a list
    
    List elements = collectElementNames (graph.getElementsByTagName ("node"));
    List order = buildOrder (graph.getElementsByTagName ("edge"));
    List negation = buildNegation (graph.getElementsByTagName ("edge"));
    
    return newDeMorganTableAlgebra (elements, order, negation);
  }
  
  public static DeMorganTableAlgebra 
    newDeMorganTableAlgebra (List elements, List lorder, List lnegation)
  {
    return new DeMorganTableAlgebra (elements, lorder, lnegation);
  }

  
  static List buildOrder (NodeList edges)
  {
    List order = new ArrayList ();
    
    int size = edges.getLength ();
    for (int i = 0; i < size; i++)
      {
	Element edge = (Element)edges.item (i);
	Element type = (Element)edge.getElementsByTagName ("type").item (0);
	if (type.getAttribute ("value").equals ("above"))
	  order.add (new String[] {edge.getAttribute ("from"), 
				   edge.getAttribute ("to")});
      }
    return order;
  }

  static List buildNegation (NodeList edges)
  {
    List negation = new ArrayList ();
    
    int size = edges.getLength ();
    for (int i = 0; i < size; i++)
      {
	Element edge = (Element)edges.item (i);
	Element type = (Element)edge.getElementsByTagName ("type").item (0);
	if (type.getAttribute ("value").equals ("neg"))
	  negation.add (new String[] {edge.getAttribute ("from"),
				      edge.getAttribute ("to")});
      }
    return negation;
  }


  static List collectElementNames (NodeList nodes)
  {
    int size = nodes.getLength ();
    List names = new ArrayList ();
    
    for (int i = 0; i < size; i++)
      {
	Element e = (Element)nodes.item (i);
	names.add (e.getAttribute ("ID"));
      }
    return names;
  }
  
  
  private static boolean sanityCheck (Element graph)
  {
    // -- no check at the moment
    return true;
  }
  
  

}
