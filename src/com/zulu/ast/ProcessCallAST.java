package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluPID;
import com.zulu.runtime.ProcessTable;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

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
	public ZuluValue execute() {
		Table.set("Message", expr.execute());
		ZuluPID id = (ZuluPID) pid.execute();
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
