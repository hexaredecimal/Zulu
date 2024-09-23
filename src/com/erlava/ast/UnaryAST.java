package com.erlava.ast;

import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyNumber;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.AtomTable;
import com.erlava.Main;
import com.erlava.optimizations.Optimization;
import com.erlava.utils.AST;

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
	public BarleyValue execute() {
		BarleyValue val1 = expr1.execute();

		switch (op) {
			case '-':
				return new BarleyNumber(-val1.asFloat().doubleValue());
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

	private BarleyValue not(BarleyValue value) {
		if (value instanceof BarleyNumber) {
			return new BarleyAtom(AtomTable.put(String.valueOf(value.asInteger().intValue() != 0)));
		} else if (value instanceof BarleyString) {
			return new BarleyAtom(AtomTable.put(String.valueOf(!(value.toString().isEmpty()))));
		} else if (value instanceof BarleyList) {
			return new BarleyAtom(AtomTable.put(String.valueOf(value.toString().equals("[]"))));
		} else if (value instanceof BarleyAtom) {
			return new BarleyAtom(String.valueOf(value.toString().equals("false")));
		} else {
			badArith(value);
		}
		return null;
	}

	public void badArith(BarleyValue value) {
		Main.error("BadArith", "an error occurred when evaluation an arithmetic expression\n  called as: \n    " + op + value, line, current);
	}

	@Override
	public String toString() {
		return String.format("%s%s", expr1, op);
	}
}
