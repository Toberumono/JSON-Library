package toberumono.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a comma-separated list of items bracketed by '[' and ']' in JSON text.
 * 
 * @author Toberumono
 */
public class JSONArray extends ArrayList<JSONData<?>> implements JSONData<List<JSONData<?>>>, ModifiableJSONData, Cloneable {
	private boolean modified;
	
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
		//We need to ensure that the modified flag is correctly set
		modified = false;
		isModified();
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

	@Override
	public JSONArray deepCopy() {
		JSONArray out = (JSONArray) clone();
		for (int i = 0; i < out.size(); i++)
			out.set(i, out.get(i).deepCopy());
		if (!isModified()) //We can take advantage of out already being flagged as modifiable to avoid having to copy the "modifiable" field
			out.clearModified();
		return out;
	}
	
	@Override
	public Object clone() {
		JSONArray out = (JSONArray) super.clone();
		out.modified = modified;
		return out;
	}
	
	/**
	 * Forwards to {@link #toJSONString()}.
	 * 
	 * @see #toJSONString()
	 */
	@Override
	public String toString() {
		return toJSONString();
	}
	
	@Override
	public String toJSONString() {
		StringBuilder out = new StringBuilder("[");
		JSONData<?> e;
		for (Iterator<JSONData<?>> iter = this.iterator(); iter.hasNext();) {
			e = iter.next();
			out.append(e.toJSONString());
			if (iter.hasNext())
				out.append(", ");
		}
		out.append(']');
		return out.toString();
	}
	
	@Override
	public StringBuilder toFormattedJSON(final StringBuilder sb, final String indentation) {
		if (size() == 0)
			return sb.append("[ ]");
		final String innerIndentation = indentation + JSONSystem.getIndentation();
		sb.append('[');
		int i = 0;
		JSONData<?> e = get(i);
		for (; i < size() - 1; e = get(++i)) {
			if (e.type() == JSONType.OBJECT || e.type() == JSONType.ARRAY) {
				e = (JSONData<?>) e.value();
				sb.append(System.lineSeparator()).append(innerIndentation);
				e.toFormattedJSON(sb, innerIndentation);
				sb.append(',');
			}
			else
				sb.append(' ').append(get(i).toJSONString()).append(',');
		}
		if (e.type() == JSONType.OBJECT || e.type() == JSONType.ARRAY) {
			sb.append(System.lineSeparator()).append(innerIndentation);
			e.toFormattedJSON(sb, innerIndentation);
			sb.append(System.lineSeparator()).append(indentation);
		}
		else
			sb.append(' ').append(e.toJSONString()).append(' ');
		return sb.append(']');
	}
	
	/**
	 * Wraps a {@link Collection} in a {@link JSONArray}.
	 * 
	 * @param c
	 *            the {@link Collection} to wrap
	 * @return a {@link JSONArray} containing the elements in {@code c}
	 */
	public static final JSONArray wrap(Collection<?> c) {
		return c.stream().collect(JSONArray::new, (l, e) -> l.add(e instanceof JSONData ? (JSONData<?>) e : JSONSystem.wrap((Object) e)), JSONArray::addAll);
	}
	
	/**
	 * Converts the elements in a {@link Collection} in {@link JSONData} and wraps them in a {@link JSONArray}.
	 * 
	 * @param c
	 *            the {@link Collection} to wrap
	 * @param converter
	 *            a {@link Function} that converts the elements in {@code c} into {@link JSONData}
	 * @param <T>
	 *            the type of the elements being wrapped
	 * @return a {@link JSONArray} containing the elements in {@code c}
	 */
	public static final <T> JSONArray wrap(Collection<T> c, Function<T, JSONData<?>> converter) {
		return c.stream().collect(JSONArray::new, (l, e) -> l.add(converter.apply(e)), JSONArray::addAll);
	}
	
	@Override
	public boolean isModified() {
		if (!modified) {
			for (JSONData<?> value : this)
				if (value instanceof ModifiableJSONData && ((ModifiableJSONData) value).isModified()) {
					modified = true;
					break;
				}
		}
		return modified;
	}
	
	@Override
	public void clearModified() {
		if (modified) {
			modified = false;
			for (JSONData<?> value : this)
				if (value instanceof ModifiableJSONData && ((ModifiableJSONData) value).isModified())
					((ModifiableJSONData) value).clearModified();
		}
	}
	
	@Override
	public boolean add(JSONData<?> e) {
		if (super.add(e)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addAll(Collection<? extends JSONData<?>> c) {
		if (super.addAll(c)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends JSONData<?>> c) {
		if (super.addAll(index, c)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		if (super.retainAll(c)) {
			modified = true;
			return true;
		}
		return false;
	}
	
	@Override
	public void clear() {
		modified = true;
		super.clear();
	}
	
	@Override
	public JSONData<?> set(int index, JSONData<?> element) {
		JSONData<?> old = super.set(index, element);
		if (old == null ^ element == null || (old != null && old.equals(element.value())))
			modified = true;
		return old;
	}
	
	@Override
	public void add(int index, JSONData<?> element) {
		modified = true;
		super.add(index, element);
	}
	
	@Override
	public JSONData<?> remove(int index) {
		modified = true;
		return super.remove(index);
	}
}
