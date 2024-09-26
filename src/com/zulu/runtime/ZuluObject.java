package com.zulu.runtime;

import java.util.HashMap;

/**
 *
 * @author hexaredecimal
 */
public class ZuluObject {

	private HashMap<String, ZuluValue> fields;
	private String parent;

	public ZuluObject(String parent, HashMap<String, ZuluValue> fields) {
		this.parent = parent;
		this.fields = fields;
	}

	public HashMap<String, ZuluValue> getFields() {
		return fields;
	}

	public String getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return parent + "@" + fields;
	}
}
