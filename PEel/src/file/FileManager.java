package file;

import java.util.*;

import exceptions.ExceptionManager;
import pe.*;
public class FileManager {
	
	private static Map<String, Integer> susFlags;
	private static Map<Class<?>, Object> parsedPEData = new HashMap<>();
	private static List<SectionHeader> sectionHeaders;
	private static boolean isMalformed;
	private static boolean isFileLoaded;
	private static ExceptionManager exceptionManager;
	
	public FileManager(ExecutableFile importedFile) {
		isFileLoaded = true;
		System.out.println("In file manager " + exceptionManager.hashCode());
		importedFile.setExceptionManager(exceptionManager);
		importedFile.parse();
		
	}
	

	public static Map<String, Integer> getSusFlags() {
		return susFlags;
	}

	public static void setSusFlags(Map<String, Integer> susFlags) {
		FileManager.susFlags = susFlags;
	}
	
	public static void setExceptionManager(ExceptionManager em) {
		exceptionManager = em;
	}
	
	/**
	 * 
	 * @param key The class of the PE data to be stored as a key for the map
	 * @param data Any type of data pertaining to the input class
	 */
	public static void setPEData(Class<?> key, Object data) {
		
			parsedPEData.put(key, data);
		
	}
	
	/**
	 * 
	 * @param <T>
	 * @param Any class
	 * @return The specified class type from the list of parsed PE data (if it exists)
	 */
	public static <T> T getPEData(Class<T> type) {
	    return type.cast(parsedPEData.get(type));
	}
	
	public static void setSectionHeaders(List<SectionHeader> sections) {
	    sectionHeaders = sections;
	}
	
	public static List<SectionHeader> getAllSections() {
		return sectionHeaders;
	}
	
	public static SectionHeader getSection(String name) {
	    for (SectionHeader section : sectionHeaders) {
	        if (section.getName().equals(name)) {
	            return section;
	        }
	    }
	    return null;
	}
	
	public static ExceptionManager getExceptionManager() {
		return exceptionManager;
	}
	//ALL FILE STATE FLAGS:
	
	
	public static void setMalformFlag(boolean flag) {
		isMalformed = flag;
	}
	
	public static boolean getMalformedFlag() {
		return isMalformed;
	}
	
	public static boolean isFileLoaded() {
		return isFileLoaded;
	}
	
	
	/**
	 * Clears all stored data about the most recently parsed PE file
	 */
	public static void clearPEData() {
		FileManager.parsedPEData.clear();
	}
}
