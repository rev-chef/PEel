package pe;

import java.util.ArrayList;

public class DataDirectories {
	private long exportTableRVA;
	private long exportTableSize;
	private long importTableRVA;
	private long importTableSize;
	private long resourceTableRVA;
	private long resourceTableSize;
	private long exceptionTableRVA;
	private long exceptionTableSize;
	private long certificateTableOffset;
	private long certificateTableSize;
	private long baseRelocTableRVA;
	private long baseRelocTableSize;
	private long debugDataRVA;
	private long debugDataSize;
	private long architecture; //Should be 0 (Reserved Section)
	private long globalPtrRVA;
	private long globalPtrSize;
	private long tlsTableRVA;
	private long tlsTableSize;
	private long loadConfigTableRVA;
	private long loadConfigTableSize;
	private long boundImportTableRVA;
	private long boundImportTableSize;
	private long IATRVA;
	private long IATSize;
	private long delayImportDescriptorRVA;
	private long delayImportDescriptorSize;
	private long CLRRuntimeRVA;
	private long CLRRuntimeSize;
	private long unnamedReserved; //Should be 0 obviously
	
	//The mondo constructor. Typed this all by hand instead of generating from fields because I am an idiot
	public DataDirectories(
	        long exportTableRVA,
	        long exportTableSize,
	        long importTableRVA,
	        long importTableSize,
	        long resourceTableRVA,
	        long resourceTableSize,
	        long exceptionTableRVA,
	        long exceptionTableSize,
	        long certificateTableOffset,
	        long certificateTableSize,
	        long baseRelocTableRVA,
	        long baseRelocTableSize,
	        long debugDataRVA,
	        long debugDataSize,
	        long architecture,
	        long globalPtrRVA,
	        long globalPtrSize,
	        long tlsTableRVA,
	        long tlsTableSize,
	        long loadConfigTableRVA,
	        long loadConfigTableSize,
	        long boundImportTableRVA,
	        long boundImportTableSize,
	        long IATRVA,
	        long IATSize,
	        long delayImportDescriptorRVA,
	        long delayImportDescriptorSize,
	        long CLRRuntimeRVA,
	        long CLRRuntimeSize,
	        long unnamedReserved
	) {

	    this.exportTableRVA = exportTableRVA;
	    this.exportTableSize = exportTableSize;
	    this.importTableRVA = importTableRVA;
	    this.importTableSize = importTableSize;
	    this.resourceTableRVA = resourceTableRVA;
	    this.resourceTableSize = resourceTableSize;
	    this.exceptionTableRVA = exceptionTableRVA;
	    this.exceptionTableSize = exceptionTableSize;
	    this.certificateTableOffset = certificateTableOffset;
	    this.certificateTableSize = certificateTableSize;
	    this.baseRelocTableRVA = baseRelocTableRVA;
	    this.baseRelocTableSize = baseRelocTableSize;
	    this.debugDataRVA = debugDataRVA;
	    this.debugDataSize = debugDataSize;
	    this.architecture = architecture;
	    this.globalPtrRVA = globalPtrRVA;
	    this.globalPtrSize = globalPtrSize;
	    this.tlsTableRVA = tlsTableRVA;
	    this.tlsTableSize = tlsTableSize;
	    this.loadConfigTableRVA = loadConfigTableRVA;
	    this.loadConfigTableSize = loadConfigTableSize;
	    this.boundImportTableRVA = boundImportTableRVA;
	    this.boundImportTableSize = boundImportTableSize;
	    this.IATRVA = IATRVA;
	    this.IATSize = IATSize;
	    this.delayImportDescriptorRVA = delayImportDescriptorRVA;
	    this.delayImportDescriptorSize = delayImportDescriptorSize;
	    this.CLRRuntimeRVA = CLRRuntimeRVA;
	    this.CLRRuntimeSize = CLRRuntimeSize;
	    this.unnamedReserved = unnamedReserved;
	}

	//Totally useless toString override that never gets used
	@Override
	public String toString() {
		return "Data Directories: [exportTableRVA=" + exportTableRVA + ", exportTableSize=" + exportTableSize
				+ ", importTableRVA=" + importTableRVA + ", importTableSize=" + importTableSize + ", resourceTableRVA="
				+ resourceTableRVA + ", resourceTableSize=" + resourceTableSize + ", exceptionTableRVA="
				+ exceptionTableRVA + ", exceptionTableSize=" + exceptionTableSize + ", certificateTableOffset="
				+ certificateTableOffset + ", certificateTableSize=" + certificateTableSize + ", baseRelocTableRVA="
				+ baseRelocTableRVA + ", baseRelocTableSize=" + baseRelocTableSize + ", debugDataRVA=" + debugDataRVA
				+ ", debugDataSize=" + debugDataSize + ", architecture=" + architecture + ", globalPtrRVA="
				+ globalPtrRVA + ", globalPtrSize=" + globalPtrSize + ", tlsTableRVA=" + tlsTableRVA + ", tlsTableSize="
				+ tlsTableSize + ", loadConfigTableRVA=" + loadConfigTableRVA + ", loadConfigTableSize="
				+ loadConfigTableSize + ", boundImportTableRVA=" + boundImportTableRVA + ", boundImportTableSize="
				+ boundImportTableSize + ", IATRVA=" + IATRVA + ", IATSize=" + IATSize + ", delayImportDescriptorRVA="
				+ delayImportDescriptorRVA + ", delayImportDescriptorSize=" + delayImportDescriptorSize
				+ ", CLRRuntimeRVA=" + CLRRuntimeRVA + ", CLRRuntimeSize=" + CLRRuntimeSize + ", unnamedReserved="
				+ unnamedReserved + "]";
	}

	public long getExportTableRVA() {
		return exportTableRVA;
	}

	public long getExportTableSize() {
		return exportTableSize;
	}

	public long getImportTableRVA() {
		return importTableRVA;
	}

	public long getImportTableSize() {
		return importTableSize;
	}

	public long getResourceTableRVA() {
		return resourceTableRVA;
	}

	public long getResourceTableSize() {
		return resourceTableSize;
	}

	public long getExceptionTableRVA() {
		return exceptionTableRVA;
	}

	public long getExceptionTableSize() {
		return exceptionTableSize;
	}

	public long getCertificateTableOffset() {
		return certificateTableOffset;
	}

	public long getCertificateTableSize() {
		return certificateTableSize;
	}

	public long getBaseRelocTableRVA() {
		return baseRelocTableRVA;
	}

	public long getBaseRelocTableSize() {
		return baseRelocTableSize;
	}

	public long getDebugDataRVA() {
		return debugDataRVA;
	}

	public long getDebugDataSize() {
		return debugDataSize;
	}

	public long getArchitecture() {
		return architecture;
	}

	public long getGlobalPtrRVA() {
		return globalPtrRVA;
	}

	public long getGlobalPtrSize() {
		return globalPtrSize;
	}

	public long getTlsTableRVA() {
		return tlsTableRVA;
	}

	public long getTlsTableSize() {
		return tlsTableSize;
	}

	public long getLoadConfigTableRVA() {
		return loadConfigTableRVA;
	}

	public long getLoadConfigTableSize() {
		return loadConfigTableSize;
	}

	public long getBoundImportTableRVA() {
		return boundImportTableRVA;
	}

	public long getBoundImportTableSize() {
		return boundImportTableSize;
	}

	public long getIATRVA() {
		return IATRVA;
	}

	public long getIATSize() {
		return IATSize;
	}

	public long getDelayImportDescriptorRVA() {
		return delayImportDescriptorRVA;
	}

	public long getDelayImportDescriptorSize() {
		return delayImportDescriptorSize;
	}

	public long getCLRRuntimeRVA() {
		return CLRRuntimeRVA;
	}

	public long getCLRRuntimeSize() {
		return CLRRuntimeSize;
	}

	public long getUnnamedReserved() {
		return unnamedReserved;
	}
	
}
