package de.upb.agw.gui.project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class CounterExampleGraphFile implements Serializable{
	
	private ArrayList<String> predicates;
	private ArrayList<String> edges;
	private ArrayList<ArrayList<String>> states;
	
	private String path;
	
	 public CounterExampleGraphFile(String parsedCounterExample) {
		 predicates = new ArrayList<String>();
		 edges = new ArrayList<String>();
		 states = new ArrayList<ArrayList<String>>();
		 
		 //remove the new line
		 parsedCounterExample = parsedCounterExample.substring(1);
		 
		 System.out.println(parsedCounterExample);
		 
		 //split the predicates from the counterexample
		 String p1[] = parsedCounterExample.split("\\["); 		 
		 String p2[] = p1[1].split("\\]");		 
		 String pred[] = p2[0].split(",");
		 for(int i = 0 ; i < pred.length ; i++ ){
			 predicates.add(pred[i].trim());
		 }		
		 
		 //split the edges from the counterexample
		 String p3[] = p2[1].split("--");
		 for(int i = 1 ; i < p3.length ; i++ ){
			 String e[] = p3[i].split("->");
			 edges.add(e[0]);
		 }
		 
		//split the states from the counterexample
		 String p4[] = p2[1].split("\\(");
		 for(int i = 1 ; i < p4.length ; i++ ){
			 String p5[] = p4[i].split("\\)");
			 String s[] = p5[0].split(","); 
			 ArrayList<String> tmp = new ArrayList<String>();
			 for( int j = 0 ; j < s.length ; j++ ){
				 tmp.add(s[j]);				 
			 }
			 states.add(tmp);				 
		 }
		 
		 generateGraph();
		 
	 }
	 
	 public void printElements(){
		 System.out.println("predicates:");
		 for(String p :  predicates){
			 System.out.println(p);
		 }
		 System.out.println("edges:");
		 for(String e :  edges){
			 System.out.println(e);
		 }
		 System.out.println("states:");
		 for(ArrayList<String> state :  states){
			 for(String p : state){
				 System.out.print(p + ", ");
			 }
			 System.out.println();
		 }
	 }
	 
	 private String generateGraph(){
		 
		 StringBuilder graph = new StringBuilder();
		 
		 graph.append("digraph CFG {\n");
		 graph.append("graph [rankdir=LR];\n");
		 
		 // states
		 for(int i = 0 ; i < states.size() ; i++ ){
			 
			 ArrayList<String> state = states.get(i);
			 StringBuilder label = new StringBuilder();
			 
			 for( int j = 0; j < state.size() ; j++ ){
				 label.append(predicates.get(j));
				 label.append(" = ");
				 label.append(state.get(j));
				 label.append("\\n");
			 }			 
			 
			 graph.append("node [ label = \"" + label.toString() + "\" ]; state_" + i + ";\n");
		 }
		 graph.append("start [style = filled, color=black, label=\"\", height=0.12,width=0.12,fontsize=1];\n");
		 
		 //edges
		 graph.append("start -> state_0[ style = \"bold\"];\n");
		 for(int i = 0 ; i < edges.size() ; i++ ){
			 graph.append("state_" + i + " -> state_" + (i+1) + " [ label = " + edges.get(i) + " ];\n");
		 }
		 
		 graph.append("label=\"grr\";\n");
		 
		 //end
		 graph.append("}");
		 
		 System.out.println(graph.toString());		 
		 
		 return graph.toString();
	 }
	 
	 public void storeCounterExampleGraph(String path, int index){
		 String file = path + "\\counterexample" + index + ".dot";
		 
		 //store the path
		 this.path = file.toString();
			System.err.println( "counterexample: Index " + index);
			// save all opened projects in a txt file
	    	try {
	    		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		        out.write(generateGraph());
		        out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
	 
	 public String toString(){
			return path;
	}
}