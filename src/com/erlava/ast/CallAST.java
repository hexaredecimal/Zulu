package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyFunction;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;
import com.erlava.utils.CallStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CallAST implements AST, Serializable {
		private static final long serialVersionUID = 1L;

    private final int line;
    private final String current;
    private AST obj;
    private ArrayList<AST> args;

    public CallAST(AST obj, ArrayList<AST> args, int line, String current) {
        this.obj = obj;
        this.args = args;
        this.line = line;
        this.current = current;
    }

    @Override
    public BarleyValue execute() {
        List<BarleyValue> arg = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            AST node = args.get(i);
            if (node instanceof UnPackAST pack) {
                LinkedList<BarleyValue> list = (((BarleyList) pack.getAst().execute()).getList());
                arg.addAll(list);
                break;
            } else arg.add(node.execute());
        }
        BarleyValue[] arguments = arg.toArray(new BarleyValue[] {});
        BarleyValue temporal = obj.execute();
        BarleyFunction function = (BarleyFunction) temporal;
        BarleyValue result;
        CallStack.enter(obj.toString() + Arrays.toString(arguments), function);
        result = function.execute(arguments);
        CallStack.exit();
        return result;
    }

    @Override
    public void visit(Optimization optimization) {
        ArrayList<AST> argss = new ArrayList<>();
        for (AST node : args) {
            argss.add(optimization.optimize(node));
        }
        args.clear();
        args.addAll(argss);
    }

    @Override
    public String toString() {
        return obj.toString() + args;
    }
}
