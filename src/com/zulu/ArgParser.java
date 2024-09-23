package com.zulu;

import java.util.ArrayList;

/**
 *
 * @author hexaredecimal
 */
public class ArgParser {

	public ArrayList<String> arr;

	public ArgParser(String[] args) {
		arr = new ArrayList<>();
		for (String arg : args) {
			arr.add(arg);
		}
	}

	public String get() {
		if (!arr.isEmpty()) {
			return arr.remove(0);
		}
		return null;
	}
}
