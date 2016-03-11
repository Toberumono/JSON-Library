package toberumono.json.exceptions;

/**
 * Root class for exceptions thrown by this library.
 * 
 * @author Toberumono
 */
public class JSONException extends RuntimeException {
	
	/**
	 * Constructs a new {@link JSONException} with no message or {@link Throwable cause}
	 */
	public JSONException() {
		super();
	}
	
	/**
	 * Constructs a new {@link JSONException} with the given message but no {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 */
	public JSONException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link JSONException} with the given message and {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Wraps the given {@link Throwable} in a {@link JSONException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONException(Throwable cause) {
		super(cause);
	}
}
