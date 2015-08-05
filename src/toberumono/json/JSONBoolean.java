package toberumono.json;

/**
 * Represents a {@link Boolean} in JSON text.
 * 
 * @author Toberumono
 */
public class JSONBoolean extends JSONValue<Boolean> {
	
	/**
	 * Constructs a {@link JSONBoolean} that encapsulates <tt>value</tt>
	 * 
	 * @param value
	 *            the {@link Boolean} to encapsulate
	 */
	JSONBoolean(Boolean value) {
		super(value, JSONType.BOOLEAN);
	}
	
	@Override
	public String toJSONString() {
		return value().toString();
	}
}
