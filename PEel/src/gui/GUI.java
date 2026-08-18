package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.formdev.flatlaf.*;

import exceptions.ErrorDisplay;
import exceptions.ExceptionManager;
import file.FileManager;
import graphics.*;
import pe.*;


public class GUI implements BananaListener{
	
	private static boolean darkTheme = false;
	private JFrame frame = new JFrame("PEel");
	private JPanel mainPanel = new JPanel();
	private JPanel infoPanel = new JPanel();
	private JScrollPane scrollPane;
	private PaintGraphics banana = new PaintGraphics();
	private PopupManager popupManager;
	private ErrorDisplay errorDisplay;
	private ExceptionManager exceptionManager;
	private FileManager fileManager;
	private String fileName;
	
	public void drawUI() {
		
		changeTheme(); //Light theme by default
		
		//ALL GUI COMPONENETS
		errorDisplay = new ErrorDisplay() {
			
			@Override
			public void displayError(String message, String popupType) {
				// TODO Auto-generated method stub
				
			}
		};
		
		exceptionManager = new ExceptionManager(errorDisplay);
		FileManager.setExceptionManager(exceptionManager);
		popupManager = new PopupManager(frame, this);
		
		//created a small method for this because the code looked unseemly here
		setIcon();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		banana.setSelectionListener(sectionName -> {
			switch(sectionName)
			{
				case "Tip":
					displayAllHeaders();
					break;
				
				case "Top":
					displayImports();
					displayExports();
					break;
				
				case "MidTop":
					displayStrings();
					break;
				case "Mid":
					displaySectionHeaders();
					break;
			}
		});
			
		frame.add(setFrameMainPanel());
		
		//Create menu bar
		MainMenuBar menuBar = new MainMenuBar(frame, this, popupManager);
		
		
		
		frame.setJMenuBar(menuBar);
		frame.setSize(800, 800);
		frame.setVisible(true);
		
		popupManager.displayWelcomePopup();
	}

	public void changeTheme() {
		try {
			if(!darkTheme)
			{
				darkTheme = true;
				UIManager.setLookAndFeel(new FlatIntelliJLaf());
				frame.repaint();
			}
			else
			{
				darkTheme = false;
				UIManager.setLookAndFeel(new FlatDarculaLaf());
				frame.repaint();
			}
			SwingUtilities.updateComponentTreeUI(frame);
		} 
		catch (Exception e) {
			System.out.println("Failed to set Look and Feel");
		}
	}
	
	
	public JPanel setFrameMainPanel() {
		
		GridLayout experimentLayout = new GridLayout(1,2);
		mainPanel.setLayout(experimentLayout);
		
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(infoPanel);
		
		mainPanel.add(scrollPane);
		return mainPanel;
	}
	
	public void addInfoPanel(ArrayList<String> readFileData) {
		infoPanel.removeAll();
		
		for(String s : readFileData)
		{
			JLabel data = new JLabel(s);
			infoPanel.add(data);
			
		}
		
		infoPanel.revalidate();
		infoPanel.repaint();
	}

	public JFrame getFrame() {
		return frame;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getInfoPanel() {
		return infoPanel;
	}
	
	public void drawBanana() {
		mainPanel.add(banana);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	
	@Override
	public void sectionSelected(String name) {
		// TODO Auto-generated method stub
		
	}

	private void displayAllHeaders() {
		
		JPanel coffPanel = new JPanel();
		JPanel optPanel = new JPanel();
		JPanel dataPanel = new JPanel();
		
		coffPanel.setLayout(new BoxLayout(coffPanel, BoxLayout.Y_AXIS));
		optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
		
		coffPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		optPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		dataPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		coffPanel.setName("Coff Header");
		optPanel.setName("Optional Header");
		dataPanel.setName("Data Directories");
		
		CoffFileHeader coffHeader = FileManager.getPEData(CoffFileHeader.class);
		OptionalHeader optionalHeader = FileManager.getPEData(OptionalHeader.class);
		DataDirectories dataHeader = FileManager.getPEData(DataDirectories.class);
		
		//In theory this will only run if something has exploded
		if(coffHeader == null || optionalHeader == null || dataHeader == null)
		{
			popupManager.displayNullErrorPopup();
		}
		
		infoPanel.removeAll();
		
		coffPanel.add(new JLabel("<html><h3>Coff Header</h3></html>"));
		addInfoLine(coffPanel, "Machine: ", coffHeader.getMachine());
		addInfoLine(coffPanel, "Number of Sections: ", coffHeader.getNumberOfSections());
		addInfoLine(coffPanel, "Timestamp: ", coffHeader.getTimeDateStamp());
		addInfoLine(coffPanel, "Pointer to Symbol Table: ", coffHeader.getPointerToSymbolTable());
		addInfoLine(coffPanel, "Characteristics: ", coffHeader.getCharacteristics());
		
		
		optPanel.add(new JLabel("<html><h3>Optional Header</h3></html>"));
		addInfoLine(optPanel, "Magic Number: ", optionalHeader.getMagic());
		addInfoLine(optPanel, "Major Linker Version: ", optionalHeader.getMajorLinkerVersion());
		addInfoLine(optPanel, "Minor Linker Version: ", optionalHeader.getMinorLinkerVersion());
		addInfoLine(optPanel, "Size of Code: ", optionalHeader.getSizeOfCode());
		addInfoLine(optPanel, "Size of Initialized Data: 0x", Long.toHexString(optionalHeader.getSizeOfInitializedData()));
		addInfoLine(optPanel, "Size of Uninitialized Data: 0x", Long.toHexString(optionalHeader.getSizeOfUninitializedData()));
		addInfoLine(optPanel, "Address of Entry Point: 0x", Long.toHexString(optionalHeader.getAddressOfEntryPoint()));
		addInfoLine(optPanel, "Base of Code Section: 0x", Long.toHexString(optionalHeader.getBaseOfCodeSection()));
		addInfoLine(optPanel, "Image Base: 0x", Long.toHexString(optionalHeader.getImageBase()));
		addInfoLine(optPanel, "Section Alignment: 0x", Long.toHexString(optionalHeader.getSectionAlignment()));
		addInfoLine(optPanel, "File Alignment: 0x", Long.toHexString(optionalHeader.getFileAlignment()));
		addInfoLine(optPanel, "Subsystem Version: ", optionalHeader.getSubsystemVersion());
		addInfoLine(optPanel, "Win32 Version Value: ", optionalHeader.getWin32VersionValue());
		addInfoLine(optPanel, "Size of Image: ", String.format("0x%X (%.2f kb)", optionalHeader.getSizeOfImage(), convertToKb(optionalHeader.getSizeOfImage()) ));
		addInfoLine(optPanel, "Size of Headers: ", optionalHeader.getSizeOfHeaders());
		addInfoLine(optPanel, "Subsystem Field: ", optionalHeader.getSubsystemField());
		addInfoLine(optPanel, "DLL Characteristics: ", optionalHeader.getDllCharacteristics());
		addInfoLine(optPanel, "Size of Stack Reserve: ", optionalHeader.getSizeOfStackReserve());
		addInfoLine(optPanel, "Size of Stack Commit: ", optionalHeader.getSizeOfStackCommit());
		addInfoLine(optPanel, "Size of Heap Reserve: ", optionalHeader.getSizeOfHeapReserve());
		addInfoLine(optPanel, "Size of Heap Commit: ", optionalHeader.getSizeOfHeapCommit());
		addInfoLine(optPanel, "Loader Flags: ", optionalHeader.getLoaderFlags());
		addInfoLine(optPanel, "Number of Data Directory Sections: ", optionalHeader.getNumberOfRVAAndSizes());
		
		dataPanel.add(new JLabel("<html><h3>Data Directories</h3></html>"));
		addInfoLine(dataPanel, "Export Table Virtual Address: 0x", Long.toHexString(dataHeader.getExportTableRVA()));
		addInfoLine(dataPanel, "Export Table Size: 0x", Long.toHexString(dataHeader.getExportTableSize()));
		addInfoLine(dataPanel, "Import Table Virtual Address: 0x", Long.toHexString(dataHeader.getImportTableRVA()));
		addInfoLine(dataPanel, "Import Table Size: 0x", Long.toHexString(dataHeader.getImportTableSize()));
		addInfoLine(dataPanel, "Resource Table Virtual Address: 0x", Long.toHexString(dataHeader.getResourceTableRVA()));
		addInfoLine(dataPanel, "Resource Table Size: 0x", Long.toHexString(dataHeader.getResourceTableSize()));
		addInfoLine(dataPanel, "Exception Table Virtual Address: 0x", Long.toHexString(dataHeader.getExceptionTableRVA()));
		addInfoLine(dataPanel, "Exception Table Size: 0x", Long.toHexString(dataHeader.getExceptionTableSize()));
		addInfoLine(dataPanel, "Certificate Table Offset: 0x", Long.toHexString(dataHeader.getCertificateTableOffset()));
		addInfoLine(dataPanel, "Certificate Table Size: 0x", Long.toHexString(dataHeader.getCertificateTableSize()));
		addInfoLine(dataPanel, "Base Reloc Table Virutal Address: 0x", Long.toHexString(dataHeader.getBaseRelocTableRVA()));
		addInfoLine(dataPanel, "Base Reloc Table Size: 0x", Long.toHexString(dataHeader.getBaseRelocTableSize()));
		addInfoLine(dataPanel, "Debug Data Virtual Address: 0x", Long.toHexString(dataHeader.getDebugDataRVA()));
		addInfoLine(dataPanel, "Debug Data Size: 0x", Long.toHexString(dataHeader.getDebugDataSize()));
		addInfoLine(dataPanel, "Architecture (Reserved Section, Should be 0): ", dataHeader.getArchitecture());
		addInfoLine(dataPanel, "Global PTR Virtual Address: 0x", Long.toHexString(dataHeader.getGlobalPtrRVA()));
		addInfoLine(dataPanel, "Global PTR Size: 0x", Long.toHexString(dataHeader.getGlobalPtrSize()));
		addInfoLine(dataPanel, "TLS Table Virtual Address: 0x", Long.toHexString(dataHeader.getTlsTableRVA()));
		addInfoLine(dataPanel, "TLS Table Size: 0x", Long.toHexString(dataHeader.getTlsTableSize()));
		addInfoLine(dataPanel, "Load Config Table Virutal Address: 0x", Long.toHexString(dataHeader.getLoadConfigTableRVA()));
		addInfoLine(dataPanel, "Load Config Table Size: 0x", Long.toHexString(dataHeader.getLoadConfigTableSize()));
		addInfoLine(dataPanel, "Bound Import Table Virtual Address: 0x", Long.toHexString(dataHeader.getBoundImportTableRVA()));
		addInfoLine(dataPanel, "Bound Import Table Size: 0x", Long.toHexString(dataHeader.getBoundImportTableSize()));
		addInfoLine(dataPanel, "Import Address Table Virtual Address: 0x", Long.toHexString(dataHeader.getIATRVA()));
		addInfoLine(dataPanel, "Import Address Table Size: 0x", Long.toHexString(dataHeader.getIATSize()));
		addInfoLine(dataPanel, "Delay Import Descriptor Virtual Address: 0x", Long.toHexString(dataHeader.getDelayImportDescriptorRVA()));
		addInfoLine(dataPanel, "Delay Import Descriptor Size: 0x", Long.toHexString(dataHeader.getDelayImportDescriptorSize()));
		addInfoLine(dataPanel, "CLR Runtime Virtual Address: 0x", Long.toHexString(dataHeader.getCLRRuntimeRVA()));
		addInfoLine(dataPanel, "CLR Runtime Size: 0x", Long.toHexString(dataHeader.getCLRRuntimeSize()));
		addInfoLine(dataPanel, "Balogna Section (Unnamed Reserved, Should be 0): ", dataHeader.getUnnamedReserved());
		
		infoPanel.add(coffPanel);
		infoPanel.add(Box.createVerticalStrut(20));
		infoPanel.add(optPanel);
		infoPanel.add(Box.createVerticalStrut(20));
		infoPanel.add(dataPanel);
		
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	
	private void displayImports() {
		infoPanel.removeAll();
		
		JPanel importPanel = new JPanel();
		importPanel.setLayout(new BoxLayout(importPanel, BoxLayout.Y_AXIS));
		importPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		ArrayList<ImportTable> imports = FileManager.getPEData(ImportTableList.class);
		
		if(imports == null) {return;}
		
		importPanel.add(new JLabel("<html><h3>Imports</h3></html>"));
		
		for(ImportTable i : imports)
		{
			addInfoLine(importPanel, "<html><b>", (i.getDllName()+ "</b></html>") );
			
			for(String process : i.getDllProcesses())
			{
				addInfoLine(importPanel, "      ", process);
			}
			importPanel.add(Box.createVerticalStrut(15));
		}
		infoPanel.add(importPanel);
		infoPanel.add(Box.createVerticalStrut(20));
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	public void displayExports() {
		
		ExportTable exports = FileManager.getPEData(ExportTable.class);
		
		if(exports == null) {return;}
		JPanel exportPanel = new JPanel();
		exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.Y_AXIS));
		exportPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		exportPanel.add(new JLabel("<html><h3>Exports</h3></html>"));
		
		for(String exportName : exports.getExportNames())
		{
			addInfoLine(exportPanel, exportName);
		}
		infoPanel.add(exportPanel);
		
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	public void displayStrings() {
		
		JPanel stringsPanel = new JPanel();
		stringsPanel.setLayout(new BoxLayout(stringsPanel, BoxLayout.Y_AXIS));
		stringsPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		infoPanel.removeAll();
		PEStringsList parsedStrings = FileManager.getPEData(PEStringsList.class);
		
		if(parsedStrings == null) {return;} //Cheap and easy TEMPORARY error prevention
		
		stringsPanel.add(new JLabel("<html><h3>Strings</h3></html>"));
		
		for(String s : parsedStrings.getStrings())
		{
			addInfoLine(stringsPanel, s);
		}
		
		infoPanel.add(stringsPanel);
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	public void displaySectionHeaders() {
		JPanel sectionsPanel = new JPanel();
		
		sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
		sectionsPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		infoPanel.removeAll();
		
		List<SectionHeader> sections = FileManager.getAllSections();
		
		if(sections == null) {return;}
		
		for(SectionHeader section : sections)
		{
			addInfoLine(sectionsPanel, "<html><b>", (section.getName()+ "</b></html>"));
			addInfoLine(sectionsPanel, "Virtual Size: 0x", Long.toHexString(section.getVirtualSize()));
			addInfoLine(sectionsPanel, "Virtual Address: 0x", Long.toHexString(section.getVirtualAddress()));
			addInfoLine(sectionsPanel, "Raw Data Size: 0x", Long.toHexString(section.getSizeOfRawData()));
			addInfoLine(sectionsPanel, "Pointer to Raw Data: 0x", Long.toHexString(section.getPointerToRawData()));
			addInfoLine(sectionsPanel, "Pointer to Relocs: 0x", Long.toHexString(section.getPointerToRelocs()));
			addInfoLine(sectionsPanel, "Pointer to Line Numbers: 0x", Long.toHexString(section.getPointerToLineNumbers()));
			addInfoLine(sectionsPanel, "Number of Relocs: ", section.getNumberOfRelocs());
			addInfoLine(sectionsPanel, "Number of Line Nums: ", section.getNumberOfLineNumbers());
			addInfoLine(sectionsPanel, "Characteristics: ", section.getCharacteristics());
			sectionsPanel.add(Box.createVerticalStrut(20));
		}
		
		infoPanel.add(sectionsPanel);
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	/**
	 * Helper method for adding lines to info panel from parsed data
	 * @param label
	 * @param data
	 */
	public void addInfoLine(JPanel panel, String label, Object data) {
		if(data == null)
		{
			data = "NULL";
			panel.add(new JLabel("<html><b>" + label + data + "</html></b>"));
			return;
		}
		panel.add(new JLabel(label + data));
	}
	
	public void addInfoLine(JPanel panel, Object data) {
		if(data == null)
		{
			data = "NULL";
			panel.add(new JLabel("<html><b>" + data + "</html></b>"));
			return;
		}
		panel.add(new JLabel((String) data));
	}
	
	public void setFileName(String name) {
		this.fileName = name;
		scrollPane.setBorder(BorderFactory.createTitledBorder(fileName));
	}
	public void setIcon() {
		InputStream input = getClass()
		        .getClassLoader()
		        .getResourceAsStream("PEEL.png");

		try 
		{
			ImageIcon icon = new ImageIcon(ImageIO.read(input));
			frame.setIconImage(icon.getImage());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double convertToKb (long bytes) {
		
		return bytes / 1024.0;
	}
	
	public ExceptionManager getExceptionManager() {
		return this.exceptionManager;
	}
	
	
}
