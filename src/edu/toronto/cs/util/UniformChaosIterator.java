package edu.toronto.cs.util;

public class UniformChaosIterator implements ChaosIterator
{
  int[] domain;
  
  public UniformChaosIterator (int[] _domain)
  {
    domain = _domain;
    assert domain.length > 0;
  }

  public int nextInt ()
  {
    return domain [Chaos.inRange (0, domain.length)];
  }
  
  
}
