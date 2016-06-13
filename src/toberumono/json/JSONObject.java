package toberumono.json;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a bracketed group of key-value pairs in a JSON file.<br>
 * This does not support {@code null} keys or values.
 * 
 * @author Toberumono
 */
public final class JSONObject extends LinkedHashMap<String, JSONData<?>> implements JSONData<LinkedHashMap<String, JSONData<?>>>, ModifiableJSONData, Cloneable {
	private boolean modified;
	
	/**
	 * Constructs an empty {@link JSONObject}
	 */
	public JSONObject() {
		super();
	}
	
	/**
	 * Constructs a new {@link JSONObject} with the key-value pairs specified in {@code m}. Also serves as the copy
	 * constructor.
	 * 
	 * @param m
	 *            the mappings to copy.
	 */
	public JSONObject(Map<String, JSONData<?>> m) {
		super(m);
		//We need to ensure that the modified flag is correctly set
		modified = false;
		isModified();
	}
	
	@Override
	public LinkedHashMap<String, JSONData<?>> value() {
		return this;
	}
	
	@Override
	public JSONType type() {
		return JSONType.OBJECT;
	}

	@Override
	public JSONObject deepCopy() {
		JSONObject out = (JSONObject) clone();
		for (Entry<String, JSONData<?>> element : out.entrySet())
			out.put(element.getKey(), element.getValue().deepCopy());
		if (!isModified()) //We can take advantage of out already being flagged as modifiable to avoid having to copy the "modifiable" field
			out.clearModified();
		return out;
	}
	
	@Override
	public Object clone() {
		JSONObject out = (JSONObject) super.clone();
		out.modified = modified;
		return out;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	/**
	 * This method calls {@link JSONSystem#wrap(Object)} on {@code value}, and assigns the result to {@code key} by
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
		return put(key, JSONSystem.wrap(value));
	}
	
	@Override
	public JSONData<?> put(String key, JSONData<?> value) {
		if (key == null || value == null)
			throw new NullPointerException();
		JSONData<?> old = super.put(key, value);
		if (old == null ^ value == null || (old != null && !old.equals(value.value())))
			modified = true;
		return old;
	}
	
	@Override
	public JSONData<?> remove(Object key) {
		JSONData<?> out = super.remove(key);
		if (!modified)
			modified = out != null;
		return out;
	}
	
	@Override
	public String toJSONString() {
		if (size() == 0)
			return "{ }";
		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		Entry<String, JSONData<?>> e;
		for (Iterator<Entry<String, JSONData<?>>> iter = entrySet().iterator(); iter.hasNext();) {
			e = iter.next();
			sb.append(System.lineSeparator()).append(JSONString.toJSONString(e.getKey())).append(" : ").append(e.getValue().toJSONString());
			if (iter.hasNext())
				sb.append(',');
		}
		return sb.append(System.lineSeparator()).append('}').toString();
	}
	
	@Override
	public StringBuilder toFormattedJSON(final StringBuilder sb, String indentation) {
		if (size() == 0)
			return sb.append("{ }");
		String innerIndentation = indentation + JSONSystem.getIndentation();
		sb.append('{');
		Entry<String, JSONData<?>> e;
		for (Iterator<Entry<String, JSONData<?>>> iter = entrySet().iterator(); iter.hasNext();) {
			e = iter.next();
			sb.append(System.lineSeparator()).append(innerIndentation).append(JSONString.toJSONString(e.getKey())).append(" : ");
			e.getValue().toFormattedJSON(sb, innerIndentation);
			if (iter.hasNext())
				sb.append(',');
		}
		return sb.append(System.lineSeparator()).append(indentation).append('}');
	}
	
	/**
	 * Wraps this {@link JSONObject} in a {@link JSONObjectWrapper} for type {@code T}.
	 * 
	 * @param <T>
	 *            determines the type that the {@link JSONObjectWrapper} will use. If this method is used correctly, this
	 *            will be automatically determined.
	 * @return a {@link JSONObjectWrapper} that encapsulates this {@link JSONObject}
	 */
	public <T> JSONObjectWrapper<T> wrap() {
		return new JSONObjectWrapper<>(this);
	}
	
	@Override
	public boolean isModified() {
		if (modified)
			return modified;
		for (JSONData<?> value : values())
			if (value instanceof ModifiableJSONData && ((ModifiableJSONData) value).isModified()) {
				modified = true;
				break;
			}
		return modified;
	}
	
	@Override
	public void clearModified() {
		if (!modified)
			return;
		modified = false;
		for (JSONData<?> value : values())
			if (value instanceof ModifiableJSONData && ((ModifiableJSONData) value).isModified())
				((ModifiableJSONData) value).clearModified();
	}
}
