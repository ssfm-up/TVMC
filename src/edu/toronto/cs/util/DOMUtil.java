package edu.toronto.cs.util;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import java.net.URL;

public class DOMUtil
{

  public static Document parse (InputStream uri) 
    throws IOException, ParserConfigurationException, SAXException
  {
    DocumentBuilder builder = getDocumentBuilder ();
    builder.setErrorHandler (new TrivialErrorHandler (true));
    try 
      {
	return builder.parse (uri);
      } 
    catch (SAXParseException ex)
      {
	return null;
      }  
  }

  public static Document parse (String uri) 
    throws IOException, ParserConfigurationException, SAXException
  {
    DocumentBuilder builder = getDocumentBuilder ();
    builder.setErrorHandler (new TrivialErrorHandler (true));
    try 
      {
	return builder.parse (uri);
      } 
    catch (SAXParseException ex)
      {
	return null;
      }  
  }
  
  public static DocumentBuilder getDocumentBuilder () 
    throws ParserConfigurationException
  {
    // -- get a document builder
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
    // -- set some common parameters
    factory.setIgnoringComments (true);
    factory.setNamespaceAware (true);
    factory.setIgnoringElementContentWhitespace (true);
    return factory.newDocumentBuilder ();
  }


  public static void main (String[] args) throws Exception
  {
    Document document = parse (args [0]);
  }
  

  public static class TrivialErrorHandler implements ErrorHandler
  {
    static final int ERROR = 1;
    static final int FATAL_ERROR = 2;
    static final int WARNING = 3;
    
    boolean warn = false;
    
    public TrivialErrorHandler ()
    {
      this (false);
    }
    
    public TrivialErrorHandler (boolean _warn)
    {
      warn = _warn;
    }
    
    public void error (SAXParseException ex)
    {
      handleError (ERROR, ex);
    }
    public void fatalError (SAXParseException ex)
    {
      handleError (FATAL_ERROR, ex);
    }
    public void warning (SAXParseException ex)
    {
      if (warn)
	handleError (WARNING, ex);
    }
    
    /***
     *** Prints an error message of the form
     *** FileName:LineNo:ColNo:Error text
     ***/
    private void handleError (int type, SAXParseException ex)
    {
      StringBuffer sb = new StringBuffer ();
      sb.append (extractFileName (ex.getSystemId ()));
      sb.append (':');

      sb.append (ex.getLineNumber ());
      sb.append (':');
      
      if (ex.getColumnNumber () >= 0)
	{
	  sb.append (ex.getColumnNumber ());
	  sb.append (':');
	}

      if (type == FATAL_ERROR || type == ERROR)
	sb.append ("Error: ");
      else
	sb.append ("Warning: ");

      sb.append (ex.getMessage ());
      System.out.println (sb);
    }
    private String extractFileName (String systemId)
    {
      if (systemId == null) return StringUtil.EmptyString;
      
      try 
	{
	  URL url = new URL (systemId);

	  String fileName = url.getFile ();
	  if (fileName != null) return fileName;
	}
      catch (Exception ex)
	{
	  // XXX log exception here
	  ex.printStackTrace ();
	}
      // -- if could not understand it just dump it as is
      return systemId;
    }
    
  }
  
}
