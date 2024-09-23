package com.zulu.ast;

import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluList;
import com.zulu.utils.AST;
import com.zulu.runtime.ZuluValue;

/**
 *
 * @author hexaredecimal
 */
public class ListIndexAST implements AST {

	private static final long serialVersionUID = 1L;
	private AST list, index;
	private String current;
	private int line;

	public ListIndexAST(AST list, AST index, int line, String current) {
		this.list = list;
		this.index = index;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		var idx = index.execute().raw();
		var arr = list.execute();

		if (!(arr instanceof ZuluList)) {
			Main.error("Invalid data", "Attempt to index a value that is not a list.", line, current);
		}

		if (!(idx instanceof Integer)) {
			Main.error("Invalid data", "Attempt to index list with non integer index.", line, current);
		}

		var pos = ((Integer) idx);
		var items = ((ZuluList) arr).getList();

		if (pos >= items.size()) {
			Main.error("Out of bounds", String.format("Attempt to index array of size %d with index %d", items.size(), pos), line, current);
		}

		return items.get(pos);
	}

	@Override
	public void visit(Optimization optimization) {
	}

}
