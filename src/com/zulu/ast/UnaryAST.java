package com.zulu.ast;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.AtomTable;
import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

public class UnaryAST implements AST {

	private static final long serialVersionUID = 1L;

	private final int line;
	private final String current;
	public AST expr1;
	private char op;

	public UnaryAST(AST expr1, char op, int line, String current) {
		this.expr1 = expr1;
		this.op = op;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue val1 = expr1.execute();

		switch (op) {
			case '-':
				return new ZuluNumber(-val1.asFloat().doubleValue());
			case 'n':
				return not(val1);
			default:
				badArith(val1);
		}
		return null;
	}

	@Override
	public void visit(Optimization optimization) {
		expr1 = optimization.optimize(expr1);
	}

	private ZuluValue not(ZuluValue value) {
		if (value instanceof ZuluNumber) {
			return new ZuluAtom(AtomTable.put(String.valueOf(value.asInteger().intValue() != 0)));
		} else if (value instanceof ZuluString) {
			return new ZuluAtom(AtomTable.put(String.valueOf(!(value.toString().isEmpty()))));
		} else if (value instanceof ZuluList) {
			return new ZuluAtom(AtomTable.put(String.valueOf(value.toString().equals("[]"))));
		} else if (value instanceof ZuluAtom) {
			return new ZuluAtom(String.valueOf(value.toString().equals("false")));
		} else {
			badArith(value);
		}
		return null;
	}

	public void badArith(ZuluValue value) {
		Main.error("BadArith", "an error occurred when evaluation an arithmetic expression\n  called as: \n    " + op + value, line, current);
	}

	@Override
	public String toString() {
		return String.format("%s%s", expr1, op);
	}
}
