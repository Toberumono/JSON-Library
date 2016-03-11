package toberumono.json.exceptions;

/**
 * Thrown when there are syntax errors in a JSON file being parsed.
 * 
 * @author Toberumono
 */
public class JSONSyntaxException extends JSONException {
	
	/**
	 * Constructs a new {@link JSONSyntaxException} with no message or {@link Throwable cause}
	 */
	public JSONSyntaxException() {
		super();
	}
	
	/**
	 * Constructs a new {@link JSONSyntaxException} with the given message but no {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 */
	public JSONSyntaxException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link JSONSyntaxException} with the given message and {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONSyntaxException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Wraps the given {@link Throwable} in a {@link JSONSyntaxException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONSyntaxException(Throwable cause) {
		super(cause);
	}
}
