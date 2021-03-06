package toberumono.json;

import toberumono.json.exceptions.JSONSerializationException;

/**
 * Represents a single value that was read from a JSON String and can be serialized into one.
 * 
 * @author Toberumono
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
	 * @return a deep clone of the {@link JSONData} instance and the value that it wraps
	 */
	public JSONData<T> deepCopy();
	
	/**
	 * Attempts to serialize the encapsulated value into a JSON String.<br>
	 * The JSON text output by this method, while syntactically accurate, is not guaranteed to be easily read or edited by
	 * users. Use {@link #toFormattedJSON()} for text that users can interact with.
	 * 
	 * @throws JSONSerializationException
	 *             if the encapsulated value does not implement {@link JSONSerializable} or an error occurs during
	 *             serialization
	 * @see #toFormattedJSON()
	 */
	@Override
	public default String toJSONString() {
		if (!(value() instanceof JSONSerializable))
			throw new JSONSerializationException("Value " + value() + " does not implement JSONSerializable");
		return ((JSONSerializable) value()).toJSONString();
	}
	
	/**
	 * This method serializes the encapsulated value into JSON like {@link #toJSONString()}; however, it also formats the
	 * data so that it is easier for users to read. This is a bit slower, and results in more dispersed text, so if speed
	 * and/or data size are a concern, use {@link #toJSONString()} instead.
	 * 
	 * @return nicely formatted JSON
	 * @see #toJSONString()
	 * @see #toFormattedJSON(StringBuilder)
	 */
	public default String toFormattedJSON() {
		return toFormattedJSON(new StringBuilder()).toString();
	}
	
	/**
	 * This method serializes the encapsulated value into JSON like {@link #toJSONString()}; however, it also formats the
	 * data so that it is easier for users to read. This is a bit slower, and results in more dispersed text, so if speed
	 * and/or data size are a concern, use {@link #toJSONString()} instead.
	 * 
	 * @param sb
	 *            the {@link StringBuilder} in which to construct the formatted JSON text
	 * @return nicely formatted JSON
	 * @see #toJSONString()
	 * @see #toFormattedJSON()
	 */
	public default String toFormattedJSON(StringBuilder sb) {
		return toFormattedJSON(sb, "").toString();
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
	public default StringBuilder toFormattedJSON(StringBuilder sb, String indentation) {
		return sb.append(toJSONString());
	}
}
