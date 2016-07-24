package edu.toronto.cs.xkripke;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.xkripke.XKripke.*;
import edu.toronto.cs.util.DOMUtil;
import edu.toronto.cs.util.*;

import java.util.*;
import org.w3c.dom.*;

import edu.toronto.cs.algebra.*;



/*** Given a DOM tree constructs a XKripke structure for it */
public class XKripkeFactory
{

  public static final String XBEL_NS="www.cs.toronto.edu/xbel";

  public static void main (String[] args) throws Exception
  {
    XKripke kripke = parse (args [0]);
    

    System.out.println ("States:");
    for (Iterator it = kripke.getStates ().entrySet ().iterator (); 
	 it.hasNext (); )
      {
	Map.Entry entry = (Map.Entry)it.next ();
	System.out.println ("State: " + entry.getKey ());
	System.out.println (entry.getValue ());
      }
    
    System.out.println ("Transitions");
    for (Iterator it = kripke.getTransitions ().iterator (); it.hasNext ();)
      System.out.println (it.next ());

//     PropOrderIf propOrder = new AlternatePropOrder (kripke.getPropNames ());
    
//     int[] pMap = propOrder.getPrimedMap ();
//     System.out.println ("Primed map");
//     for (int i = 0; i < pMap.length; i++)
//       System.out.println (i + " = " + pMap [i]);
//     System.out.println ("Primed props");
//     pMap = propOrder.getPrimedProps ();
//     for (int i = 0; i < pMap.length; i++)
//       System.out.println (i + " = " + pMap [i]);

  }
  
  /**
   ** Parses the files specified by the String [] and creates XKripke
   ** for each one of them.
   **/  
  public static XKripke [] parse (String [] files) throws Exception
  {
    XKripke [] result = new XKripke [files.length];
    Document document;

    for (int i = 0; i < files.length; i++)
      result [i] = parse (files [i]);
    return result;
  }
  /**
   ** Parses the file specified by uri and creates XKripke structure
   ** from it.
   **/  
  public static XKripke parse (String uri) throws Exception
  {
    Document document = DOMUtil.parse (uri);
    return buildXKripke (document);
  }

  public static XKripke buildXKripke (Document document) throws Exception
  {
    NodeList graph = document.getElementsByTagName ("graph");
    
    if (graph == null || graph.getLength () != 1)
      return null;
    
    return buildXKripke ((Element)graph.item (0));
  }
  
  public static XKripke buildXKripke (Element graph) throws Exception
  {
    if (!sanityCheck (graph)) return null;
    
    // -- create an empty XKripke model
    XKripke kripke = new XKripke ();

    // -- first obtain a lattice to be used
    kripke.setAlgebra (buildAlgebra (graph));

    // -- build the states
    buildStates (kripke, graph.getElementsByTagName ("node"));
    // -- build the transitions
    buildTransitions (kripke, graph.getElementsByTagName ("edge"));

    return kripke;
  }
  
  static IAlgebra buildAlgebra (Element graph) throws Exception
  {
    //System.out.println ("Got: " + graph);
    Element logic =
      (Element)graph.getElementsByTagNameNS ("*","logic").item (0);

    if (!logic.getLocalName ().equals ("logic") ||
	!logic.getNamespaceURI ().equals (XBEL_NS))
      {
	System.out.println ("Got logic: " + logic);
	System.out.println ("tag name: " + logic.getTagName ());
	System.out.println ("NS: " + logic.getPrefix ());
	System.out.println ("NS URI: " + logic.getNamespaceURI ());
	System.out.println ("No Algebra");
	return null;
      }
    
    return XMLDeMorganAlgebraParser.parse (logic.getAttribute ("xlink:href"));
  }

  static void buildStates (XKripke kripke, NodeList nodes)
  {
    
    int size = nodes.getLength ();
    for (int i = 0; i < size; i++)
      {
	Element stateNode = (Element)nodes.item (i);
	XKripkeState state = new XKripkeState (stateNode.getAttribute ("ID"));
	
	String initial = stateNode.getAttribute ("xbel:initial");
	if (initial != null && initial.equals ("true"))
	  {
	  state.setInitial (true);
	  System.out.println ("Default initial state: ");
	  System.out.println (state);
	  //Assert.assert(false);
	  }
	
	
	for (org.w3c.dom.Node node = stateNode.getFirstChild (); node != null; 
	     node = node.getNextSibling ())
	  {
	    if (node.getNodeType () != org.w3c.dom.Node.ELEMENT_NODE)
	      continue;
	    Element child = (Element)node;
	    // -- skip non attr nodes
	    if (!child.getTagName ().equals ("attr"))
	      continue;
	    // -- skip non prop nodes
	    if (!child.getAttribute ("type").equals ("prop"))
	      continue;
	    // -- add new proposition to the state
	    state.addProp (new XKripkeProp (child.getAttribute ("name"),
					    child.getAttribute ("value")));
	    
	  }
	kripke.addState (state);
      }
  }
  
  static void buildTransitions (XKripke kripke, NodeList nodes)
  {
    int size = nodes.getLength ();

    for (int i = 0; i < size; i++)
      {
	// -- process next edge
	Element edge = (Element)nodes.item (i);
	Element attr = (Element)edge.getElementsByTagName ("attr").item (0);
	if (attr.getAttribute ("name").equals ("weight"))
	  kripke.addTransition (edge.getAttribute ("from"),
				edge.getAttribute ("to"),
				attr.getAttribute ("value"));
	else
	  throw new RuntimeException ("BAD");
      }
    
  }
  
  static boolean sanityCheck (Element graph)
  {
    return true;
  }
  
  
}
