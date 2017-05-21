package de.upb.agw.gui.tab;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.upb.agw.gui.component.EditorView;
import de.upb.agw.gui.project.SpotlightFile;

public class Texteditor extends JScrollPane implements Tab{
	
	private SpotlightFile spotlightFile;
	private JTextArea textArea;
	private EditorView editor;
	private String lastSavedText;
	
	private boolean saved;
	
	public Texteditor(SpotlightFile spotlightFile, EditorView editor){
		super();
		this.spotlightFile = spotlightFile;
		this.editor = editor;
		
		saved = true;
	
		textArea = new JTextArea();
		
		loadFileInTextArea();
		lastSavedText = textArea.getText();
		
		textArea.addKeyListener(createKeyListener());
		textArea.setEditable(true);
		textArea.setTabSize(2);		
        setViewportView(textArea);	
	}
	
	/**
	 * Load the file from hard disk and display the file in the TextView
	 */
	private void loadFileInTextArea(){
		try {
			textArea.read(new FileReader(spotlightFile), null);			
		} catch (FileNotFoundException e) {				
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Store the file at hard disk
	 */
	private void storeFileInTextArea(){
		try {
			textArea.write(new FileWriter(spotlightFile));
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}
	
	/**
	 * Create and return the keyListiner for the JTeaxarea
	 * @return
	 */
	private KeyListener createKeyListener(){
		
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent k) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent k) {
				if(saved){
					checkChanges();
				}				
			}
			
			@Override
			public void keyPressed(KeyEvent k) {				
				
				if(k.isControlDown() && k.getKeyCode() == k.VK_S && !saved){
					saveFile();
				}				
			}
		};		
	}
	
	/**
	 * Returns the spotlightfile, which is displayed in this tab
	 * @return
	 */
	public SpotlightFile getSpotlightFile(){
		return spotlightFile;
	}
	
	/**
	 * Add the unsaved marker "*" and enable the save button
	 */
	private void setUnsavedMarker(){					
		editor.setUnsavedMarker();
		saved=false;
		editor.enableSaveButton(!saved);
	}	
	
	/**
	 * Check, if the textarea input have changed 
	 */
	public void checkChanges(){
		if(!(lastSavedText.equals(textArea.getText()))){					
			setUnsavedMarker();
		}
	}
	
	/**
	 * Removed the unsaved marker "*" and disable the save button
	 */
	private void unsetUnsavedMaker(){
		editor.unsetUnsavedMaker();
		saved = true;
		lastSavedText = textArea.getText();
		editor.enableSaveButton(!saved);
	}
	
	/**
	 * Save the file in this tab
	 */
	public void saveFile(){
		if(!saved){
			storeFileInTextArea();
			unsetUnsavedMaker();
		}
	}
	
	/**
	 * Return if the file in this tab is saved
	 * @return
	 */
	public boolean isSaved(){
		return saved;
	}
}
