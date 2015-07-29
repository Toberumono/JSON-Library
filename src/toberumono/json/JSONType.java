package toberumono.json;

/**
 * Represents the possible types that a JSON value can have in this library.
 * 
 * @author Toberumono
 * @see JSONData#type()
 */
public enum JSONType {
	/**
	 * Indicates that the wrapped value is a {@link String}.
	 */
	STRING,
	/**
	 * Indicates that the wrapped value is an instance of the type being used to represent numbers.
	 */
	NUMBER,
	/**
	 * Indicates that the value is an instance of {@link JSONObject}
	 */
	OBJECT,
	/**
	 * Indicates that the value is an instance of {@link JSONArray}
	 */
	ARRAY,
	/**
	 * Indicates that the wrapped value is a {@link Boolean}.
	 */
	BOOLEAN,
	/**
	 * Indicates that the wrapped value is {@code null}.
	 */
	NULL,
	/**
	 * Indicates that the wrapped value is not one of the default JSON types, but does implement {@link JSONSerializable}.
	 */
	WRAPPED
}
