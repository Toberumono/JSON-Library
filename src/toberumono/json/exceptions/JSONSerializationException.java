package toberumono.json.exceptions;

/**
 * Thrown when there is an error serializing JSON data.
 * 
 * @author Toberumono
 */
public class JSONSerializationException extends JSONException {
	
	/**
	 * Constructs a new {@link JSONSerializationException} with no message or {@link Throwable cause}
	 */
	public JSONSerializationException() {
		super();
	}
	
	/**
	 * Constructs a new {@link JSONSerializationException} with the given message but no {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 */
	public JSONSerializationException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link JSONSerializationException} with the given message and {@link Throwable cause}
	 * 
	 * @param message
	 *            a message describing the details of the exception. Retrieved later via a call to {@link #getMessage()}
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONSerializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Wraps the given {@link Throwable} in a {@link JSONSerializationException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap. Retrieved later via a call to {@link #getCause()}
	 */
	public JSONSerializationException(Throwable cause) {
		super(cause);
	}
}
