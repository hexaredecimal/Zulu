package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class UnStrictAST implements AST {

	private static final long serialVersionUID = 1L;

	@Override
	public ZuluValue execute() {
		Table.strict = false;
		return new ZuluAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "UnStrictAST{}";
	}
}
