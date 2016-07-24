package edu.toronto.cs.mdd;

/*** This class is used to package a point, mostly needed for false points */
public class MDDPoint
{
  MDDNode node;
  int value;
  
  public MDDPoint (MDDNode _node, int _val)
  {
    node = _node;
    value = _val;
  }

  public MDDNode getNode ()
  {
    return node;
  }
  
  public int getValue ()
  {
    return value;
  }
  
  public void setNode (MDDNode v)
  {
    node = v;
  }
  public void setValue (int v)
  {
    value = v;
  }
  
}
