package pe;

import java.util.ArrayList;

public class ExportTable {
	
	private ArrayList<String> exportNames;
	private long exportFlags; //Reserved, should be 0
	private long timeDateStamp;
	private long majorVersion;
	private long minorVersion;
	private long nameRVA;
	private long ordinalBase;
	private long numAddrEntries;
	private long numNamePointers;
	private long exportAddrRVA;
	private long namePointerRVA;
	private long ordinalRVA;
	
	//Big wunga-ass constructor
	public ExportTable(ArrayList<String> exportNames, long exportFlags, long timeDateStamp, long majorVersion, long minorVersion,
			long nameRVA, long ordinalBase, long numAddrEntries, long numNamePointers, long exportAddrRVA,
			long namePointerRVA, long ordinalRVA) 
	{
		this.exportNames = exportNames;
		this.exportFlags = exportFlags;
		this.timeDateStamp = timeDateStamp;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.nameRVA = nameRVA;
		this.ordinalBase = ordinalBase;
		this.numAddrEntries = numAddrEntries;
		this.numNamePointers = numNamePointers;
		this.exportAddrRVA = exportAddrRVA;
		this.namePointerRVA = namePointerRVA;
		this.ordinalRVA = ordinalRVA;
	}
	
	
	public ArrayList<String> getExportNames() {
		return exportNames;
	}
	public long getExportFlags() {
		return exportFlags;
	}
	public long getTimeDateStamp() {
		return timeDateStamp;
	}
	public long getMajorVersion() {
		return majorVersion;
	}
	public long getMinorVersion() {
		return minorVersion;
	}
	public long getNameRVA() {
		return nameRVA;
	}
	public long getOrdinalBase() {
		return ordinalBase;
	}
	public long getNumAddrEntries() {
		return numAddrEntries;
	}
	public long getNumNamePointers() {
		return numNamePointers;
	}
	public long getExportAddrRVA() {
		return exportAddrRVA;
	}
	public long getNamePointerRVA() {
		return namePointerRVA;
	}
	public long getOrdinalRVA() {
		return ordinalRVA;
	}
	
	
	
}
