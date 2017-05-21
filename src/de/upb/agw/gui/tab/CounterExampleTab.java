package de.upb.agw.gui.tab;

import java.awt.GridLayout;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaPanel;
import att.grappa.Parser;
import de.upb.agw.gui.project.CounterExampleGraphFile;
import de.upb.agw.gui.project.CounterExampleGraphs;
import de.upb.agw.gui.project.DotGraphFile;
import de.upb.agw.gui.project.DotGraphs;

public class CounterExampleTab extends JPanel implements Tab{
	
	private CounterExampleGraphs counterExampleGraphs;
	
	GrappaPanel gp;
	Graph graph = null;
	
	public CounterExampleTab(CounterExampleGraphs counterExampleGraphs){
		this.counterExampleGraphs = counterExampleGraphs;
		setLayout(new GridLayout(counterExampleGraphs.size(), 1));
		
		for( CounterExampleGraphFile counterExampleGraphFile : counterExampleGraphs){
			System.out.println(counterExampleGraphFile.toString());
			
			Graph graph = parseGraph(counterExampleGraphFile);	
			addGraphToPanel(graph);
		}
	}
	
	public CounterExampleGraphs getCounterExampleGraphs(){
		return counterExampleGraphs;
	}
	
	private Graph parseGraph(CounterExampleGraphFile counterExampleGraphFile){
		Parser program = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("dot " + counterExampleGraphFile.toString());		
			program = new Parser(p.getInputStream(),System.err);
			program.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return program.getGraph();
	}
	
	private void addGraphToPanel(Graph graph){
		JScrollPane scrollPane = new JScrollPane();
		
//		graph.setEditable(true);
//		graph.setMenuable(true);
		graph.setErrorWriter(new PrintWriter(System.err,true));
//		graph.printGraph(new PrintWriter(System.out));
		
		gp = new GrappaPanel(graph);
	    gp.addGrappaListener(new GrappaAdapter());
	    gp.setScaleToFit(true);

	    setVisible(true);
	    scrollPane.setViewportView(gp);
	    scrollPane.setVisible(true);
	    add(scrollPane);
	}
}