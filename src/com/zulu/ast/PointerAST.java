package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluPointer;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class PointerAST implements AST {

	private static final long serialVersionUID = 1L;
	private AST value;

	public PointerAST(AST value) {
		this.value = value;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluPointer(value.execute());
	}

	@Override
	public void visit(Optimization optimization) {
		value.visit(optimization);
	}

	@Override
	public String toString() {
		return "#" + value;
	}
}
