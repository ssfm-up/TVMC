package de.upb.agw.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicToolTipUI;

import de.upb.agw.gui.SpotlightGui;
import de.upb.agw.gui.dialog.NewProjektDialog;
import de.upb.agw.gui.dialog.OpenProjektDialog;
import de.upb.agw.main.Starter;

public class ToolBar extends JToolBar{
	
	private SpotlightGui gui;
	
	private JButton bNewProject;
	private JButton bOpenProject;
	private JButton bRun;
	private JButton bSave;	
	private JButton bDelete;	
	private JButton bClearConsole;


	static final private String BUTTON_NEWPROJECT = "new";
	static final private String BUTTON_OPENPROJECT = "open";   
	static final private String BUTTON_RUNSPOTLIGHT = "run";
	static final private String BUTTON_SAVE = "save";
	static final private String BUTTON_DELETE = "del";
	static final private String BUTTON_CLEAR_CONSOLE = "clearConsole";
	
	public ToolBar(String titel, final SpotlightGui gui, final JFrame currentFrame){
		super(titel);
		this.gui = gui;		

		// new project Button
		bNewProject = createButton(BUTTON_NEWPROJECT, BUTTON_NEWPROJECT, "open a dialog to create a new project" , "new project");
		this.add(bNewProject);
		bNewProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				NewProjektDialog newProjectDialog = new NewProjektDialog(gui, currentFrame);    	            
	            newProjectDialog.pack();
	            newProjectDialog.setVisible(true);		
			}			
		});

		// open Button
		bOpenProject = createButton(BUTTON_OPENPROJECT, BUTTON_OPENPROJECT, "open a new project which consists of the three spotlight files" , "open project");
		this.add(bOpenProject);
		bOpenProject.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				OpenProjektDialog openProjectDialog = new OpenProjektDialog(gui, currentFrame);    	            
				openProjectDialog.pack();
				openProjectDialog.setVisible(true);			
			}			
		});
		
		this.add(new Separator());
		
		// run Button
		bRun = createButton(BUTTON_RUNSPOTLIGHT, BUTTON_RUNSPOTLIGHT, "run the selected project with spotlight" , "run spotlight");
		this.enableRunButton(false);
		this.add(bRun);
		bRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.startSpotlight();
			}			
		});

		// save Button
		bSave = createButton(BUTTON_SAVE, BUTTON_SAVE, "save the project, which is currently opened in the tab" , "save project");
		bSave.setEnabled(false);
		this.add(bSave);
		
		bSave.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.saveFileInActiveTab();			
			}			
		});	
			
		this.add(new Separator());
		
		// delete Button
		bDelete = createButton(BUTTON_DELETE, BUTTON_DELETE, "remove the project from the project explorer" , "remove project");
		this.add(bDelete);
		bDelete.setEnabled(false);
		bDelete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				gui.removeSelectedProject();			
			}			
		});	
		
		// clear console Button
		bClearConsole = createButton(BUTTON_CLEAR_CONSOLE, BUTTON_CLEAR_CONSOLE, "clear the console" , "clear console");
		this.add(bClearConsole);
		bClearConsole.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				gui.clearConsole();			
			}			
		});	
	}
	
	/**
	 * Returns a button with the given parameter
	 * @param imageName
	 * @param actionCommand
	 * @param toolTipText
	 * @param altText
	 * @return
	 */
	protected JButton createButton(String imageName, String actionCommand, String toolTipText, String altText) {
		//Look for the image.
		String imgLocation = imageName + ".gif";
		URL imageURL = SpotlightGui.class.getResource(imgLocation);
			
		//Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
					
		if (imageURL != null) {             
			button.setIcon(new ImageIcon(imageURL, altText));
		} 
		else {	
			button.setText(altText);
		}
		return button;
	}
	
	/**
     * Activate or deactivate the button which starts spotlight with the selected project.
     * @param enable
     */
    public void enableRunButton(boolean enable){
    	bRun.setEnabled(enable);
    }
    
    /**
     * Activate or deactivate the button which stores the file in the active tab.
     * @param enable
     */
    public void enableSaveButton(boolean enable){
    	bSave.setEnabled(enable);
    }

    /**
     * Activate or deactivate the button which removes a a project from the project explorer.
     * @param enable
     */
	public void enableDeleteButton(boolean enable) {
		bDelete.setEnabled(enable);
	}
}