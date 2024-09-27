package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.ZuluNull;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluPointer;
import com.zulu.runtime.ZuluReference;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluValue;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hexaredecimal
 */
public class CastAST implements AST {

	private String target;
	private AST expr;
	public CastAST(AST expression, String target) {
		this.target = target;
		this.expr = expression;
	}
	
	@Override
	public ZuluValue execute() {
		var result = expr.execute(); 
		if (target.equals("Number")) {
			return new ZuluNumber(result.asFloat());
		} else if (target.equals("String")) {
			return new ZuluString(result.toString());
		} else if (target.equals("Atom")) {
			return new ZuluAtom(result.raw().toString().toLowerCase());
		} else if (target.equals("List")) {
			LinkedList<ZuluValue> list = new LinkedList<>();
			list.add(result);
			return new ZuluList(list);
		} else if (target.equals("Null")) {
			return new ZuluNull();
		} else if (target.equals("Pointer")) {
			return new ZuluPointer(result);
		} else if (target.equals("Reference")) {
			return new ZuluReference(result);
		} else {
			throw new ZuluException("CastError", "Cannot cast to type `" + target + "`");
		}
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
