package com.erlava.ast;

import com.erlava.Main;
import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyPointer;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import com.erlava.utils.BarleyException;
import com.erlava.utils.Pointers;

import java.io.Serializable;

public class UnPointAST implements AST, Serializable {

    private final int line;
    private final String current;
    private AST ast;

    public UnPointAST(AST ast, int line, String current) {
        this.ast = ast;
        this.line = line;
        this.current = current;
    }

    @Override
    public BarleyValue execute() {
        BarleyValue execute = ast.execute();
        if (!(execute instanceof BarleyPointer))
            Main.error("BadPointer", "expected POINTER as pointer, got '" + execute.toString() + "'", line, current);
        BarleyValue res = Pointers.get(execute.toString());
        if (res == null) throw new BarleyException("BadPointer", "segmentation fault");
        return res;
    }

    @Override
    public void visit(Optimization optimization) {
        ast.visit(optimization);
    }

    @Override
    public String toString() {
        return "##" +  ast.toString();
    }
}
