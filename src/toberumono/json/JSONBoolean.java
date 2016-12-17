package toberumono.json;

/**
 * Represents a {@link Boolean} in JSON text.
 * 
 * @author Toberumono
 */
public class JSONBoolean extends JSONValue<Boolean> {
	/**
	 * A {@link JSONBoolean} representing the value {@code true}. This is provided for the purpose of reducing memory consumption when handling a
	 * large number of {@link JSONBoolean JSONBooleans}.
	 */
	public static final JSONBoolean TRUE = new JSONBoolean(true);
	/**
	 * A {@link JSONBoolean} representing the value {@code false}. This is provided for the purpose of reducing memory consumption when handling a
	 * large number of {@link JSONBoolean JSONBooleans}.
	 */
	public static final JSONBoolean FALSE = new JSONBoolean(false);
	
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
	
	@Override
	public JSONBoolean deepCopy() {
		return this;
	}
	
	/**
	 * A simple convenience method for accessing {@link #TRUE} and {@link #FALSE}.
	 * 
	 * @param value
	 *            the {@link Boolean} value to be represented as a {@link JSONBoolean}
	 * @return the {@link JSONBoolean} that wraps the {@link Boolean} value equal to the passed {@code value}
	 */
	public static JSONBoolean valueOf(Boolean value) {
		return value ? TRUE : FALSE;
	}
}
