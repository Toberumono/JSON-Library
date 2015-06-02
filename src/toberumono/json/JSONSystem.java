package toberumono.json;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import toberumono.customStructures.tuples.Pair;
import toberumono.json.exceptions.JSONSyntaxException;

import lipstone.joshua.lexer.Descender;
import lipstone.joshua.lexer.Lexer;
import lipstone.joshua.lexer.Rule;
import lipstone.joshua.lexer.Token;
import lipstone.joshua.lexer.Type;

import static toberumono.json.JSONType.BOOLEAN;

public class JSONSystem {
	static final String LineSeparator = System.lineSeparator();
	
	public static final Function<String, ? extends Number> defaultReader = Double::new;
	public static final Function<? super Number, String> defaultWriter = String::valueOf;
	private static Function<String, ? extends Number> reader = defaultReader;
	private static Function<? super Number, String> writer = defaultWriter;
	
	private static final Type JSONValueType = new Type("JSONValue");
	private static final Type JSONArrayType = new Type("JSONArray");
	private static final Type JSONObjectType = new Type("JSONObject");
	private static final Type JSONKeyValuePairType = new Type("JSONKeyValuePair");
	private static final Lexer lexer = new Lexer(false);
	static {
		String quotes = "\"\u301D\u301E", basicNumber = "([0-9]+(\\.[0-9]*)?|\\.[0-9]+)"; //To avoid copy-pasting
		lexer.ignore("Spaces", Pattern.compile("\\s+"));
		lexer.addRule("String", new Rule(Pattern.compile("[" + quotes + "](([^" + quotes + "]|(?<=\\\\)[" + quotes + "])*?)[" + quotes + "]"),
				(m, l) -> new Token(new JSONString(m.group(1)), JSONValueType)));
		lexer.addRule("Number", new Rule(Pattern.compile("[\\+\\-]?(" + basicNumber + "([eE][\\+\\-]?" + basicNumber + ")?|infinity)", Pattern.CASE_INSENSITIVE),
				(m, l) -> new Token(new JSONNumber<>(JSONSystem.reader.apply(m.group())), JSONValueType)));
		lexer.addRule("Boolean", new Rule(Pattern.compile("(true|false)", Pattern.CASE_INSENSITIVE),
				(m, l) -> new Token(new JSONValue<>(Boolean.valueOf(m.group()), BOOLEAN), JSONValueType)));
		lexer.addRule("Null", new Rule(Pattern.compile("null", Pattern.CASE_INSENSITIVE & Pattern.LITERAL),
				(m, l) -> new Token(new JSONNull(), JSONValueType)));
		lexer.addRule("Colon", new Rule(Pattern.compile(":", Pattern.LITERAL), (m, l) -> {
			@SuppressWarnings("unchecked")
			String key = ((JSONData<String>) l.popPreviousToken().getCar()).value();
			return new Token(new Pair<String, JSONData<?>>(key, (JSONData<?>) l.getNextToken(true).getCar()), JSONKeyValuePairType);
		}));
		lexer.addRule("Comma", new Rule(Pattern.compile(",", Pattern.LITERAL),
				(m, l) -> l.getNextToken(true)));
		lexer.addDescender("Array", new Descender("[", "]", l -> {}, (m, l) -> {
			JSONArray array = new JSONArray(m.length());
			for (; !m.isNull(); m = m.getNextToken())
				array.add((JSONData<?>) m.getCar());
			return new Token(array, JSONArrayType);
		}));
		lexer.addDescender("Object", new Descender("{", "}", l -> {}, (m, l) -> {
			JSONObject object = new JSONObject();
			for (; !m.isNull(); m = m.getNextToken()) {
				@SuppressWarnings("unchecked")
				Pair<String, JSONData<?>> pair = (Pair<String, JSONData<?>>) m.getCar();
				object.put(pair.getX(), pair.getY());
			}
			return new Token(object, JSONObjectType);
		}));
	}
	
	/**
	 * Sets the functions used to read numbers from and write numbers to {@link String strings}.<br>
	 * Defaults to Double::new and String::valueOf<br>
	 * Replaces nulls with the appropriate default
	 * 
	 * @param reader
	 *            the function with which to read numbers from a {@link String string}
	 * @param writer
	 *            the function with which to write numbers to a {@link String string}
	 * @see #defaultReader
	 * @see #defaultWriter
	 */
	public static final void setNumberHandlers(Function<String, ? extends Number> reader, Function<? super Number, String> writer) {
		JSONSystem.reader = (reader == null ? defaultReader : reader);
		JSONSystem.writer = (writer == null ? defaultWriter : writer);
	}
	
	/**
	 * Resets the number reading and writing functions to their defaults.
	 * 
	 * @see #defaultReader
	 * @see #defaultWriter
	 */
	public static final void resetNumberHandlers() {
		reader = defaultReader;
		writer = defaultWriter;
	}
	
	/**
	 * @return the function used to read numbers from {@link String strings}
	 */
	public static final Function<String, ? extends Number> getReader() {
		return reader;
	}
	
	/**
	 * @return the function used to write numbers to {@link String strings}
	 */
	public static final Function<? super Number, String> getWriter() {
		return writer;
	}
	
	/**
	 * Reads the text from the file at <tt>path</tt> and then parses it as JSON text.
	 * 
	 * @param path
	 *            the {@link Path} to the file to open
	 * @return the root node in the JSON file. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 *         and determine the type
	 * @throws IOException
	 *             if there is an error opening the file
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> loadJSON(Path path) throws IOException {
		final StringBuilder sb = new StringBuilder();
		Files.lines(path).forEach(s -> sb.append(s).append(LineSeparator));
		return parseJSON(sb.toString());
	}
	
	/**
	 * Parses already-loaded JSON text
	 * 
	 * @param json
	 *            the JSON text to parse
	 * @return the root node in the JSON text. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> parseJSON(String json) {
		try {
			return (JSONData<?>) lexer.lex(json.trim()).getCar();
		}
		catch (Exception e) {
			throw new JSONSyntaxException(e.getMessage());
		}
	}
	
	/**
	 * Writes the JSON data to the file at <tt>path</tt> if the file does not exist, it is created. If the file already
	 * exists, it is overwritten.<br>
	 * Convenience method for {@link #writeJSON(JSONData, Path, boolean)} with <tt>formatting</tt> set to true
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param path
	 *            the {@link Path} to the file
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 */
	public static final void writeJSON(JSONData<?> root, Path path) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb = root.toFormattedJSON(sb, "");
		Files.write(path, Arrays.asList(sb.toString().split(LineSeparator)), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}
	
	/**
	 * Writes the JSON data to the file at <tt>path</tt> if the file does not exist, it is created. If the file already
	 * exists, it is overwritten.
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param path
	 *            the {@link Path} to the file
	 * @param formatting
	 *            if true, then the nicer formatting is used. The speed penalty is minor, so this should almost always be
	 *            true
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Path)
	 */
	public static final void writeJSON(JSONData<?> root, Path path, boolean formatting) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb = formatting ? root.toFormattedJSON(sb, "") : sb.append(root.toJSONString());
		Files.write(path, Arrays.asList(sb.toString().split(LineSeparator)), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}
	
	/**
	 * Attempts to wrap <tt>value</tt> within the appropriate container for this library.<br>
	 * If the value is one of the basic types for JSON, it gets wrapped within the that type's container (including null).<br>
	 * If it implements {@link JSONRepresentable}, this method calls {@link JSONRepresentable#toJSONObject()} and returns the
	 * result.<br>
	 * Otherwise, it wraps the value within a {@link JSONValue} with {@link JSONType#WRAPPED} as the type.
	 * 
	 * @param value
	 *            the value to wrap
	 * @param <T>
	 *            the container type (this is determined automatically if this method is used appropriately)
	 * @return the wrapped value
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends JSONData<?>> T wrap(Object value) {
		if (value == null)
			return (T) new JSONNull();
		if (value instanceof JSONData)
			return (T) value;
		if (value instanceof Number)
			return (T) new JSONNumber<>((Number) value);
		if (value instanceof String)
			return (T) new JSONString((String) value);
		if (value instanceof Boolean)
			return (T) new JSONValue<>((Boolean) value, JSONType.BOOLEAN);
		if (value instanceof List)
			return (T) JSONArray.wrap((List<?>) value);
		if (value.getClass().isArray())
			return (T) JSONArray.wrap(Arrays.asList(value));
		if (value instanceof JSONRepresentable)
			return (T) ((JSONRepresentable) value).toJSONObject();
		return (T) new JSONValue<>(value, JSONType.WRAPPED);
	}
}
