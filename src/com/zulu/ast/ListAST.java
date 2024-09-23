package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluList;
import com.zulu.utils.AST;
import java.util.LinkedList;
import com.zulu.runtime.ZuluValue;

public class ListAST implements AST {

	private static final long serialVersionUID = 1L;
	private LinkedList<AST> array;

	public ListAST(LinkedList<AST> array) {
		this.array = array;
	}

	@Override
	public ZuluValue execute() {
		LinkedList<ZuluValue> arr = new LinkedList<>();
		for (AST ast : array) {
			arr.add(ast.execute());
		}
		return new ZuluList(arr);
	}

	@Override
	public void visit(Optimization optimization) {
		optimization.optimize(this);
	}

	public LinkedList<AST> getArray() {
		return array;
	}

	@Override
	public String toString() {
		return array.toString();
	}
}
