package com.barley.ast;

import com.barley.optimizations.Optimization;
import com.barley.runtime.BarleyValue;
import com.barley.runtime.Table;
import com.barley.utils.AST;

import java.io.Serializable;

public class ExtractBindAST implements AST, Serializable {

    private String constant;

    public ExtractBindAST(String constant) {
        this.constant = constant;
    }

    @Override
    public BarleyValue execute() {
        return Table.get(constant);
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return constant;
    }
}
