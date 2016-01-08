package toberumono.json.exceptions;

/**
 * Thrown when there are syntax errors in a JSON file being parsed.
 * 
 * @author Toberumono
 */
public class JSONSyntaxException extends JSONException {

	/**
	 * Creates a new {@link JSONSyntaxException} with the given {@code message}.
	 * 
	 * @param message
	 *            the message to associate with the {@link JSONSerializationException}
	 */
	public JSONSyntaxException(String message) {
		super(message);
	}

	/**
	 * Wraps the given {@link Throwable} in a {@link JSONSyntaxException}
	 * 
	 * @param cause
	 *            the {@link Throwable} to wrap
	 */
	public JSONSyntaxException(Throwable cause) {
		super(cause);
	}
}
