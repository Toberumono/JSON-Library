package toberumono.json;

/**
 * Represents a {@code null} value in JSON text.
 * 
 * @author Toberumono
 */
public class JSONNull implements JSONData<Void> {
	
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
		return new JSONNull();
	}
	
	@Override
	public String toString() {
		return "null";
	}
}
