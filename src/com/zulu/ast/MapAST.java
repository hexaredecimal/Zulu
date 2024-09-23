package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluReference;
import com.zulu.utils.AST;
import java.util.HashMap;
import java.util.Map;
import com.zulu.runtime.ZuluValue;

public class MapAST implements AST {

	private static final long serialVersionUID = 1L;
	private HashMap<AST, AST> map;

	public MapAST(HashMap<AST, AST> map) {
		this.map = map;
	}

	@Override
	public ZuluValue execute() {
		HashMap<ZuluValue, ZuluValue> result = new HashMap<>();
		for (Map.Entry<AST, AST> entry : map.entrySet()) {
			result.put(entry.getKey().execute(), entry.getValue().execute());
		}

		return new ZuluReference(result);
	}

	@Override
	public void visit(Optimization optimization) {

	}
}
