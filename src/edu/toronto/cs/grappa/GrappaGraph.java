package edu.toronto.cs.grappa;


import java.util.*;
import java.awt.Color;
import java.io.*;

import att.grappa.*;

import edu.toronto.cs.util.*;

/***
 *** A builder pattern on top of Graphviz's Grappa
 ***/
public class GrappaGraph implements GrappaConstants
{
  // -- Grappa's representation for the graph -- this is what we are building
  Graph graph;
  
  public GrappaGraph ()
  {
    this ("no-name");
  }
  
  public GrappaGraph (String graphName)
  {
    graph = new Graph (graphName);
  }
  
  public String toString ()
  {
    StringWriter stringWriter = new StringWriter ();
    graph.printGraph (stringWriter);
    return stringWriter.toString ();
  }

  public Graph getGraph ()
  {
    return graph;
  }

  /*
    creates a new node in g and returns the node for attribute setting
  */
  public GrappaNode node ()
  {
    GrappaNode node = new GrappaNode ();
    return node;
  }
  


  /*** graph attributes ***/
  public GrappaGraph orientation (String s)
  {
    graph.setAttribute ("orientation", s);
    return this;
  }

  public GrappaGraph center (boolean v)
  {
    graph.setAttribute ("center", String.valueOf (v));
    return this;
  }
  
  public GrappaGraph size (String v)
  {
    graph.setAttribute ("size", v);
    return this;
  }
  
  public GrappaGraph editable (boolean v)
  {
    graph.setEditable (v);
    return this;
  }
  
  public GrappaGraph errorWriter (PrintWriter w)
  {
    graph.setErrorWriter (w);
    return this;
  }
  
  public GrappaGraph errorWriter (OutputStream v)
  {
    return errorWriter (new PrintWriter (v, true));
  }
  
  public class  GrappaNode 
  {
    // -- Grappa's representation of a node
    Node node;
    
    public GrappaNode ()
    {
      node = new Node (graph);
    }

   
    public void deleteNode ()
    {
      node.delete ();
    }
    
    
    public GrappaNode label (String name)
    {
      node.setAttribute ("label", name);
      return this;
    }
    
    public GrappaNode color (String value)
    {
      node.setAttribute ("color", value);
      return this;
    }
    public GrappaNode color (Color c)
    {
      return color (ColorUtil.toHexString (c));
    }

    public GrappaNode shape (String value)
    {
      node.setAttribute ("shape", value);
      return this;
    }

    
    public GrappaNode fontFamily (String family)
    {
      node.setAttribute ("fontname", family);
      return this;
    }
    public GrappaNode fontStyle (String style)
    {
      // XXX no way to set fontStyle
      return this;
    }
    public GrappaNode style (String style)
    {
      node.setAttribute ("style", style);
      return this;
    }
    public GrappaNode border (String type)
    {
      // XXX no way to set border use different shapes instead
      return this;
    }

    // -- creates a new edge
    public GrappaEdge edge (GrappaNode tail)
    {
      // -- by default dot seem to layout graphs from the bottom of the page
      // -- we, on the other hand, want the graphs to start at the top of
      // -- the page. For now, we simply trick it by reversing direction
      // -- of all the edges :)
      GrappaEdge edge = new GrappaEdge (tail, this);
      return edge;
    }

    public Node getNode ()
    {
      return node;
    }
  }

  
  public class GrappaEdge
  {
    Edge edge;

    public GrappaEdge (GrappaNode head, GrappaNode tail)
    {
      edge = new Edge (graph , tail.getNode (), head.getNode ());
    }
    
    public GrappaEdge color (String v)
    {
      edge.setAttribute ("color", v);
      return this;
    }

    public GrappaEdge color (Color c)
    {
      return color (ColorUtil.toHexString (c));
    }

    public GrappaEdge label (String v)
    {
      edge.setAttribute ("label", v);
      return this;
    }
    
    
    public GrappaEdge lineType (String v)
    {
      edge.setAttribute ("style", v);
      return this;
    }
  }

}
