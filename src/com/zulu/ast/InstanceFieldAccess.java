/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluObject;
import com.zulu.runtime.ZuluReference;
import com.zulu.utils.AST;
import com.zulu.utils.BarleyException;
import com.zulu.runtime.ZuluValue;

/**
 *
 * @author hexaredecimal
 */
public class InstanceFieldAccess implements AST {

	private static final long serialVersionUID = 1L;
	private AST object;
	private String key;

	public InstanceFieldAccess(AST result, String field) {
		this.object = result;
		this.key = field;
	}

	@Override
	public ZuluValue execute() {
		ZuluValue v = object.execute();
		ZuluObject obj = null;

		if (v instanceof ZuluReference) {
			ZuluReference ref = (ZuluReference) v;
			Object r = ref.raw();
			if (!(r instanceof ZuluObject)) {
				throw new BarleyException("Runtime", "Expected an object reference but found " + v);
			}
			obj = (ZuluObject) r;
		} else {
			throw new BarleyException("Runtime", "Expected an object reference but found " + v);
		}

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
