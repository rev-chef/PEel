package pe;

import java.util.ArrayList;

public class ImportTableList extends ArrayList<ImportTable>{

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for(ImportTable imported : this) 
		{
			builder.append(imported).append("\n");
		}
		
		return builder.toString();
	}
}
