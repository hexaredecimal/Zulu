package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluPID;
import com.zulu.runtime.ProcessTable;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.utils.BarleyException;
import com.zulu.utils.CallStack;
import com.zulu.runtime.ZuluValue;

public class RecieveAST implements AST {

	private static final long serialVersionUID = 1L;
	private AST pid, body;
	private ZuluPID p;

	public RecieveAST(AST pid, AST body) {
		this.pid = pid;
		this.body = body;
		this.p = (ZuluPID) pid.execute();
		ProcessTable.receives.put(p, new JavaFunctionAST(args -> {
			Table.set("Rest", ProcessTable.get(p));
			ZuluValue previous = ProcessTable.get(p);
			try {
				ProcessTable.put(p, body.execute());
			} catch (BarleyException ex) {
				System.out.printf("** ERROR REPORT IN THREAD %s: %s\n", p, ex.getText());
				int count = CallStack.getCalls().size();
				if (count == 0) {
					return previous;
				}
				System.out.println(String.format("\nCall stack was:"));
				for (CallStack.CallInfo info : CallStack.getCalls()) {
					System.out.println("    " + count + ". " + info);
					count--;
				}
			}
			Table.remove("Rest");
			return ProcessTable.get(p);
		}, new ZuluValue[]{}));
	}

	@Override
	public ZuluValue execute() {
		return p;
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "recieve " + pid + " -> " + body;
	}
}
