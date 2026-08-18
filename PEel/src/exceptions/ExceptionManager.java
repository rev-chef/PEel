package exceptions;

public class ExceptionManager {


	    private ErrorDisplay errorDisplay;

	    public ExceptionManager(ErrorDisplay errorDisplay) {
	        this.errorDisplay = errorDisplay;
	    }

	    public void handleException(String message, String errorType) {
	    	errorDisplay.displayError(message, errorType);
	    }
	}
