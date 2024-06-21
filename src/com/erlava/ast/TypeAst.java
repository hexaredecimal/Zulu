/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.TypeTable;
import com.erlava.utils.AST;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author hexaredecimal
 */
public class TypeAst implements AST, Serializable{
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
	public BarleyValue execute() {
		return new BarleyAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {
		// TODO: 
	}
}
