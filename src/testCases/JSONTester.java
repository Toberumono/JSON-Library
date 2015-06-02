package testCases;

import java.io.IOException;
import java.nio.file.Paths;

import toberumono.json.JSONData;
import toberumono.json.JSONSystem;

public class JSONTester {
	
	public static void main(String[] args) throws IOException {
		JSONData<?> o = JSONSystem.loadJSON(Paths.get("/Users/joshualipstone/Dropbox/workspace/lipstone.joshua.parser/data", "BasicPlugin.json"));
		System.out.println(o.toString());
		JSONSystem.writeJSON(o, Paths.get("/Users/joshualipstone/Dropbox/workspace/lipstone.joshua.parser/data", "BasicPlugin.json"));
	}
}
