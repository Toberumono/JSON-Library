package toberumono.json;

public class JSONString extends JSONValue<String> {
	
	JSONString(String value) {
		super(value, JSONType.STRING);
	}
	
	@Override
	public String toJSONString() {
		return toJSONString(value());
	}
	
	public static String toJSONString(String str) {
		StringBuilder sb = new StringBuilder(str.length() + 2);
		sb.append("\"");
		str.chars().forEach(c -> {
			if (c == '\t')
				sb.append("\\t");
			else if (c == '\b')
				sb.append("\\b");
			else if (c == '\n')
				sb.append("\\n");
			else if (c == '\r')
				sb.append("\\r");
			else if (c == '\f')
				sb.append("\\f");
			else if (c == '\'')
				sb.append("\\'");
			else if (c == '\"')
				sb.append("\\\"");
			else if (c == '\\')
				sb.append("\\\\");
			else
				sb.append((char) c);
		});
		return sb.append("\"").toString();
	}
}
