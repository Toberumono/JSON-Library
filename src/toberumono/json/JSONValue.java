package toberumono.json;

/**
 * The common root of most of the wrapper classes in this library.
 * 
 * @author Toberumono
 * @param <T>
 *            the type of the wrapped value
 */
abstract class JSONValue<T> implements JSONData<T> {
	private final T value;
	private final JSONType type;
	
	JSONValue(T value, JSONType type) {
		this.value = value;
		this.type = type;
	}
	
	JSONValue(JSONValue<T> original) {
		value = original.value;
		type = original.type;
	}
	
	@Override
	public final T value() {
		return value;
	}
	
	@Override
	public final JSONType type() {
		return type;
	}
	
	/**
	 * This method just forwards to {@link JSONData#value() value's} toString method.
	 * 
	 * @return {@code value.toString()}
	 */
	@Override
	public String toString() {
		return value.toString();
	}
	
	/**
	 * This is equivalent to calling {@code this.value().hashCode()}
	 * 
	 * @return the hash code of the wrapped value
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	/**
	 * If {@code o} is an instance of {@link JSONData}, then this method performs the equivalent of
	 * {@code this.value().equals(o.value())} (requisite null checks eliminated for brevity). If o is {@code null} or is not
	 * an instance of {@link JSONData}, it performs the equivalent of {@code this.value().equals(o)}.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return value == null;
		if (o instanceof JSONData)
			o = ((JSONData<?>) o).value();
		return (value != null && value.equals(o)) || (value == null && o == null);
	}
}
