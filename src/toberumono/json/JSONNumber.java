package toberumono.json;

import java.util.function.Function;

public class JSONNumber<T> extends JSONValue<T> {

	JSONNumber(T value) {
		super(value, JSONType.NUMBER);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		return ((Function<Object, String>) JSONSystem.getWriter()).apply(value());
	}
}
