package edu.toronto.cs.proof;


public interface StateNameGenerator 
{
  
  // required to be implemented
  public String getFreshName();
  
  // optional: allow for reuse of names
  public void returnName(String s);
  
}
