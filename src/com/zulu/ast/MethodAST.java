package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.parser.Parser;
import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.UserFunction;
import com.zulu.utils.AST;
import com.zulu.utils.Clause;
import com.zulu.runtime.ZuluValue;

public class MethodAST implements AST {

	private static final long serialVersionUID = 1L;
	public UserFunction method;
	private Parser parser;
	private String name;

	public String getName() {
		return name;
	}

	public MethodAST(Parser parser, UserFunction method, String name) {
		this.parser = parser;
		this.method = method;
		this.name = name;
	}

	@Override
	public ZuluValue execute() {
		parser.methods.put(name, method);
		return new ZuluAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {
		method.optimize(optimization);
	}

	@Override
	public String toString() {
		String result = name;
		for (Clause cl : method.clauses) {
			result += cl.getArgs();
			if (cl.getGuard() != null) {
				result += " when " + cl.getGuard();
			}
			result += " -> ";
			result += cl.getResult();
		}
		return result;
	}
}
