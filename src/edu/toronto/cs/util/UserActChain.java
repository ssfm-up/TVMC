package edu.toronto.cs.util;

import java.util.*;
import java.awt.event.*;

import javax.swing.event.*;
import javax.swing.*;


public interface UserActChain extends Action
{
  // builder pattern
  UserActChain add (UserActChain a);
  
  boolean confirm ();
  boolean confirm (UserAct stateinfo);
  
  boolean execute (UserAct stateinfo);

  void reset ();

  UserActChain get (int i);
  UserActChain get (int i, int l);
  
  int length ();
  int length (int i);
  
  int stage ();
  int stage (int i);
  
  // Determines whether the prompting at this stage will have an
  // effect on the state of the system whether or not the Act
  // succeeds. (eg, the save-ctl dialog -- the user probably expects
  // this to succeed whether or not they do indeed quit...)
  boolean immanent ();
}
