package toberumono.json;

import toberumono.utils.general.Strings;

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
	
	/**
	 * Converts a {@link String} into a value that can be written to a JSON file.<br>
	 * This just escapes {@code str} through a call to {@link Strings#escape(String)} and encloses the result in '"'s.
	 * 
	 * @param str
	 *            the {@link String} to convert to a valid JSON string.
	 * @return {@code str} in a form that can be written to a valid JSON file
	 */
	public static String toJSONString(String str) {
		return "\"" + Strings.escape(str) + "\"";
	}
}
