package com.erlava.optimizations;

import com.erlava.ast.RemoteAST;
import com.erlava.ast.UnaryAST;
import com.erlava.ast.ProcessCallAST;
import com.erlava.ast.MethodAST;
import com.erlava.ast.CompileAST;
import com.erlava.ast.CallAST;
import com.erlava.ast.JavaFunctionAST;
import com.erlava.ast.GeneratorAST;
import com.erlava.ast.CaseAST;
import com.erlava.ast.ListAST;
import com.erlava.ast.BlockAST;
import com.erlava.ast.RecieveAST;
import com.erlava.ast.TernaryAST;
import com.erlava.ast.BinaryAST;
import com.erlava.ast.ExtractBindAST;
import com.erlava.ast.ConsAST;
import com.erlava.ast.ConstantAST;
import com.erlava.ast.BindAST;
import com.erlava.utils.AST;

public interface Optimization {

    String summary();

    int count();

    AST optimize(BinaryAST ast);

    AST optimize(BindAST ast);

    AST optimize(BlockAST ast);

    AST optimize(CallAST ast);

    AST optimize(CaseAST ast);

    AST optimize(CompileAST ast);

    AST optimize(ConsAST ast);

    AST optimize(ConstantAST ast);

    AST optimize(ExtractBindAST ast);

    AST optimize(GeneratorAST ast);

    AST optimize(JavaFunctionAST ast);

    AST optimize(ListAST ast);

    AST optimize(MethodAST ast);

    AST optimize(ProcessCallAST ast);

    AST optimize(RemoteAST ast);

    AST optimize(TernaryAST ast);

    AST optimize(RecieveAST ast);

    AST optimize(UnaryAST ast);

    AST optimize(AST ast);
}
