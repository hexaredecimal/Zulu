package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;
import java.util.LinkedList;

public class ListAST implements AST, Serializable {

    private LinkedList<AST> array;

    public ListAST(LinkedList<AST> array) {
        this.array = array;
    }

    @Override
    public BarleyValue execute() {
        LinkedList<BarleyValue> arr = new LinkedList<>();
        for (AST ast : array) {
            arr.add(ast.execute());
        }
        return new BarleyList(arr);
    }

    @Override
    public void visit(Optimization optimization) {
        optimization.optimize(this);
    }

    public LinkedList<AST> getArray() {
        return array;
    }

    @Override
    public String toString() {
        return array.toString();
    }
}
