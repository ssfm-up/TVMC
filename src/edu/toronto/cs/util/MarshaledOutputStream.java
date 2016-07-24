package edu.toronto.cs.util;

import java.io.*;

public class MarshaledOutputStream extends OutputStream implements DataOutput
{
  
  OutputStream out;
  
  public MarshaledOutputStream (OutputStream _out) 
  {
    out = _out;
  }

  // implementation of java.io.DataOutput interface

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeInt(int v) throws IOException
  {
    // -- an integer is 4 bytes
    writeByte ((v >>> 24) & 0xFF);
    writeByte ((v >>> 16) & 0xFF);
    writeByte ((v >>> 8)  & 0xFF);
    writeByte ((v >>> 0)  & 0xFF);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeUTF(String param1) throws IOException
  {
    assert false : "Not implemented";
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeByte(int b) throws IOException
  {
    write (b);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeShort(int v) throws IOException
  {
    // -- short is 2 bytes
    writeByte ((v >>> 8) & 0xFF);
    writeByte ((v >>> 0) & 0xFF);
  }

  public void writeString (String s) throws IOException
  {
    writeBytes (s);
  }
  
  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeBytes(String s) throws IOException
  {
    // -- string is written as size_of_string char_array
    // -- where size_of_string is short
    writeShort ((short)s.length ());
    char[] data = s.toCharArray ();
    for (int i = 0; i < data.length; i++)
      writeChar ((int)data [i]);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeChar(int v) throws IOException
  {
    // -- char is one byte
    writeByte (v & 0xFF);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeFloat(float param1) throws IOException
  {
    assert false : "Not implemented";
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeBoolean(boolean b) throws IOException
  {
    // -- boolean is one byte, '1' is true, '0' is false
    writeByte (b ? 1 : 0);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeLong(long v) throws IOException
  {
    // -- long is 8 bytes
    writeByte ((int)(v >>> 56) & 0xFF);
    writeByte ((int)(v >>> 48) & 0xFF);
    writeByte ((int)(v >>> 40) & 0xFF);
    writeByte ((int)(v >>> 32) & 0xFF);
    writeByte ((int)(v >>> 24) & 0xFF);
    writeByte ((int)(v >>> 16) & 0xFF);
    writeByte ((int)(v >>>  8) & 0xFF);
    writeByte ((int)(v >>>  0) & 0xFF);
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeDouble(double param1) throws IOException
  {
    assert false : "Not implemented";
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void writeChars(String s) throws IOException
  {
    assert false : "Not implemented";
  }


  public void writeByteArray (byte[] data) throws IOException
  {
    writeShort ((short)data.length);
    for (int i = 0; i < data.length; i++)
      writeByte (data [i]);
  }

  // -- writes an integer array with its size
  public void writeIntArray (int[] data) throws IOException
  {
    writeShort ((short)data.length);
    writeIntArrayFixed (data);
  }
  // -- writes an integer array of a fixed size
  public void writeIntArrayFixed (int[] data) throws IOException
  {
    for (int i = 0; i < data.length; i++)
      writeInt (data [i]);
  }
  
  
  public void writeCharArrayFixed (char[] data) throws IOException
  {
    for (int i = 0; i < data.length; i++)
      writeChar (data [i]);
  }
  



  

  /**** OutputStream methods ***/
  public void flush () throws IOException
  {
    out.flush ();
  }
  public void close () throws IOException
  {
    out.close ();
  }
  
  public void write (int b) throws IOException
  {
    out.write (b);
  }
  public void write (byte[] data) throws IOException
  {
    out.write (data);
  }
  public void write (byte[] data, int off, int len) throws IOException
  {
    out.write (data, off, len);
  }
  
}
