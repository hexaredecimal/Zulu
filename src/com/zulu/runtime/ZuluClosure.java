package com.zulu.runtime;

import com.zulu.utils.Function;

public class ZuluClosure extends ZuluFunction {

	private static final long serialVersionUID = 1L;
	private Table.Scope scope;

	public ZuluClosure(Function function) {
		super(function);
		this.scope = Table.scope;
	}

	@Override
	public ZuluValue execute(ZuluValue... args) {
		Table.Scope old = Table.scope;
		Table.scope = scope;
		ZuluValue result = super.execute(args);
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
