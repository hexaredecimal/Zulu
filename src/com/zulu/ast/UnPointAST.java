package com.zulu.ast;

import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluPointer;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;
import com.zulu.utils.Pointers;
import com.zulu.runtime.ZuluValue;

public class UnPointAST implements AST {

	private static final long serialVersionUID = 1L;
	private final int line;
	private final String current;
	private AST ast;

	public UnPointAST(AST ast, int line, String current) {
		this.ast = ast;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue execute = ast.execute();
		if (!(execute instanceof ZuluPointer)) {
			Main.error("BadPointer", "expected POINTER as pointer, got '" + execute.toString() + "'", line, current);
		}
		ZuluValue res = Pointers.get(execute.toString());
		if (res == null) {
			throw new ZuluException("BadPointer", "segmentation fault");
		}
		return res;
	}

	@Override
	public void visit(Optimization optimization) {
		ast.visit(optimization);
	}

	@Override
	public String toString() {
		return "##" + ast.toString();
	}
}
