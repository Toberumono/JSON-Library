package toberumono.json;

public class JSONNumber<T extends Number> extends JSONValue<T> {

	JSONNumber(T value) {
		super(value, JSONType.NUMBER);
	}
	
	@Override
	public String toJSONString() {
		return JSONSystem.getWriter().apply(value());
	}
}
