package toberumono.json;

import java.util.function.Function;

/**
 * Represents a number in JSON text.
 * 
 * @author Joshua Lipstone
 * @param <T>
 *            the type being used to represent numbers
 */
public class JSONNumber<T> extends JSONValue<T> {
	
	/**
	 * Constructs a new {@link JSONNumber} that wraps the given <tt>value</tt>
	 * 
	 * @param value
	 *            the number of type <tt>T</tt> to wrap
	 */
	JSONNumber(T value) {
		super(value, JSONType.NUMBER);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		return ((Function<Object, String>) JSONSystem.getWriter()).apply(value());
	}
}
