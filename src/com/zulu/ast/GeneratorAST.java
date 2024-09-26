package com.zulu.ast;

import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.reflection.Reflection;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;

import java.util.Collection;
import java.util.LinkedList;
import com.zulu.runtime.ZuluValue;

public class GeneratorAST implements AST {

	private static final long serialVersionUID = 1L;
	private final int line;
	private final String current;
	public AST iterable;
	public AST gen;
	private String var;

	public GeneratorAST(AST gen, String var, AST iterable, int line, String current) {
		this.gen = gen;
		this.var = var;
		this.iterable = iterable;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue value = iterable.execute();
		ZuluList list = null;
		if (value instanceof Reflection.ObjectValue o) {
			Object object = o.object;
			if (object instanceof Collection<?> ob) {
				Object[] objs = ob.toArray(new Object[0]);
				LinkedList<ZuluValue> lst = new LinkedList<>();
				for (Object v : objs) {
					lst.add(new Reflection.ObjectValue(v));
				}
				list = new ZuluList(lst);
			}
		} else {
			if (!(value instanceof ZuluList)) {
				Main.error("BadGenerator", "expected list as enumerable", line, current);
			}
			list = (ZuluList) value;
		}
		LinkedList<ZuluValue> result = new LinkedList<>();
		int size = list.getList().size();
		if (size >= 1000000000) {
			throw new ZuluException("BadGenerator", "generator '" + (gen + " || " + var + " -> " + list) + "' will cause 'segmentation fault'");
		}
		Table.push();
		for (int i = 0; i < size; i++) {
			ZuluValue obj = list.getList().get(i);
			Table.set(var, obj);
			Table.set("ITERATION", new ZuluNumber(i));
			ZuluValue res = (gen.execute());
			if (res.toString().equals("generator_skip")) {
				continue;
			}
			result.add(res);
		}
		Table.pop();
		return new ZuluList(result);
	}

	@Override
	public void visit(Optimization optimization) {
		iterable = optimization.optimize(iterable);
	}

	@Override
	public String toString() {
		return gen + " || " + var + " -> " + iterable;
	}
}
