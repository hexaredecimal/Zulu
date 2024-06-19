package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;

import java.io.Serializable;

public class UnStrictAST implements AST, Serializable {
    @Override
    public BarleyValue execute() {
        Table.strict = false;
        return new BarleyAtom("ok");
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return "UnStrictAST{}";
    }
}
