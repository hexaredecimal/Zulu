package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.AtomTable;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Modules;
import com.erlava.utils.AST;
import com.erlava.utils.Function;

import java.io.Serializable;
import java.util.HashMap;

public class CompileAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    private String module;
    private HashMap<String, Function> methods;

    public CompileAST(String module, HashMap<String, Function> methods) {
        this.module = module;
        this.methods = methods;
    }

    @Override
    public BarleyValue execute() {
        Modules.put(module, methods);
        return new BarleyAtom(AtomTable.put("ok"));
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return "";
    }
}
