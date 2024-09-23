package com.erlava.runtime;

import com.erlava.utils.Function;

public class BarleyClosure extends BarleyFunction {

	private static final long serialVersionUID = 1L;
	private Table.Scope scope;

	public BarleyClosure(Function function) {
		super(function);
		this.scope = Table.scope;
	}

	@Override
	public BarleyValue execute(BarleyValue... args) {
		Table.Scope old = Table.scope;
		Table.scope = scope;
		BarleyValue result = super.execute(args);
		Table.scope = old;
		return result;
	}

	public Table.Scope getScope() {
		return scope;
	}

	@Override
	public String toString() {
		return "#Closure<" + hashCode() + ">";
	}
}
