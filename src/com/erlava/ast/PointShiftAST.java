package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyPointer;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import com.erlava.utils.Pointers;

import java.io.Serializable;

public class PointShiftAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    private AST pointer, value;

    public PointShiftAST(AST pointer, AST value) {
        this.pointer = pointer;
        this.value = value;
    }

    @Override
    public BarleyValue execute() {
        BarleyPointer point = (BarleyPointer) pointer.execute();
        Pointers.put(point.toString(), value.execute());
        return point;
    }

    @Override
    public void visit(Optimization optimization) {
        pointer.visit(optimization);
        value.visit(optimization);
    }

    @Override
    public String toString() {
        return pointer + " >> " + value;
    }
}
