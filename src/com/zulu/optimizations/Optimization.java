package com.zulu.optimizations;

import com.zulu.ast.RemoteAST;
import com.zulu.ast.UnaryAST;
import com.zulu.ast.ProcessCallAST;
import com.zulu.ast.MethodAST;
import com.zulu.ast.CompileAST;
import com.zulu.ast.CallAST;
import com.zulu.ast.JavaFunctionAST;
import com.zulu.ast.GeneratorAST;
import com.zulu.ast.CaseAST;
import com.zulu.ast.ListAST;
import com.zulu.ast.BlockAST;
import com.zulu.ast.RecieveAST;
import com.zulu.ast.TernaryAST;
import com.zulu.ast.BinaryAST;
import com.zulu.ast.ExtractBindAST;
import com.zulu.ast.ConsAST;
import com.zulu.ast.ConstantAST;
import com.zulu.ast.BindAST;
import com.zulu.utils.AST;

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
