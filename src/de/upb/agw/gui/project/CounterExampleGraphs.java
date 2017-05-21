package de.upb.agw.gui.project;

import java.io.Serializable;
import java.util.ArrayList;

public class CounterExampleGraphs extends ArrayList<CounterExampleGraphFile> implements Serializable{
	
	private String projectName;
	
	public CounterExampleGraphs(String projectName) {
		this.projectName = projectName;
	}
	
	public String toString(){
		return "counter example - " + this.projectName;
	}
}
