package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyPID;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.ProcessTable;
import com.erlava.runtime.Table;
import com.erlava.utils.AST;
import com.erlava.utils.BarleyException;
import com.erlava.utils.CallStack;

import java.io.Serializable;

public class RecieveAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    private AST pid, body;
    private BarleyPID p;

    public RecieveAST(AST pid, AST body) {
        this.pid = pid;
        this.body = body;
        this.p = (BarleyPID) pid.execute();
        ProcessTable.receives.put(p, new JavaFunctionAST(args -> {
            Table.set("Rest", ProcessTable.get(p));
            BarleyValue previous = ProcessTable.get(p);
            try {
                ProcessTable.put(p, body.execute());
            } catch (BarleyException ex) {
                System.out.printf("** ERROR REPORT IN THREAD %s: %s\n", p, ex.getText());
                int count = CallStack.getCalls().size();
                if (count == 0) return previous;
                System.out.println(String.format("\nCall stack was:"));
                for (CallStack.CallInfo info : CallStack.getCalls()) {
                    System.out.println("    " + count + ". " + info);
                    count--;
                }
            }
            Table.remove("Rest");
            return ProcessTable.get(p);
        }, new BarleyValue[]{}));
    }

    @Override
    public BarleyValue execute() {
        return p;
    }

    @Override
    public void visit(Optimization optimization) {

    }

    @Override
    public String toString() {
        return "recieve " + pid + " -> " + body;
    }
}
