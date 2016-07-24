package edu.toronto.cs.util;

import java.util.*;

import java.awt.event.*;

public abstract class FinalUserActElement extends UserActElement
{
  public FinalUserActElement (String name)
  {
    super (name);
  }

  public FinalUserActElement (boolean imm, boolean autoreset)
  {
    super (imm, autoreset);
  }
   
  public FinalUserActElement ()
  {
    super ();
  }
  
  public UserActChain add (UserActChain a)
  {
    return a.add (this);
  }
}
