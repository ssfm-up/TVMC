package edu.toronto.cs.util;

import java.util.Random;

public class Chaos 
{

  static Random r = new Random();
  public static void seed(int s) {
    r = new Random(s);
	
  }

  public static int nextUpTo(int n) {
    return Math.round(r.nextFloat() * (float)(n-1));
  }

  public static int inRange(int lo, int hi) 
  {
    assert lo < hi;
    return lo + nextUpTo(hi-lo);
  }
  public static boolean nextThreshold(float theta) {
    float q = r.nextFloat();
    //	System.out.println("r="+q+", theta="+theta);
    return (r.nextFloat() < theta);
  }

  public static double nextDouble ()
  {
    return r.nextDouble ();
  }
  
}
