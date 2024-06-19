package com.erlava.optimizations;

import com.erlava.ast.RemoteAST;
import com.erlava.ast.UnaryAST;
import com.erlava.ast.ProcessCallAST;
import com.erlava.ast.MethodAST;
import com.erlava.ast.CompileAST;
import com.erlava.ast.CallAST;
import com.erlava.ast.GeneratorAST;
import com.erlava.ast.CaseAST;
import com.erlava.ast.ListAST;
import com.erlava.ast.BlockAST;
import com.erlava.ast.TernaryAST;
import com.erlava.ast.RecieveAST;
import com.erlava.ast.BinaryAST;
import com.erlava.ast.ExtractBindAST;
import com.erlava.ast.ConsAST;
import com.erlava.ast.ConstantAST;
import com.erlava.ast.BindAST;
import com.erlava.utils.AST;
import com.erlava.utils.Clause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariableGrabber {

    private HashMap<String, VariableInfo> info;
    private HashMap<String, Integer> mods;
    private TableEmulator result;

    public TableEmulator emulate(ArrayList<AST> nodes) {
        getInfo(nodes);
        return result;
    }

    public Map<String, VariableInfo> getInfo(ArrayList<AST> nodes) {
        info = new HashMap<>();
        mods = new HashMap<>();
        result = new TableEmulator();
        for (AST node : nodes) {
            cast(node);
        }
        return info;
    }

    private AST optimize(BindAST ast) {
        HashMap<String, VariableInfo> vars = ast.emulate(info, mods);
        for (Map.Entry<String, VariableInfo> entry : vars.entrySet()) {
            result.set(entry.getKey(), entry.getValue());
        }
        return ast;
    }

    private AST optimize(BlockAST ast) {
        for (AST node : ast.block) {
            cast(node);
        }
        return ast;
    }

    private AST optimize(MethodAST ast) {
        ArrayList<Clause> clauses = ast.method.clauses;
        result.push();
        for (Clause cl : clauses) {
            cast(cl.getResult());
        }
        return ast;
    }

    private AST optimize(CaseAST ast) {
        for (CaseAST.Pattern pattern : ast.patterns) {
            cast(pattern.result);
        }
        return ast;
    }

    private AST optimize(AST ast) {
        return ast;
    }

    public AST cast(AST ast) {
        if (ast instanceof BinaryAST) {
            return optimize((BinaryAST) ast);
        } else if (ast instanceof BindAST) {
            return optimize((BindAST) ast);
        } else if (ast instanceof CallAST) {
            return optimize((CallAST) ast);
        } else if (ast instanceof CaseAST) {
            return optimize((CaseAST) ast);
        } else if (ast instanceof CompileAST) {
            return optimize((CompileAST) ast);
        } else if (ast instanceof ConsAST) {
            return optimize((ConsAST) ast);
        } else if (ast instanceof ConstantAST) {
            return optimize((ConstantAST) ast);
        } else if (ast instanceof ExtractBindAST) {
            return optimize((ExtractBindAST) ast);
        } else if (ast instanceof GeneratorAST) {
            return optimize((GeneratorAST) ast);
        } else if (ast instanceof ListAST) {
            return optimize((ListAST) ast);
        } else if (ast instanceof MethodAST) {
            return optimize((MethodAST) ast);
        } else if (ast instanceof ProcessCallAST) {
            return optimize((ProcessCallAST) ast);
        } else if (ast instanceof RecieveAST) {
            return optimize((RecieveAST) ast);
        } else if (ast instanceof RemoteAST) {
            return optimize((RemoteAST) ast);
        } else if (ast instanceof TernaryAST) {
            return optimize((TernaryAST) ast);
        } else if (ast instanceof UnaryAST) {
            return optimize((UnaryAST) ast);
        } else if (ast instanceof BlockAST) {
            return optimize((BlockAST) ast);
        }
        return ast;
    }

}
