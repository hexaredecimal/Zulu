/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.TypeTable;
import com.zulu.utils.AST;
import java.util.ArrayList;
import com.zulu.runtime.ZuluValue;

/**
 *
 * @author hexaredecimal
 */
public class TypeAst implements AST {

	private static final long serialVersionUID = 1L;
	private String type_name;
	private ArrayList<String> fields;

	public TypeAst(String type_name, ArrayList<String> fields) {
		this.type_name = type_name;
		this.fields = fields;
		TypeTable.types.put(type_name, fields);
	}

	public String getType_name() {
		return type_name;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return this.type_name + " -> " + this.fields;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
