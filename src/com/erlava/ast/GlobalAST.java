package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

public class GlobalAST implements AST {

	private static final long serialVersionUID = 1L;

	private AST global;

	public GlobalAST(AST global) {
		this.global = global;
	}

	@Override
	public BarleyValue execute() {
		global.execute();
		return new BarleyAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "global " + global;
	}
}
