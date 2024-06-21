/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.runtime;

import java.util.HashMap;

/**
 *
 * @author hexaredecimal
 */
public class BarleyObject {
	private HashMap<String, BarleyValue> fields;
	private String parent;

	public BarleyObject(String parent, HashMap<String, BarleyValue> fields) {
		this.parent = parent; 
		this.fields = fields;
	}

	public HashMap<String, BarleyValue> getFields() {
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
