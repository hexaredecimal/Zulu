package com.zulu.ast;

import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class ExtractBindAST implements AST {

	private static final long serialVersionUID = 1L;

	private final int line;
	private final String current;
	private String constant;

	public ExtractBindAST(String constant, int line, String current) {
		this.constant = constant;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		if (!Table.isExists(constant)) {
			Main.error("UnboundVar", "unbound var '" + constant + "'", line, current);
		}
		return Table.get(constant);
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return constant;
	}
}
