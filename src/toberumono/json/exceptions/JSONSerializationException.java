package toberumono.json.exceptions;

/**
 * Thrown when there is an error serializing JSON data.
 * 
 * @author Joshua Lipstone
 */
public class JSONSerializationException extends JSONException {
	
	public JSONSerializationException() {
		super();
	}
	
	public JSONSerializationException(String message) {
		super(message);
	}
}
