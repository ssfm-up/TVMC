package edu.toronto.cs.util;

import java.io.*;

/**
 * Reads text from a Reader, copying characters read to a Writer.
 * <p>
 * Inspired by the Unix tee command. 
 */

public class TeeBufferedReader extends BufferedReader
{
  // a fileWriter to be used
  Writer copy;

  // a prefix to append to a begining of every new line
  String prefix;

  // true if the next write to copy should be prepended with prefix, false
  // otherwise
  boolean writePrefix;

  // true if each write to copy should be flushed, false otherwise
  boolean flushWrites;

  // buffers the output to copy
  StringBuffer outputBuffer;

  /*
   * Takes a file name of a file to write to and an InputStream 
   * from which to read 
   */  
  // XXX what is this for?

  /**
   * Creates a new <code>TeeBufferedReader</code> instance specifying 
  * source and logFile.
   *
   * @param source a <code>Reader</code> with the information to be logged
   * @param copy a <code>Writer</code> which creates a log file
   */

  public TeeBufferedReader(Reader source, Writer copy)
  {
    super(source);
    init(copy, "");
  }


  /**
   * Creates a new <code>TeeBufferedReader</code> instance specifying
  *  a source only
   *
   * @param source a <code>Reader</code> with the information to be logged
   */

  public TeeBufferedReader (Reader source)
  {
    super (source);
    throw new UnsupportedOperationException ();
  }


  /**
   * Creates a new <code>TeeBufferedReader</code> instance.
   *
   * @param source a <code>Reader</code> with the information to be logged
   * @param x an <code>int</code> 
   */

  public TeeBufferedReader (Reader source, int sz)
  {
    super (source, sz);
    throw new UnsupportedOperationException ();
  }

  /**
   * Constructs a TeeBufferedReader with the specified Reader, 
  * Writer and prefix. 
   *
   * @param source a <code>Reader</code> with the information to be logged
   * @param copy a <code>Writer</code> which creates a log of all information.
   * @param prefix a <code>String</code> that should be added before each 
  *        line of CVC output
   */

  public TeeBufferedReader(Reader source, Writer copy, String prefix)
  {
    super (source);
    init (copy, prefix);
  }

  /*
   * Constructs a TeeBufferedReader with the specified Reader, Writer, 
  * prefix and the append flag. 
   */  

  private void init (Writer copy, String prefix)
  {
    this.copy = copy;
    this.prefix = prefix;
  }

  /**
   * Appends a given character to the outputBuffer. If the character 
  * is at the end of the line, adds the prefix to the line, and prints
  * the whole line to the logFile 
   *
   * @param c an <code>int</code> a character to be appended
   * @exception IOException if an error occurs
   */

  private void write (int c) throws IOException
  {
    // don't do buffering here; if the caller wants buffering, pass in a
    // buffered Writer to the constructor

    // if copy == null, then do not write to the file
    if ( copy == null) return;
    
    if (writePrefix)
    {
      copy.write (prefix);
      writePrefix = false;
    }

    copy.write (c);

    if ((char) c == '\n')
      writePrefix = true;
  }

  /** 
   * Makes it so that the next character read is copied out with the prefix
   * prepended.
   */
  public void prefixOn ()
  {
    writePrefix = true;
  }

  /**
   * reads a character from the Reader and sends it to be written 
  * to the logFile
   *
   * @return an <code>int</code> a character that was read
   * @exception IOException if an error occurs
   */

  public int read() throws IOException
  {
    int c = super.read();
    this.write(c);
    return c;
  }


  /**
   * reads a line from reader and writes it to the logFile.
   *
   * @return a <code>String</code> line that was read
   * @exception IOException if an error occurs
   */

  public String readLine() throws IOException
  {
    String line = "";
    char c = (char)this.read();
    while(c != '\n'){
      line += c;
      c = (char)this.read();
    }
    return line;
  }

  /**
   * Writes the content of outputBuffer to the logFile and closes the 
   * underlying reader and writer. 
   *
   * @exception IOException if an I/O error occurs
   */

  public void close() throws IOException
  {
    copy.close ();
    super.close ();
  }

  public static void main(String[] args) throws Exception
  {

    Writer w = new FileWriter("logFile");
    Reader r = new FileReader("inputFile");

    TeeBufferedReader logger = new TeeBufferedReader(r, w);

    System.out.println("***");

    while (logger.ready ())
    {
      System.out.println("--");
      System.out.print((char)(logger.read()));
    }
    w.close();
    r.close();
  }
}
