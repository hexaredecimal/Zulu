package com.erlava.ast;

import com.erlava.Main;
import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hexaredecimal
 */
public class MapIndexAST implements AST {

	private static final long serialVersionUID = 1L;
	private AST map, index;
	private String current;
	private int line;

	public MapIndexAST(AST map, AST index, int line, String current) {
		this.map = map;
		this.index = index;
		this.line = line;
		this.current = current;
	}

	@Override
	public BarleyValue execute() {
		var result = map.execute().raw();
		var key = index.execute();

		if (!(result instanceof Map<?, ?>)) {
			Main.error("Invalid data", "Attempt to index a value that is not a map.", line, current);
		}

		Map<?, ?> map = (HashMap<BarleyValue, BarleyValue>) result;
		var value = map.get(key);
		if (value == null) {
			Main.error("Null Value", String.format("Key `%s` is not part of the map", key), line, current);
		}

		return (BarleyValue) value;
	}

	@Override
	public void visit(Optimization optimization) {
	}

}
