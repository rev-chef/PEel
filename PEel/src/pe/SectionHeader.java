package pe;

public class SectionHeader {
	
	private String name;
	private long virtualSize;
	private long virtualAddress;
	private long sizeOfRawData;
	private long pointerToRawData;
	private long pointerToRelocs;
	private long pointerToLineNumbers;
	private int numberOfRelocs;
	private int numberOfLineNumbers;
	private long characteristics;
	
	public SectionHeader(String name, long virtualSize, long virtualAddress, long sizeOfRawData, long pointerToRawData,
			long pointerToRelocs, long pointerToLineNumbers, int numberOfRelocs, int numberOfLineNumbers,
			long characteristics) {
		this.name = name;
		this.virtualSize = virtualSize;
		this.virtualAddress = virtualAddress;
		this.sizeOfRawData = sizeOfRawData;
		this.pointerToRawData = pointerToRawData;
		this.pointerToRelocs = pointerToRelocs;
		this.pointerToLineNumbers = pointerToLineNumbers;
		this.numberOfRelocs = numberOfRelocs;
		this.numberOfLineNumbers = numberOfLineNumbers;
		this.characteristics = characteristics;
	}
	public String getName() {
		return name;
	}
	public long getVirtualSize() {
		return virtualSize;
	}
	public long getVirtualAddress() {
		return virtualAddress;
	}
	public long getSizeOfRawData() {
		return sizeOfRawData;
	}
	public long getPointerToRawData() {
		return pointerToRawData;
	}
	public long getPointerToRelocs() {
		return pointerToRelocs;
	}
	public long getPointerToLineNumbers() {
		return pointerToLineNumbers;
	}
	public int getNumberOfRelocs() {
		return numberOfRelocs;
	}
	public int getNumberOfLineNumbers() {
		return numberOfLineNumbers;
	}
	public long getCharacteristics() {
		return characteristics;
	}
	
	
	
	
	
	
}
