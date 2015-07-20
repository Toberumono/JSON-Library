package toberumono.json.exceptions;

/**
 * Root class for exceptions thrown by this library.
 * 
 * @author Joshua Lipstone
 */
public class JSONException extends RuntimeException {
	
	JSONException() {
		super();
	}
	
	JSONException(String message) {
		super(message);
	}

	public JSONException(Throwable t) {
		super(t);
	}
}
