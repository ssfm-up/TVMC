package edu.toronto.cs.util;

import java.io.*;

/****
 **** A utiltility class for marshaling things 
 ****/
public class MarshalUtil
{

  public void marshalByte (byte b, OutputStream out) throws IOException
  {
    out.write (b);
  }
  
  
  
  
}
