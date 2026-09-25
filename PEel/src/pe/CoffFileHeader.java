package pe;

import java.util.*;

import file.PEFile;

public class CoffFileHeader {
	private final String machine;
	private final int numberOfSections;
	private final Date timeDateStamp;
	private final long pointerToSymbolTable; //Deprecated
	private final int optionalHeaderSize;
	private final ArrayList<String> characteristics;
	
public CoffFileHeader(String machine, 
					  int numberOfSections, 
					  Date timeDateStamp, 
					  long pointerToSymbolTable, 
					  int optionalHeaderSize, 
					  ArrayList<String> characteristics) {
	this.machine = machine;
	this.numberOfSections = numberOfSections;
	this.timeDateStamp = timeDateStamp;
	this.pointerToSymbolTable = pointerToSymbolTable;
	this.optionalHeaderSize = optionalHeaderSize;
	this.characteristics = characteristics;
}

public String getMachine() {
	return machine;
}

public int getNumberOfSections() {
	return numberOfSections;
}

public Date getTimeDateStamp() {
	return timeDateStamp;
}

public long getPointerToSymbolTable() {
	return pointerToSymbolTable;
}

public int getOptionalHeaderSize() {
	return optionalHeaderSize;
}

public ArrayList<String> getCharacteristics() {
	return characteristics;
}

public String isDLL() {
	if(this.characteristics.contains("Dynamic-link library (DLL)"))
	{
		return "DLL";
	}
	return "Executable Image";
}
@Override
public String toString() {
	return "Coff File Header: machine=" + machine + ", numberOfSections=" + numberOfSections + ", timeDateStamp="
			+ timeDateStamp + ", pointerToSymbolTable=" + pointerToSymbolTable + ", optionalHeaderSize="
			+ optionalHeaderSize + ", characteristics=" + characteristics + "]";
}

	
	
}
