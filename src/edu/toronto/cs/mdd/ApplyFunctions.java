package edu.toronto.cs.mdd;

import edu.toronto.cs.algebra.*;


public class ApplyFunctions
{
  public static interface ApplyFunction
  {
    public boolean isCacheable ();
  }

  public static interface BinApplyFunction extends ApplyFunction
  {
    public MDDNode binApply (MDDNode node1, MDDNode node2);
    public boolean isSymetric ();
  }

  public static interface UnaryApplyFunction extends ApplyFunction
  {
    public MDDNode unaryApply (MDDNode node);
  }

  public static interface QuantifyFunction extends ApplyFunction
  {
    public MDDNode getIdentity (int var);
    public BinApplyFunction getOperator (int var);
    public boolean canTerminate (int var, MDDNode node);
  }

  public static abstract class AbstractApplyFunction implements ApplyFunction
  {
    public boolean isCacheable ()
    {
      return true;
    }
  }  

  public static abstract class TerminalBinApplyFunction 
    extends AbstractApplyFunction implements BinApplyFunction
  {
    MDDManager mddMgr;
    public TerminalBinApplyFunction (MDDManager _mddMgr)
    {
      mddMgr = _mddMgr;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      if (node1.isConstant () && node2.isConstant ())
	return mddMgr.getLeafNode (binApply (node1.getValue (), 
					     node2.getValue ()));

      return null;
    }
    public abstract boolean isSymetric ();
    public abstract int binApply (int v1, int v2);
  }


  public static abstract class AlgebraFunction 
    extends TerminalBinApplyFunction
  {
    IAlgebra algebra;
    MDDLeafNode top;
    MDDLeafNode bot;

    public AlgebraFunction (MDDManager mddMgr, IAlgebra _algebra)
    {
      super (mddMgr);
      algebra = _algebra;
      top = mddMgr.getLeafNode (algebra.top ().getId ());
      bot = mddMgr.getLeafNode (algebra.bot ().getId ());
    }

  }

  public static abstract class BelnapFunction
    extends AbstractApplyFunction implements BinApplyFunction
  {
    BelnapAlgebra algebra;
    MDDLeafNode infoTop;
    MDDLeafNode infoBot;
    public BelnapFunction (MDDManager mddMgr, BelnapAlgebra _algebra)
    {
      algebra = _algebra;
      infoTop = mddMgr.getLeafNode (algebra.infoTop ().getId ());
      infoBot = mddMgr.getLeafNode (algebra.infoBot ().getId ());
    }  

    public boolean isSymetric ()
    {
      return true;
    }
    
  }

  public static class InfoMeetFunction extends BelnapFunction
  {
    public InfoMeetFunction (MDDManager mddMgr, BelnapAlgebra algebra)
    {
      super (mddMgr, algebra);
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      if (node1 == node2) return node1;
      if (node1 == infoTop) return node2;
      if (node2 == infoTop) return node1;
      if (node1.isConstant () && node2.isConstant ())
	return infoBot;
      return null;
    }
  }

  public static class InfoJoinFunction extends BelnapFunction
  {
    public InfoJoinFunction (MDDManager mddMgr, BelnapAlgebra algebra)
    {
      super (mddMgr, algebra);
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      if (node1 == node2) return node1;
      if (node1 == infoBot) return node2;
      if (node2 == infoBot) return node1;
      if (node1.isConstant () && node2.isConstant ())
	return infoTop;
      return null;
    }
  }
  
  
  

  public static class MeetFunction extends AlgebraFunction
  {
    public MeetFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
    }
    public boolean isSymetric ()
    {
      return true;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      if (node1 == node2) return node1;
      if (node1 == top) return node2;
      else if (node2 == top) return node1;
      else if (node1 == bot || node2 == bot) return bot;

      return super.binApply (node1, node2);
    }
    public int binApply (int v1, int v2)
    {
      return algebra.getValue (v1).meet (algebra.getValue (v2)).getId ();
    }
  }

  public static class JoinFunction extends AlgebraFunction
  {
    public JoinFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
    }
    public boolean isSymetric ()
    {
      return true;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      
      if (node1 == node2) return node1;
      if (node1 == bot) return node2;
      else if (node2 == bot) return node1;
      else if (node1 == top || node2 == top) return top;

      return super.binApply (node1, node2);
    }
    public int binApply (int v1, int v2)
    {
      return algebra.getValue (v1).join (algebra.getValue (v2)).getId ();
    }
  }

  public static class ImpliesFunction extends AlgebraFunction
  {
    public ImpliesFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
    }
    public boolean isSymetric ()
    {
      return false;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {

      if (node2 == top) return top;
      else if (node1 == bot) return top;
      else if (node1 == top) return node2;

      return super.binApply (node1, node2);

    }
    public int binApply (int v1, int v2)
    {
      return algebra.getValue (v1).impl (algebra.getValue (v2)).getId ();
    }
  }


  public static class EqualsFunction extends AlgebraFunction
  {
    MDDNode top;
    MDDNode bot;
    
    public EqualsFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
      top = mddMgr.getLeafNode (algebra.top ().getId ());
      bot = mddMgr.getLeafNode (algebra.bot ().getId ());
    }
    public boolean isSymetric ()
    {
      return true;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      if (node1 == node2) return top;

      if (node1.isConstant () && node2.isConstant ())
	return node1 == node2 ? top : bot;
      
      return null;
    }
    // XXX remove
    public int binApply (int v1, int v2)
    {
      return 5;
    }
  }

  public static class BelowFunction extends AlgebraFunction
  {
    public BelowFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
    }
    public boolean isSymetric ()
    {
      return false;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      MDDNode answer = super.binApply (node1, node2);
      if (answer != null) return answer;
      
      if (node1 == node2) return top;
      if (node1 == bot) return top;
      else if (node2 == top) return top;

      return answer;
    }
    public int binApply (int v1, int v2)
    {
      return algebra.getValue (v1).leq (algebra.getValue (v2)).getId ();
    }
  }

  public static class AboveFunction extends AlgebraFunction
  {
    public AboveFunction (MDDManager mddMgr, IAlgebra algebra)
    {
      super (mddMgr, algebra);
    }
    public boolean isSymetric ()
    {
      return false;
    }

    public MDDNode binApply (MDDNode node1, MDDNode node2)
    {
      MDDNode answer = super.binApply (node1, node2);
      if (answer != null) return answer;
      
      if (node1 == node2) return top;
      if (node1 == top) return top;
      else if (node2 == bot) return top;

      return answer;
    }
    public int binApply (int v1, int v2)
    {
      return algebra.getValue (v1).geq (algebra.getValue (v2)).getId ();
    }
  }


  public static class NegFunction 
    extends AbstractApplyFunction implements UnaryApplyFunction
  {
    MDDManager mddMgr;
    IAlgebra algebra;
    
    MDDNode top;
    MDDNode bot;

    public NegFunction (MDDManager _mddMgr, IAlgebra _algebra)
    {
      mddMgr = _mddMgr;
      algebra = _algebra;

      top = mddMgr.getLeafNode (algebra.top ().getId ());
      bot = mddMgr.getLeafNode (algebra.bot ().getId ());
    }

    public MDDNode unaryApply (MDDNode node)
    {
      if (!node.isConstant ()) return null;

      if (node == top) return bot;
      else if (node == bot) return top;
      else
	return mddMgr.getLeafNode 
	  (algebra.getValue (node.getValue ()).neg ().getId ());
      
    }
  }


  public static class UniformQuantify 
    extends AbstractApplyFunction implements QuantifyFunction
  {

    MDDNode identity;
    BinApplyFunction operator;
    MDDNode terminalNode;

    public UniformQuantify (MDDNode _identity, BinApplyFunction _operator, 
			    MDDNode _terminalNode)
    {
      identity = _identity;
      operator = _operator;
      terminalNode = _terminalNode;
    }

    public MDDNode getIdentity (int var)
    {
      return identity;
    }
    public BinApplyFunction getOperator (int var)
    {
      return operator;
    }
    public boolean canTerminate (int var, MDDNode node)
    {
      return node == terminalNode;
    }
    public boolean isCacheable ()
    { return true; }
  }




}
