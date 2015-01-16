package lipstone.joshua.jsonLib;

import java.util.HashMap;

/**
 * A convinence class that allows a {@link JSONObject} to be wrapped such that values will be automatically converted from
 * {@link JSONValue} to the desired value type, <b>{@literal <T>}</b>.<br>
 * If it is supplied with a {@link JSONObject}, this will forward any put and remove calls to the wrapped {@link JSONObject}.
 * However, this is highly inefficient for regularly modified objects due to the cost of the conversions.
 * 
 * @author Joshua Lipstone
 * @param <T>
 *            the desired value type
 */
public class JSONObjectWrapper<T extends JSONValueHandler> extends HashMap<String, T> implements JSONValueHandler {
	private final JSONValueReader<T> reader;
	private JSONObject wrapped;
	
	public JSONObjectWrapper(JSONValueReader<T> reader) {
		super();
		this.reader = reader;
		wrapped = null;
	}
	
	public JSONObjectWrapper(JSONObject base, JSONValueReader<T> reader) {
		this(reader);
		for (String key : base.keySet())
			super.put(key, reader.read(base.get(key)));
		wrapped = base;
	}
	
	@Override
	public T put(String key, T value) {
		if (wrapped != null)
			wrapped.putFromWrapper(key, value.toJSONValue());
		return super.put(key, value);
	}
	
	public T put(String key, JSONValue<?> value) {
		if (wrapped != null)
			wrapped.putFromWrapper(key, value);
		return super.put(key, reader.read(value));
	}
	
	@Override
	public T remove(Object key) {
		if (wrapped != null)
			wrapped.removeFromWrapper(key);
		return super.remove(key);
	}
	
	@Override
	public JSONValue<JSONObject> toJSONValue() {
		return new JSONValue<>(wrapped, JSONType.OBJECT);
	}
	
	@Override
	public String toJSONString() {
		return toJSONValue().toJSONString();
	}
	
	/**
	 * This unwraps the wrapped JSONObject if there was one, but does not otherwise change the map that this
	 * {@link JSONObjectWrapper} represents.
	 * 
	 * @return the newly unwrapped {@link JSONObject} if there was one, otherwise null;
	 */
	public JSONObject unwrap() {
		JSONObject output = wrapped;
		if (wrapped != null)
			wrapped.unwrap();
		return output;
	}
	
	public void wrap(JSONObject wrapped) {
		if (this.wrapped != null)
			unwrap();
		this.wrapped = wrapped;
		wrapped.wrap(this);
	}
	
	void putFromWrapped(String key, JSONValue<?> value) {
		super.put(key, reader.read(value));
	}
	
	void removeFromWrapped(Object key) {
		super.remove(key);
	}
	
	void unwrapFromWrapped() {
		wrapped = null;
	}
	
	void wrapFromWrapped(JSONObject wrapped) {
		this.wrapped = wrapped;
		for (String key : wrapped.keySet())
			put(key, reader.read(wrapped.get(key)));
	}
}
