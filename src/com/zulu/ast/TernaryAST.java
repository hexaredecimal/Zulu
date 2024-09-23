package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class TernaryAST implements AST {

	private static final long serialVersionUID = 1L;

	//private final int line;
	//private final String current;
	public AST term, left, right;

	public TernaryAST(AST term, AST left, AST right, int line, String current) {
		this.term = term;
		this.left = left;
		this.right = right;
		//this.line = line;
		//this.current = current;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue t = term.execute();
		if (t.toString().equals("true")) {
			return left.execute();
		} else {
			return right.execute();
		}
	}

	@Override
	public void visit(Optimization optimization) {
		term = optimization.optimize(term);
		left = optimization.optimize(left);
		right = optimization.optimize(right);
	}

	@Override
	public String toString() {
		return String.format("%s ? %s :: %s", term, left, right);
	}
}
