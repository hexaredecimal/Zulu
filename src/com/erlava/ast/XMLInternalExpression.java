package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

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
	public BarleyValue execute() {
		return exp.execute();
	}

	@Override
	public void visit(Optimization optimization) {
		exp.visit(optimization);
	}
}
