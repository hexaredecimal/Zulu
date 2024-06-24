/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyReference;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.BarleyXML;
import com.erlava.utils.AST;
import java.io.Serializable;
import java.util.ArrayList;
import xmlparser.XmlParser;

/**
 *
 * @author hexaredecimal
 */
public class XMLAST implements AST, Serializable{
	ArrayList<AST> nodes; 

	public XMLAST(ArrayList<AST> nodes) {
		this.nodes = nodes;
	}
	

	private BarleyValue parserXMLString(String xml) {
		XmlParser parser = new XmlParser(); 
		return new BarleyReference(new BarleyXML(parser.fromXml(xml)));
	}
	 
	
	@Override
	public BarleyValue execute() {
		String txt = ""; 
		for (AST node: nodes) {
			if (node instanceof StringAST) {
				BarleyString s = (BarleyString) node.execute();
				for (byte b: s.getString()) {
					txt += (char)b;
				}
			} else {
				txt += node.execute().toString();
			}
		}

		return parserXMLString(txt);
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
