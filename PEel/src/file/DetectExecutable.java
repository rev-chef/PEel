package file;

import java.io.*; 
import exceptions.*;


public final class DetectExecutable {
	
	
	public static ExecutableFile detect(File file) {
		try {
			//Do not create RAF if file is larger than 2GB
			long fileSize = file.length();
			if(fileSize>Integer.MAX_VALUE)
			{
				throw new FileTooLargeException();
			}
			
			RandomAccessFile exeFile = new RandomAccessFile(file, "r");
			
			//Find virtual address 0
			exeFile.seek(0);
			
			short MZheader = exeFile.readShort();
			
			 //If first 2 bytes of file are "MZ"
				
			if(MZheader == 0x4D5A && confirmPE(exeFile)) 
			{
				return new PEFile(file, FileManager.getExceptionManager());
			}
			
		}
		//Really dumb way of handling these exceptions but this class is static so fuck me
		catch (FileNotFoundException e) {
			FileManager.getExceptionManager().handleException(e.getMessage(), "error");
		}
		catch (IOException e) {
			FileManager.getExceptionManager().handleException(e.getMessage(), "error");
		}
		catch (FileTooLargeException e) {
			FileManager.getExceptionManager().handleException(e.getMessage(), "error");
		}
	return null;
		
	}
private static boolean confirmPE(RandomAccessFile raf) {
		
		try {
			//seek e_lfanew to find NT header offset
			raf.seek(0x3c);
			
			//seek the NT header
			raf.seek(Integer.reverseBytes(raf.readInt()));
			
			//Reverse bytes to convert to Little-endian
			int peHeader = Integer.reverseBytes(raf.readInt());
			
			//0x5045 = "PE"
			return peHeader == 0x4550;
			
		} 
		
		catch (IOException e) {
			
			FileManager.getExceptionManager().handleException(e.getMessage(), "error");
		}
		return false;
	}
}


