package toberumono.json;

import toberumono.json.exceptions.JSONSerializationException;

/**
 * Indicates that an object can be serialized into a JSON string.
 * 
 * @author Toberumono
 */
public interface JSONSerializable {
	
	/**
	 * Serializes the object into a valid JSON String.
	 * 
	 * @return a valid JSON String-based representation of the object
	 * @throws JSONSerializationException
	 *             if an error occurs during serialization
	 */
	public String toJSONString();
}
