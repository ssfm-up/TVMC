package de.upb.agw.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import de.upb.agw.gui.SpotlightFileFilter;
import de.upb.agw.gui.SpotlightGui;
import de.upb.agw.gui.project.DotGraphFile;
import de.upb.agw.gui.project.SpotlightProject;



public class OpenProjektDialog extends JDialog implements ActionListener{
	
	SpotlightGui parent;
	
	// gui elemets
	private JFileChooser fileChooser;
	private JTextField name;
	private JTextField cFilePath;
	private JTextField ctlFilePath;
	private JTextField initFilePath;
	private JButton bCCode;
	private JButton bCTLCode;
	private JButton bInitCode;
	private JButton bOk;
	private JButton bCancel;
	
	private boolean firstFileChoosen;
		
	//the chosen files
	private File cFile;
	private File ctlFile;
	private File initFile;
	private ArrayList<DotGraphFile> graphs;
	
	// the file filter for *.c, *.ctl and *.pred extensions
	private SpotlightFileFilter spotlightFileFilter;
	
	//button commands
	private final String CBUTTON = "cbutton";
	private final String CTLBUTTON = "ctlbutton";
	private final String INIT = "init";
	private final String OK = "ok";
	private final String CANCEL = "cancel";
	
	public OpenProjektDialog(SpotlightGui parent, JFrame frame) {
		
		this.parent = parent;		
		setModal(true);
		
		this.firstFileChoosen = false;
				
		this.setTitle("Open a new project");
		this.setResizable(false);
		
		setFramePosition(frame);
		
		// create the filter for the chosen files
		spotlightFileFilter = new SpotlightFileFilter();
		
		//create the filechooser
		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(spotlightFileFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// generate the distance
		Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10);
		
		JPanel titledBorders = new JPanel();
        titledBorders.setBorder(paneEdge);
        titledBorders.setLayout(new BoxLayout(titledBorders, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());
        
        /**
         * textfield for project name
         */
        TitledBorder projectTitle = BorderFactory.createTitledBorder("project name");
		JPanel projectPanel = new JPanel();		
		
		//textfield
		name = createTextField(true);
		projectPanel.add(name);	
		name.addKeyListener(new KeyListener(){
			
			@Override
			public void keyTyped(KeyEvent e) {checkProjectComplete();}
			
			@Override
			public void keyReleased(KeyEvent e) {checkProjectComplete();}
			
			@Override
			public void keyPressed(KeyEvent e) {checkProjectComplete();}
		});
	
		projectPanel.setBorder(projectTitle);
		titledBorders.add(projectPanel);
        
        /**
		 * c File
		 */
        TitledBorder cCodeTitle = BorderFactory.createTitledBorder("c file");
		JPanel cFilePanel = new JPanel();		
		
		//textfield
		cFilePath = createTextField(false);
		cFilePanel.add(cFilePath);
		
		//button
		bCCode = createButton("choose c file", this, CBUTTON);
		cFilePanel.add(bCCode);
	
		cFilePanel.setBorder(cCodeTitle);
		titledBorders.add(cFilePanel);
		
		/**
		 * ctl file
		 */
		TitledBorder cTLTitel = BorderFactory.createTitledBorder("ctl file");
		JPanel ctlFilePanel = new JPanel();	
		
		//textfield
		ctlFilePath = createTextField(false);
		ctlFilePanel.add(ctlFilePath);
		
		//button
		bCTLCode = createButton("choose ctl file", this, CTLBUTTON);
		ctlFilePanel.add(bCTLCode);
	
		ctlFilePanel.setBorder(cTLTitel);
		titledBorders.add(ctlFilePanel);
		
		/**
		 * init
		 */
		TitledBorder initTitel = BorderFactory.createTitledBorder("init");
		JPanel initFilePanel = new JPanel();
		
		//textfield
		initFilePath = createTextField(false);
		initFilePanel.add(initFilePath);
		
		//button
		bInitCode = createButton("choose init file", this, INIT);
		initFilePanel.add(bInitCode);
	
		initFilePanel.setBorder(initTitel);
		titledBorders.add(initFilePanel);
				
		/**
		 * create ok and cancel button
		 */
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel okCancelPanel = new JPanel();
		
		// ok button
		bOk = createButton("create project", this, OK);
		okCancelPanel.add(bOk);
		bOk.setEnabled(false);
	
		// cancel button
		bCancel = createButton("cancel", this, CANCEL);
		okCancelPanel.add(bCancel);

		// add to panel
		panel.add(Box.createHorizontalStrut(5));
		panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		panel.add(Box.createHorizontalStrut(5));
		panel.add(okCancelPanel);
		
		add(titledBorders, BorderLayout.NORTH);	
		add(panel, BorderLayout.SOUTH);	
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		if(event.getActionCommand().equals(OK)){			
			
			parent.addProject(new SpotlightProject(name.getText(), cFile, ctlFile, initFile));
			dispose();
			return;
		}
		else if(event.getActionCommand().equals(CANCEL)){			
			dispose();
			return;
		}
		
		String buttonText = "";
		
		if(event.getActionCommand().equals(CBUTTON)){			
			spotlightFileFilter.setExtension("c");	
			buttonText = "Choose C file";
		}
		else if(event.getActionCommand().equals(CTLBUTTON)){
			spotlightFileFilter.setExtension("ctl");	
			buttonText = "Choose CTL file";
		}
		else{ 
			spotlightFileFilter.setExtension("pred");	
			buttonText = "Choose init file";
		}
		
		int result = fileChooser.showDialog(null,buttonText);
		
		 //Process the results.
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(!firstFileChoosen){
            	checkOtherFiles(file);
            	firstFileChoosen = true;
            }
                  
            if(event.getActionCommand().equals(CBUTTON)){			
    			cFile=file;
            	cFilePath.setText(file.getAbsolutePath());
    		}
    		else if(event.getActionCommand().equals(CTLBUTTON)){
    			ctlFile=file;
    			ctlFilePath.setText(file.getAbsolutePath());
    		}
    		else{ 
    			initFile=file;
    			initFilePath.setText(file.getAbsolutePath());
    		}         
        }
        
        checkProjectComplete();       
	}
	
	/**
	 * Check if other project files with same name exists. 
	 * @param file
	 */
	private void checkOtherFiles(File file) {
		
		String[] splitName = file.getName().split("\\.");
		String[] splitPath = file.getAbsolutePath().split("\\.");
		
		// exit condition
		if(splitName.length < 1 || splitPath.length < 1){
			return;			
		}
						
		File fileTmp;
		
		// check c file
		fileTmp = new File(splitPath[0] + ".c");
		
		if(fileTmp.canRead()){
			setCFile(fileTmp);
		}
		
		// check ctl file
		fileTmp = new File(splitPath[0] + ".ctl");
		
		if(fileTmp.canRead()){
			setCtlFile(fileTmp);
		}
		
		// check init file
		fileTmp = new File(splitPath[0] + ".pred");
		
		if(fileTmp.canRead()){
			setInitFile(fileTmp);
		}
		
		setProjectName(splitName[0]);
		
		checkProjectComplete();		
	}
	
	/**
	 * create and return a JButton with the parameter
	 * @param buttonText
	 * @param actionListener
	 * @param actionCommand
	 * @return the JButton
	 */
	private static JButton createButton(String buttonText, OpenProjektDialog actionListener, String actionCommand ){
		JButton button = new JButton(buttonText);
		button.addActionListener(actionListener);
		button.setActionCommand(actionCommand);
		
		return button;		
	}
	
	/**
	 * create a JTextField
	 * @return
	 */
	private static JTextField createTextField(boolean editable){
		JTextField textField = new JTextField();
		if(editable){
			textField.setColumns(39);
		}
		else{
			textField.setColumns(30);
		}
		textField.setEditable(editable);
		
		return textField;		
	}
	
	/**
	 * Method to check, if the project is complete
	 */
	private void checkProjectComplete(){
		
		 if( !(cFilePath.getText().equals("") || ctlFilePath.getText().equals("") || initFilePath.getText().equals("") || name.getText().equals("")) ){
	        	bOk.setEnabled(true);	        	
	      }
		 else{
			 bOk.setEnabled(false);			
		 }		
	}
	
	/**
	 * Centered the dialog in the middle of the parent frame
	 * @param frame
	 */
	private void setFramePosition(JFrame frame){        
        
        Point point = frame.getLocationOnScreen();      
        setLocation(point.x+150, point.y+50);
    }
	
	private void setCFile(File cFile){
		this.cFile=ctlFile;
		this.cFilePath.setText(cFile.getAbsolutePath());		
	}
	
	private void setCtlFile(File ctlFile){
		this.ctlFile=ctlFile;
		this.ctlFilePath.setText(ctlFile.getAbsolutePath());		
	}
	
	private void setInitFile(File initFile){
		this.initFile=initFile;
		this.initFilePath.setText(initFile.getAbsolutePath());		
	}
	
	private void setProjectName(String name){
		this.name.setText(name);
	}
}
