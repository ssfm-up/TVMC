package de.upb.agw.gui.tab;

import java.awt.GridLayout;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaPanel;
import att.grappa.Parser;
import de.upb.agw.gui.project.DotGraphFile;
import de.upb.agw.gui.project.DotGraphs;

public class DotGraphsTab extends JPanel implements Tab{
	
	private DotGraphs dotGraphs;
	
	GrappaPanel gp;
	Graph graph = null;
	
	public DotGraphsTab(DotGraphs dotGraphs){
		this.dotGraphs = dotGraphs;
		setLayout(new GridLayout(1,dotGraphs.size()));
		
		for( DotGraphFile dotGraphFile : dotGraphs){
			System.out.println(dotGraphFile.toString());
			
			Graph graph = parseGraph(dotGraphFile);			
			addGraphToPanel(graph);
		}
	}
	
	public DotGraphs getDotGraphs(){
		return dotGraphs;
	}
	
	private Graph parseGraph(DotGraphFile dotGraphFile){
		Parser program = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("dot " + dotGraphFile);		
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