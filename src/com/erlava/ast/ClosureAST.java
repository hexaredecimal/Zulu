package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyClosure;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.UserFunction;
import com.erlava.utils.AST;

import java.io.Serializable;

public class ClosureAST implements AST, Serializable {

    private UserFunction function;

    public ClosureAST(UserFunction function) {
        this.function = function;
    }

    @Override
    public BarleyValue execute() {
        return new BarleyClosure(function);
    }

    @Override
    public void visit(Optimization optimization) {
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
