package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

public class PackAST implements AST {

		private static final long serialVersionUID = 1L;
    public String name;

    public PackAST(String name, int line, String current) {
        this.name = name;
    }

    @Override
    public BarleyValue execute() {
        return new BarleyString("pack");
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return "pack_expr " + name;
    }
}
