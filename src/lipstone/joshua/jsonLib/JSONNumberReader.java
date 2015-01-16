package lipstone.joshua.jsonLib;

import java.util.regex.Matcher;

@FunctionalInterface
public interface JSONNumberReader {
	public Number read(Matcher match);
}
