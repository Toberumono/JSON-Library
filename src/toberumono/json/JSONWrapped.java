package toberumono.json;

import toberumono.utils.general.Strings;

/**
 * Represents a value that is not part of the default JSON type set, but can be serialized to value JSON text (it implements
 * {@link JSONSerializable})
 * 
 * @author Toberumono
 * @param <T>
 *            the type of value to wrap
 */
public class JSONWrapped<T extends JSONSerializable> extends JSONValue<T> {
	
	/**
	 * Constructs a new {@link JSONWrapped} that wraps the given <tt>value</tt>
	 * 
	 * @param value
	 *            the value to wrap
	 */
	public JSONWrapped(T value) {
		super(value, JSONType.WRAPPED);
	}
	
	@Override
	public String toJSONString() {
		return "\"" + Strings.escape(value().toJSONString()) + "\"";
	}
}
