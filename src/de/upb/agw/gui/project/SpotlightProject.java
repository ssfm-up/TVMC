package de.upb.agw.gui.project;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class SpotlightProject implements Serializable{
	
	private String projectName;
	
	private SpotlightFile cFile;
	private SpotlightFile ctlFile;
	private SpotlightFile initFile;
	
	private DotGraphs graphs;
	private CounterExampleGraphs counterExampleGraphs;
	
	public SpotlightProject(String projectName, File cFile, File ctlFile, File initFile){
		this.projectName = projectName;
		this.cFile = new SpotlightFile(cFile.getAbsolutePath(), "c-code", this);
		this.ctlFile = new SpotlightFile(ctlFile.getAbsolutePath(), "ctl-code", this);
		this.initFile = new SpotlightFile(initFile.getAbsolutePath(), "init", this);
		this.graphs = new DotGraphs(projectName);
		this.counterExampleGraphs = new CounterExampleGraphs(projectName);
	}
	
	public  String toString(){
		return projectName;		
	}
	
	public SpotlightFile getcFile(){
		return cFile;
	}
	
	public SpotlightFile getctlFile(){
		return ctlFile;
	}
	
	public SpotlightFile getinitFile(){
		return initFile;
	}
	
	public String getPath(){
		
		String s = cFile.getAbsolutePath();
		int index = s.lastIndexOf("\\");
		s = s.substring(0, index+1);
		
		return s;
	}
	
	public void addDotGraph(String file){
		graphs.add(new DotGraphFile(file));		
	}
	
	public DotGraphs getDotGraphs(){
		return graphs;
	}

	public void removeDotGraphs() {
		while(graphs.size() > 0){
			graphs.remove(0);
		}		
	}
	
	public void addCounterExampleGraph(CounterExampleGraphFile counterExampleGraph){
		counterExampleGraphs.add(counterExampleGraph);
	}
	
	public CounterExampleGraphs getCounterExampleGraph(){
		return counterExampleGraphs;
	}
	
	public void removeCounterExampleGraphs() {
		while(counterExampleGraphs.size() > 0){
			counterExampleGraphs.remove(0);
		}	
	}
}