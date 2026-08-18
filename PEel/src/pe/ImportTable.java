package pe;

import java.util.ArrayList;

public class ImportTable {

	private String dllName;
	private long importLookupRVA;
	private long timeStamp;
	private long forwarderChain;
	private long nameRVA;
	private long thunkTable;
	private ArrayList<String> dllProcesses;
	
	public ImportTable(String dllName, long importLookupRVA, long timeStamp, long forwarderChain, long nameRVA, long thunkTable,
			ArrayList<String> dllProcesses) {
		super();
		this.dllName = dllName;
		this.importLookupRVA = importLookupRVA;
		this.timeStamp = timeStamp;
		this.forwarderChain = forwarderChain;
		this.nameRVA = nameRVA;
		this.thunkTable = thunkTable;
		this.dllProcesses = dllProcesses;
	}
	
	public String getDllName() {
		return dllName;
	}
	
	public long getImportLookupRVA() {
		return importLookupRVA;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getForwarderChain() {
		return forwarderChain;
	}

	public long getNameRVA() {
		return nameRVA;
	}

	public long getThunkTable() {
		return thunkTable;
	}

	public ArrayList<String> getDllProcesses() {
		return dllProcesses;
	}
	
	
}
