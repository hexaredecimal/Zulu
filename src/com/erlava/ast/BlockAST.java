package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    public ArrayList<AST> block;

    public BlockAST(ArrayList<AST> block) {
        this.block = block;
    }

    @Override
    public BarleyValue execute() {
        int size = block.size();
        BarleyValue last = null;
        Table.push();
        for (AST ast : block) {
            last = ast.execute();
        }
        Table.pop();
        return last;
    }

    @Override
    public void visit(Optimization optimization) {
        ArrayList<AST> result = new ArrayList<>();
        for (AST node : block) {
            result.add(optimization.optimize(node));
        }
        block = result;
    }

    public boolean add(AST ast) {
        return block.add(ast);
    }

    @Override
    public String toString() {
        String result = "";
        for (AST node : block) {
            result += node + "\n";
        }
        return result;
    }
}
