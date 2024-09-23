package com.zulu.units;


import java.util.HashMap;
import com.zulu.runtime.ZuluValue;

public class Unit {

	private final HashMap<String, ZuluValue> fields;

	public Unit(HashMap<String, ZuluValue> fields) {
		this.fields = fields;
	}

	public ZuluValue get(Object key) {
		return fields.get(key.toString());
	}

	public ZuluValue put(String key, ZuluValue value) {
		return fields.put(key, value);
	}

	@Override
	public String toString() {
		return "#Unit" + fields;
	}
}
