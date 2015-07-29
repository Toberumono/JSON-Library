package toberumono.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a comma-separated list of items bracketed by '[' and ']' in JSON text.
 * 
 * @author Toberumono
 */
public class JSONArray extends ArrayList<JSONData<?>> implements JSONData<List<JSONData<?>>> {
	
	/**
	 * Constructs an empty JSON array.
	 */
	public JSONArray() {
		super();
	}
	
	/**
	 * Constructs an empty {@link JSONArray} with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the {@link JSONArray}
	 */
	public JSONArray(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Constructs a {@link JSONArray} with the elements in the given collection. Also serves as the copy constructor.
	 * 
	 * @param c
	 *            the items to insert into the new {@link JSONArray}
	 */
	public JSONArray(Collection<? extends JSONData<?>> c) {
		super(c);
	}
	
	/**
	 * @return the {@link JSONArray} as a {@link List}.
	 */
	@Override
	public List<JSONData<?>> value() {
		return this;
	}
	
	/**
	 * @return {@link JSONType#ARRAY}
	 */
	@Override
	public final JSONType type() {
		return JSONType.ARRAY;
	}
	
	/**
	 * Forwards to {@link #toJSONString()}
	 * 
	 * @see #toJSONString()
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
	
	/**
	 * Wraps a {@link Collection} in a {@link JSONArray}
	 * 
	 * @param c
	 *            the {@link Collection} to wrap
	 * @return a {@link JSONArray} containing the elements in <tt>c</tt>
	 */
	static final JSONArray wrap(Collection<?> c) {
		return c.stream().collect(JSONArray::new, (li, e) -> li.add(e instanceof JSONData ? (JSONData<?>) e : JSONSystem.wrap((Object) e)), JSONArray::addAll);
	}
}
