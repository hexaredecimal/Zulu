package com.zulu.patterns;

import com.zulu.runtime.ZuluValue;

public class ConstantPattern extends Pattern {

	private static final long serialVersionUID = 1L;
	private ZuluValue constant;

	public ConstantPattern(ZuluValue constant) {
		this.constant = constant;
	}

	public ZuluValue getConstant() {
		return constant;
	}

	@Override
	public String toString() {
		return constant.toString();
	}
}
