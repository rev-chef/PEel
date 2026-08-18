package file;

import java.io.*;

import exceptions.ExceptionManager;



public abstract class ExecutableFile{
	
	public abstract void parse();
	
	public abstract void setExceptionManager(ExceptionManager em);
	
	public abstract File getFile();
	
	public ExecutableFile(File file) {
		
	}
}