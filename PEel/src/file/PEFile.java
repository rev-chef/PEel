package file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import exceptions.CharacteristicsFieldInvalidException;
import exceptions.ErrorDisplay;
import exceptions.ExceptionManager;
import exceptions.OptionalHeaderSizeInvalidException;
import pe.*;


public class PEFile extends ExecutableFile {
	private RandomAccessFile PE;
	private int ntHeaderOffset;
	private int coffHeaderOffset;
	
	//Constants for reading different byte lengths
	public static final int BYTE = 1;
	public static final int SHORT = 2;
	public static final int RGBTRIPLE = 3; //RGBTRIPLE pog
	public static final int DWORD = 4;
	public static final int QWORD = 8;
	
	private CoffFileHeader coffFileHeader;
	private OptionalHeader optionalHeader;
	private DataDirectories dataHeader;
	private ImportTableList importTables;
	private ExportTable exportTable;
	private PEStringsList stringsList;
	private ExceptionManager exceptionManager;
	
	private Map<String, String> machineTypes = new HashMap<>();
	private Map<Integer, String> charFlags = new HashMap<>();
	private Map<Integer, String> subsystems = new HashMap<>();
	private Map<Integer, String> dllCharacteristics = new HashMap<>();
	
	private ArrayList<String> foundCharacteristics = new ArrayList<>();
	ArrayList<String> foundDllCharacteristics = new ArrayList<>();
	private Map<String, Integer> susFlags = new HashMap<>();
	private ArrayList<SectionHeader> sections = new ArrayList<>();
	
	private long sizeOfStackReserve;
	private long sizeOfStackCommit;
	private long sizeOfHeapReserve;
	private long sizeOfHeapCommit;
	private long loaderFlags;
	private long imageBase;
	private long numberOfRvaAndSizes;
	
	public PEFile(File file, ExceptionManager em) {
			super(file);
			exceptionManager = em;
		try {
			this.PE = new RandomAccessFile(file, "r");
			
		} 
		catch (FileNotFoundException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		this.machineTypes = loadMachineTypes();
		this.charFlags = loadCharacteristics();
		this.subsystems = loadSubsystems();
		this.dllCharacteristics = loadDLLCharacteristics();
	}
	
	@Override
	public void parse() {
		try {
			
			//Seek e_lfanew
			PE.seek(0x3c);
			
			//Find out where the NT Header Offset is located, store it
			ntHeaderOffset = Integer.reverseBytes(PE.readInt());
			
			coffHeaderOffset = ntHeaderOffset + 4;
			
			
			PE.seek(coffHeaderOffset); //After this seek, we parse linearly without saving more offsets to variables
			
			/*
			 * Seeks the beginning of COFF header, reads 2 byte machine code
			 * Compares to list of machine codes based on official Microsoft Documentation
			 */
			int machineValue = (int) PEFile.readAndConvert(PE, SHORT);

			String hexKey = "0x" + Integer.toHexString(machineValue);
			
			String machineName = machineTypes.get(hexKey);
			
			//Seek number of sections
			int numSections = (int) PEFile.readAndConvert(PE, SHORT);
			//Seek timestamp and convert to date format
			
			long readTimeStamp= PEFile.readAndConvert(PE, DWORD);
			Date timeStamp = new Date(readTimeStamp * 1000L);
			SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
			//IGNORE COFF SYMBOL TABLES (FOR NOW)
			
			long pointerToSymbolTable = PEFile.readAndConvert(PE, QWORD);
			
			//Size of optional header
			int optionalHeaderSize = (int) PEFile.readAndConvert(PE, SHORT);
			
			//Characteristics section
			int rawCharacteristicsHex = (int) PEFile.readAndConvert(PE, SHORT);
			decodeCharValue(rawCharacteristicsHex);
			
			 
			CoffFileHeader coffFileHeader = new CoffFileHeader(machineName, numSections, timeStamp, pointerToSymbolTable, optionalHeaderSize, foundCharacteristics);
			
			this.coffFileHeader = coffFileHeader;
			FileManager.setPEData(coffFileHeader.getClass(), coffFileHeader);
			
			//Optional Header
			int optMagicNum = (int) PEFile.readAndConvert(PE, SHORT);
			readOptHeaderMagicNum(optMagicNum);
			boolean pe32 = optMagicNum == 0x10b;
			
			byte majorLinkerVersion = (byte) PEFile.readAndConvert(PE, BYTE);
			
			byte minorLinkerVersion = (byte) PEFile.readAndConvert(PE, BYTE);
			
			//Optional header info
			long sizeOfCode = PEFile.readAndConvert(PE, DWORD);
			
			long sizeOfInitializedData = PEFile.readAndConvert(PE, DWORD);
			
			long sizeOfUninitializedData = PEFile.readAndConvert(PE, DWORD);
			
			long addressOfEntryPoint = PEFile.readAndConvert(PE, DWORD);
						
			long baseOfCodeSection = PEFile.readAndConvert(PE, DWORD);
			
			//File splits into separate paths for PE32 or PE32+
			if(optMagicNum == 0x10b)
			{
				parsePE32(PE, 24);
			}
			else if(optMagicNum == 0x20b)
			{
				parsePE32Plus(PE, 24);
			}
			
			//File offsets reconverge after imageBase section is read
			long sectionAlignment = PEFile.readAndConvert(PE, DWORD);
			
			long fileAlignment = PEFile.readAndConvert(PE, DWORD);
			
			//Skip OS system versions / Image versions (FOR NOW)
			PE.skipBytes(QWORD);
			
			int majorSubsystemVersion = (int) PEFile.readAndConvert(PE, SHORT);
			int minorSubsystemVersion = (int) PEFile.readAndConvert(PE, SHORT);
			String subsystemVersion = checkSubsystemVersion(majorSubsystemVersion, minorSubsystemVersion);
			long win32VersionValue = PEFile.readAndConvert(PE, DWORD);
			
			if(win32VersionValue != 0) {susFlags.put("win32VersionValue NOT 0? (Must always be 0)", 1);} //Im not creating a helper method for this
			
			long sizeOfImage = PEFile.readAndConvert(PE, DWORD);
			
			long sizeOfHeaders = PEFile.readAndConvert(PE, DWORD);
			
			//long checksum = PEFile.readAndConvert(PE, DWORD); //Ignore for now
			PE.skipBytes(DWORD);
			
			int subsystemField = (int) PEFile.readAndConvert(PE, SHORT);
			
			int dllCharacteristics = (int) PEFile.readAndConvert(PE, SHORT);
			for(String s : decodeDLLCharacteristics(dllCharacteristics))
			{
				foundDllCharacteristics.add(s);
			}			
			
			if(optMagicNum == 0x10b)
			{
				numberOfRvaAndSizes = parsePE32(PE, 72);
			}
			else if(optMagicNum == 0x20b)
			{
				numberOfRvaAndSizes = parsePE32Plus(PE, 72);
			}
			OptionalHeader optionalHeader = new OptionalHeader(
					pe32,
					optMagicNum, 
					majorLinkerVersion, 
					minorLinkerVersion, 
					sizeOfCode, 
					sizeOfInitializedData, 
					sizeOfUninitializedData, 
					addressOfEntryPoint, 
					baseOfCodeSection, 
					imageBase, 
					sectionAlignment, 
					fileAlignment, 
					subsystemVersion, 
					win32VersionValue, 
					sizeOfImage, 
					sizeOfHeaders, 
					subsystemField, 
					foundDllCharacteristics, 
					sizeOfStackReserve, 
					sizeOfStackCommit, 
					sizeOfHeapReserve, 
					sizeOfHeapCommit,
					loaderFlags,
					numberOfRvaAndSizes);
			
			this.optionalHeader = optionalHeader;
			
			FileManager.setPEData(optionalHeader.getClass(), optionalHeader);
			
			long exportTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long exportTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long importTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long importTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long resourceTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long resourceTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long exceptionTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long exceptionTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long certificateTableOffset = PEFile.readAndConvert(PE, DWORD);
			
			long certificateTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long baseRelocTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long baseRelocTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long debugDataRVA = PEFile.readAndConvert(PE, DWORD);
			
			long debugDataSize = PEFile.readAndConvert(PE, DWORD);
			
			long architecture = PEFile.readAndConvert(PE, QWORD);
			if(architecture != 0) {susFlags.put("Architecture header non-zero (reserved-section)", 1);}
			
			long globalPtrRVA = PEFile.readAndConvert(PE, DWORD);
			
			long globalPtrSize = PEFile.readAndConvert(PE, DWORD);
			
			long tlsTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long tlsTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long loadConfigTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long loadConfigTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long boundImportTableRVA = PEFile.readAndConvert(PE, DWORD);
			
			long boundImportTableSize = PEFile.readAndConvert(PE, DWORD);
			
			long IATRVA = PEFile.readAndConvert(PE, DWORD);
			
			long IATSize = PEFile.readAndConvert(PE, DWORD);
			
			long delayImportDescriptorRVA = PEFile.readAndConvert(PE, DWORD);
			
			long delayImportDescriptorSize = PEFile.readAndConvert(PE, DWORD);
			
			long CLRRuntimeRVA = PEFile.readAndConvert(PE, DWORD);
			
			long CLRRuntimeSize = PEFile.readAndConvert(PE, DWORD);
			
			long unnamedReserved = PEFile.readAndConvert(PE, QWORD);
			if(unnamedReserved != 0) {susFlags.put("Reserved section in data directory header non-zero", 1);}
			
			DataDirectories dataDirectories = new DataDirectories(
				    exportTableRVA,
				    exportTableSize,
				    importTableRVA,
				    importTableSize,
				    resourceTableRVA,
				    resourceTableSize,
				    exceptionTableRVA,
				    exceptionTableSize,
				    certificateTableOffset,
				    certificateTableSize,
				    baseRelocTableRVA,
				    baseRelocTableSize,
				    debugDataRVA,
				    debugDataSize,
				    architecture,
				    globalPtrRVA,
				    globalPtrSize,
				    tlsTableRVA,
				    tlsTableSize,
				    loadConfigTableRVA,
				    loadConfigTableSize,
				    boundImportTableRVA,
				    boundImportTableSize,
				    IATRVA,
				    IATSize,
				    delayImportDescriptorRVA,
				    delayImportDescriptorSize,
				    CLRRuntimeRVA,
				    CLRRuntimeSize,
				    unnamedReserved
				);
			this.dataHeader = dataDirectories;
			
			FileManager.setPEData(dataDirectories.getClass(), dataDirectories); 
			
			for(int i = 0; i < coffFileHeader.getNumberOfSections(); i++)
			{
				String name = readSectionHeaderName(PE);
				long virtualSize = PEFile.readAndConvert(PE, DWORD);
				long virtualAddress = PEFile.readAndConvert(PE, DWORD);
				long sizeOfRawData = PEFile.readAndConvert(PE, DWORD);
				long pointerToRawData = PEFile.readAndConvert(PE, DWORD);
				long pointerToRelocs = PEFile.readAndConvert(PE, DWORD);
				long pointerToLineNumbers = PEFile.readAndConvert(PE, DWORD);
				int numberOfRelocs = (int) PEFile.readAndConvert(PE, SHORT);
				int numberOfLineNumbers = (int) PEFile.readAndConvert(PE, SHORT);
				long characteristics = PEFile.readAndConvert(PE, DWORD);
				
				sections.add(new SectionHeader(
						name, virtualSize, virtualAddress, sizeOfRawData, pointerToRawData, pointerToRelocs,
						pointerToLineNumbers, numberOfRelocs, numberOfLineNumbers, characteristics));
			}
			
			//Seek the beginning of import section
			PE.seek(rvaToOffset(importTableRVA));
			
			this.importTables = readImportTable();
			
			if(exportTableRVA != 0)
			{
			PE.seek(rvaToOffset(exportTableRVA));
			
			this.exportTable = readExportTable();
			}
			else
			{
				this.exportTable = null;
			}
			
			this.stringsList = new PEStringsList(strings()); 
			
			FileManager.setMalformFlag(checkForMalform());
			
			FileManager.setPEData(PEStringsList.class, stringsList);
			
			FileManager.setPEData(ImportTableList.class, importTables);
			
			FileManager.setPEData(ExportTable.class, exportTable); 
			
			FileManager.setSectionHeaders(sections);
			
			FileManager.setSusFlags(susFlags);
			
			
		} 
		
		
		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		} 

		catch (CharacteristicsFieldInvalidException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
	}
	public String readSectionHeaderName(RandomAccessFile PE) {
		byte[] nameBytes = new byte[QWORD];
		String name = "";
		try 
		{
			
		PE.readFully(nameBytes);
		 
		name = new String(nameBytes, StandardCharsets.US_ASCII).split("\0", 2)[0];
		
		}
		
		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		return name;
	}
	
	public ImportTableList readImportTable() {
		String dllName = new String();
		long importLookupRVA; 
		long timeStamp;
		long forwarderChain;
		long nameRVA;
		long thunkTable;
		ArrayList<String> processes = new ArrayList<>();
		ImportTableList imports = new ImportTableList();
		try 
		{
			while(true)
			{
				//TODO: Store these in an object
				importLookupRVA = PEFile.readAndConvert(PE, DWORD);
				timeStamp = PEFile.readAndConvert(PE, DWORD);
				forwarderChain = PEFile.readAndConvert(PE, DWORD);
				nameRVA = PEFile.readAndConvert(PE, DWORD);
				thunkTable = PEFile.readAndConvert(PE, DWORD);
				
				
				if(importLookupRVA == 0 && timeStamp == 0 && forwarderChain == 0 && nameRVA == 0 && thunkTable == 0)
				{
					break;
				}
				
				long nameOffset = rvaToOffset(nameRVA);
				long importTableOffset = rvaToOffset(importLookupRVA);
				
				long currentPointerPosition = PE.getFilePointer(); //Preserve current pointer position
				PE.seek(nameOffset);
				dllName = readAsciiString(PE);
				
				processes = resolveImportLookupTable(importTableOffset);
				imports.add(new ImportTable(dllName, importLookupRVA, timeStamp, forwarderChain, nameRVA, thunkTable, processes));
				
				
				
				PE.seek(currentPointerPosition); //Reset pointer position
				
				
			}
			
			return imports;
			
		} 
		
		
		catch (IOException e) 
		{
			exceptionManager.handleException(e.getMessage(), "error");
		}
		return null;
	}
	
	/**
	 * Helper method for readImportTable that finds all imported methods of a DLL, handles both ordinal and hint/name table based imports
	 * @param offset
	 * @return
	 */
	public ArrayList<String> resolveImportLookupTable(long offset) {
		ArrayList<String> processes = new ArrayList<>();
		try 
		{
			PE.seek(offset);
			
			boolean pe32 = optionalHeader.isPe32();
			
			//This must loop through both the ordinal logic and name logic since each import may be different
			while(true) 
			{
				long thunk = pe32 ? PEFile.readAndConvert(PE, DWORD) : PEFile.readAndConvert(PE, QWORD);
				
				if(thunk == 0) { break; }
				
				//determine if imported by ordinal or name/hint table
				long ordinalBitMask = pe32 ? 0x80000000L : 0x8000000000000000L;
				boolean ordinalImport = (thunk & ordinalBitMask) != 0;
				
				//Bitmask for PE32 name is not required, but fuck it
				long nameBitmask = pe32 ? 0xFFFFFFFFL : 0x7FFFFFFFFFFFFFFFL;
					
					
				if(ordinalImport)
				{
					//If Imported by ordinal number:
					long ordinalNumber = thunk & ordinalBitMask; //Zero out high bits
					processes.add("ORDINAL NUMBER:" + ordinalNumber);
					//TODO: resolve ordinal import names
					
				}
				
				else
				{
					//Non-ordinal import (Hint/Name table)
					long nameRVA = thunk & nameBitmask;
					long nameTableOffset = rvaToOffset(nameRVA);
					
					//Save position before reading import name
					long currentPosition = PE.getFilePointer();
					
					PE.seek(nameTableOffset);
					
					//Hint is for loader optimization, not important
					int hint = (int) PEFile.readAndConvert(PE, SHORT);
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						
					byte b;
					while ((b = PE.readByte()) != 0) 
					{
					    buffer.write(b);
					}
						
					String importName = buffer.toString(StandardCharsets.US_ASCII);
					processes.add(importName);
					PE.seek(currentPosition); //Return to position
				}	
			  }	
		   } 
		
		catch (IOException e) {exceptionManager.handleException(e.getMessage(), "error");}
		return processes;
		
	}
	
	@Override
	public void setExceptionManager(ExceptionManager em) {
		this.exceptionManager = em; 
	}
	
	public ExportTable readExportTable() {
		
		ArrayList<String> names = new ArrayList<>();
		ExportTable export;
		try 
		{
			
				long exportFlags = PEFile.readAndConvert(PE, DWORD);
				long timeDateStamp = PEFile.readAndConvert(PE, DWORD);
				long majorVersion = PEFile.readAndConvert(PE, SHORT);
				long minorVersion = PEFile.readAndConvert(PE, SHORT);
				long nameRVA = PEFile.readAndConvert(PE, DWORD);
				long ordinalBase = PEFile.readAndConvert(PE, DWORD);
				long addrTableEntries = PEFile.readAndConvert(PE, DWORD);
				long numNamePointers = PEFile.readAndConvert(PE, DWORD);
				long exportAddressRVA = PEFile.readAndConvert(PE, DWORD);
				long namePointerRVA = PEFile.readAndConvert(PE, DWORD);
				long ordinalTableRVA = PEFile.readAndConvert(PE, DWORD);
			
				long currentPosition = PE.getFilePointer();
				
				
				PE.seek(rvaToOffset(namePointerRVA));
				
				for(int i = 0; i < numNamePointers; i++)
				{
					long nameOffset = rvaToOffset(PEFile.readAndConvert(PE, DWORD));
					
					long currentPos = PE.getFilePointer();
					
					PE.seek(nameOffset);
					String exportName = readAsciiString(PE);
					
					names.add(exportName);
					
					PE.seek(currentPos);
				}
				
				export = new ExportTable(names, exportFlags, timeDateStamp, majorVersion, 
				                       minorVersion, nameRVA, ordinalBase, addrTableEntries, 
				                       numNamePointers, exportAddressRVA, namePointerRVA, ordinalTableRVA);
				PE.seek(currentPosition);
				
				return export;
		}
		

		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		return null;
	}
	
	/**
	 * Uses StringBuilder class to read bytes from the file and convert to Ascii characters. Stops when reaches a null byte.
	 * This method should only be used to read null-terminated strings.
	 * @param Random Access File
	 * @return String from the file interpreted as ASCII characters converted from raw bytes
	 * @throws IOException
	 */
	public static String readAsciiString(RandomAccessFile file) throws IOException {

	    StringBuilder sb = new StringBuilder();

	    while (true) {
	        int b = file.readUnsignedByte();

	        if (b == 0) {
	            break;
	        }

	        sb.append((char)b);
	    }

	    return sb.toString();
	}
	
	public static String readStrings(RandomAccessFile file) throws IOException {

	    StringBuilder sb = new StringBuilder();

	    while (file.getFilePointer() < file.length()) {
	        int b = file.readUnsignedByte();

	        if (b >= 0x20 && b <= 0x7E) //Valid extended ASCII range of characters
	        {
	            sb.append((char)b);
	        } 
	        
	        else 
	        {
	            break;
	        }

	    }

	   return sb.length() > 4 ? sb.toString() : null;
	}
	
	//DO NOT DELETE, REQUIRED INHERITED METHOD
	public File getFile() {
		return null;
	}
	
	/**
	 * Loads from .txt file in res folder
	 * @return Map of machine types and their corresponding hex codes based on Microsoft documentation
	 */
	public Map<String, String> loadMachineTypes() {
		
		InputStream input = getClass()
	            .getClassLoader()
	            .getResourceAsStream("MachineTypes.txt");

	    if (input == null) {
	        exceptionManager.handleException(
	            "Could not find MachineTypes.txt",
	            "error"
	        );
	        return machineTypes;
	    }
	    Scanner file = new Scanner(input);
		while(file.hasNextLine())
		{
			String line = file.nextLine();
			String[] data = line.split(",");
			
			String hexKey = data[0].trim();
			String machineName = data[1].trim();
			machineTypes.put(hexKey, machineName);
		}
		file.close();
		return machineTypes;
	}
	
	/**
	 * Helper method to automatically parse section and convert to little-endian
	 * @return the bytes read from the RAF as a long
	 * @param RAF, size to read in bytes (use constants)
	 */
	public static long readAndConvert(RandomAccessFile f, int sizeToRead) throws IOException {
		long readData = 0; //"read" as in past-tense, like "I read a book yesterday". English sucks man.
		switch(sizeToRead)
		{
		case 1:
			readData = Byte.toUnsignedLong(f.readByte());
			break;
			
		case 2:
			readData = Short.toUnsignedLong(Short.reverseBytes(f.readShort()));
			break;
		
		case 3:
			//Manual 3-byte read, fml 
			//These must be int so we can read an unsigned byte, since we only read values 0-255, fml
			int bh = f.readUnsignedByte();
			int bm = f.readUnsignedByte();
			int bl = f.readUnsignedByte();
			
			//Order bytes and convert to Little-endian, fml
			readData = (bl << 16) | (bm << 8) | bh;
			break;
		case 4:
			readData = Integer.toUnsignedLong(Integer.reverseBytes(f.readInt()));
			break;
		
		case 8:
			readData = Long.reverseBytes(f.readLong());
			break;
			
		default:
			System.out.println("Invalid size selection");
		}
		
		return readData;
	}
		/**
		 * Checks the size of the optional header for indications of malformation
		 * Checks against 3 known valid values
		 * @param optionalHeaderSize
		 * @return Message indicating state of the PE's optional header size
		 * @throws OptionalHeaderSizeInvalidException
		 */
	public static String checkOptionalHeaderSize(int optionalHeaderSize) {
		String message = null;
		String giveSize = "Optional header size is: " + optionalHeaderSize + " ";
		if (optionalHeaderSize == 224)
		{	
			message = ("Which indicates PE32 format (32-bit executable)");
		}
		else if(optionalHeaderSize == 240)
		{
			message = ("Which indicates PE32+ format (64-bit executable)");
		}
		else if(optionalHeaderSize == 0)
		{
			message = ("Which indicates Object file/ Unknown file format (Optional Header Size is zero)");
		}
		else
		{
			message = ("Which does not indicate a certain format");
		}
		return giveSize + message;
	}
	
	public Map<Integer, String> loadCharacteristics() {
		
		Scanner file;
		try {
			file = new Scanner(new File("res/CharacteristicsFlags.txt"));
		
			while(file.hasNextLine())
			{
				String line = file.nextLine();
				String[] data = line.split(",");
				
				String hexString = data[0].trim();
				int hexKey = Integer.decode(hexString);
				String charName = data[1].trim();
				charFlags.put(hexKey, charName);
			}
			file.close();
		} 
		catch (FileNotFoundException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		return charFlags;
	}
	
	public Map<Integer, String> loadSubsystems() {
			
		InputStream input = getClass()
	            .getClassLoader()
	            .getResourceAsStream("Subsystems.txt");

	    if (input == null) {
	        exceptionManager.handleException(
	            "Could not find Subsystems.txt",
	            "error"
	        );
	        return subsystems;
	    }
	    
	    Scanner file = new Scanner(input);
	    
			
				while(file.hasNextLine())
				{
					String line = file.nextLine();
					String[] data = line.split(",");
					//the first string of the line is not required, taken from official microsoft documentation
					String systemCode = data[1].trim();
					int hexKey = Integer.decode(systemCode);
					String description = data[2].trim();
					subsystems.put(hexKey, description);
				}
				file.close();
		
			return subsystems;
		}

	public Map<Integer, String> loadDLLCharacteristics() {
		
		InputStream input = getClass()
	            .getClassLoader()
	            .getResourceAsStream("DLLCharacteristics.txt");

	    if (input == null) {
	        exceptionManager.handleException(
	            "Could not find MachineTypes.txt",
	            "error"
	        );
	        return dllCharacteristics;
	    }
	    
	    Scanner file = new Scanner(input);
		
			while(file.hasNextLine())
			{
				String line = file.nextLine();
				String[] data = line.split(",");
				//the first string of the line is not required, taken from official microsoft documentation
				String value = data[1].trim();
				int hexKey = Integer.decode(value);
				String description = data[2].trim();
				dllCharacteristics.put(hexKey, description);
			}
			file.close();
		 
		return dllCharacteristics;
	}
	/**
	 * 
	 * @param subsystem value read from PE
	 * @return Subsystem description of the decoded subsystem value according to official Microsoft documentation
	 */
	public String decodeSubsystem(int hexkey) {
		for(Integer key : subsystems.keySet())
		{
			if(hexkey == key)
			{
				return subsystems.get(key);
			}
			else
			{
				susFlags.put("Subsystem Invalid", 1);
				return "Error or invalid subsystem";
			}
		}
		return null; //This should never run. Java complains without it here
	}
	
	
	/**
	 * Decodes the 4-byte characteristic hex code from the PE
	 * @param charHex: raw hexcode read from PE
	 * @return Characteristics that match those of the PE's given hex code based on Microsoft documentation
	 */
			
	public ArrayList<String> decodeCharValue(int charHex) throws CharacteristicsFieldInvalidException {
		for(Integer key : charFlags.keySet())
		{
			if((charHex & key) != 0)
			{
				foundCharacteristics.add(charFlags.get(key));
			}
		}
		return foundCharacteristics;
	}
	
	public ArrayList<String> decodeDLLCharacteristics(int hexKey) {
		for(Integer key : dllCharacteristics.keySet())
		{
			if((hexKey & key) != 0)
			{
				foundCharacteristics.add(dllCharacteristics.get(key));
			}
		}
		return foundCharacteristics;
	}
	
	public String readOptHeaderMagicNum(int optMagicNum) {
		String fileType;
		switch(optMagicNum)
		{
		case(0x20B):
			fileType = "PE32+ (64 bit)";
			break;
		case(0x10B):
			fileType = "PE32 (32 bit)";
			break;
		case(0):
			fileType = "Value is 0, .obj file?";
		default:
			fileType = "Unknown";
		}
		return fileType;
	}
	
	public boolean checkDLL() {
		boolean isDLL = false;
		
		for(String s : foundCharacteristics)
		{
			if(s.contains("DLL")) {isDLL=true;}
		}
		//Check export table here
		
		return isDLL;
	}
	
	private long parsePE32(RandomAccessFile PE, int pointerOffset) {
		//Parse baseOfData
		try {
			switch(pointerOffset)
			{
			case 24:
				long baseOfData = PEFile.readAndConvert(PE, DWORD);
				
				long imageBase = PEFile.readAndConvert(PE, DWORD);
				this.imageBase = imageBase;
				
				checkImageBase(imageBase);
				return imageBase;
			
			case 72:
				//Useless fields, parse for now
				long sizeOfStackReserve = PEFile.readAndConvert(PE, DWORD);
				this.sizeOfStackReserve = sizeOfStackReserve;
				
				long sizeOfStackCommit = PEFile.readAndConvert(PE, DWORD);
				this.sizeOfStackCommit = sizeOfStackCommit;
				
				long sizeOfHeapReserve = PEFile.readAndConvert(PE, DWORD);
				this.sizeOfHeapReserve = sizeOfHeapReserve;
				
				long sizeOfHeapCommit = PEFile.readAndConvert(PE, DWORD);
				this.sizeOfHeapCommit = sizeOfHeapCommit;
				
				long loaderFlags = PEFile.readAndConvert(PE, DWORD); //This field is essentially irrelevant, a non-zero value is nothing special
				if(loaderFlags != 0) {susFlags.put("Loader flags not 0 (reserved section)", 1);}
				this.loaderFlags = loaderFlags;
				
				//Extremely useful field
				long numberOfRVAAndSizes = PEFile.readAndConvert(PE, DWORD);
				this.numberOfRvaAndSizes = numberOfRVAAndSizes;
				return numberOfRVAAndSizes;
				//File is now at offset 96
			}
			return 0;
		} 
		

		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
			return -1;
		}
	}
	
	private long parsePE32Plus(RandomAccessFile PE, int pointerOffset) {
		//Skip baseOfData
		try {
			switch(pointerOffset)
			{
			case 24:
				
				long imageBase = PEFile.readAndConvert(PE, QWORD); //8 bytes long on PE32+
				this.imageBase = imageBase;
				checkImageBase(imageBase);
			break;
			
			case 72:
				//Useless fields, parse for now
				long sizeOfStackReserve = PEFile.readAndConvert(PE, QWORD);
				this.sizeOfStackReserve = sizeOfStackReserve;
				
				long sizeOfStackCommit = PEFile.readAndConvert(PE, QWORD);
				this.sizeOfStackCommit = sizeOfStackCommit;
				
				long sizeOfHeapReserve = PEFile.readAndConvert(PE, QWORD);
				this.sizeOfHeapReserve = sizeOfHeapReserve;
				
				long sizeOfHeapCommit = PEFile.readAndConvert(PE, QWORD);
				this.sizeOfHeapCommit = sizeOfHeapCommit;
				
				long loaderFlags = PEFile.readAndConvert(PE, DWORD); //This field is essentially irrelevant, a non-zero value is nothing special
				if(loaderFlags != 0) {susFlags.put("Loader flags not 0 (reserved section)", 1);}
				this.loaderFlags = loaderFlags;
				
				//Extremely useful field
				long numberOfRVAAndSizes = PEFile.readAndConvert(PE, DWORD);
				this.numberOfRvaAndSizes = numberOfRVAAndSizes;
				return numberOfRVAAndSizes;
				//File is now at offset 112
			}
			return 0;
		} 
		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
			return -1;
		}
	}
	
	/**
	 * Converts a parsed input RVA value into raw file offset that can be sought using RAF
	 * @param RVA value
	 * @return Raw file offset
	 */
	private long rvaToOffset(long rva) {
		
		for (SectionHeader section : sections) 
		{
		    long start = section.getVirtualAddress();
		    long end = start + section.getVirtualSize();

		    if (rva >= start && rva < end) 
		    {
		        return section.getPointerToRawData() + (rva - start);
		    }
		}
		return 0;
	}
	
	private void checkImageBase(long imageBase) {
		
		if(imageBase % 65536 == 0) //64kb
		{
			return;
		}
		else
		{
			susFlags.put("ImageBase size invalid", 2);
		}
		
	}
	
	private boolean checkForMalform() {
		
		boolean secSizeDiff = false;
		boolean lowImports = false;
		
		int i = 0;
		for(SectionHeader section : sections)
		{
			if(section.getName().matches("UPX.")) {return true;}
			
			if(section.getVirtualSize() > section.getSizeOfRawData())
			{
				i++;
			}
		}
		
		if(i >= (coffFileHeader.getNumberOfSections() / 2)) //Truncated integer division is intended. Rough majority
		{
			secSizeDiff = true;
		}
		
		if(importTables.size() < 5)
		{
			lowImports = true;
		}
		
		return secSizeDiff && lowImports;
	}
	
	private String checkSubsystemVersion (int major, int minor) {
		//NOT BULLETPROOF, These subsystem numbers are not standardized in any way at all because why would they be
		//I had AI generate this list
		switch(major)
		{
		case 4:
			if(minor == 1)
			{
				return "Windows NT 4.0-ish";
			}
			else
			{
				return "Windows 98";
			}
		case 5:
			switch(minor)
			{
				case 0:
					return "Windows 2000";
				case 1:
					return "Windows XP";
				case 2:
					return "Windows Server 2003";
				default:
					return "Windows 5.X Unkown Variant";
			}
		case 6:
			switch(minor)
			{
				case 0:
					return "Windows Vista";
				case 1:
					return "Windows 7";
				case 2:
					return "Windows 8";
				case 3:
					return "Windows 8.1";
				default:
					return "Windows 6.X Unknown Variant";
			}
		case 10:
			return "Windows 10/11";
			
		default:
			susFlags.put("Unknown Subsystem Version", 1);
			return "Future or Unkown Windows Subsystem Version";
			
		}
	}
	private ArrayList<String> strings() {
		ArrayList<String> allStrings = new ArrayList<>();
		try 
		{
			long previousPointer = PE.getFilePointer();
			long loopPointer = 0;
			PE.seek(0);
			
			while(loopPointer < PE.length())
			{
				String parsedString = readStrings(PE);
				loopPointer = PE.getFilePointer();
				
				if(parsedString != null)
				{
				allStrings.add(parsedString);
				}
				
			}
			
			PE.seek(previousPointer);
			return allStrings;
		} 
		catch (EOFException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		catch (IOException e) {
			exceptionManager.handleException(e.getMessage(), "error");
		}
		return allStrings;
	}


	
	
	
}
