package com.erlava.units;

import com.erlava.runtime.BarleyValue;

import java.util.HashMap;

public class Unit {

	private final HashMap<String, BarleyValue> fields;

	public Unit(HashMap<String, BarleyValue> fields) {
		this.fields = fields;
	}

	public BarleyValue get(Object key) {
		return fields.get(key.toString());
	}

	public BarleyValue put(String key, BarleyValue value) {
		return fields.put(key, value);
	}

	@Override
	public String toString() {
		return "#Unit" + fields;
	}
}
