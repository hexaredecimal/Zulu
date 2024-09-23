package com.erlava.utils;

import java.io.Serializable;

public class EntryPoint implements Serializable {

	private final String name;
	private final String method;

	public EntryPoint(String name, String method) {
		this.name = name;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public String getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "EntryPoint{"
			+ "name='" + name + '\''
			+ ", method='" + method + '\''
			+ '}';
	}
}
