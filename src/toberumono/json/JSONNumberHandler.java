package toberumono.json;

@FunctionalInterface
public interface JSONNumberHandler<T, V> {
	public T handle(V input);
}
