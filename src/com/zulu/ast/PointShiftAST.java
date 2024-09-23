package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluPointer;
import com.zulu.utils.AST;
import com.zulu.utils.Pointers;
import com.zulu.runtime.ZuluValue;

public class PointShiftAST implements AST {

	private static final long serialVersionUID = 1L;
	private AST pointer, value;

	public PointShiftAST(AST pointer, AST value) {
		this.pointer = pointer;
		this.value = value;
	}

	@Override
	public ZuluValue execute() {
		ZuluPointer point = (ZuluPointer) pointer.execute();
		Pointers.put(point.toString(), value.execute());
		return point;
	}

	@Override
	public void visit(Optimization optimization) {
		pointer.visit(optimization);
		value.visit(optimization);
	}

	@Override
	public String toString() {
		return pointer + " >> " + value;
	}
}
