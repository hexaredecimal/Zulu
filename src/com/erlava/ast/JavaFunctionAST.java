package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import com.erlava.utils.Function;

import java.io.Serializable;
import java.util.Arrays;

public class JavaFunctionAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    private Function function;
    private BarleyValue[] args;

    public JavaFunctionAST(Function function, BarleyValue[] args) {
        this.function = function;
        this.args = args;
    }

    @Override
    public BarleyValue execute() {
        return function.execute(args);
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return function.toString() + Arrays.asList(args);
    }
}
