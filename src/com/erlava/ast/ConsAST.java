package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;
import java.util.LinkedList;

public class ConsAST implements AST, Serializable {

    public AST left, right;

    public ConsAST(AST left, AST right, int line, String current) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BarleyValue execute() {
        LinkedList<BarleyValue> list = new LinkedList<>();
        list.add(left.execute());
        list.add(right.execute());
        return new BarleyList(list);
    }

    @Override
    public void visit(Optimization optimization) {
        left = optimization.optimize(left);
        right = optimization.optimize(right);
    }


    public AST getLeft() {
        return left;
    }

    public AST getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " | " + right;
    }
}
