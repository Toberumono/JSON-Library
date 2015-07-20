package toberumono.json.exceptions;

/**
 * Thrown when there are syntax errors in a JSON file being parsed.
 * 
 * @author Joshua Lipstone
 */
public class JSONSyntaxException extends JSONException {
	
	public JSONSyntaxException(String message) {
		super(message);
	}

	public JSONSyntaxException(Throwable t) {
		super(t);
	}
}
