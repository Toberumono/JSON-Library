package toberumono.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

import toberumono.json.exceptions.JSONSyntaxException;
import toberumono.lexer.BasicDescender;
import toberumono.lexer.BasicLexer;
import toberumono.lexer.BasicRule;
import toberumono.lexer.errors.EmptyInputException;
import toberumono.lexer.errors.LexerException;
import toberumono.lexer.util.CommentPatterns;
import toberumono.lexer.util.DefaultIgnorePatterns;
import toberumono.structures.sexpressions.BasicConsType;
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
	
	private static final ConsType JSONValueType = new BasicConsType("JSONValue");
	private static final ConsType JSONArrayType = new BasicConsType("JSONArray");
	private static final ConsType JSONObjectType = new BasicConsType("JSONObject");
	private static final ConsType JSONKeyValuePairType = new BasicConsType("JSONKeyValuePair");
	private static final BasicLexer lexer = new BasicLexer(DefaultIgnorePatterns.WHITESPACE);
	private static boolean comments = Boolean.parseBoolean(System.getProperty("json.comments", "true"));
	private static String indentation = System.getProperty("json.indentation", "\t");
	
	static {
		String sign = "[\\+\\-]", basicNumber = "([0-9]+(\\.[0-9]*)?|0?\\.[0-9]+)", exp = basicNumber + "([eE]" + sign + "?" + basicNumber + ")?", infinity = "(" + exp + "|infinity)"; //To avoid copy-pasting
		lexer.addRule("String", new BasicRule(Pattern.compile("[\"\u201C]((\\\\[tbnrf'\"\u201C\u201D\\\\]|[^\"\u201C\u201D\\\\])*?)[\"\u201D]"), //Supports straight quotes and Unicode left and right-quotes
				(l, s, m) -> {
					try {
						return new ConsCell(new JSONString(Strings.unescape(m.group(1))), JSONValueType);
					}
					catch (UnsupportedEncodingException e) {
						return new ConsCell(new JSONString(m.group(1)), JSONValueType);
					}
				}));
		lexer.addRule("Number", new BasicRule(
				Pattern.compile("(" + sign + "?" + infinity + "(" + sign + "(i" + infinity + "|" + infinity + "i|i))?|" + sign + "?(i" + infinity + "|" + infinity + "i|i)(" + sign + infinity + ")?)",
						Pattern.CASE_INSENSITIVE),
				(l, s, m) -> new ConsCell(new JSONNumber<>(JSONSystem.reader.apply(m.group())), JSONValueType)));
		lexer.addRule("Boolean", new BasicRule(Pattern.compile("(true|false)", Pattern.CASE_INSENSITIVE),
				(l, s, m) -> new ConsCell(new JSONBoolean(Boolean.valueOf(m.group())), JSONValueType)));
		lexer.addRule("Null", new BasicRule(Pattern.compile("null", Pattern.CASE_INSENSITIVE & Pattern.LITERAL),
				(l, s, m) -> new ConsCell(new JSONNull(), JSONValueType)));
		lexer.addRule("Colon", new BasicRule(Pattern.compile(":", Pattern.LITERAL), (l, s, m) -> {
			String key = ((JSONString) s.popLast().getCar()).value();
			return new ConsCell(new Pair<String, JSONData<?>>(key, (JSONData<?>) l.getNextConsCell(s, true).getCar()), JSONKeyValuePairType);
		}));
		lexer.addRule("Comma", new BasicRule(Pattern.compile(",", Pattern.LITERAL), (l, s, m) -> {
			try {
				return l.getNextConsCell(s, true);
			}
			catch (EmptyInputException e) { //This can occur if there is a dangling comma
				return null;
			}
		}));
		lexer.addDescender("Array", new BasicDescender("[", "]", (l, s, m) -> {
			JSONArray array = new JSONArray();
			for (; m != null; m = m.getNext())
				array.add((JSONData<?>) m.getCar());
			return new ConsCell(array, JSONArrayType);
		}));
		lexer.addDescender("Object", new BasicDescender("{", "}", (l, s, m) -> {
			JSONObject object = new JSONObject();
			for (; m != null; m = m.getNext()) {
				@SuppressWarnings("unchecked")
				Pair<String, JSONData<?>> pair = (Pair<String, JSONData<?>>) m.getCar();
				object.put(pair.getX(), pair.getY());
			}
			return new ConsCell(object, JSONObjectType);
		}));
		if (comments)
			lexer.addIgnore(CommentPatterns.SINGLE_LINE_COMMENT);
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
		final String sep = System.lineSeparator();
		for (String line : Files.readAllLines(path))
			sb.append(line).append(sep);
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
	 * Reads JSON text from a {@link Readable}
	 * 
	 * @param json
	 *            the JSON text to parse
	 * @return the root node in the JSON text. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 * @throws IOException
	 *             if an error occurs while reading from the {@link Readable}
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> readJSON(Readable json) throws IOException {
		StringBuilder sb = new StringBuilder();
		CharBuffer cbuff = CharBuffer.allocate(1024);
		while (json.read(cbuff) != -1) {
			cbuff.flip();
			sb.append(cbuff);
			cbuff.clear();
		}
		return parseJSON(sb.toString());
	}
	
	/**
	 * Reads JSON text from a {@link Reader}
	 * 
	 * @param json
	 *            the JSON text to parse
	 * @return the root node in the JSON text. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 * @throws IOException
	 *             if an error occurs while reading from the {@link Reader}
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> readJSON(Reader json) throws IOException {
		return readJSON(json instanceof BufferedReader ? (BufferedReader) json : new BufferedReader(json));
	}
	
	/**
	 * Reads JSON text from an {@link InputStream}
	 * 
	 * @param json
	 *            the JSON text to parse
	 * @return the root node in the JSON text. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 * @throws IOException
	 *             if an error occurs while reading from the {@link InputStream}
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> readJSON(InputStream json) throws IOException {
		return readJSON(new BufferedReader(new InputStreamReader(json)));
	}
	
	/**
	 * Reads JSON text from a {@link BufferedReader}
	 * 
	 * @param json
	 *            the JSON text to parse
	 * @return the root node in the JSON text. Use {@link JSONData#value()} and {@link JSONData#type()} to access the value
	 * @throws IOException
	 *             if an error occurs while reading from the {@link BufferedReader}
	 * @throws JSONSyntaxException
	 *             if there is an error while parsing the JSON text
	 * @see JSONData#type()
	 * @see JSONData#value()
	 */
	public static final JSONData<?> readJSON(BufferedReader json) throws IOException {
		StringBuilder sb = new StringBuilder();
		final String sep = System.lineSeparator();
		String line;
		while ((line = json.readLine()) != null)
			sb.append(line).append(sep);
		return parseJSON(sb.toString());
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
	 * arguments for the {@link OpenOption} parameter and forwards to {@link #writeJSON(JSONData, Appendable, boolean)}.<br>
	 * <b>Note</b>: For consistency with previous versions (and general good formatting), it prints a terminating newline
	 * after calling {@link #writeJSON(JSONData, Appendable, boolean)} if formatting is enabled.
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
	 * @see #writeJSON(JSONData, Appendable, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Path path, boolean formatting) throws IOException {
		try (Writer w = Files.newBufferedWriter(path)) {
			writeJSON(root, w, formatting);
			if (formatting)
				w.write(System.lineSeparator()); //This is to keep a terminating newline
		}
	}
	
	/**
	 * Writes the JSON data in text form to the given {@link Appendable} (base interface of {@link Writer} and {@link StringBuffer}).<br>
	 * Convenience method for {@link #writeJSON(JSONData, Appendable, boolean)} with {@code formatting} set to true.
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param writer
	 *            the {@link Appendable} to which to write
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Appendable, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Appendable writer) throws IOException {
		writeJSON(root, writer, true);
	}
	
	/**
	 * Writes the JSON data in text form to the given {@link Appendable} (base interface of {@link Writer} and {@link StringBuffer}).<br>
	 * 
	 * @param root
	 *            the root node of the JSON data
	 * @param writer
	 *            the {@link Appendable} writer to which to write
	 * @param formatting
	 *            if true, then the nicer formatting is used. The speed penalty is minor, so this should almost always be
	 *            true
	 * @throws IOException
	 *             if there is an error while writing to the file
	 * @see JSONData#toFormattedJSON()
	 * @see #writeJSON(JSONData, Appendable)
	 * @see #writeJSON(JSONData, Path, boolean)
	 */
	public static final void writeJSON(JSONData<?> root, Appendable writer, boolean formatting) throws IOException {
		writer.append(formatting ? root.toFormattedJSON() : root.toJSONString());
		if (writer instanceof Flushable) //Handles Writers
			((Flushable) writer).flush();
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
	
	/**
	 * A convenience method for simplifying the process of upgrading configuration files. If a field needs to be moved from
	 * one location to another in a JSON file, this method will go through the list of locations in {@code containerChain}
	 * and transfer the field into the last location in the chain. If the field is not found in any of the locations,
	 * {@code defaultValue} is used.<br>
	 * Example Usage:<br>
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	JSONObject json = JSONSystem.loadJSON(path);
	 * 	JSONString def = new JSONString("testing testing");
	 * 	JSONObject oldSpot1 = json.get("oldSpot1"), oldSpot2 = json.get("oldSpot2"), newSpot = json.get("newSpot");
	 * 	transferField("foo", def, oldSpot1, oldSpot2, newSpot);
	 * }
	 * </pre>
	 * 
	 * @param name
	 *            the name of the field to be transferred
	 * @param defaultValue
	 *            the default value of the field
	 * @param containerChain
	 *            an array of {@link JSONObject JSONObjects} from oldest to newest wherein the field could be found
	 */
	public static void transferField(String name, JSONData<?> defaultValue, JSONObject... containerChain) {
		if (containerChain.length == 0)
			return;
		if (containerChain.length == 1) {
			if (!containerChain[0].containsKey(name))
				containerChain[0].put(name, defaultValue);
			return;
		}
		JSONData<?> value = null;
		int lim = containerChain.length - 1;
		for (int i = 0; i < lim; i++)
			if (containerChain[i].containsKey(name))
				value = containerChain[i].remove(name);
		if (value == null)
			value = defaultValue;
		containerChain[lim].put(name, value);
	}
}
