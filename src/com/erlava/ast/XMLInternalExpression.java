/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;
import com.erlava.utils.AST;
import java.io.Serializable;

/**
 *
 * @author hexaredecimal
 */
public class XMLInternalExpression implements AST, Serializable {
	
	private AST exp; 

	public XMLInternalExpression(AST expresson) {
		exp = expresson; 		
	}
	
	@Override
	public BarleyValue execute() {
		return exp.execute();
	}

	@Override
	public void visit(Optimization optimization) {
		exp.visit(optimization);
	}
}
