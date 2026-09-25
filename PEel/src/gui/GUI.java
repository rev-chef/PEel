package gui;

import java.awt.*;
import java.io.*;
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
	private JPanel infoComparePanel = new JPanel();
	private JScrollPane scrollPane;
	private PaintGraphics banana = new PaintGraphics();
	private PopupManager popupManager;
	private ErrorDisplay errorDisplay;
	private ExceptionManager exceptionManager;
	private FileManager fileManager;
	private String fileName;
	private static String bytesFlag; //variable 
	
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
		
		bytesFlag = "kb";
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
				case "MidBot":
					displayFileInformation();
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
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		infoPanel.setSize(scrollPane.getSize());
		
		mainPanel.add(scrollPane);
		return mainPanel;
	}
	
	public JPanel setComparisonPanel() {
		
		return null;
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
		
		JPanel coffHeaderPanel = new JPanel();
		JPanel optHeaderPanel = new JPanel();
		JPanel dataHeaderPanel = new JPanel();
		
		JTextField coffTxt = new JTextField();
		JTextField optTxt = new JTextField();
		JTextField dataTxt = new JTextField();
		
		coffPanel.setLayout(new BoxLayout(coffPanel, BoxLayout.Y_AXIS));
		optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
		
		coffHeaderPanel.setLayout(new BoxLayout(coffHeaderPanel, BoxLayout.X_AXIS));
		optHeaderPanel.setLayout(new BoxLayout(optHeaderPanel, BoxLayout.X_AXIS));
		dataHeaderPanel.setLayout(new BoxLayout(dataHeaderPanel, BoxLayout.X_AXIS));
		
		coffPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		optPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		dataPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		coffPanel.setName("Coff Header");
		optPanel.setName("Optional Header");
		dataPanel.setName("Data Directories");
		
		initializeTextField(coffTxt, coffPanel);
		initializeTextField(optTxt, optPanel);
		initializeTextField(dataTxt, dataPanel);
		
		CoffFileHeader coffHeader = FileManager.getPEData(CoffFileHeader.class);
		OptionalHeader optionalHeader = FileManager.getPEData(OptionalHeader.class);
		DataDirectories dataHeader = FileManager.getPEData(DataDirectories.class);
		
		//In theory this will only run if something has exploded
		if(coffHeader == null || optionalHeader == null || dataHeader == null)
		{
			popupManager.displayNullErrorPopup();
		}
		
		infoPanel.removeAll();
		
		coffHeaderPanel.add(new JLabel("<html><h3>Coff Header</h3></html>"));
		addInfoLine(coffPanel, "Machine: ", coffHeader.getMachine());
		addInfoLine(coffPanel, "Number of Sections: ", coffHeader.getNumberOfSections());
		addInfoLine(coffPanel, "Timestamp: ", coffHeader.getTimeDateStamp());
		addInfoLine(coffPanel, "Pointer to Symbol Table: ", coffHeader.getPointerToSymbolTable());
		addInfoLine(coffPanel, "Characteristics: ", coffHeader.getCharacteristics());
		
		
		optHeaderPanel.add(new JLabel("<html><h3>Optional Header</h3></html>"));
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
		addInfoLine(optPanel, "Size of Image: ", String.format("0x%X (%.2f " + bytesFlag + ")", optionalHeader.getSizeOfImage(),  convertToKb(optionalHeader.getSizeOfImage())  ));
		addInfoLine(optPanel, "Size of Headers: ", optionalHeader.getSizeOfHeaders());
		addInfoLine(optPanel, "Subsystem Field: ", optionalHeader.getSubsystemField());
		addInfoLine(optPanel, "DLL Characteristics: ", optionalHeader.getDllCharacteristics());
		addInfoLine(optPanel, "Size of Stack Reserve: ", optionalHeader.getSizeOfStackReserve());
		addInfoLine(optPanel, "Size of Stack Commit: ", optionalHeader.getSizeOfStackCommit());
		addInfoLine(optPanel, "Size of Heap Reserve: ", optionalHeader.getSizeOfHeapReserve());
		addInfoLine(optPanel, "Size of Heap Commit: ", optionalHeader.getSizeOfHeapCommit());
		addInfoLine(optPanel, "Loader Flags: ", optionalHeader.getLoaderFlags());
		addInfoLine(optPanel, "Number of Data Directory Sections: ", optionalHeader.getNumberOfRVAAndSizes());
		
		dataHeaderPanel.add(new JLabel("<html><h3>Data Directories</h3></html>"));
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
		
		infoPanel.add(coffHeaderPanel);
		infoPanel.add(coffPanel);
		infoPanel.add(Box.createVerticalStrut(20));
		
		infoPanel.add(optHeaderPanel);
		infoPanel.add(optPanel);
		infoPanel.add(Box.createVerticalStrut(20));
		
		infoPanel.add(dataHeaderPanel);
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
		imports.toString();
		
		importPanel.add(new JLabel("<html><h3>Imports</h3></html>"));
		
		for(ImportTable i : imports)
		{
			importPanel.add(new JLabel("<html><b> "+ i.getDllName() +"</b></html>"));
			
			for(String process : i.getDllProcesses())
			{
				addInfoLine(importPanel, process);
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
			addStringsLine(stringsPanel, s);
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
			JPanel sectionsPanelHeader = new JPanel();
			sectionsPanelHeader.setLayout(new BoxLayout(sectionsPanelHeader, BoxLayout.Y_AXIS));
			sectionsPanelHeader.add(new JLabel("<html><h3>" + section.getName() + "</h3></html>"));
			sectionsPanel.add(sectionsPanelHeader);
			
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
	
	public void displayFileInformation() {
		JPanel hashPanel = new JPanel();
		hashPanel.setLayout(new BoxLayout(hashPanel, BoxLayout.Y_AXIS));
		hashPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		infoPanel.removeAll();
		
		addInfoLine(hashPanel, "File Size: ", String.format("0x%X (%.2f" + bytesFlag + ")", FileManager.getFileSize(), (convertToKb(FileManager.getFileSize()) )));
		addInfoLine(hashPanel,"MD5 Hash: ", FileManager.getFileHash().get(0));
		addInfoLine(hashPanel,"SHA-1 Hash: ", FileManager.getFileHash().get(1));
		addInfoLine(hashPanel,"SHA-256 Hash: ", FileManager.getFileHash().get(2));
		addInfoLine(hashPanel,"SHA-512 Hash: ", FileManager.getFileHash().get(3));
		
		infoPanel.add(hashPanel);
		infoPanel.revalidate();
		infoPanel.repaint();
	}
		
	/**
	 * Helper method for adding lines to info panel from parsed data
	 * @param label
	 * @param data
	 */
	public void addInfoLine(JPanel panel, String label, Object data) {
		JTextField txt = new JTextField();
		initializeTextField(txt, panel);
		
		if(data == null)
		{
			data = "NULL";
			txt.setText("<html><b>" + data + "</html></b>");
			panel.add(txt);
			return;
		}
		
		txt.setText(label + String.valueOf(data));
		panel.add(txt);
	}
	/**
	 * Overloaded version to accept arguments without a label attached
	 * @param panel
	 * @param data
	 */
	public void addInfoLine(JPanel panel, Object data) {
		JTextField txt = new JTextField();
		initializeTextField(txt, panel);
		if(data instanceof Integer) {
			data = String.valueOf(data);
		}
		if(data == null)
		{
			data = "NULL";
			txt.setText("<html><b>" + data + "</html></b>");
			panel.add(txt);
			return;
		}
		txt.setText(String.valueOf(data));
		panel.add(txt);
	}
	
	/**
	 * Performs the same function as addInfoLine(), but adds JLabels directly to panel instead of inside a JTextField
	 * <p>This is because separate JTextFields add a lot of whitespace, and the users doesn't really need to select/copy the output of strings</p>
	 * @param panel
	 * @param data
	 */
	public void addStringsLine(JPanel panel,  Object data) {

			panel.add(new JLabel(String.valueOf(data)));
			return;
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
		
		if(bytes >= 1000000)
		{
			return convertToMb(bytes);
		}
		bytesFlag = "kb";
		return bytes / 1000.0;
	}
	
	public void initializeTextField(JTextField txt, JPanel panel) {
		txt.setEditable(false);
		txt.setBackground(panel.getBackground());
	}
	
	public double convertToMb (long bytes) {
		
		bytesFlag = "mb";
		return bytes / (1024.0 * 1024);
	}
	
	public ExceptionManager getExceptionManager() {
		return this.exceptionManager;
	}
	
	
}
