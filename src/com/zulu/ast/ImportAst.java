/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluAtom;
import com.zulu.utils.AST;
import java.util.List;
import com.zulu.runtime.ZuluValue;

/**
 *
 * @author hexaredecimal
 */
public class ImportAst implements AST {

	private List<AST> nodes;

	public ImportAst(List<AST> n) {
		this.nodes = n;
	}

	public List<AST> getNodes() {
		return nodes;
	}

	@Override
	public ZuluValue execute() {
		return new ZuluAtom("ok");
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
