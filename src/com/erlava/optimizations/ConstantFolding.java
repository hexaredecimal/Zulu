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

import java.util.LinkedList;

public class ConstantFolding implements Optimization {

	private int count;

	@Override
	public String summary() {
		return "Performed " + count + " folding optimizations";
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public AST optimize(BinaryAST ast) {
		AST left = optimize(ast.expr1);
		AST right = optimize(ast.expr2);
		if ((left instanceof ConstantAST) && (right instanceof ConstantAST)) {
			count++;
			return new ConstantAST(ast.execute());
		} else {
			return ast;
		}
	}

	@Override
	public AST optimize(BindAST ast) {
		ast.visit(this);
		count++;
		return ast;
	}

	@Override
	public AST optimize(BlockAST ast) {
		ast.visit(this);
		count++;
		return ast;
	}

	@Override
	public AST optimize(CallAST ast) {
		count++;
		ast.visit(this);
		return ast;
	}

	@Override
	public AST optimize(CaseAST ast) {
		return ast;
	}

	@Override
	public AST optimize(CompileAST ast) {
		return ast;
	}

	@Override
	public AST optimize(ConsAST ast) {
		count++;
		if ((ast.left instanceof ConstantAST) && (ast.right instanceof ConstantAST)) {
			return new ConstantAST(ast.execute());
		} else {
			return ast;
		}
	}

	@Override
	public AST optimize(ConstantAST ast) {
		return ast;
	}

	@Override
	public AST optimize(ExtractBindAST ast) {
		return ast;
	}

	@Override
	public AST optimize(GeneratorAST ast) {
		ast.iterable.visit(this);
		return ast;
	}

	@Override
	public AST optimize(JavaFunctionAST ast) {
		return ast;
	}

	@Override
	public AST optimize(ListAST ast) {
		LinkedList<AST> result = new LinkedList<>();
		for (AST node : ast.getArray()) {
			node.visit(this);
			result.add(optimize(node));
		}
		count++;
		return new ListAST(result);
	}

	@Override
	public AST optimize(MethodAST ast) {
		ast.visit(this);
		return ast;
	}

	@Override
	public AST optimize(ProcessCallAST ast) {
		optimize(ast.expr);
		return ast;
	}

	@Override
	public AST optimize(RemoteAST ast) {
		return ast;
	}

	@Override
	public AST optimize(TernaryAST ast) {
		count++;
		if ((ast.term instanceof ConstantAST) && (ast.left instanceof ConstantAST) && (ast.right instanceof ConstantAST)) {
			return new ConstantAST(ast.execute());
		}
		return ast;
	}

	@Override
	public AST optimize(RecieveAST ast) {
		return ast;
	}

	@Override
	public AST optimize(UnaryAST ast) {
		count++;
		AST left = optimize(ast.expr1);
		if (left instanceof ConstantAST) {
			return new ConstantAST(ast.execute());
		}
		return ast;
	}

	@Override
	public AST optimize(AST ast) {
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
		ast.visit(this);
		return ast;
	}
}
