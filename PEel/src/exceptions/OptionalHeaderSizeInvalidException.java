package exceptions;

public class OptionalHeaderSizeInvalidException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage() {
		
		return "Optional header size is abnormal, check for malformation";
		
	}
}
