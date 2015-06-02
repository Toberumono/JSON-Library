package toberumono.json;

public class JSONValue<T> implements JSONData<T> {
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
	 * If the value is an instance of {@link JSONSerializable}, this forwards to the value's
	 * {@link JSONSerializable#toJSONString() toJSONString()}.<br>
	 * Otherwise, it forwards to the value's toString() method.
	 */
	@Override
	public String toString() {
		if (value instanceof JSONSerializable)
			return ((JSONSerializable) value).toJSONString();
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
	 * If <tt>o</tt> is an instance of {@link JSONData}, then this method performs the equivalent of
	 * {@code this.value().equals(o.value())} (requisite null checks eliminated for brevity). If o is <tt>null</tt> or is not
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
