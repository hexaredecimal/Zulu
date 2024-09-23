package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;

public class StrictAST implements AST {

	private static final long serialVersionUID = 1L;

	@Override
	public BarleyValue execute() {
		Table.strict = true;
		return new BarleyAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "StrictAST{}";
	}
}
