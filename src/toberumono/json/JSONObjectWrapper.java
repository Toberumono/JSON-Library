package toberumono.json;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import toberumono.customStructures.collections.iterators.WrappedIterator;

/**
 * Wraps a JSONObject in order to internally unpack the {@link JSONData} into values of type <tt>T</tt>.<br>
 * <b>This implicitly assumes that <i>all</i> values in the original {@link JSONObject} are of type {@link JSONData
 * JSONData&lt;T&gt;}</b>
 * 
 * @author Joshua Lipstone
 * @param <T>
 *            the type of values that the {@link JSONObject} contained
 */
public final class JSONObjectWrapper<T> implements Map<String, T>, JSONData<JSONObject> {
	private final JSONObject back;
	private Values values = null;
	private Set<Map.Entry<String, T>> entrySet = null;
	
	public JSONObjectWrapper(JSONObject back) {
		this.back = back;
	}
	
	@Override
	public JSONObject value() {
		return back;
	}
	
	@Override
	public JSONType type() {
		return JSONType.OBJECT;
	}
	
	@Override
	public int size() {
		return back.size();
	}
	
	@Override
	public boolean isEmpty() {
		return back.isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return back.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return back.containsValue(JSONSystem.wrap(value));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		JSONData<?> out = back.get(key);
		return out == null ? null : (T) out.value();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T put(String key, T value) {
		JSONData<?> out = back.put(key, JSONSystem.wrap(value));
		return out == null ? null : (T) out.value();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T remove(Object key) {
		JSONData<?> out = back.remove(key);
		return out == null ? null : (T) out.value();
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends T> m) {
		m.forEach((k, v) -> put(k, JSONSystem.wrap(v)));
	}
	
	@Override
	public void clear() {
		back.clear();
	}
	
	@Override
	public Set<String> keySet() {
		return back.keySet();
	}
	
	@Override
	public Collection<T> values() {
		return (values == null) ? values = new Values(back.values()) : values;
	}
	
	final class ValueIterator implements Iterator<T> {
		private final Iterator<JSONData<?>> back;
		
		public ValueIterator(Iterator<JSONData<?>> back) {
			this.back = back;
		}
		
		@Override
		public boolean hasNext() {
			return back.hasNext();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			return (T) back.next().value();
		}
		
	}
	
	final class Values extends AbstractCollection<T> {
		private final Collection<JSONData<?>> back;
		
		Values(Collection<JSONData<?>> back) {
			this.back = back;
		}
		
		@Override
		public final int size() {
			return back.size();
		}
		
		@Override
		public final void clear() {
			JSONObjectWrapper.this.clear();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public final Iterator<T> iterator() {
			return new WrappedIterator<>(back.iterator(), e -> (T) e.value());
		}
		
		@Override
		public final boolean contains(Object o) {
			return containsValue(o);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public final void forEach(Consumer<? super T> action) {
			back.forEach(v -> action.accept((T) v.value()));
		}
	}
	
	@Override
	public Set<Map.Entry<String, T>> entrySet() {
		Set<Map.Entry<String, T>> es;
		return (es = entrySet) == null ? (entrySet = new EntrySet(back.entrySet())) : es;
	}
	
	private final Function<String, String> esk = k -> k;
	@SuppressWarnings("unchecked") private final Function<JSONData<?>, T> esvc = v -> (T) v.value();
	private final Function<T, JSONData<?>> esvu = v -> JSONSystem.wrap(v);
	
	private final class WrappedEntry implements Map.Entry<String, T> {
		private final Map.Entry<String, JSONData<?>> back;
		
		WrappedEntry(Map.Entry<String, JSONData<?>> back) {
			this.back = back;
		}
		
		@Override
		public String getKey() {
			return esk.apply(back.getKey());
		}
		
		@Override
		public T getValue() {
			return esvc.apply(back.getValue());
		}
		
		@Override
		public T setValue(T value) {
			return esvc.apply(back.setValue(esvu.apply(value)));
		}
		
	}
	
	final class EntrySet extends AbstractSet<Map.Entry<String, T>> {
		private final Set<Map.Entry<String, JSONData<?>>> back;
		
		EntrySet(Set<Map.Entry<String, JSONData<?>>> back) {
			this.back = back;
		}
		
		@Override
		public final int size() {
			return back.size();
		}
		
		@Override
		public final void clear() {
			back.clear();
		}
		
		@Override
		public final Iterator<Map.Entry<String, T>> iterator() {
			return new WrappedIterator<>(back.iterator(), WrappedEntry::new);
		}
		
		@Override
		public final boolean contains(Object o) {
			return back.contains(o);
		}
		
		@Override
		public final boolean remove(Object o) {
			return back.remove(o);
		}
		
		@Override
		public final void forEach(Consumer<? super Map.Entry<String, T>> action) {
			back.forEach(v -> action.accept((Map.Entry<String, T>) new WrappedEntry(v)));
		}
	}
	
	@Override
	public String toJSONString() {
		return back.toJSONString();
	}
}
