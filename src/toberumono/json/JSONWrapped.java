package toberumono.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import toberumono.utils.general.Strings;

/**
 * Represents a value that is not part of the default JSON type set, but can be serialized to value JSON text (it implements
 * {@link JSONSerializable})
 * 
 * @author Toberumono
 * @param <T>
 *            the type of value to wrap
 */
public class JSONWrapped<T extends JSONSerializable> extends JSONValue<T> {
	
	/**
	 * Constructs a new {@link JSONWrapped} that wraps the given {@code value}
	 * 
	 * @param value
	 *            the value to wrap
	 */
	public JSONWrapped(T value) {
		super(value, JSONType.WRAPPED);
	}
	
	@Override
	public String toJSONString() {
		return "\"" + Strings.escape(value().toJSONString()) + "\"";
	}
	
	/**
	 * {@inheritDoc}<br>
	 * <b>Note:</b> If the wrapped {@link #value() value} implements {@link Cloneable}, this method will attempt to use the
	 * wrapped {@link #value() value's} {@link #clone()} method. If {@link #value() value} is not an instance of
	 * {@link Cloneable} or its {@link #clone()} method fails, this method will attempt to use a copy {@link Constructor} (a
	 * {@link Constructor} which takes an instance of the value's type as it's only argument). If that fails or the copy
	 * {@link Constructor} for {@link #value() value} does not exist, this method will throw an
	 * {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the wrapped {@link #value() value} could not be copied by the methods described above
	 */
	@Override
	@SuppressWarnings("unchecked")
	public JSONWrapped<T> deepCopy() {
		if (value() instanceof Cloneable) {
			try {
				Method clone = value().getClass().getMethod("clone");
				clone.setAccessible(true);
				return new JSONWrapped<>((T) clone.invoke(value()));
			}
			catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
		}
		try {
			Constructor<?> copy = value().getClass().getConstructor(value().getClass());
			copy.setAccessible(true);
			return new JSONWrapped<>((T) copy.newInstance(value()));
		}
		catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
		throw new UnsupportedOperationException("Cannot create a deep copy with a wrapped value of type " + value().getClass());
	}
}
