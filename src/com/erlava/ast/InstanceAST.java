/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyObject;
import com.erlava.runtime.BarleyReference;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author hexaredecimal
 */
public class InstanceAST implements AST {

	private static final long serialVersionUID = 1L;
	private HashMap<String, AST> fields;
	private String parent;

	public InstanceAST(String parent, HashMap<String, AST> fields) {
		this.parent = parent;
		this.fields = fields;
	}

	@Override
	public BarleyValue execute() {
		HashMap<String, BarleyValue> obj = new HashMap<>();
		for (Entry<String, AST> entry : fields.entrySet()) {
			String f_name = entry.getKey();
			BarleyValue f_val = entry.getValue().execute();
			obj.put(f_name, f_val);
		}
		return new BarleyReference(new BarleyObject(parent, obj));
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
