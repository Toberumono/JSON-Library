package toberumono.json;

/**
 * Indicates that an object can be represented as a {@link JSONObject}.
 * 
 * @author Joshua Lipstone
 */
public interface JSONRepresentable extends JSONSerializable {
	
	/**
	 * Converts the object into a value {@link JSONObject}
	 * 
	 * @return a valid {@link JSONObject}
	 */
	public JSONObject toJSONObject();
	
	@Override
	public default String toJSONString() {
		return toJSONObject().toJSONString();
	}
}
