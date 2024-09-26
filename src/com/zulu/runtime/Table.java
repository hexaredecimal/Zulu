package com.zulu.runtime;

import com.zulu.utils.ZuluException;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Table {

	private static final Object lock = new Object();
	public static boolean strict = false;
	public static volatile Scope scope;

	static {
		Table.clear();
	}

	private Table() {
	}

	public static Map<String, ZuluValue> variables() {
		return scope.vars();
	}

	public static void clear() {
		scope = new Scope();
	}

	public static void push() {
		if (strict) {
			return;
		}
		synchronized (lock) {
			scope = new Scope(scope);
		}
	}

	public static void pop() {
		if (strict) {
			return;
		}
		synchronized (lock) {
			if (scope.parent != null) {
				scope = scope.parent;
			}
		}
	}

	public static boolean isExists(String key) {
		synchronized (lock) {
			return findScope(key).isFound;
		}
	}

	public static ZuluValue get(String key) {
		synchronized (lock) {
			final ScopeFindData scopeData = findScope(key);
			if (scopeData.isFound) {
				return scopeData.scope.variables.get(key);
			}
		}
		throw new ZuluException("UnboundVar", "unbound var '" + key + "'");
	}

	public static void set(String key, ZuluValue value) {
		if (strict) {
			scope.variables.put(key, value);
			return;
		}
		synchronized (lock) {
			findScope(key).scope.variables.put(key, value);
		}
	}

	public static void define(String key, ZuluValue value) {
		synchronized (lock) {
			scope.variables.put(key, value);
		}
	}

	public static void remove(String key) {
		synchronized (lock) {
			findScope(key).scope.variables.remove(key);
		}
	}

	/*
     * Find scope where variable exists.
	 */
	private static ScopeFindData findScope(String variable) {
		final ScopeFindData result = new ScopeFindData();

		Scope current = scope;
		do {
			if (current.variables.containsKey(variable)) {
				result.isFound = true;
				result.scope = current;
				return result;
			}
		} while ((current = current.parent) != null);

		result.isFound = false;
		result.scope = scope;
		return result;
	}

	public static class Scope implements Serializable {

		final Scope parent;
		final Map<String, ZuluValue> variables;

		Scope() {
			this(null);
		}

		Scope(Scope parent) {
			this.parent = parent;
			variables = new ConcurrentHashMap<>();
		}

		public Map<String, ZuluValue> vars() {
			Scope current = this;
			Map<String, ZuluValue> result = new HashMap<>();
			do {
				result.putAll(current.variables);
			} while ((current = current.parent) != null);
			return result;
		}
	}

	private static class ScopeFindData {

		boolean isFound;
		Scope scope;
	}
}
