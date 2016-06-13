package toberumono.json;

/**
 * Represents a {@link String} in JSON text.
 * 
 * @author Toberumono
 */
public class JSONString extends JSONValue<String> {
	
	/**
	 * Constructs a {@link JSONString} that encapsulates {@code value}
	 * 
	 * @param value
	 *            the {@link String} to encapsulate
	 */
	public JSONString(String value) {
		super(value, JSONType.STRING);
	}
	
	@Override
	public String toJSONString() {
		return toJSONString(value());
	}

	@Override
	public JSONString deepCopy() {
		return new JSONString(value());
	}
	
	/**
	 * Converts a {@link String} into a value that can be written to a JSON file.<br>
	 * This just escapes {@code str} to match the proper JSON format and encloses the result in '"'s.
	 * 
	 * @param str
	 *            the {@link String} to convert to a valid JSON string.
	 * @return {@code str} in a form that can be written to a valid JSON file
	 */
	public static String toJSONString(String str) {
		StringBuilder sb = new StringBuilder(str.length());
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
			else if (c == '\"')
				sb.append("\\\"");
			else if (c == '\\')
				sb.append("\\\\");
			else
				sb.append((char) c);
		});
		return "\"" + sb.toString() + "\"";
	}
}
