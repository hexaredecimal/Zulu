/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author hexaredecimal
 */
public class ImportAst implements AST, Serializable {

	private List<AST> nodes;

	public ImportAst(List<AST> n) {
		this.nodes = n;
	}

	public List<AST> getNodes() {
		return nodes;
	}

	@Override
	public BarleyValue execute() {
		return new BarleyAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
