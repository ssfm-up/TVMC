package edu.toronto.cs.davinci;

import java.util.*;
import java.awt.Color;


import edu.toronto.cs.util.*;


/**
 * This class representats a DaVinci graph as a list of terms. It
 * follows a Builder design pattern to allow construction of graphs
 * that can be dumped to DaVinci format
 *
 * @author <a href="mailto:arie@kanga.fm.sandbox">Arie Gurfinkel</a>
 * @version 1.0
 */
public class DaVinciGraph
{
  Set nodes;

  int idCounter;
  
  public DaVinciGraph ()
  {
    nodes = new HashSet ();
    idCounter = 0;
  }
  

  public String toString ()
  {
    return enumerate (nodes);
  }
  
  String quote (String s)
  {
    return StringUtil.quote (s);
  }
  
  String enumerate (Iterator it)
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ('[');
    while (it.hasNext ())
    {
	sb.append (it.next ().toString ());
	if (it.hasNext ())
	  sb.append (',');
      }
    sb.append (']');
    return sb.toString ();
  }
  
  String enumerate (Collection collection)
  {
    Iterator it = collection.iterator ();
    return enumerate (it);
  }

  public FullNode node ()
  {
    FullNode node = new FullNode (getUniqueId (), getType ());
    nodes.add (node);
    return node;
  }
  
  public RefNode ref (FullNode node)
  {
    return new RefNode (node);
  }
  
  String getUniqueId ()
  {
    return String.valueOf (idCounter++);
  }
  
  String getType ()
  {
    return "t1";
  }
  

  public abstract class Term
  {
    String id;
    String type;
    List attributes;


    public Term (String _id, String _type)
    {
      id = _id;
      type = _type;
      attributes = new ArrayList ();
    }

    public String getId ()
    {
      return id;
    }
    public void setId (String v)
    {
      id = v;
    }

    public boolean equals (Object o)
    {
      if (o.getClass () != this.getClass ()) return false;
      return id.equals (((Term)o).getId ());
    }
    public int hashCode ()
    {
      return id.hashCode ();
    }

    public Term attr (String name, String value)
    {
      attributes.add (new Attribute (name, value));
      return this;
    }
  }
  

  public interface Node 
  {
  }
  

  public class  FullNode extends Term implements Node
  {
    List edges;

    public FullNode (String id, String type)
    {
      super (id, type);
      edges = new ArrayList ();
    }
    

    public Edge edge (Node destNode)
    {
      if (destNode instanceof RefNode)
	return edge ((RefNode)destNode);
      
      return edge (ref ((FullNode)destNode));
    }
    
    public Edge edge (RefNode destNode)
    {
      Edge edge = new Edge (getUniqueId (), getType (), destNode);
      edges.add (edge);
      return edge;
    }

    public LabeledEdge edge (RefNode destNode, String label)
    {
      return labeledEdge (destNode, label);
    }
    
    public LabeledEdge labeledEdge (Node destNode, String label)
    {
      LabeledEdge edge = new LabeledEdge (getUniqueId (), 
					  getType (),
					  destNode).label (label);
      edges.add (edge);
      return edge;
    }
    
    
    
    public FullNode label (String name)
    {
      return (FullNode)attr ("OBJECT", name);
    }
    
    public FullNode color (String value)
    {
      return (FullNode)attr ("COLOR", value);
    }
    public FullNode fontFamily (String family)
    {
      return (FullNode)attr ("FONTFAMILY", family);
    }
    public FullNode fontStyle (String style)
    {
      return (FullNode)attr ("FONTSTYLE", style);
    }
    
    public FullNode box (String type)
    {
      return (FullNode)attr ("_GO", type);
    }
    public FullNode border (String type)
    {
      return (FullNode)attr ("BORDER", type);
    }
    
    public FullNode hidden (boolean hide)
    {
      return (FullNode)attr ("HIDDEN", hide ? "true" : "false");
    }
    
    
    
    //public FullNode color (Color color);
    

    public String toString ()
    {
      return "l(" + quote (id) + ",n(" + quote (type) + "," + 
	enumerate(attributes) + "," + enumerate (edges) + "))";
    }
  }


  public class RefNode implements Node
  {
    FullNode node;
    
    public RefNode (FullNode _node)
    {
      node = _node;
    }
    public String toString ()
    {
      return "r(" + quote (node.getId ()) + ")";
    }
    
    
  }
  
  public class Edge extends Term
  {
    Node destNode;

    public Edge (String id, String type, Node _destNode)
    {
      super (id, type);
      destNode = _destNode;
    }
    
    public Edge color (String v)
    {
      return (Edge)attr ("EDGECOLOR", v);
    }
    public Edge lineType (String v)
    {
      return (Edge)attr ("EDGEPATTERN", v);
    }
    public Edge dir (String v)
    {
      return (Edge)attr ("_DIR", v);
    }
    public Edge head (String v)
    {
      return (Edge)attr ("HEAD", v);
    }
    
    
    public String toString ()
    {
      return "l(" + quote (id) + 
	",e(" + quote (type) + "," + enumerate(attributes) + 
	"," + destNode + "))";
    } 
  }

  
  public class LabeledEdge extends Edge
  {
    FullNode label;
    Edge toLabel;
    Edge fromLabel;

    public LabeledEdge (String id, String type, Node destNode)
    {      
      super (id, type, destNode);

      label = new FullNode (getUniqueId (), getType ()).border ("none");
      toLabel = new Edge (getUniqueId (), getType (), label);
      toLabel.dir ("none");
      fromLabel = label.edge (destNode);
    }

    public LabeledEdge label (String v)
    {
      label.label (v);
      return this;
    }
    
    
    public Term attr (String name, String value)
    {
      toLabel.attr (name, value);
      fromLabel.attr (name, value);
      return this;
    }

    public Edge head (String v)
    {
      fromLabel.head (v);
      return this;
    }
    
    public Edge dir (String v)
    {
      assert false : "dir attribued is not supported on labeled edges";
      return this;
    }  
    
    public String toString ()
    {
      return toLabel.toString ();
    }
  }
  
  

  public class Attribute
  {
    String name;
    String value;

    public Attribute (String _name, String _value)
    {
      name = _name;
      value = _value;
    }
    public String toString ()
    {
      return "a(" + quote (name) + "," + quote (value) + ")";
    }    
  }

  static public StringBuffer pad (int length, String s, char character)
  {
    StringBuffer sb = new StringBuffer ();
    sb.append (s);
    if (s.length () >= length)
      return sb;
    
    for (int i = 0; i < (length - s.length ()); i++)
      {
	sb.insert (0, character);
      }
    return sb;
  } 

  static public StringBuffer translateColour (Object colour)
  {
    StringBuffer sb = new StringBuffer ();
    
    if (colour instanceof Color)
      {
	sb.append ('#');
	sb.append
	  (pad (2, Integer.toHexString(((Color)colour).getRed ()), '0'));
	sb.append
	  (pad (2, Integer.toHexString(((Color)colour).getGreen ()), '0'));
	sb.append
	  (pad (2, Integer.toHexString(((Color)colour).getBlue ()), '0'));
	return sb;
      }
    
    // Default case  
    sb.append (colour.toString ());
    return sb;
      
  }

  /**** Tester method ****/
  public static void main (String[] args)
  {
    DaVinciGraph graph = new DaVinciGraph ();
    
    FullNode a = graph.node ().label ("a");
    FullNode b = graph.node ().label ("b");
    a.labeledEdge (graph.ref (b), "label").lineType ("double");
    System.out.println (graph);
  }
  
  
}

