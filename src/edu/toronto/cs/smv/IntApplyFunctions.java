package edu.toronto.cs.smv;

import edu.toronto.cs.mdd.*;
import edu.toronto.cs.mdd.ApplyFunctions.*;


public class IntApplyFunctions
{

  
  public static class PlusFunction extends TerminalBinApplyFunction
  {
    public PlusFunction (MDDManager mddMgr)
    {
      super (mddMgr);
    }
    
    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      MDDNode answer = super.binApply (node1, node2);
      if (answer != null) return answer;
      
      if (node1.isConstant () && node1.getValue () == 0) return node2;
      if (node2.isConstant () && node2.getValue () == 0) return node1;

      return answer;
    }
    
    public boolean isSymetric ()
    {
      return true;
    }
    public int binApply (int v1, int v2)
    {
      return v1 + v2;
    }
    
  }

  public static class LtFunction extends TerminalBinApplyFunction
  {
    int top;
    int bot;
    
    public LtFunction (MDDManager mddMgr, int _top, int _bot)
    {
      super (mddMgr);
      top = _top;
      bot = _bot;
    }
    
    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      MDDNode answer = super.binApply (node1, node2);
      if (answer != null) return answer;

      return answer;
    }
    
    public boolean isSymetric ()
    {
      return false;
    }
    public int binApply (int v1, int v2)
    {
      return (v1 < v2) ? top : bot;
    }
    
  }

  
}
