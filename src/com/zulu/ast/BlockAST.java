package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import java.util.ArrayList;
import com.zulu.runtime.ZuluValue;

public class BlockAST implements AST {

	private static final long serialVersionUID = 1L;
	public ArrayList<AST> block;

	public BlockAST(ArrayList<AST> block) {
		this.block = block;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue last = null;
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
