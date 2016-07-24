package de.upb.agw.gui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


public class SpotlightFileFilter extends FileFilter {

	private String extension;
	
	public SpotlightFileFilter(){
		extension = "c";
	}	
	
	//Accept all directories and the extension file.
    public boolean accept(File file) {
    	
    	if (file.isDirectory()) {
            return true;
        }

    	String extension = getExtensionOfFile(file);
        if (extension != null) {
        	return getExtensionOfFile(file).equals(this.extension);
        }
        
        return false;
    }
    
    public static String getExtensionOfFile(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    //The description of this filter
    public String getDescription() {
        return "Just " + this.extension + " files";
    }
    
    public void setExtension(String extension){
    	this.extension = extension;
    }
}
