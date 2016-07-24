package edu.toronto.cs.util;

import java.util.*;
import java.awt.event.*;


public class UserAct
{
  String name;
  int express = 0;
  ActionEvent e;
  Map data;

  
  public UserAct (String _name, int _express, ActionEvent _e)
  {
    name = _name;
    express = _express;
    e = _e;
    data = new HashMap ();
  }

  public UserAct (String _name, boolean _express, ActionEvent _e)
  {
    this (_name, _express ? 1 : 0, _e);
  }
  
  public ActionEvent getActionEvent ()
  {
    return e;
  }
  
  public String getName ()
  {
    return name;
  }
  
  public void setExpress ()
  {
    express++;
  }
  
  public int getExpress ()
  {
    return express;
  }

  public boolean isExpress ()
  {
    return express > 0;
  }

  public void setData (Object key, Object obj)
  {
    data.put (key, obj);
  }
  
  public Object getData (Object key)
  {
    return data.get (key);
  }
  
}
