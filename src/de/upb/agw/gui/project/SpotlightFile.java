package de.upb.agw.gui.project;

import java.io.File;


public class SpotlightFile extends File{

	private String name;
	private SpotlightProject project;	
	
	public SpotlightFile(String path, String name, SpotlightProject project){
		super(path);
		this.name=name;
		this.project = project;
	}
	
	public String toString(){
		return name + " [" + this.getAbsolutePath() + "]";
	}
	
	public String toTabString(){
		return name + " [" + project + "]";
	}
}