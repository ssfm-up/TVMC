package de.upb.agw.gui.project;

import java.io.Serializable;

public class DotGraphFile implements Serializable{
	
	String path;
	
	public DotGraphFile(String path){
		this.path = path;
	}
	
	public String toString(){
		return path;
	}
}
