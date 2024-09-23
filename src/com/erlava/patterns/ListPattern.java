package com.erlava.patterns;

import com.erlava.utils.AST;

import java.util.LinkedList;

public class ListPattern extends Pattern {

	private static final long serialVersionUID = 1L;
	private LinkedList<AST> arr;

	public ListPattern(LinkedList<AST> arr) {
		this.arr = arr;
	}

	public LinkedList<AST> getArr() {
		return arr;
	}

	@Override
	public String toString() {
		return arr.toString();
	}
}
