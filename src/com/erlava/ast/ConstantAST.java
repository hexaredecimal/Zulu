package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;

public class ConstantAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    public BarleyValue constant;

    public ConstantAST(BarleyValue constant) {
        this.constant = constant;
    }

    @Override
    public BarleyValue execute() {
        return constant;
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return constant.toString();
    }
}
