package toberumono.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONArray extends ArrayList<JSONData<?>> implements JSONData<ArrayList<JSONData<?>>> {
	
	public JSONArray() {
		super();
	}
	
	public JSONArray(int initialCapacity) {
		super(initialCapacity);
	}
	
	public JSONArray(Collection<? extends JSONData<?>> c) {
		super(c);
	}
	
	@Override
	public ArrayList<JSONData<?>> value() {
		return this;
	}
	
	@Override
	public final JSONType type() {
		return JSONType.ARRAY;
	}
	
	/**
	 * Forwards to {@link #toJSONString()}
	 */
	@Override
	public String toString() {
		return toJSONString();
	}
	
	@Override
	public String toJSONString() {
		String out = "[ ";
		out += stream().reduce("", (a, b) -> a + " , " + b.toJSONString(), (a, b) -> a + " , " + b);
		return out + " ]";
	}
	
	@Override
	public StringBuilder toFormattedJSON(final StringBuilder sb, final String indentation) {
		if (size() == 0)
			return sb.append("[ ]");
		final String innerIndentation = indentation + "\t", mapIndentation = innerIndentation + "\t";
		sb.append("[ ");
		int i = 0;
		JSONData<?> e = get(i);
		for (; i < size() - 1; e = get(++i)) {
			if (e.type() == JSONType.OBJECT || e.type() == JSONType.ARRAY) {
				e = (JSONData<?>) e.value();
				sb.deleteCharAt(sb.length() - 1); //Delete the excess space character
				sb.append(JSONSystem.LineSeparator).append(mapIndentation);
				get(i).toFormattedJSON(sb, mapIndentation);
				sb.append(",").append(JSONSystem.LineSeparator).append(mapIndentation);
				if (get(i + 1).type() == JSONType.OBJECT || get(i + 1).type() == JSONType.ARRAY)
					sb.append(" ");
			}
			else
				sb.append(get(i).toJSONString()).append(", ");
		}
		if (e.type() == JSONType.OBJECT || e.type() == JSONType.ARRAY) {
			sb.deleteCharAt(sb.length() - 1); //Delete the excess space character
			sb.append(JSONSystem.LineSeparator).append(mapIndentation);
			get(i).toFormattedJSON(sb, mapIndentation);
			return sb.append(JSONSystem.LineSeparator).append(innerIndentation).append("]");
		}
		return sb.append(get(i).toJSONString()).append(" ]");
	}
	
	static final JSONArray wrap(List<?> list) {
		return list.stream().collect(JSONArray::new, (li, e) -> li.add(e instanceof JSONData ? (JSONData<?>) e : JSONSystem.wrap((Object) e)), JSONArray::addAll);
	}
}
