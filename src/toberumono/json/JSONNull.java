package toberumono.json;

/**
 * Represents a {@code null} value in JSON text.
 * 
 * @author Toberumono
 */
public class JSONNull implements JSONData<Void> {
	/**
	 * A {@link JSONNull} representing the value {@code null}. This is provided for the purpose of reducing memory consumption when handling a large
	 * number of {@link JSONNull JSONNulls}.
	 */
	public static final JSONNull NULL = new JSONNull();
	
	@Override
	public String toJSONString() {
		return "null";
	}
	
	/**
	 * @return {@code null}
	 */
	@Override
	public Void value() {
		return null;
	}
	
	/**
	 * @return {@link JSONType#NULL}
	 */
	@Override
	public JSONType type() {
		return JSONType.NULL;
	}
	
	@Override
	public JSONNull deepCopy() {
		return this;
	}
	
	@Override
	public String toString() {
		return "null";
	}
}
