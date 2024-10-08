package com.zulu.optimizations;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluNumber;
import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TableEmulator implements Serializable {

	private static final long serialVersionUID = 1L;
	private final ZuluAtom lock = new ZuluAtom("lock");
	public volatile Scope scope = new Scope();

	public TableEmulator() {
	}

	public Map<String, VariableInfo> variables() {
		return scope.variables;
	}

	public void clear() {
		scope = new Scope();
	}

	public void push() {
		synchronized (lock) {
			scope = new Scope(scope);
		}
	}

	public void pop() {
		synchronized (lock) {
			if (scope.parent != null) {
				scope = scope.parent;
			}
		}
	}

	public boolean isExists(String key) {
		synchronized (lock) {
			return findScope(key).isFound;
		}
	}

	public VariableInfo get(String key) {
		synchronized (lock) {
			final ScopeFindData scopeData = findScope(key);
			if (scopeData.isFound) {
				return scopeData.scope.variables.get(key);
			}
		}
		return new VariableInfo(new ZuluNumber(0), 0);
	}

	public void set(String key, VariableInfo value) {
		synchronized (lock) {
			findScope(key).scope.variables.put(key, value);
		}
	}

	public void define(String key, VariableInfo value) {
		synchronized (lock) {
			scope.variables.put(key, value);
		}
	}

	public void remove(String key) {
		synchronized (lock) {
			findScope(key).scope.variables.remove(key);
		}
	}

	/*
     * Find scope where variable exists.
	 */
	private ScopeFindData findScope(String variable) {
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

		private static final long serialVersionUID = 1L;
		final Scope parent;
		final Map<String, VariableInfo> variables;

		Scope() {
			this(null);
		}

		Scope(Scope parent) {
			this.parent = parent;
			variables = new ConcurrentHashMap<>();
		}
	}

	private static class ScopeFindData implements Serializable {

		private static final long serialVersionUID = 1L;
		boolean isFound;
		Scope scope;
	}
}
