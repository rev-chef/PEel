package gui;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import file.DetectExecutable;
import file.ExecutableFile;
import file.FileManager;



public class MainMenuBar extends JMenuBar{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Menu bar attributes
	private JMenu editMenu = new JMenu();
	private JFrame frame;
	private GUI gui;
	private PopupManager pm;
	public MainMenuBar(JFrame frame, GUI gui, PopupManager pm) {
		this.frame = frame;
		this.gui = gui;
		this.pm = pm;
		JMenu fileMenu = populateFileMenu();
		JMenu viewMenu = populateViewMenu();
		this.add(fileMenu);
		this.add(viewMenu);
	}
	
	private JMenu populateFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem importFileMenuItem, refreshFileMenuItem;
		
		importFileMenuItem = new JMenuItem("Import File", KeyEvent.VK_F8);
		refreshFileMenuItem = new JMenuItem("Reload File", KeyEvent.VK_F7);
		
		//Upload file
		JFileChooser fileImportChooser = new JFileChooser();
		FileNameExtensionFilter fileExtensionFilter = new FileNameExtensionFilter("Executables and DLLs", "exe", "dll");
		
		fileImportChooser.setFileFilter(fileExtensionFilter);
		
		importFileMenuItem.addActionListener(e -> 
		{
			//TODO: Handle null files 
		    int returnVal = fileImportChooser.showOpenDialog(frame);

		    if (returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		       File f = fileImportChooser.getSelectedFile();
		       ExecutableFile exe = DetectExecutable.detect(f); //Legal becuase detect() returns subclass instance of abstract class ExecutableFile()
		       gui.setFileName(f.getName());
		       FileManager m = new FileManager(exe); //Pass to manager class
		       if(FileManager.getMalformedFlag()) {pm.displayMalformPopup();}
		       
		       if (FileManager.isFileLoaded()) {
		    	    gui.drawBanana();
		    	} 
		       else {
		    	    pm.displayInvalidFilePopup();
		    	}
		       
		    }
		});
		 fileMenu.add(importFileMenuItem);
		
		return fileMenu;
	}
	
	private JMenu populateViewMenu() {
		JMenu viewMenu = new JMenu("View");
		JMenuItem changeThemeMenuItem = new JMenuItem("Change Theme");
		
		changeThemeMenuItem.addActionListener(e -> {gui.changeTheme();});
		viewMenu.add(changeThemeMenuItem);
		return viewMenu;
	}
	
	
}
