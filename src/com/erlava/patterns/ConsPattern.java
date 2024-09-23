package com.erlava.patterns;

public class ConsPattern extends Pattern {

	private static final long serialVersionUID = 1L;
	private String left, right;

	public ConsPattern(String left, String right) {
		this.left = left;
		this.right = right;
	}

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	@Override
	public String toString() {
		return left + " | " + right;
	}
}
