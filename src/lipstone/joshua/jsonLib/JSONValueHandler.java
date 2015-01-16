package lipstone.joshua.jsonLib;

/**
 * Describes the methods needed for an object to be integrated into this JSON system.
 * 
 * @author Joshua Lipstone
 */
public interface JSONValueHandler {
	
	/**
	 * Converts this object into a JSONValue
	 * 
	 * @return a JSONValue representation of this object
	 */
	public JSONValue<?> toJSONValue();
	
	/**
	 * Serializes this class into syntactically correct JSON text.
	 * 
	 * @return Strictly syntactically correct JSON text.
	 */
	public default String toJSONString() {
		return toJSONValue().toJSONString();
	}
}
