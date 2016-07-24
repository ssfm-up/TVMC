package edu.toronto.cs.util;

import java.io.*;

public class MarshaledInputStream extends InputStream implements DataInput
{
  InputStream in;
  public MarshaledInputStream (InputStream _in)
  {
    in = _in;
  }

  public String readString () throws IOException
  {
    return readLine ();
  }
  
  
  // implementation of java.io.DataInput interface

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public String readLine() throws IOException
  {
    // -- reads a string written by writeByte
    // -- first short is the size;
    int size = readShort ();
    char[] data = new char [size];
    
    for (int i = 0; i < data.length; i++)
      data [i] = readChar ();
    return new String (data);
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public int readInt() throws IOException
  {
    int a = in.read ();
    int b = in.read ();
    int c = in.read ();
    int d = in.read ();
    
    return  (a << 24) | (b << 16) | (c << 8) | (d << 0);
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public byte readByte() throws IOException
  {
    return (byte)(in.read () & 0xFF);
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public short readShort() throws IOException
  {
    int a = read ();
    int b = read ();
    
    return (short)((a << 8) | (b << 0));
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public String readUTF() throws IOException
  {
    assert false :  "Not implemented";
    return null;
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public char readChar() throws IOException
  {

    return (char)(read () & 0xFF);
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public float readFloat() throws IOException
  {
    assert false : "Not implemented";
    return 0f;
  }

  /**
   *
   * @param param1 <description>
   * @exception java.io.IOException <description>
   */
  public void readFully(byte[] param1) throws IOException
  {
    assert false : "Not implemented";
  }

  /**
   *
   * @param param1 <description>
   * @param param2 <description>
   * @param param3 <description>
   * @exception java.io.IOException <description>
   */
  public void readFully(byte[] param1, int param2, int param3) 
    throws IOException
  {
    assert false : "Not implemented";
  }

  /**
   *
   * @param param1 <description>
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public int skipBytes(int n) throws IOException
  {
    return (int)in.skip (n);
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public boolean readBoolean() throws IOException
  {
    int b = read ();
    return b == 1 ? true : false;
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public int readUnsignedByte() throws IOException
  {
    int b = read ();
    return b;
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public int readUnsignedShort() throws IOException
  {
    int a = read ();
    int b = read ();
    return (int)(((a & 0xFF) << 8) | ((b & 0xFF) << 0));
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public long readLong() throws IOException
  {
    assert false : "Not implemented";
    return 0L;
  }

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public double readDouble() throws IOException
  {
    assert false : "Not implemented";
    return 0.0;
  }

  public byte[] readByteArray () throws IOException
  {
    int size = readShort ();
    byte[] data = new byte [size];
    for (int i = 0; i < data.length; i++)
      data [i] = (byte)readByte ();
    return data;
  }
  public int[] readIntArray () throws IOException
  {
    return readIntArrayFixed (readShort ());
  }
  
  public int[] readIntArrayFixed (int size) throws IOException
  {
    int[] data = new int [size];
    for (int i = 0; i < data.length; i++)
      data [i] = readInt ();
    return data;
  }
  
  


  // implementation of java.io.InputStream interface

  /**
   *
   * @return <description>
   * @exception java.io.IOException <description>
   */
  public int read() throws IOException
  {
    return in.read ();
  }

  public int available () throws IOException
  {
    return in.available ();
  }
  
  public void close () throws IOException
  {
    in.close ();
  }
  
  public void mark (int v)
  {
    in.mark (v);
  }
  public boolean markSupported ()
  {
    return in.markSupported ();
  }
  
  public int read (byte[] b) throws IOException
  {
    return in.read (b);
  }
  public int read (byte[] b, int off, int len) throws IOException
  {
    return in.read (b, off, len);
  }
  public void reset () throws IOException
  {
    in.reset ();
  }
  public long skip (long n) throws IOException
  {
    return in.skip (n);
  }
  
    


}
