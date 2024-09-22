package com.erlava.ast;

import com.erlava.Main;
import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import java.io.Serializable;

/**
 *
 * @author hexaredecimal
 */
public class ListIndexAST implements AST, Serializable {

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
	public BarleyValue execute() {
		var idx = index.execute().raw();
		var arr = list.execute();

		if (!(arr instanceof BarleyList)) {
			Main.error("Invalid data", "Attempt to index a value that is not a list.", line, current);
		}

		if (!(idx instanceof Integer)) {
			Main.error("Invalid data", "Attempt to index list with non integer index.", line, current);
		}

		var pos = ((Integer) idx);
		var items = ((BarleyList) arr).getList();
		
		if (pos >= items.size()) {
			Main.error("Out of bounds", String.format("Attempt to index array of size %d with index %d", items.size(), pos), line, current);
		}
		
		return items.get(pos);
	}

	@Override
	public void visit(Optimization optimization) {
	}

}
