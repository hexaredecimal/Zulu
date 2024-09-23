package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluString;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class PackAST implements AST {

	private static final long serialVersionUID = 1L;
	public String name;

	public PackAST(String name, int line, String current) {
		this.name = name;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluString("pack");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "pack_expr " + name;
	}
}
