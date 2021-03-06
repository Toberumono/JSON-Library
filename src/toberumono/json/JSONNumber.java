package toberumono.json;

import java.util.function.Function;

/**
 * Represents a number in JSON text.
 * 
 * @author Toberumono
 * @param <T>
 *            the type being used to represent numbers (must subclass {@link Number})
 */
public class JSONNumber<T extends Number> extends JSONValue<T> {
	
	/**
	 * Constructs a new {@link JSONNumber} that wraps the given {@code value}
	 * 
	 * @param value
	 *            the number of type {@code T} to wrap
	 */
	public JSONNumber(T value) {
		super(value, JSONType.NUMBER);
	}

	@Override
	public JSONNumber<T> deepCopy() {
		return new JSONNumber<>(value());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		return ((Function<Number, String>) JSONSystem.getWriter()).apply(value());
	}
}
