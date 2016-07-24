package de.upb.agw.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.upb.agw.gui.component.EditorView;
import de.upb.agw.gui.component.ProjectExplorerView;
import de.upb.agw.gui.component.ToolBarView;
import de.upb.agw.gui.project.CounterExampleGraphFile;
import de.upb.agw.gui.project.CounterExampleGraphs;
import de.upb.agw.gui.project.DotGraphs;
import de.upb.agw.gui.project.SpotlightFile;
import de.upb.agw.gui.project.SpotlightProject;
import de.upb.agw.main.Starter;


public class SpotlightGui  extends JPanel {
	
	private ProjectExplorerView projectExplorer;	
	private JTextArea console;
	private EditorView editor;	
	private ToolBarView toolBar;
	
	private static JFrame currentFrame;

	static final private String SAVETREE = "treenavigation.txt";
	static final private String SAVEDOT = "programm";
	
	//stores all project for the list
	private ArrayList<SpotlightProject> projectsInExplorer;
	
	private FileOutputStream fStream;
	    
	    public SpotlightGui(){
	        super(new BorderLayout());
	        
	        try {
	        	UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	        } catch (ClassNotFoundException e) {				
				e.printStackTrace();
			} catch (InstantiationException e) {				
				e.printStackTrace();
			} catch (IllegalAccessException e) {				
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {				
				e.printStackTrace();
			}

			projectsInExplorer = new  ArrayList<SpotlightProject>();			
			
	        // create the toolbar
	        toolBar = new ToolBarView("Spotlight toolbar", this, currentFrame);        
	        
	        // create the JTree	       
	        projectExplorer = new ProjectExplorerView(this);  
	        
	        // load the project which are opend in the project explorer from file SAVETREE
	        try {
	        	FileInputStream fStream = new FileInputStream(SAVETREE);
	        	ObjectInputStream iStream = new ObjectInputStream(fStream);
	        	ArrayList<SpotlightProject> readObject = (ArrayList<SpotlightProject>) iStream.readObject();
	        	projectsInExplorer = readObject;					
			} catch (ClassNotFoundException e) {			
				e.printStackTrace();			
			}
			catch (FileNotFoundException e) {			
				System.out.println("Navigationsdatei nicht gefunden");
			}
			catch (IOException e) {			
				System.out.println("Navigationsdatei mit falscher Struktur gefunden");
			}
			// insert all projects the the tree navigation
			initCreateNavigationTree();			
	        
	        // create the editor view	   
	        editor = new EditorView(this);
	        
	        //create the console
	        console = new JTextArea();
	        console.setEditable(false);          	 
	        
	        // create the horizontal split pane for the project explorer and the editor area
	        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectExplorer , editor);
	        splitPane1.setOneTouchExpandable(true);
	        splitPane1.setMaximumSize(new Dimension(200,300));
	        splitPane1.setDividerLocation(150);
	        splitPane1.setBorder(null);
	        
	        // create the vertical split pane for the project explorer and the editor area
	        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane1, new JScrollPane(console));
	        splitPane2.setOneTouchExpandable(true);
			splitPane2.setDividerLocation(450);

	        // layout of the main panel
	        //setPreferredSize(new Dimension(800, 600));
			
	        
	        add(toolBar, BorderLayout.PAGE_START);
	        add(splitPane2, BorderLayout.CENTER);
	    }

		private void initCreateNavigationTree() {
			
			for(SpotlightProject file : projectsInExplorer ){
				insertProjectInTree(file);
			}						
		}		
	    
	    /**
	     * Create the GUI and show it.
	     */
	    private static void createAndShowGUI() {
	        //Create and set up the window.
	        currentFrame = new JFrame("SpotLight GUI");
	        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        currentFrame.add(new SpotlightGui());
	        
	        setFramePosition(currentFrame);

	        currentFrame.pack();
	        currentFrame.setVisible(true);
	    }

	    /**
	     * Add a project to the project explorer
	     * 
	     * @param project
	     */
	    public void addProject(SpotlightProject project){
	    	
	    	projectsInExplorer.add(project);
	    	insertProjectInTree(project);
	    	storeProjectsToHDD();	    	
	    }
	    
	    /**
	     * Remove a project from the data structure
	     * @param project
	     */
	    public void removeProject(SpotlightProject project){
	    	
	    	projectsInExplorer.remove(project);
	    	storeProjectsToHDD();	    	
	    }
	    
	    private void storeProjectsToHDD(){
	    	// save all opened projects in a txt file SAVETREE
	    	try {
				fStream = new FileOutputStream(SAVETREE);
				
				ObjectOutputStream oStream = new ObjectOutputStream(fStream);
				oStream.writeObject(projectsInExplorer);
				oStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    /**
	     * Inserts a project in the project explorer
	     * 
	     * @param project
	     */
	    private void insertProjectInTree(SpotlightProject project){	    	
	    	projectExplorer.insertProjectInTree(project);     	
	    }	    
	    
	    /**
	     * Open a project file in a new tab. If this file is always shown in a tab, the tab will focus only. 
	     * 
	     * @param spotlightFile
	     */
	    public void openedOrFocusTab(SpotlightFile spotlightFile){
	    	editor.openedOrFocusTab(spotlightFile);
	    }
	    
	    public static void main(String[] args) {
	        //Schedule a job for the event dispatch thread:
	        //creating and showing this application's GUI.
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                //Turn off metal's use of bold fonts
		        UIManager.put("swing.boldMetal", Boolean.FALSE);
		        createAndShowGUI();
	            }
	        });
	    }
	    
	    /**
	     * Activate or deactivate the button which starts spotlight with the selected project.
	     * @param enable
	     */
	    public void enableRunButton(boolean enable){
	    	toolBar.enableRunButton(enable);
	    }
	    
	    /**
	     * Activate or deactivate the button which stores the file in the active tab.
	     * @param enable
	     */
	    public void enableSaveButton(boolean enable){
	    	toolBar.enableSaveButton(enable);
	    }
	    
	    /**
	     * Activate or deactivate the button which removes a 
	     * @param enable
	     */
	    public void enableDeleteButton(boolean enable){
	    	toolBar.enableDeleteButton(enable);
	    }
	    	    
	    /**
	     * Centered the JFrame on the screen. Dependence of the screen solution.
	     * 
	     * @param frame
	     */
	    private static void setFramePosition(JFrame frame){
	    	Dimension frameSize = new Dimension(800, 600);

	        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	        int top = (screenSize.height - frameSize.height) / 2;
	        int left = (screenSize.width - frameSize.width) / 2;
	       
	        frame.setPreferredSize(frameSize);
	       
	        frame.setLocation(left, top);
	    }	
	    
	    /**
	     * Run spotlight and filter the dot graph from output.
	     */
	    public void startSpotlight(){	    	
			try {
				Starter.startFromGui(projectExplorer.getSelectedSpotlightProject(), console);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			
			String s = console.getText();
			String arr[] = s.split("\\$GRAPH\\$");
			
			// no dot graph in the output
			if(arr.length < 3){
				return;
			}
			
			projectExplorer.getSelectedSpotlightProject().removeDotGraphs();
			
			for(int i = 1 ; i < arr.length-1 ; i++ ){
				String file = storeDotGraph(arr[i], i-1);
				projectExplorer.getSelectedSpotlightProject().addDotGraph(file);
			}
			
			String counterExamples[] = arr[arr.length -1].split("\\$CE\\$");
			
			projectExplorer.getSelectedSpotlightProject().removeCounterExampleGraphs();
			
			for(int i = 1 ; i < counterExamples.length ; i+=2 ){
				System.out.println(i + ": " );
				
				File newFolder = new File(projectExplorer.getSelectedSpotlightProject().getPath() + "counterexample");
				newFolder.mkdir();
				
				CounterExampleGraphFile r = new CounterExampleGraphFile(counterExamples[i]);
				
				r.storeCounterExampleGraph(newFolder.toString(), (int)Math.ceil(i/2.0) );
				projectExplorer.getSelectedSpotlightProject().addCounterExampleGraph(r);
				
				System.out.println("Path: " + projectExplorer.getSelectedSpotlightProject().getPath());
				System.out.println(newFolder.toString());
			}
			
			storeProjectsToHDD();
			
			projectExplorer.removeGraphLines();
			
			// add the lines in the project explorer
			projectExplorer.addDotGraphLine(projectExplorer.getSelectedSpotlightProject().getDotGraphs());
			projectExplorer.addCounterExampleLine(projectExplorer.getSelectedSpotlightProject().getCounterExampleGraph());
	    }

	    /**
	     * save the project, which is opened in the active tab.
	     */
		public void saveFileInActiveTab() {
			editor.saveFileInActiveTab();
		}

		public void removeTabsFromProject(SpotlightProject selectedProject) {
			editor.removeTabsFromProject(selectedProject);
		}

		public void removeSelectedProject() {
			projectExplorer.removeSelectedProject();			
		}

		public void clearConsole() {
			console.setText("");
		}

		public void newProjectDialog() {
			System.err.println("AAAAAAAAAAAAAAAAAAA");
		}
		
		private String storeDotGraph(String dotGraph, int index){
			String file = projectExplorer.getSelectedSpotlightProject().getPath() + SAVEDOT + index + ".dot";
			System.err.println( "storeDotGraph: Index " + index);
			// save all opened projects in a txt file SAVETREE
	    	try {
	    		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		        out.write(dotGraph);
		        out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return file;
		}

		public void openedOrFocusTab(DotGraphs dotGraphs) {
			editor.openedOrFocusTab(dotGraphs);
		}
		
		public void openedOrFocusTab(CounterExampleGraphs counterExampleGraphs) {
			editor.openedOrFocusTab(counterExampleGraphs);
		}
}