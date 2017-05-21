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
import java.io.FileWriter;
import java.io.IOException;

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

public class NewProjektDialog extends JDialog implements ActionListener{
	
	SpotlightGui parent;
	
	// gui elemets
	private JFileChooser fileChooser;
	private JTextField name;
	private JTextField filePath;
	private JButton bDestination;
	private JButton bOk;
	private JButton bCancel;
	
	private boolean firstFileChoosen;
		
	//the chosen files
	private String  destinationPath;
	private File cFile;
	private File ctlFile;
	private File initFile;
	
	// the file filter for *.c, *.ctl and *.pred extensions
	private SpotlightFileFilter spotlightFileFilter;
	
	//button commands
	private final String CBUTTON = "cbutton";
	private final String OK = "ok";
	private final String CANCEL = "cancel";
	
	public NewProjektDialog(SpotlightGui parent, JFrame frame) {
		
		this.parent = parent;		
		setModal(true);
		
		this.firstFileChoosen = false;
				
		this.setTitle("Create a new project");
		this.setResizable(false);
		
		setFramePosition(frame);
		
		// create the filter for the chosen files
		spotlightFileFilter = new SpotlightFileFilter();
		
		//create the filechooser
		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(spotlightFileFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		
		// generate the distance
		Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10);
		
		JPanel titledBorders = new JPanel();
        titledBorders.setBorder(paneEdge);
        titledBorders.setLayout(new BoxLayout(titledBorders, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());
        
        /**
		 * Destination
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
		 * Destination
		 */
        TitledBorder CodeTitle = BorderFactory.createTitledBorder("files");
		JPanel FilePanel = new JPanel();		
		
		//textfield
		filePath = createTextField(false);
		FilePanel.add(filePath);
		
		//button
		bDestination = createButton("choose c file", this, CBUTTON);
		FilePanel.add(bDestination);
	
		FilePanel.setBorder(CodeTitle);
		titledBorders.add(FilePanel);
		
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
			
			createFilesOfProject();
			//parent.addProject(new SpotlightProject(name.getText(), cFile, ctlFile, initFile));
			dispose();
			return;
		}
		else if(event.getActionCommand().equals(CANCEL)){			
			dispose();
			return;
		}
		
		String buttonText = "";		
		
		int result = fileChooser.showDialog(null,buttonText);
		
		//Process the results.
        if (result == JFileChooser.APPROVE_OPTION) {
            destinationPath = fileChooser.getSelectedFile().getAbsolutePath();
            filePath.setText(destinationPath);
        }
        
        checkProjectComplete();       
	}
	
	/**
	 * Check if other project files with same name exists. 
	 * @param file
	 */
	private void createFilesOfProject() {

		cFile = new File(destinationPath + "\\" + name.getText() + ".c");
		
		try {
			FileWriter f = new FileWriter(cFile);
			f.write("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ctlFile = new File(destinationPath + "\\" + name.getText() + ".ctl");
		initFile = new File(destinationPath + "\\" + name.getText() + ".pred");	
	}

	/**
	 * create and return a JButton with the parameter
	 * @param buttonText
	 * @param actionListener
	 * @param actionCommand
	 * @return the JButton
	 */
	private static JButton createButton(String buttonText, NewProjektDialog actionListener, String actionCommand ){
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
		
		 if( !(filePath.getText().equals("") || name.getText().equals("")) ){
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
	
	private void setProjectName(String name){
		this.name.setText(name);
	}
}
