package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.AtomTable;
import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.Modules;
import com.zulu.utils.AST;
import com.zulu.utils.Function;
import java.util.HashMap;
import com.zulu.runtime.ZuluValue;

public class CompileAST implements AST {

	private static final long serialVersionUID = 1L;
	private String module;
	private HashMap<String, Function> methods;

	public CompileAST(String module, HashMap<String, Function> methods) {
		this.module = module;
		this.methods = methods;
	}

	@Override
	public ZuluValue execute() {
		Modules.put(module, methods);
		return new ZuluAtom(AtomTable.put("ok"));
	}

	@Override
	public void visit(Optimization optimization) {

	}

	@Override
	public String toString() {
		return "";
	}
}
