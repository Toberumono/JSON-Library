package toberumono.json;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

import toberumono.json.exceptions.JSONSyntaxException;
import toberumono.lexer.Descender;
import toberumono.lexer.Lexer;
import toberumono.lexer.Rule;
import toberumono.lexer.errors.LexerException;
import toberumono.lexer.util.CommentPatterns;
import toberumono.lexer.util.DefaultIgnorePatterns;
import toberumono.structures.sexpressions.ConsCell;
import toberumono.structures.sexpressions.ConsType;
import toberumono.structures.tuples.Pair;
import toberumono.utils.general.Strings;

/**
 * Core class for this library. Contains methods to read from and write to JSON files as well as change the type used for
 * numbers when reading from and writing to JSON files.
 * 
 * @author Toberumono
 * @see #loadJSON(Path)
 * @see #parseJSON(String)
 * @see #writeJSON(JSONData, Path)
 * @see #wrap(Object)
 */
public class JSONSystem {
	
	/**
	 * The default method by which the {@link JSONSystem} reads numbers from {@link String Strings}.
	 */
	public static final Function<String, ? extends Number> defaultReader = s -> {
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return Double.parseDouble(s);
		}
	};
	/**
	 * The default method by which the {@link JSONSystem} writes numbers to {@link String Strings}.
	 */
	public static final Function<? extends Number, String> defaultWriter = Number::toString;
	/**
	 * The default type that the {@link JSONSystem} expects numbers to have ({@link Number}).
	 */
	public static final Class<Number> defaultNumberType = Number.class;
	private static Function<String, ? extends Number> reader = defaultReader;
	private static Function<? extends Number, String> writer = defaultWriter;
	private static Class<? extends Number> numberType = defaultNumberType;
	
	private static final ConsType JSONValueType = new ConsType("JSONValue");
	private static final ConsType JSONArrayType = new ConsType("JSONArray");
	private static final ConsType JSONObjectType = new ConsType("JSONObject");
	private static final ConsType JSONKeyValuePairType = new ConsType("JSONKeyValuePair");
	private static final Lexer lexer = new Lexer(DefaultIgnorePatterns.WHITESPACE, CommentPatterns.SINGLE_LINE_COMMENT);
	private static boolean comments = true;
	private static String indentation = "  ";
	
	static {
		String quotes = "\"\u301D\u301E", sign = "[\\+\\-]", basicNumber = "([0-9]+(\\.[0-9]*)?|0?\\.[0-9]+)", exp = basicNumber + "([eE]" + sign + "?" + basicNumber + ")?", infinity =
				"(" + exp + "|infinity)"; //To avoid copy-pasting
		lexer.addRule("String", new Rule(Pattern.compile("[" + quotes + "](([^" + quotes + "]|(?<=\\\\)[" + quotes + "])*)[" + quotes + "]"),
				(l, s, m) -> {
					try {
						return new ConsCell(new JSONString(Strings.unescape(m.group(1))), JSONValueType);
					}
					catch (UnsupportedEncodingException e) {
						return new ConsCell(new JSONString(m.group(1)), JSONValueType);
					}
				}));
		lexer.addRule("Number",
				new Rule(Pattern.compile("(" + sign + "?" + infinity + "(" + sign + "(i" + infinity + "|" + infinity + "i|i))?|" + sign + "?(i" + infinity + "|" + infinity + "i|i)(" + sign + infinity
						+ ")?)", Pattern.CASE_INSENSITIVE), (l, s, m) -> new ConsCell(new JSONNumber<>(JSONSystem.reader.apply(m.group())), JSONValueType)));
		lexer.addRule("Boolean", new Rule(Pattern.compile("(true|false)", Pattern.CASE_INSENSITIVE),
				(l, s, m) -> new ConsCell(new JSONBoolean(Boolean.valueOf(m.group())), JSONValueType)));
		lexer.addRule("Null", new Rule(Pattern.compile("null", Pattern.CASE_INSENSITIVE & Pattern.LITERAL),
				(l, s, m) -> new ConsCell(new JSONNull(), JSONValueType)));
		lexer.addRule("Colon", new Rule(Pattern.compile(":", Pattern.LITERAL), (l, s, m) -> {
			@SuppressWarnings("unchecked")
			String key = ((JSONData<String>) s.popPreviousConsCell().getCar()).value();
			return new ConsCell(new Pair<String, JSONData<?>>(key, (JSONData<?>) l.getNextConsCell(s, true).getCar()), JSONKeyValuePairType);
		}));
		lexer.addRule("Comma", new Rule(Pattern.compile(",", Pattern.LITERAL), (l, s, m) -> l.getNextConsCell(s, true)));
		lexer.addDescender("Array", new Descender("[", "]", (l, s, m) -> {
			JSONArray array = new JSONArray(m.length());
			for (; !m.isNull(); m = m.getNextConsCell())
				array.add((JSONData<?>) m.getCar());
			return new ConsCell(array, JSONArrayType);
		}));
		lexer.addDescender("Object", new Descender("{", "}", (l, s, m) -> {
			JSONObject object = new JSONObject();
			for (; !m.isNull(); m = m.getNextConsCell()) {
				@SuppressWarnings("unchecked")
				Pair<String, JSONData<?>> pair = (Pair<String, JSONData<?>>) m.getCar();
				object.put(pair.getX(), pair.getY());
			}
			return new ConsCell(object, JSONObjectType);
		}));
	}
	
	/**
	 * Enable parsing comments in JSON text.<br>
	 * Comments start with '//' and continue to the end of the line.
	 * 
	 * @see #disableComments()
	 * @see #setComments(boolean)
	 */
	public static final void enableComments() {
		if (!comments) {
			lexer.addIgnore(CommentPatterns.SINGLE_LINE_COMMENT);
			comments = true;
		}
	}
	
	/**
	 * Disable parsing comments in JSON text.<br>
	 * Comments start with '//' and continue to the end of the line.
	 * 
	 * @see #enableComments()
	 * @see #setComments(boolean)
	 */
	public static final void disableComments() {
		if (comments) {
			lexer.removeIgnore(CommentPatterns.SINGLE_LINE_COMMENT);
			comments = false;
		}
	}
	
	/**
	 * @return the {@link String} used to indent successive elements within a JSON structure
	 * @see JSONData#toFormattedJSON()
	 */
	public static String getIndentation() {
		return indentation;
	}
	
	/**
	 * @param indentation
	 *            the {@link String} to use to indent successive elements within a JSON structure
	 */
	public static void setIndentation(String indentation) {
		JSONSystem.indentation = indentation;
	}
	
	/**
	 * @return whether parsing of comments in JSON text is currently enabled
	 */
	public static final boolean areCommentsEnabled() {
		return comments;
	}
	
	/**
	 * Set whether parsing comments in JSON files is be enabled.<br>
	 * Comments start with '//' and continue to the end of the line.<br>
	 * This method forwards to {@link #enableComments()} or {@link #disableComments()} depending on the value of
	 * {@code enabled}.
	 * 
	 * @param enabled
	 *            whether parsing of comments in JSON text should be enabled
	 * @see #enableComments()
	 * @see #disableComments()
	 * @see #areCommentsEnabled()
	 */
	public static final void setComments(boolean enabled) {
		if (enabled)
			enableComments();
		else
			disableComments();
	}
	
	/**
	 * Sets the functions used to read numbers from and write numbers to {@link String strings}.<br>
	 * Defaults to Double::new and String::valueOf<br>
	 * Replaces nulls with the appropriate default
	 * 
	 * @param numberType
	 *            a {@link Class} object representing the class of the type being used for numbers
	 * @param reader
	 *            the function with which to read numbers from a {@link String string}
	 * @param writer
	 *            the function with which to write numbers to a {@link String string}
	 * @param <T>
	 *            synchronizes the type used for numbers in two functions. If this method is used correctly, this will not
	 *            have to be explicitly set.
	 * @see #defaultReader
	 * @see #defaultWriter
	 */
	public static final <T extends Number> void setNumberHandlers(Class<T> numberType, Function<String, T> reader, Function<T, String> writer) {
		JSONSystem.reader = (reader == null ? defaultReader : reader);
		JSONSystem.writer = (writer == null ? defaultWriter : writer);
		JSONSystem.numberType = numberType;
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
		numberType = defaultNumberType;
	}
	
	/**
	 * @return the function used to read numbers from {@link String strings}
	 */
	public static final Function<String, ? extends Object> getReader() {
		return reader;
	}
	
	/**
	 * @return the function used to write numbers to {@link String strings}
	 */
	public static final Function<? extends Object, String> getWriter() {
		return writer;
	}
	
	/**
	 * Reads the text from the file at {@code path} and then parses it as JSON text.
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
		Files.lines(path).forEach(s -> sb.append(s).append(System.lineSeparator()));
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
			JSONData<?> out = (JSONData<?>) lexer.lex(json.trim()).getCar();
			if (out instanceof ModifiableJSONData)
				((ModifiableJSONData) out).clearModified();
			return out;
		}
		catch (LexerException e) {
			throw new JSONSyntaxException(e);
		}
	}
	
	/**
	 * Writes the JSON data to the file at {@code path} if the file does not exist, it is created. If the file already
	 * exists, it is overwritten.<br>
	 * Convenience method for {@link #writeJSON(JSONData, Path, boolean)} with {@code formatting} set to true.
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param path
	 *            the {@link Path} to the file
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Path, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Path path) throws IOException {
		writeJSON(root, path, true);
	}
	
	/**
	 * Writes the JSON data to the file at {@code path} if the file does not exist, it is created. If the file already
	 * exists, it is overwritten.<br>
	 * This simply creates a new {@link BufferedWriter} via {@link Files#newBufferedWriter(Path, OpenOption...)} without any
	 * arguments for the {@link OpenOption} parameter and forwards to {@link #writeJSON(JSONData, Writer, boolean)}.<br>
	 * <b>Note</b>: For consistency with previous versions (and general good formatting), it prints a terminating newline
	 * after calling {@link #writeJSON(JSONData, Writer, boolean)} if formatting is enabled.
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
	 * @see #writeJSON(JSONData, Writer, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Path path, boolean formatting) throws IOException {
		try (Writer w = Files.newBufferedWriter(path)) {
			writeJSON(root, w, formatting);
			if (formatting)
				w.write(System.lineSeparator()); //This is to keep a terminating newline
		}
	}
	
	/**
	 * Writes the JSON data in text form to the given {@link Writer}.<br>
	 * Convenience method for {@link #writeJSON(JSONData, Writer, boolean)} with {@code formatting} set to true.
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param writer
	 *            the {@link Writer} to which to write
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Writer, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Writer writer) throws IOException {
		writeJSON(root, writer, true);
	}
	
	/**
	 * Writes the JSON data in text form to the given {@link Writer}.
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param writer
	 *            the {@link Writer} to which to write
	 * @param formatting
	 *            if true, then the nicer formatting is used. The speed penalty is minor, so this should almost always be
	 *            true
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Writer)
	 * @see #writeJSON(JSONData, Path, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Writer writer, boolean formatting) throws IOException {
		writer.write(formatting ? root.toFormattedJSON() : root.toJSONString());
	}
	
	/**
	 * Attempts to wrap {@code value} within the appropriate container for this library.<br>
	 * If the value is one of the basic types for JSON, it gets wrapped within the that type's container (including
	 * {@code null}).<br>
	 * If it implements {@link JSONRepresentable}, this method calls {@link JSONRepresentable#toJSONObject()} and returns the
	 * result.<br>
	 * If the value is an instance of {@link JSONSerializable}, it wraps the value within a {@link JSONWrapped} object.
	 * Otherwise, it throws an {@link UnsupportedOperationException}.
	 * 
	 * @param value
	 *            the value to wrap, which must
	 * @param <T>
	 *            the container type (this is determined automatically if this method is used appropriately)
	 * @return the wrapped value
	 * @throws UnsupportedOperationException
	 *             if the value is not part of JSON's default supported values and does not implement
	 *             {@link JSONSerializable} or {@link JSONRepresentable}
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends JSONData<?>> T wrap(Object value) {
		if (value == null)
			return (T) new JSONNull();
		if (value instanceof JSONData)
			return (T) value;
		if (numberType.isInstance(value))
			return (T) new JSONNumber<>((Number) value);
		if (value instanceof String)
			return (T) new JSONString((String) value);
		if (value instanceof Boolean)
			return (T) new JSONBoolean((Boolean) value);
		if (value instanceof Collection)
			return (T) JSONArray.wrap((Collection<?>) value);
		if (value.getClass().isArray())
			return (T) JSONArray.wrap(Arrays.asList(value));
		if (value instanceof JSONRepresentable)
			return (T) ((JSONRepresentable) value).toJSONObject();
		if (value instanceof JSONSerializable)
			return (T) new JSONWrapped<>((JSONSerializable) value);
		throw new UnsupportedOperationException("Cannot wrap a value that is not part of JSON's default supported values and does not implement JSONSerializable or JSONRepresentable");
	}
}
