package com.erlava.ast;

import com.erlava.Main;
import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;

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
	public BarleyValue execute() {
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
