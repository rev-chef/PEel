package exceptions;

public class FileTooLargeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		String message = "File size must be less than 2GB";
		return message;
	}
	
	
}
