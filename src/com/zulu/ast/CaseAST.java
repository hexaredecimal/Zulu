package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.utils.BarleyException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.zulu.runtime.ZuluValue;

public final class CaseAST implements AST {

	private static final long serialVersionUID = 1L;
	public final AST expression;
	public final List<Pattern> patterns;

	public CaseAST(AST expression, List<Pattern> patterns) {
		this.expression = expression;
		this.patterns = patterns;
	}

	@Override
	public ZuluValue execute() {
		Table.push();
		ZuluValue res = eval();
		return res;
	}

	@Override
	public void visit(Optimization optimization) {

	}

	public ZuluValue eval() {
		final ZuluValue value = expression.execute();
		for (Pattern p : patterns) {
			if (p instanceof ConstantPattern) {
				final ConstantPattern pattern = (ConstantPattern) p;
				if (match(value, pattern.constant) && optMatches(p)) {
					return evalResult(p.result);
				}
			}
			if (p instanceof VariablePattern) {
				final VariablePattern pattern = (VariablePattern) p;
				if (pattern.variable.equals("_")) {
					return evalResult(p.result);
				}

				if (Table.isExists(pattern.variable)) {
					if (match(value, Table.get(pattern.variable)) && optMatches(p)) {
						return evalResult(p.result);
					}
				} else {
					Table.define(pattern.variable, value);
					if (optMatches(p)) {
						final ZuluValue result = evalResult(p.result);
						Table.remove(pattern.variable);
						return result;
					}
					Table.remove(pattern.variable);
				}
			}
			if ((value instanceof ZuluList) && (p instanceof ListPattern)) {
				final ListPattern pattern = (ListPattern) p;
				if (matchListPattern((ZuluList) value, pattern)) {
					// Clean up variables if matched
					final ZuluValue result = evalResult(p.result);
					for (String var : pattern.parts) {
						Table.remove(var);
					}
					return result;
				}
			}
			if ((value instanceof ZuluList) && (p instanceof TuplePattern)) {
				final TuplePattern pattern = (TuplePattern) p;
				if (matchTuplePattern((ZuluList) value, pattern) && optMatches(p)) {
					return evalResult(p.result);
				}
			}
		}
		throw new BarleyException("BadMatch", "no patterns were matched. patterns: " + patterns);
	}

	private boolean matchTuplePattern(ZuluList array, TuplePattern p) {
		if (p.values.size() != array.getList().size()) {
			return false;
		}

		final int size = array.getList().size();
		for (int i = 0; i < size; i++) {
			final AST expr = p.values.get(i);
			if ((expr != TuplePattern.ANY) && (expr.execute().equals(expr))) {
				return false;
			}
		}
		return true;
	}

	private boolean matchListPattern(ZuluList array, ListPattern p) {
		final List<String> parts = p.parts;
		final int partsSize = parts.size();
		final int arraySize = array.getList().size();
		switch (partsSize) {
			case 0: // match [] { case []: ... }
				if ((arraySize == 0) && optMatches(p)) {
					return true;
				}
				return false;

			case 1: // match arr { case [x]: x = arr ... }
				final String variable = parts.get(0);
				Table.define(variable, array);
				if (optMatches(p)) {
					return true;
				}
				Table.remove(variable);
				return false;

			default: { // match arr { case [...]: .. }
				if (partsSize == arraySize) {
					// match [0, 1, 2] { case [a::b::c]: a=0, b=1, c=2 ... }
					return matchListPatternEqualsSize(p, parts, partsSize, array);
				} else if (partsSize < arraySize) {
					// match [1, 2, 3] { case [head :: tail]: ... }
					return matchListPatternWithTail(p, parts, partsSize, array, arraySize);
				}
				return false;
			}
		}
	}

	private boolean matchListPatternEqualsSize(ListPattern p, List<String> parts, int partsSize, ZuluList array) {
		// Set variables
		for (int i = 0; i < partsSize; i++) {
			Table.define(parts.get(i), array.getList().get(i));
		}
		if (optMatches(p)) {
			// Clean up will be provided after evaluate result
			return true;
		}
		// Clean up variables if no match
		for (String var : parts) {
			Table.remove(var);
		}
		return false;
	}

	private boolean matchListPatternWithTail(ListPattern p, List<String> parts, int partsSize, ZuluList array, int arraySize) {
		// Set element variables
		final int lastPart = partsSize - 1;
		for (int i = 0; i < lastPart; i++) {
			Table.define(parts.get(i), array.getList().get(i));
		}
		// Set tail variable
		final ZuluList tail = new ZuluList(arraySize - partsSize + 1);
		for (int i = lastPart; i < arraySize; i++) {
			tail.set(i - lastPart, array.getList().get(i));
		}
		Table.define(parts.get(lastPart), tail);
		// Check optional condition
		if (optMatches(p)) {
			// Clean up will be provided after evaluate result
			return true;
		}
		// Clean up variables
		for (String var : parts) {
			Table.remove(var);
		}
		return false;
	}

	private boolean match(ZuluValue value, ZuluValue constant) {
		return value.equals(constant);
	}

	private boolean optMatches(Pattern pattern) {
		if (pattern.optCondition == null) {
			return true;
		}
		return pattern.optCondition.execute().toString() != "false";
	}

	private ZuluValue evalResult(AST s) {
		return s.execute();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("case ").append(expression).append(" {");
		for (Pattern p : patterns) {
			sb.append("\n  of ").append(p);
		}
		sb.append("\n}");
		return sb.toString();
	}

	public abstract static class Pattern implements Serializable {

		public AST result;
		public AST optCondition;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			if (optCondition != null) {
				sb.append(" when ").append(optCondition);
			}
			sb.append(": ").append(result);
			return sb.toString();
		}
	}

	public static class ConstantPattern extends Pattern {

		ZuluValue constant;

		public ConstantPattern(ZuluValue pattern) {
			this.constant = pattern;
		}

		@Override
		public String toString() {
			return constant.toString().concat(super.toString());
		}
	}

	public static class VariablePattern extends Pattern {

		public String variable;

		public VariablePattern(String pattern) {
			this.variable = pattern;
		}

		@Override
		public String toString() {
			return variable.concat(super.toString());
		}
	}

	public static class ListPattern extends Pattern {

		List<String> parts;

		public ListPattern() {
			this(new ArrayList<>());
		}

		ListPattern(List<String> parts) {
			this.parts = parts;
		}

		public void add(String part) {
			parts.add(part);
		}

		@Override
		public String toString() {
			final Iterator<String> it = parts.iterator();
			if (it.hasNext()) {
				final StringBuilder sb = new StringBuilder();
				sb.append("[").append(it.next());
				while (it.hasNext()) {
					sb.append(" :: ").append(it.next());
				}
				sb.append("]").append(super.toString());
				return sb.toString();
			}
			return "[]".concat(super.toString());
		}
	}

	public static class TuplePattern extends Pattern {

		private static final AST ANY = new AST() {
			@Override
			public ZuluValue execute() {
				return new ZuluNumber(1);
			}

			@Override
			public void visit(Optimization optimization) {

			}

			@Override
			public String toString() {
				return "_".concat(super.toString());
			}
		};
		public List<AST> values;

		public TuplePattern() {
			this(new ArrayList<AST>());
		}

		public TuplePattern(List<AST> parts) {
			this.values = parts;
		}

		public void addAny() {
			values.add(ANY);
		}

		public void add(AST value) {
			values.add(value);
		}

		@Override
		public String toString() {
			final Iterator<AST> it = values.iterator();
			if (it.hasNext()) {
				final StringBuilder sb = new StringBuilder();
				sb.append('(').append(it.next());
				while (it.hasNext()) {
					sb.append(", ").append(it.next());
				}
				sb.append(')').append(super.toString());
				return sb.toString();
			}
			return "()".concat(super.toString());
		}
	}
}
