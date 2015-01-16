package lipstone.joshua.jsonLib;

import java.util.ArrayList;

public class JSONValue<T> implements JSONValueHandler {
	private final JSONType type;
	private final Object value;
	
	public JSONValue(T value, JSONType type) {
		this.value = value;
		this.type = type;
	}
	
	public JSONType getType() {
		return type;
	}
	
	public T getValue() {
		@SuppressWarnings("unchecked")
		T out = (T) value;
		return out;
	}

	@Override
	public JSONValue<?> toJSONValue() {
		return this;
	}
	
	@Override
	public String toJSONString() {
		switch (type) {
			case ARRAY:
				@SuppressWarnings("unchecked")
				ArrayList<JSONValue<?>> vals = (ArrayList<JSONValue<?>>) value;
				String output = "[ ";
				for (JSONValue<?> val : vals)
					output = output + val.toJSONString() + ", ";
				if (output.length() > 2)
					return output.substring(0, output.length() - 2) + " ]";
				else
					return output + "]";
			case BOOLEAN:
				return (Boolean) value ? "true" : "false";
			case NULL:
				return "null";
			case NUMBER:
				return JSONSystem.numberWriter.write(value);
			case OBJECT:
				return ((JSONObject) value).toJSONString();
			case STRING:
				return "\"" + ((String) value).replaceAll("\"", "\\\\\"") + "\"";
			default:
				return "";
		}
	}
	
	@Override
	public String toString() {
		return toJSONString();
	}
}
