package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluClosure;
import com.zulu.runtime.UserFunction;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class ClosureAST implements AST {

	private static final long serialVersionUID = 1L;

	private UserFunction function;

	public ClosureAST(UserFunction function) {
		this.function = function;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluClosure(function);
	}

	@Override
	public void visit(Optimization optimization) {
	}

	@Override
	public String toString() {
		return function.toString();
	}
}
