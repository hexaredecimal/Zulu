package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluList;
import com.zulu.utils.AST;
import java.util.LinkedList;
import com.zulu.runtime.ZuluValue;

public class ConsAST implements AST {

	private static final long serialVersionUID = 1L;
	public AST left, right;

	public ConsAST(AST left, AST right, int line, String current) {
		this.left = left;
		this.right = right;
	}

	@Override
	public ZuluValue execute() {
		LinkedList<ZuluValue> list = new LinkedList<>();
		list.add(left.execute());
		list.add(right.execute());
		return new ZuluList(list);
	}

	@Override
	public void visit(Optimization optimization) {
		left = optimization.optimize(left);
		right = optimization.optimize(right);
	}

	public AST getLeft() {
		return left;
	}

	public AST getRight() {
		return right;
	}

	@Override
	public String toString() {
		return left + " | " + right;
	}
}
