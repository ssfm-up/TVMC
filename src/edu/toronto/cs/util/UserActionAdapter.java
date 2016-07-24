package edu.toronto.cs.util;

import java.util.*;

import java.awt.event.*;
import javax.swing.*;

public class UserActionAdapter extends UserActElement
{
  Action act;
  boolean imm;
  boolean wrapper;
  

  public UserActionAdapter (Action a, boolean _imm, boolean autor)
  {
    super (""+a.getValue (Action.SHORT_DESCRIPTION), _imm, autor);
    if (a instanceof UserActChain)
      wrapper = true;
    else
      wrapper = false;
    act = a;
    imm = _imm;
  }
  
  public boolean doConfirm (UserAct stateinfo)
  {
    if (wrapper)
      return ((UserActChain)act).confirm (stateinfo);
    if (imm)
      act.actionPerformed (stateinfo.getActionEvent ());
    return true;
  }
  
  public boolean doExecute (UserAct stateinfo)
  {
    if (wrapper)
      return ((UserActChain)act).execute (stateinfo);
    if (!imm)
      act.actionPerformed (stateinfo.getActionEvent ());
    return true;
  }
}

  
  
