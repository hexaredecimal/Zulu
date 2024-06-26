package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;

public class UnPackAST implements AST, Serializable {
		private static final long serialVersionUID = 1L;

    public AST ast;

    public UnPackAST(AST ast, int line, String current) {
        this.ast = ast;
    }

    @Override
    public BarleyValue execute() {
        return new BarleyString("unpack");
    }

    @Override
    public void visit(Optimization optimization) {

    }

    public AST getAst() {
        return ast;
    }

    @Override
    public String toString() {
        return "unpack_expr " + ast;
    }
}
