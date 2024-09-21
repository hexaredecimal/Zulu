package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyPointer;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;

public class PointerAST implements AST, Serializable {
	private static final long serialVersionUID = 1L;
	private AST value;

	public PointerAST(AST value) {
		this.value = value;
	}

	@Override
	public BarleyValue execute() {
		return new BarleyPointer(value.execute());
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
