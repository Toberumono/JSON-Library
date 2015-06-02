package toberumono.json;

public class JSONNull implements JSONData<Void> {
	
	JSONNull() {/* ensure that this class cannot be initialized outside of this package */}
	
	@Override
	public String toJSONString() {
		return "null";
	}
	
	@Override
	public Void value() {
		return null;
	}
	
	@Override
	public JSONType type() {
		return JSONType.NULL;
	}
}
