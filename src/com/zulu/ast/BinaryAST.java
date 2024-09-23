package com.zulu.ast;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.AtomTable;
import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.utils.AST;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;
import com.zulu.runtime.ZuluValue;

public class BinaryAST implements AST {

	private static final long serialVersionUID = 1L;
	public AST expr1;
	public AST expr2;
	public char op;
	private String current;
	private int line;

	public BinaryAST(AST expr1, AST expr2, char op, int line, String current) {
		this.expr1 = expr1;
		this.expr2 = expr2;
		this.op = op;
		this.current = current;
		this.line = line;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue val1 = expr1.execute();
		ZuluValue val2 = expr2.execute();

		if (val1 instanceof ZuluList) {
			ZuluList list1 = (ZuluList) val1;
			switch (op) {
				case '+':
					if (!(val2 instanceof ZuluList)) {
						badArith(val1, val2);
					}
					ZuluList list2 = (ZuluList) val2;
					LinkedList<ZuluValue> result = new LinkedList<>();
					result.addAll(list1.getList());
					result.addAll(list2.getList());
					return new ZuluList(result);
				case '=':
					return new ZuluAtom(addAtom(String.valueOf(list1.equals(val2))));
				default:
					badArith(val1, val2);
			}
		}

		if (val1 instanceof ZuluString || val2 instanceof ZuluString) {
			String str1 = val1.toString();
			String str2 = val2.toString();
			switch (op) {
				case '+':
					return new ZuluString(str1 + str2);
				case '=':
					return new ZuluAtom(addAtom(String.valueOf(str1.equals(str2))));
				default:
					badArith(val1, val2);
			}
		}

		switch (op) {
			case '-':
				return new ZuluNumber(val1.asFloat().subtract(val2.asFloat()));
			case '*':
				return new ZuluNumber(val1.asFloat().multiply(val2.asFloat()));
			case '/':
				return new ZuluNumber(val1.asFloat().divide(val2.asFloat(), new MathContext(2, RoundingMode.HALF_UP)));
			case '+':
				return new ZuluNumber(val1.asFloat().add(val2.asFloat()));
			case '>':
				return new ZuluAtom(addAtom(String.valueOf(val1.asFloat().doubleValue() > val2.asFloat().doubleValue())));
			case '<':
				return new ZuluAtom(addAtom(String.valueOf(val1.asFloat().doubleValue() < val2.asFloat().doubleValue())));
			case 't':
				return new ZuluAtom(addAtom(String.valueOf(val1.asFloat().doubleValue() <= val2.asFloat().doubleValue())));
			case 'g':
				return new ZuluAtom(addAtom(String.valueOf(val1.asFloat().doubleValue() >= val2.asFloat().doubleValue())));
			case '=':
				return new ZuluAtom(addAtom(String.valueOf(val1.equals(val2))));
			case 'a':
				return new ZuluAtom(addAtom(String.valueOf(istrue(val1) && istrue(val2))));
			case 'o':
				return new ZuluAtom(addAtom(String.valueOf(istrue(val1) || istrue(val2))));
			default:
				badArith(val1, val2);
		}
		return null;
	}

	@Override
	public void visit(Optimization optimization) {
		this.expr1 = optimization.optimize(expr1);
		this.expr2 = optimization.optimize(expr2);
	}

	private boolean istrue(ZuluValue value) {
		return value.toString().equals("true");
	}

	public void badArith(ZuluValue val1, ZuluValue val2) {
		Main.error("BadArith", "an error has occurred when evaluating an arithmetic expression", line, current);
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", expr1, op, expr2);
	}

	private int addAtom(String atom) {
		return AtomTable.put(atom);
	}
}
