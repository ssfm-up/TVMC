package edu.toronto.cs.mvset;

/**
 * Sequential composition of two relations
 *
 *
 * Created: Fri Jun 10 11:12:12 2005
 *
 * @author <a href="mailto:arie@eon.cs">Arie Gurfinkel</a>
 * @version 1.0
 */
public class SeqMvRelation implements MvRelation
{
  MvRelation first;
  MvRelation second;
  
  
  /**
   * Creates a new <code>SeqMvRelation</code> instance.
   *
   * @param _first a <code>MvRelation</code> value
   * @param _second a <code>MvRelation</code> value
   */
  public SeqMvRelation (MvRelation _first, MvRelation _second)
  {
    first = _first;
    second = _second;
  }

  public MvSet fwdImage (MvSet v)
  {
    return second.fwdImage (first.fwdImage (v));
  }
  
  public MvSet bwdImage (MvSet v)
  {
    return first.bwdImage (second.bwdImage (v));
  }
  
  public MvSet dualBwdImage (MvSet v)
  {
    return first.dualBwdImage (second.dualBwdImage (v));
  }
  
  public MvRelation getFirst ()
  {
    return first;
  }
  
  public MvRelation getSecond ()
  {
    return second;
  }
  

  public SeqMvRelation seqAfter (MvRelation v)
  {
    return new SeqMvRelation (this, v);
  }
  public SeqMvRelation seqBefore (MvRelation v)
  {
    return new SeqMvRelation (v, this);
  }

  public void setTrans (MvSet v)
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " does not support seetting of the transition relation");
    
  }

  public MvSet toMvSet ()
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " cannot be converted to an MvSet");

  }
  


  /**
   * return cube of pre-state variables
   *
   */
  public MvSet getPreVariablesCube ()
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " does not support this method");

  }


  /**
   * get cube of post-state variables
   *
   */
  public MvSet getPostVariablesCube ()
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " does not support this method");

  }



  /**
   * get map from pre- to post-state variables
   *
   */
  public int[] getPreToPostMap ()
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " does not support this method");
      
  }



  /**
   * get map from post- to pre-state variables
   *
   */
  public int[] getPostToPreMap ()
  {
    throw new UnsupportedOperationException 
      (this.getClass () + 
       " does not support this method");
      
  }
    


}
