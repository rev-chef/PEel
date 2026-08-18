package exceptions;

import java.util.*;

public interface ErrorDisplay{

//	public static Map<Boolean, String> IOExceptionFlag = new Map<>();
//	private static boolean EOFExceptionFlag;
//	private static boolean FileNotFoundFlag;
//	private static boolean characteristicsInvalid;
	
//	public static void setIOFlag(String stackTrace) {
//		IOExceptionFlag.put(true, stackTrace);
//	}
	
	void displayError(String message, String popupType) ;

	
}
