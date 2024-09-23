/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluObject;
import com.zulu.runtime.ZuluReference;
import com.zulu.utils.AST;
import java.util.HashMap;
import java.util.Map.Entry;
import com.zulu.runtime.ZuluValue;

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
	public ZuluValue execute() {
		HashMap<String, ZuluValue> obj = new HashMap<>();
		for (Entry<String, AST> entry : fields.entrySet()) {
			String f_name = entry.getKey();
			ZuluValue f_val = entry.getValue().execute();
			obj.put(f_name, f_val);
		}
		return new ZuluReference(new ZuluObject(parent, obj));
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
