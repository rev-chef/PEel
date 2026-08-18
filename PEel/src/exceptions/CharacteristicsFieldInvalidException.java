package exceptions;

public class CharacteristicsFieldInvalidException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		
		return "Characteristics field does not correspond to any official attributes";
	}
	
	
}
