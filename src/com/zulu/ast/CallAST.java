package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluFunction;
import com.zulu.runtime.ZuluList;
import com.zulu.utils.AST;
import com.zulu.utils.CallStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import com.zulu.runtime.ZuluValue;

public class CallAST implements AST {

	private static final long serialVersionUID = 1L;

	//private final int line;
	//private final String current;
	private AST obj;
	private ArrayList<AST> args;

	public CallAST(AST obj, ArrayList<AST> args, int line, String current) {
		this.obj = obj;
		this.args = args;
		//this.line = line;
		//this.current = current;
	}

	@Override
	public ZuluValue execute() {
		List<ZuluValue> arg = new ArrayList<>();
		for (int i = 0; i < args.size(); i++) {
			AST node = args.get(i);
			if (node instanceof UnPackAST pack) {
				LinkedList<ZuluValue> list = (((ZuluList) pack.getAst().execute()).getList());
				arg.addAll(list);
				break;
			} else {
				arg.add(node.execute());
			}
		}
		ZuluValue[] arguments = arg.toArray(new ZuluValue[]{});
		ZuluValue temporal = obj.execute();
		ZuluFunction function = (ZuluFunction) temporal;
		ZuluValue result;
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
