package toberumono.json;

import java.util.HashMap;
import java.util.Map;

import static toberumono.json.JSONSystem.LineSeparator;

/**
 * Represents a bracketed group of key-value pairs in a JSON file.
 * 
 * @author Joshua Lipstone
 */
public final class JSONObject extends HashMap<String, JSONData<?>> implements JSONData<HashMap<String, JSONData<?>>> {
	
	/**
	 * Constructs an empty {@link JSONObject}
	 */
	public JSONObject() {
		super();
	}
	
	/**
	 * Constructs a new {@link JSONObject} with the key-value pairs specified in <tt>m</tt>. Also serves as the copy
	 * constructor.
	 * 
	 * @param m
	 *            the mappings to copy.
	 */
	public JSONObject(Map<String, JSONData<?>> m) {
		super(m);
	}
	
	@Override
	public HashMap<String, JSONData<?>> value() {
		return this;
	}
	
	@Override
	public JSONType type() {
		return JSONType.OBJECT;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	/**
	 * This method calls {@link JSONSystem#wrap(Object)} with <tt>value</tt>, and assigns the result to <tt>key</tt> by
	 * forwarding to {@link #put(String, JSONData)}
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be wrapped and associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also
	 *         indicate that the map previously associated null with key.)
	 * @see #put(String, JSONData)
	 */
	public JSONData<?> put(String key, Object value) {
		return super.put(key, JSONSystem.wrap(value));
	}
	
	@Override
	//This is just for JavaDoc's benefit.
	public JSONData<?> put(String key, JSONData<?> value) {
		return super.put(key, value);
	}
	
	@Override
	public String toJSONString() {
		if (size() == 0)
			return "{ }";
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		this.entrySet().stream().forEach(e -> sb.append(LineSeparator).append("\t").append(JSONString.toJSONString(e.getKey())).append(" : ").append(e.getValue().toJSONString()).append(","));
		return sb.deleteCharAt(sb.length() - 1).append(LineSeparator).append("}").toString();
	}
	
	@Override
	public StringBuilder toFormattedJSON(final StringBuilder sb, String indentation) {
		if (size() == 0)
			return sb.append("{ }");
		String innerIndentation = indentation + "\t";
		sb.append("{");
		forEach((k, v) -> {
			sb.append(LineSeparator).append(innerIndentation).append(JSONString.toJSONString(k)).append(" : ");
			if (v instanceof JSONObject)
				v.toFormattedJSON(sb.append(LineSeparator).append(innerIndentation + "\t"), innerIndentation + "\t").append(",");
			else {
				v.toFormattedJSON(sb, innerIndentation);
				sb.append(",");
			}
		});
		return sb.deleteCharAt(sb.length() - 1).append(LineSeparator).append(indentation).append("}");
	}
	
	/**
	 * Wraps this {@link JSONObject} in a {@link JSONObjectWrapper} for type <tt>T</tt>.
	 * 
	 * @param <T>
	 *            determines the type that the {@link JSONObjectWrapper} will use. If this method is used correctly, this
	 *            will be automatically determined.
	 * @return a {@link JSONObjectWrapper} that encapsulates this {@link JSONObject}
	 */
	public <T> JSONObjectWrapper<T> wrap() {
		return new JSONObjectWrapper<>(this);
	}
}
