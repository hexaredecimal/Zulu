package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.parser.Parser;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.UserFunction;
import com.erlava.utils.AST;
import com.erlava.utils.Clause;

import java.io.Serializable;

public class MethodAST implements AST, Serializable {
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
	public BarleyValue execute() {
		parser.methods.put(name, method);
		return new BarleyAtom("ok");
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
