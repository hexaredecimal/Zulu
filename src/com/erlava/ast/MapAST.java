package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyReference;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapAST implements AST, Serializable {

		private static final long serialVersionUID = 1L;
    private HashMap<AST, AST> map;

    public MapAST(HashMap<AST, AST> map) {
        this.map = map;
    }

    @Override
    public BarleyValue execute() {
        HashMap<BarleyValue, BarleyValue> result = new HashMap<>();
        for (Map.Entry<AST, AST> entry : map.entrySet()) {
            result.put(entry.getKey().execute(), entry.getValue().execute());
        }

        return new BarleyReference(result);
    }

    @Override
    public void visit(Optimization optimization) {

    }
}
