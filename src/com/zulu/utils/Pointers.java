package com.zulu.utils;


import java.util.HashMap;
import com.zulu.runtime.ZuluValue;

public class Pointers {

	private static final HashMap<String, ZuluValue> pointers = new HashMap<>();

	public static ZuluValue get(String key) {
		ZuluValue result = pointers.get(key);
		if (result == null) {
			throw new BarleyException("BadPointer", "segmentation fault");
		}
		return result;
	}

	public static ZuluValue put(String key, ZuluValue value) {
		return pointers.put(key, value);
	}

	public static void clear() {
		pointers.clear();
	}

	public static ZuluValue remove(String key) {
		return pointers.remove(key);
	}

	public static HashMap<String, ZuluValue> getPointers() {
		return pointers;
	}
}
