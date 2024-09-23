package com.zulu.patterns;

public class VariablePattern extends Pattern {

	private static final long serialVersionUID = 1L;
	private String variable;

	public VariablePattern(String variable) {
		this.variable = variable;
	}

	public String getVariable() {
		return variable;
	}

	@Override
	public String toString() {
		return variable;
	}
}
