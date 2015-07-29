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
		JSONData<?> o = JSONSystem.loadJSON(Paths.get("/Users/joshualipstone/Dropbox/workspace/lipstone.joshua.parser/data", "BasicPlugin.json"));
		System.out.println(o.toString());
		JSONSystem.writeJSON(o, Paths.get("/Users/joshualipstone/Dropbox/workspace/lipstone.joshua.parser/data", "BasicPlugin.json"));
		JSONData<?> t2 = JSONSystem.parseJSON("{\"aNumber\":5,\"bNumber\":77.0}");
		System.out.println(t2.toString());
	}
}
