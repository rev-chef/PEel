package pe;

import java.util.ArrayList;

public class ParsedPE {
	
	 CoffFileHeader coffheader;
	 OptionalHeader optionalheader;
	 DataDirectories datadirectories;
	 SectionHeader sectionheader;
	 ArrayList<ImportTable> imports;
	 
}
