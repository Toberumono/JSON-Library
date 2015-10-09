package testCases;

import java.io.IOException;
import java.nio.file.Paths;

import toberumono.json.JSONData;
import toberumono.json.JSONSystem;

/**
 * A quick testing class
 * 
 * @author Toberumono
 */
public class JSONTester {
	
	/**
	 * The main method
	 * 
	 * @param args
	 *            this is ignored
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		JSONData<?> o = JSONSystem.loadJSON(Paths.get("./", "test-data.json"));
		System.out.println(o.toString());
		JSONSystem.writeJSON(o, Paths.get("./", "test-data.json"));
	}
}
