package toberumono.json;

/**
 * Represents a <tt>null</tt> value in JSON text.
 * 
 * @author Toberumono
 */
public class JSONNull implements JSONData<Void> {
	
	JSONNull() {/* ensure that this class cannot be initialized outside of this package */}
	
	@Override
	public String toJSONString() {
		return "null";
	}
	
	/**
	 * @return null
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
}
