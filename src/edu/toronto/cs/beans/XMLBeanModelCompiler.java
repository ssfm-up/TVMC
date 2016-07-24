package edu.toronto.cs.beans;

import java.io.*;
import java.beans.*;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.util.*;




public class XMLBeanModelCompiler implements ModelCompiler
{
  File xmlFile;

  public XMLBeanModelCompiler ()
  {
  }
  
  public void setXmlFile (File v)
  {
    xmlFile = v;
  }
  public File getXmlFile ()
  {
    return xmlFile;
  }
  

  public XKripkeStructure compile ()
  {
    try {
      XMLDecoder decoder = new XMLDecoder 
	(new BufferedInputStream 
	 (new FileInputStream (xmlFile)));
	  
      ModelCompiler compiler = (ModelCompiler)decoder.readObject ();
      decoder.close ();
      return compiler.compile ();
    }
    catch (Exception ex) {
      assert false : ex;
    }
    return null;
  }
  
}
