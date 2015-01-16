package lipstone.joshua.jsonLib;

/**
 * Represents a function that takes a {@link JSONValue} and converts it to the given output type.
 * 
 * @author Joshua Lipstone
 * @param <T>
 *            the output type of this method
 */
@FunctionalInterface
public interface JSONValueReader<T extends JSONValueHandler> {
	public T read(JSONValue<?> input);
}
