package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluAtom;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class GlobalAST implements AST {

	private static final long serialVersionUID = 1L;

	private AST global;

	public GlobalAST(AST global) {
		this.global = global;
	}

	@Override
	public ZuluValue execute() {
		global.execute();
		return new ZuluAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "global " + global;
	}
}
