package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import com.zulu.utils.Function;
import java.util.Arrays;
import com.zulu.runtime.ZuluValue;

public class JavaFunctionAST implements AST {

	private static final long serialVersionUID = 1L;
	private Function function;
	private ZuluValue[] args;

	public JavaFunctionAST(Function function, ZuluValue[] args) {
		this.function = function;
		this.args = args;
	}

	@Override
	public ZuluValue execute() {
		return function.execute(args);
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return function.toString() + Arrays.asList(args);
	}
}
