package toberumono.json;

public class JSONWrapped<T extends JSONSerializable> extends JSONValue<T> {
	
	JSONWrapped(T value) {
		super(value, JSONType.WRAPPED);
	}
	
	@Override
	public String toJSONString() {
		return "\"" + JSONSystem.escape(value().toJSONString()) + "\"";
	}
}
