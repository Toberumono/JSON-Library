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
		return "\"" + JSONSystem.escape(str) + "\"";
	}
}
