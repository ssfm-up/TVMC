package de.upb.agw.gui.component;

import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.upb.agw.gui.SpotlightGui;
import de.upb.agw.gui.project.CounterExampleGraphs;
import de.upb.agw.gui.project.DotGraphs;
import de.upb.agw.gui.project.SpotlightFile;
import de.upb.agw.gui.project.SpotlightProject;
import de.upb.agw.gui.tab.ButtonTabComponent;
import de.upb.agw.gui.tab.CounterExampleTab;
import de.upb.agw.gui.tab.DotGraphsTab;
import de.upb.agw.gui.tab.Tab;
import de.upb.agw.gui.tab.Texteditor;

public class EditorView extends JTabbedPane{
	
	//stores all Spotlightfile which are opend in a tab
	private ArrayList<Texteditor> filesInTab;
	
	//stores all DotGraphs wich are opend in tab
	private ArrayList<DotGraphsTab> dotGraphsInTab;
	
	//stores all CounterExampleGraphs wich are opend in tab
	private ArrayList<CounterExampleTab> counterExampleInTab;
	
	// The Gui which includes the editor
	SpotlightGui gui;
	
	// the active tab
	private Texteditor activeTab;
	
	public EditorView(SpotlightGui gui){
		this.gui = gui;
		this.filesInTab = new ArrayList<Texteditor>();
		this.dotGraphsInTab = new ArrayList<DotGraphsTab>();
		this.counterExampleInTab = new ArrayList<CounterExampleTab>();
		
		this.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent c) {
				
				activeTab = null;
				
				if(getSelectedComponent() instanceof Texteditor){
					setActiveTab();					
				}				
			}
		});
	}
	
	/**
     * Open a project file in a new tab. If this file is always shown in a tab, the tab will focus only. 
     * 
     * @param spotlightFile
     */
    public void openedOrFocusTab(SpotlightFile spotlightFile){
    	
    	Texteditor tab = getTabFromFile(spotlightFile);
    	
    	if(tab != null){
    		//file is opened in tab before
    		int index = this.indexOfComponent(tab);	    		
    		
    		System.out.println("Index des geöffneten Tabs: " + index);
    		
    		// focus opened tab
    		this.setSelectedIndex(index);
    		return;
    	}	    	
    	
    	// Create the tab
       	tab = new Texteditor(spotlightFile, this);
       	    	
        this.add(spotlightFile.toTabString() + "   ",tab);	        
        this.setTabComponentAt((this.getTabCount()-1), new ButtonTabComponent(this, tab));
        this.setSelectedIndex(this.getTabCount()-1);
        
        filesInTab.add(tab);
    }
    
    public void openedOrFocusTab(DotGraphs dotGraphs){
		
		DotGraphsTab tab = getTabFromDotGraphs(dotGraphs);
    	
    	if(tab != null){
    		//file is opened in tab before
    		int index = this.indexOfComponent(tab);	    		
    		
    		// focus opened tab
    		this.setSelectedIndex(index);
    		return;
    	}	    	
    	
    	// Create the tab
       	tab = new DotGraphsTab(dotGraphs);
       	    	
        this.add(dotGraphs.toString() + "   ",tab);	        
        this.setTabComponentAt((this.getTabCount()-1), new ButtonTabComponent(this, tab));
        this.setSelectedIndex(this.getTabCount()-1);
        
        dotGraphsInTab.add(tab);
	}
    
    public void openedOrFocusTab(CounterExampleGraphs counterExampleGraphs){
		
    	CounterExampleTab tab = getTabFromCounterExampleGraphs(counterExampleGraphs);
    	
    	if(tab != null){
    		//file is opened in tab before
    		int index = this.indexOfComponent(tab);	    		
    		
    		// focus opened tab
    		this.setSelectedIndex(index);
    		return;
    	}	    	
    	
    	// Create the tab
       	tab = new CounterExampleTab(counterExampleGraphs);
       	    	
        this.add(counterExampleGraphs.toString() + "   ",tab);	        
        this.setTabComponentAt((this.getTabCount()-1), new ButtonTabComponent(this, tab));
        this.setSelectedIndex(this.getTabCount()-1);
        
    	counterExampleInTab.add(tab);
	}
    
    private CounterExampleTab getTabFromCounterExampleGraphs(CounterExampleGraphs counterExampleGraphs) {
    	for(CounterExampleTab tab : counterExampleInTab){
    		CounterExampleGraphs tmp = tab.getCounterExampleGraphs();
    		if(tmp.equals(counterExampleGraphs)){
    			return tab;
    		}
    	}
    	System.out.println("nicht geöffnet");
    	return null;
	}

	/**
     * Return the tab which displays the file in a tab.
     * @param spotlightFile
     * @return
     */
    private Texteditor getTabFromFile(SpotlightFile spotlightFile){
    	    	
    	for(Texteditor tab : filesInTab){
    		SpotlightFile file = tab.getSpotlightFile();
    		if(file.equals(spotlightFile)){
    			return tab;
    		}
    	}
    
    	return null;
    }   
    
    /**
     * Return the tab which displays the file in a tab.
     * @param spotlightFile
     * @return
     */
    private DotGraphsTab getTabFromDotGraphs(DotGraphs dotGraphs){
    	    	
    	for(DotGraphsTab dotGraphsTab : dotGraphsInTab){
    		DotGraphs tmp = dotGraphsTab.getDotGraphs();
    		if(tmp.equals(dotGraphs)){
    			return dotGraphsTab;
    		}
    	}
    
    	return null;
    }
    
    /**
     * Delete an closed tab from data structure. All open tabs are stored in the data structure.
     * @param spotlightFile
     */
    public void removeTab(Tab tab){	    	
    	if(tab instanceof Texteditor){
    		filesInTab.remove(tab);
    	}
    	else if(tab instanceof DotGraphsTab){
    		dotGraphsInTab.remove(tab);
    	}  
    	else if(tab instanceof CounterExampleTab){
    		counterExampleInTab.remove(tab);
    	}  
    }
    
    public void setUnsavedMarker(){
    	int index = getSelectedIndex();
    	String titel = getTitleAt(index);
    	titel = titel.substring(0, titel.length()-2);
    	this.setTitleAt(index, titel + "*");
    	System.err.println("set");
    }
    
    public void unsetUnsavedMaker(){
    	int index = getSelectedIndex();
    	String titel = getTitleAt(index);
    	titel = titel.substring(0, titel.length()-1);
    	this.setTitleAt(index, titel + "  ");
    	System.err.println("unset");
    }

    /**
     * Save the 
     */
	public void saveFileInActiveTab() {
		if(this.getSelectedComponent() instanceof Texteditor){
			Texteditor tab = (Texteditor) this.getSelectedComponent();
			tab.saveFile();
		}		
	}
	
	/**
	 * Notice the active tab and enable or disable the save button in the toolbar
	 */
	private void setActiveTab(){
		activeTab = (Texteditor)getSelectedComponent();
		enableSaveButton(!activeTab.isSaved());
	}
	
	/**
	 * Enable the save button in the toolbar
	 * @param enable
	 */
	public void enableSaveButton(boolean enable){
    	gui.enableSaveButton(enable);
    }
	
	/**
	 * Remove the open tab from the given file. The tab must be active.
	 * @param spotlightfile
	 */
	public void removeTabFromFile(SpotlightFile spotlightfile){
		Texteditor tab = getTabFromFile(spotlightfile);		
		
		// remove the displayed tab in the editor
		remove(tab);
		
		// remove the tab from datastructure
		removeTab(tab);
	}
	
	public void removeTabFromDotGraph(DotGraphs dotGraphs){
		DotGraphsTab tab = getTabFromDotGraphs(dotGraphs);		
		
		// remove the displayed tab in the editor
		remove(tab);
		
		// remove the tab from datastructure
		removeTab(tab);
	}

	/**
	 * Remove all opened tabs from a given project.
	 * @param selectedProject
	 */
	public void removeTabsFromProject(SpotlightProject selectedProject) {		
		removeTabFromFile(selectedProject.getcFile());
		removeTabFromFile(selectedProject.getctlFile());
		removeTabFromFile(selectedProject.getinitFile());
		if(selectedProject.getDotGraphs() != null){
			removeTabFromDotGraph(selectedProject.getDotGraphs());
		}
	}
}
