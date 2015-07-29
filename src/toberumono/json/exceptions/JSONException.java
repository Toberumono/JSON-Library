package toberumono.json.exceptions;

/**
 * Root class for exceptions thrown by this library.
 * 
 * @author Toberumono
 */
public class JSONException extends RuntimeException {
	
	JSONException() {
		super();
	}
	
	JSONException(String message) {
		super(message);
	}
	
	/**
	 * Wraps the given {@link Throwable} in a {@link JSONException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap
	 */
	public JSONException(Throwable cause) {
		super(cause);
	}
}
