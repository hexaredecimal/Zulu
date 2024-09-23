package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class ConstantAST implements AST {

	private static final long serialVersionUID = 1L;
	public ZuluValue constant;

	public ConstantAST(ZuluValue constant) {
		this.constant = constant;
	}

	@Override
	public ZuluValue execute() {
		return constant;
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return constant.toString();
	}
}
