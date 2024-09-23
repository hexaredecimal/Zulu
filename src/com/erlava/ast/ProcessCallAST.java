package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyPID;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.ProcessTable;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;

public class ProcessCallAST implements AST {

	private static final long serialVersionUID = 1L;
	//private final int line;
	//private final String current;
	public AST pid, expr;

	public ProcessCallAST(AST pid, AST expr, int line, String current) {
		this.pid = pid;
		this.expr = expr;
		//this.line = line;
		//this.current = current;
	}

	@Override
	public BarleyValue execute() {
		Table.set("Message", expr.execute());
		BarleyPID id = (BarleyPID) pid.execute();
		JavaFunctionAST ast = (JavaFunctionAST) ProcessTable.receives.get(id);
		return ast.execute();
	}

	@Override
	public void visit(Optimization optimization) {
		expr = optimization.optimize(expr);
	}

	@Override
	public String toString() {
		return pid + " ! " + expr;
	}
}
