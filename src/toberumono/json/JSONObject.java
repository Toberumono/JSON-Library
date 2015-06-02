package toberumono.json;

import java.util.HashMap;
import java.util.Map;

import static toberumono.json.JSONSystem.LineSeparator;

public final class JSONObject extends HashMap<String, JSONData<?>> implements JSONData<HashMap<String, JSONData<?>>> {
	
	public JSONObject() {
		super();
	}
	
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
	
	public JSONData<?> put(String key, Object value) {
		return super.put(key, JSONSystem.wrap(value));
	}
	
	@Override
	public String toJSONString() {
		if (size() == 0)
			return "{ }";
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		this.entrySet().stream().forEach(e -> sb.append(LineSeparator).append("\t").append(e.getKey()).append(" : ").append(e.getValue().toJSONString()).append(","));
		return sb.deleteCharAt(sb.length() - 1).append(LineSeparator).append("}").toString();
	}
	
	@Override
	public StringBuilder toFormattedJSON(final StringBuilder sb, String indentation) {
		if (size() == 0)
			return sb.append("{ }");
		String innerIndentation = indentation + "\t";
		sb.append("{");
		entrySet().forEach(e -> {
			sb.append(LineSeparator).append(innerIndentation).append(JSONString.toJSONString(e.getKey())).append(" : ");
			if (e.getValue() instanceof JSONObject)
				e.getValue().toFormattedJSON(sb.append(LineSeparator).append(innerIndentation + "\t"), innerIndentation + "\t").append(",");
				else
					e.getValue().toFormattedJSON(sb, innerIndentation).append(",");
			});
		return sb.deleteCharAt(sb.length() - 1).append(LineSeparator).append(indentation).append("}");
	}
	
	public <T> JSONObjectWrapper<T> wrap() {
		return new JSONObjectWrapper<>(this);
	}
}
