package lipstone.joshua.jsonLib;

import java.util.HashMap;

public class JSONObject extends HashMap<String, JSONValue<?>> implements JSONValueHandler {
	private JSONObjectWrapper<?> wrapper;
	
	public JSONObject() {
		super();
		wrapper = null;
	}
	
	/**
	 * This method simply wraps this {@link JSONObject} into a {@link JSONValue} and returns it.
	 * 
	 * @return a {@link JSONValue} that wraps this {@link JSONObject}
	 */
	@Override
	public JSONValue<?> toJSONValue() {
		return new JSONValue<>(this, JSONType.OBJECT);
	}
	
	@Override
	public String toJSONString() {
		String output = "{\n";
		for (String key : this.keySet())
			if (get(key) == null)
				output = output + "\"" + key + "\" : null,\n";
			else
				output = output + "\"" + key + "\" : " + get(key).toJSONString() + ",\n";
		if (output.length() > 2)
			output = output.substring(0, output.length() - 2) + "\n}";
		else
			output = output + "}";
		return output;
	}
	
	@Override
	public JSONValue<?> put(String key, JSONValue<?> value) {
		if (wrapper != null)
			wrapper.putFromWrapped(key, value);
		return super.put(key, value);
	}
	
	@Override
	public JSONValue<?> remove(Object key) {
		if (wrapper != null)
			wrapper.removeFromWrapped(key);
		return super.remove(key);
	}
	
	final void wrap(JSONObjectWrapper<?> wrapper) throws UnsupportedOperationException {
		if (this.wrapper != null)
			this.wrapper.unwrap();
		this.wrapper = wrapper;
		wrapper.wrapFromWrapped(this);
	}
	
	final void unwrap() {
		if (this.wrapper != null)
			this.wrapper.unwrapFromWrapped();
		this.wrapper = null;
	}
	
	final void putFromWrapper(String key, JSONValue<?> value) {
		super.put(key, value);
	}
	
	final void removeFromWrapper(Object key) {
		super.remove(key);
	}
}
