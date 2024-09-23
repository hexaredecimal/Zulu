package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluString;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class UnPackAST implements AST {

	private static final long serialVersionUID = 1L;

	public AST ast;

	public UnPackAST(AST ast, int line, String current) {
		this.ast = ast;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluString("unpack");
	}

	@Override
	public void visit(Optimization optimization) {

	}

	public AST getAst() {
		return ast;
	}

	@Override
	public String toString() {
		return "unpack_expr " + ast;
	}
}
