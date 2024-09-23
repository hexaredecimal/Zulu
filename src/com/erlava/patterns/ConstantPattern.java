package com.erlava.patterns;

import com.erlava.runtime.BarleyValue;

public class ConstantPattern extends Pattern {

	private static final long serialVersionUID = 1L;
	private BarleyValue constant;

	public ConstantPattern(BarleyValue constant) {
		this.constant = constant;
	}

	public BarleyValue getConstant() {
		return constant;
	}

	@Override
	public String toString() {
		return constant.toString();
	}
}
