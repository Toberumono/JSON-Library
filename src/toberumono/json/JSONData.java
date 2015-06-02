package toberumono.json;

import toberumono.json.exceptions.JSONSerializationException;

/**
 * Represents a single value that was read from a JSON String and can be serialized into one.
 * 
 * @author Joshua Lipstone
 * @param <T>
 *            the type of the encapsulated value.
 */
public interface JSONData<T> extends JSONSerializable {
	
	/**
	 * @return the encapsulated value
	 */
	public T value();
	
	/**
	 * @return the {@link JSONType} of the encapsulated value
	 */
	public JSONType type();
	
	/**
	 * Attempts to serialize the encapsulated value into a JSON String.<br>
	 * The JSON text output by this method, while syntactically accurate, is not particularly readable. Use
	 * {@link #toFormattedJSON()} for more readable (but still valid) JSON text.
	 * 
	 * @throws JSONSerializationException
	 *             if the encapsulated value does not implement {@link JSONSerializable} or an error occurs during
	 *             serialization
	 * @see #toFormattedJSON()
	 */
	@Override
	public default String toJSONString() {
		if (!(value() instanceof JSONSerializable))
			throw new JSONSerializationException("Value does not implement JSONSerializable");
		return ((JSONSerializable) value()).toJSONString();
	}
	
	/**
	 * This method serializes the encapsulated value into JSON like {@link #toJSONString()}; however, it also formats the
	 * data for readability. This is a bit slower, and results in more dispersed text, so if speed and data size are a
	 * concern, use {@link #toJSONString()} instead.
	 * 
	 * @return nicely formatted JSON
	 * @see #toJSONString()
	 */
	public default String toFormattedJSON() {
		return toFormattedJSON(new StringBuilder(), "").toString();
	}
	
	/**
	 * For internal calls <em>only</em>.
	 * 
	 * @param sb
	 *            the {@link StringBuilder} in which to construct the nicely formatted JSON
	 * @param indentation
	 *            the indentation level for the chunk. Only used by {@link JSONObject} and {@link JSONArray}
	 * @return a pointer to {@link StringBuilder sb}
	 */
	default StringBuilder toFormattedJSON(StringBuilder sb, String indentation) {
		return sb.append(toJSONString());
	}
}
