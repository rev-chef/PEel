package pe;

import java.util.ArrayList;

public class OptionalHeader {
	private boolean pe32; //True = PE32, False = PE32+
	private int magic;
	private byte majorLinkerVersion;
	private byte minorLinkerVersion;
	private long sizeOfCode;
	private long sizeOfInitializedData;
	private long sizeOfUninitializedData;
	private long addressOfEntryPoint;
	private long baseOfCodeSection;
	//Split
	private long baseOfData; //Only for PE32
	private long imageBase;
	//Re-converge
	private long sectionAlignment;
	private long fileAlignment;
	private String subsystemVersion; //Combined both major and minor
	private long win32VersionValue; //Should always be 0
	private long sizeOfImage;
	private long sizeOfHeaders;
	private int subsystemField;
	private ArrayList<String> dllCharacteristics;
	//Split again
	private long sizeOfStackReserve;
	private long sizeOfStackCommit;
	private long sizeOfHeapReserve;
	private long sizeOfHeapCommit;
	private long loaderFlags;
	private long numberOfRVAAndSizes; //Used to determine number of data directories
	
public OptionalHeader(boolean pe32, 
					  int magic, 
					  byte majorLinkerVersion, 
					  byte minorLinkerVersion, 
					  long sizeOfCode, 
					  long sizeOfInitializedData, 
					  long sizeOfUninitializedData, 
					  long addressOfEntryPoint, 
					  long baseOfCodeSection, 
					  long imageBase, //Base of data as a setter
					  long sectionAlignment, 
					  long fileAlignment, 
					  String subsystemVersion, 
					  long win32Versionvalue, 
					  long sizeOfImage, 
					  long sizeOfHeaders, 
					  int subsystemField, 
					  ArrayList<String> dllCharacteristics, 
					  long sizeOfStackReserve, 
					  long sizeOfStackCommit, 
					  long sizeOfHeapReserve, 
					  long sizeOfHeapCommit, 
					  long loaderFlags, 
					  long numberOfRvaAndSizes) {
	
	this.pe32 = pe32;
	this.magic = magic;
	this.majorLinkerVersion = majorLinkerVersion;
	this.minorLinkerVersion = minorLinkerVersion;
	this.sizeOfCode = sizeOfCode;
	this.sizeOfInitializedData = sizeOfInitializedData;
	this.sizeOfUninitializedData = sizeOfUninitializedData;
	this.addressOfEntryPoint = addressOfEntryPoint;
	this.baseOfCodeSection = baseOfCodeSection;
	this.imageBase = imageBase;
	this.sectionAlignment = sectionAlignment;
	this.fileAlignment = fileAlignment;
	this.subsystemVersion = subsystemVersion;
	this.win32VersionValue = win32Versionvalue;
	this.sizeOfImage = sizeOfImage;
	this.sizeOfHeaders = sizeOfHeaders;
	this.subsystemField = subsystemField;
	this.dllCharacteristics = dllCharacteristics;
	this.sizeOfStackReserve = sizeOfStackReserve;
	this.sizeOfStackCommit = sizeOfStackCommit;
	this.sizeOfHeapReserve = sizeOfHeapReserve;
	this.loaderFlags = loaderFlags;
	this.numberOfRVAAndSizes = numberOfRvaAndSizes;
	
	
}
	
	public boolean isPe32() {
		return pe32;
	}
	public int getMagic() {
		return magic;
	}
	public byte getMajorLinkerVersion() {
		return majorLinkerVersion;
	}
	public byte getMinorLinkerVersion() {
		return minorLinkerVersion;
	}
	public long getSizeOfCode() {
		return sizeOfCode;
	}
	public long getSizeOfInitializedData() {
		return sizeOfInitializedData;
	}
	public long getSizeOfUninitializedData() {
		return sizeOfUninitializedData;
	}
	public long getAddressOfEntryPoint() {
		return addressOfEntryPoint;
	}
	public long getBaseOfCodeSection() {
		return baseOfCodeSection;
	}
	public long getBaseOfData() {
		return baseOfData;
	}
	public long getImageBase() {
		return imageBase;
	}
	public long getSectionAlignment() {
		return sectionAlignment;
	}
	public long getFileAlignment() {
		return fileAlignment;
	}
	public String getSubsystemVersion() {
		return subsystemVersion;
	}
	public long getWin32VersionValue() {
		return win32VersionValue;
	}
	public long getSizeOfImage() {
		return sizeOfImage;
	}
	public long getSizeOfHeaders() {
		return sizeOfHeaders;
	}
	public int getSubsystemField() {
		return subsystemField;
	}
	public ArrayList<String> getDllCharacteristics() {
		return dllCharacteristics;
	}
	public long getSizeOfStackReserve() {
		return sizeOfStackReserve;
	}
	public long getSizeOfStackCommit() {
		return sizeOfStackCommit;
	}
	public long getSizeOfHeapReserve() {
		return sizeOfHeapReserve;
	}
	public long getSizeOfHeapCommit() {
		return sizeOfHeapCommit;
	}
	public long getLoaderFlags() {
		return loaderFlags;
	}
	public long getNumberOfRVAAndSizes() {
		return numberOfRVAAndSizes;
	}
	
	 //Must be a setter in case of PE32+
	public void setBaseOfData(long baseOfData) {
		this.baseOfData = baseOfData;
	}

	@Override
	public String toString() {
		return "Optional Header: pe32=" + pe32 + ", magic=" + magic + ", majorLinkerVersion=" + majorLinkerVersion
				+ ", minorLinkerVersion=" + minorLinkerVersion + ", sizeOfCode=" + sizeOfCode
				+ ", sizeOfInitializedData=" + sizeOfInitializedData + ", sizeOfUninitializedData="
				+ sizeOfUninitializedData + ", addressOfEntryPoint=" + addressOfEntryPoint + ", baseOfCodeSection="
				+ baseOfCodeSection + ", baseOfData=" + baseOfData + ", imageBase=" + imageBase + ", sectionAlignment="
				+ sectionAlignment + ", fileAlignment=" + fileAlignment + ", subsystemVersion=" + subsystemVersion
				+ ", win32VersionValue=" + win32VersionValue + ", sizeOfImage=" + sizeOfImage + ", sizeOfHeaders="
				+ sizeOfHeaders + ", subsystemField=" + subsystemField + ", dllCharacteristics=" + dllCharacteristics
				+ ", sizeOfStackReserve=" + sizeOfStackReserve + ", sizeOfStackCommit=" + sizeOfStackCommit
				+ ", sizeOfHeapReserve=" + sizeOfHeapReserve + ", sizeOfHeapCommit=" + sizeOfHeapCommit
				+ ", loaderFlags=" + loaderFlags + ", numberOfRVAAndSizes=" + numberOfRVAAndSizes + "]";
	}
	
	
}
