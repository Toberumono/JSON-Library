package toberumono.json;

/**
 * Represents a {@link Boolean} in JSON text.
 * 
 * @author Toberumono
 */
public class JSONBoolean extends JSONValue<Boolean> {
	
	/**
	 * Constructs a {@link JSONBoolean} that encapsulates {@code value}
	 * 
	 * @param value
	 *            the {@link Boolean} to encapsulate
	 */
	public JSONBoolean(Boolean value) {
		super(value, JSONType.BOOLEAN);
	}
	
	@Override
	public String toJSONString() {
		return value().toString();
	}
}
