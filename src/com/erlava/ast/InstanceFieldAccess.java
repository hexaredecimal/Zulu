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
import com.erlava.utils.BarleyException;

/**
 *
 * @author hexaredecimal
 */
public class InstanceFieldAccess implements AST {

	private AST object;
	private String key;

	public InstanceFieldAccess(AST result, String field) {
		this.object = result;
		this.key = field;
	}

	@Override
	public BarleyValue execute() {
		BarleyValue v = object.execute();
		BarleyObject obj = null;

		if (v instanceof BarleyReference) {
			BarleyReference ref = (BarleyReference) v;
			Object r = ref.raw();
			if (!(r instanceof BarleyObject)) {
				throw new BarleyException("Runtime", "Expected an object reference but found " + v);
			}
			obj = (BarleyObject) r;
		} else 
				throw new BarleyException("Runtime", "Expected an object reference but found " + v);

		if (!obj.getFields().containsKey(key)) {
				throw new BarleyException("Runtime", key + " is not a field of type " + obj.getParent());
		}
		
		return obj.getFields().get(key);
	}

	@Override
	public void visit(Optimization optimization) {
		// TODO: 
	}

}
