package edu.toronto.cs.util;


// Prime numbers used for hashing
public class Primes
{
  public static long[] primes = {12582917L, 4256249L, 741457L, 1618033999L};

  public static long getPrime (int i)
  {
    return primes [i % primes.length];
  }
  
}
