package edu.toronto.cs.util;

import java.util.*;

import java.awt.event.*;
import javax.swing.*;

public abstract class UserActElement extends AbstractAction implements UserActChain
{
  //  boolean expressness = false;
  boolean immanence = false;
  boolean passed = false;
  boolean autoreset = true;

  String name;

  UserActChain next = null;

  public UserActElement (String _name)
  {
    super (_name);
    name = _name;
  }

  public UserActElement (String _name, boolean imm, boolean autor)
  {
    this (_name);
    immanence = imm;
    autoreset = autor;
  }

  public UserActElement (boolean imm, boolean autor)
  {
    this ("", imm, autor);
  }

  public UserActElement ()
  {
    this ("");
  }
  
  public UserActChain add (UserActChain a)
  {
    if (next != null)
      next = next.add (a);
    else
      next = a;
    return this;
  }

  public void reset ()
  {
    passed = false;
    if (next != null)
      next.reset ();
  }
  
  public boolean confirm (UserAct stateinfo)
  {
    UserAct act = (UserAct) stateinfo;
    if (!passed)
      passed = doConfirm (act);
    if (passed)
      {
	if (immanence)
	  doExecute (stateinfo);
	passed = passed && (!autoreset);
	return ((next == null) || next.confirm (act));
      }
    return false;
  }

  public boolean confirm ()
  {
    return confirm (new UserAct ("", false, null));
  }

  public abstract boolean doConfirm (UserAct stateinfo);

  public boolean execute (UserAct stateinfo)
  {
    if (immanence)
      if (next == null)
	return true;
      else
	return next.execute (stateinfo);
    
    if (!doExecute (stateinfo))
      return false;
    
    if (next != null)
      return next.execute (stateinfo);
    
    return true;
  }

  public boolean doExecute (UserAct stateinfo)
  {
    return true;
  }
  
  public UserActChain get (int i)
  {
    return get (i, 0);
  }
  
  public UserActChain get (int i, int l)
  {
    if (i == l || next == null)
      return this;
    return next.get (i, l+1);
  }
  
  public int length ()
  {
    return length (0);
  }
  
  public int length (int i)
  {
    if (next != null)
      return next.length (i+1);
    return i+1;
  }
  
  public int stage ()
  {
    return stage (0);
  }
  
  public int stage (int i)
  {
    if (!passed)
      return i; 
    i++;
    if (next != null)
      i = next.stage (i);
    return i;
  }
  
  // Determines whether the prompting at this stage will have an
  // effect on the state of the system whether or not the Act
  // succeeds.
  public boolean immanent ()
  {
    return immanence;
  }

  public String getName ()
  {
    return name;
  }

  public void actionPerformed (ActionEvent e)
  {
    UserAct u = new UserAct (name, false, e);
    if (confirm (u))
      {
	if (!execute (u))
	  errorHandle (u);
      }
    else
      errorHandle (u);
    
  }

  protected void errorHandle (UserAct u)
  {
  }
  


}
