package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

/**
 *
 * @author hexaredecimal
 */
public class XMLInternalExpression implements AST {

	private static final long serialVersionUID = 1L;
	private AST exp;

	public XMLInternalExpression(AST expresson) {
		exp = expresson;
	}

	@Override
	public ZuluValue execute() {
		return exp.execute();
	}

	@Override
	public void visit(Optimization optimization) {
		exp.visit(optimization);
	}
}
