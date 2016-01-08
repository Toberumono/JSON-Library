package toberumono.json.exceptions;

/**
 * Thrown when there is an error serializing JSON data.
 * 
 * @author Toberumono
 */
public class JSONSerializationException extends JSONException {
	
	/**
	 * Creates a new {@link JSONSerializationException} without a message or cause.
	 */
	public JSONSerializationException() {
		super();
	}
	
	/**
	 * Creates a new {@link JSONSerializationException} with the given {@code message}.
	 * 
	 * @param message
	 *            the message to associate with the {@link JSONSerializationException}
	 */
	public JSONSerializationException(String message) {
		super(message);
	}
	
	/**
	 * Wraps the given {@link Throwable} in a {@link JSONSerializationException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap
	 */
	public JSONSerializationException(Throwable cause) {
		super(cause);
	}
}
