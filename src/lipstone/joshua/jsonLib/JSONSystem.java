package lipstone.joshua.jsonLib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

import lipstone.joshua.customStructures.tuples.Pair;
import lipstone.joshua.lexer.Descender;
import lipstone.joshua.lexer.Lexer;
import lipstone.joshua.lexer.Rule;
import lipstone.joshua.lexer.Token;
import lipstone.joshua.lexer.Type;
import lipstone.joshua.lexer.errors.LexerException;

public class JSONSystem {
	
	private static final Lexer lexer = new Lexer();
	public static final Type JSONNumberType = new Type("JSONNumber");
	public static final Type JSONStringType = new Type("JSONString");
	public static final Type JSONBooleanType = new Type("JSONBoolean");
	public static final Type JSONNullType = new Type("JSONNull");
	public static final Type JSONObjectType = new Type("JSONObject");
	public static final Type JSONArrayType = new Type("JSONArray");
	public static final Type JSONObjectDataType = new Type("JSONObjectData");
	static JSONNumberWriter numberWriter = (number) -> {
		return ((Double) (double) number).toString();
	};
	static JSONNumberReader numberReader = (match) -> {
		return Double.parseDouble(match.group());
	};
	
	static {
		lexer.ignore("Spaces", Pattern.compile("[\\s]+"));
		lexer.addRule("Number", new Rule(Pattern.compile("[+-]?([0-9]+(\\.[0-9]*)?|(\\.[0-9]+))(E[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+))?"), (match, l) -> {
			return new Token(new JSONValue<>(numberReader.read(match), JSONType.NUMBER), JSONNumberType);
		}));
		lexer.addRule("String", new Rule(Pattern.compile("[\"\u301D\u301E]([^\"\u301D\u301E]|(?<=\\\\)[\"\u301D\u301E])*[\"\u301D\u301E]", Pattern.DOTALL), (match, l) -> {
			return new Token(new JSONValue<>(match.group().substring(1, match.group().length() - 1).replaceAll("\\\\\"", "\""), JSONType.STRING), JSONStringType);
		}));
		lexer.addRule("Boolean", new Rule(Pattern.compile("(true|false)"), (match, l) -> new Token(new JSONValue<>(match.group().startsWith("t"), JSONType.BOOLEAN), JSONBooleanType)));
		lexer.addRule("Null", new Rule(Pattern.compile("null"), (match, l) -> new Token(new JSONValue<>(null, JSONType.NULL), JSONNullType)));
		lexer.addRule("Colon", new Rule(Pattern.compile(":"), (match, l) -> {
			Token key = l.popPreviousToken();
			Token value = l.getNextToken(true);
			return new Token(new Pair<String, JSONValue<?>>((String) ((JSONValue<?>) key.getCar()).getValue(), (JSONValue<?>) value.getCar()), JSONObjectDataType);
		}));
		lexer.addRule("Comma", new Rule(Pattern.compile(","), (match, l) -> lexer.getNextToken(true)));
		lexer.addDescender("Array", new Descender("[", "]", l -> {}, (match, l) -> {
			ArrayList<JSONValue<?>> array = new ArrayList<>();
			for (; !match.isNull(); match = match.getNextToken())
				array.add((JSONValue<?>) match.getCar());
			return new Token(new JSONValue<>(array, JSONType.ARRAY), JSONArrayType);
		}));
		lexer.addDescender("Object", new Descender("{", "}", l -> {}, (match, l) -> {
			JSONObject object = new JSONObject();
			while (match.getCarType().equals(JSONObjectDataType)) {
				object.put((String) ((Pair<?, ?>) match.getCar()).getX(), (JSONValue<?>) ((Pair<?, ?>) match.getCar()).getY());
				while (!(match = match.getNextToken()).isNull() && !match.getCarType().equals(JSONObjectDataType));
			}
			return new Token(new JSONValue<>(object, JSONType.OBJECT), JSONObjectType);
		}));
	}
	
	public static void OverrideNumberHandlers(JSONNumberReader reader, JSONNumberWriter writer) {
		numberReader = reader;
		numberWriter = writer;
	}
	
	public static JSONValue<JSONObject> loadJSON(Path filePath) throws IOException, LexerException {
		StringBuilder stringBuilder = new StringBuilder();
		Files.lines(filePath).forEach((a) -> stringBuilder.append(a));
		return parseJSONText(stringBuilder.toString());
	}
	
	public static JSONValue<JSONObject> parseJSONText(String text) throws LexerException {
		try {
			@SuppressWarnings("unchecked")
			JSONValue<JSONObject> out = (JSONValue<JSONObject>) lexer.lex(text).getCar();
			return out;
		}
		catch (Exception e) {
			throw new LexerException(e.getMessage(), e.getCause());
		}
	}
	
	public static void saveJSON(JSONValueHandler value, Path filePath) throws IOException {
		Files.write(filePath, value.toJSONString().getBytes());
	}
}
