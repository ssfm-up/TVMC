package de.upb.agw.gui.project;

import java.io.Serializable;
import java.util.ArrayList;

public class DotGraphs extends ArrayList<DotGraphFile> implements Serializable{
	
	private String projectName;
	
	public DotGraphs(String projectName) {
		this.projectName = projectName;
	}
	
	public String toString(){
		return "dot graphs - " + this.projectName;
	}
}
