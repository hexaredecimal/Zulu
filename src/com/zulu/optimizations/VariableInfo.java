package com.zulu.optimizations;

import com.zulu.runtime.ZuluValue;

public final class VariableInfo {

	public ZuluValue value;
	public int modifications;

	public VariableInfo(ZuluValue value, int modifications) {
		this.value = value;
		this.modifications = modifications;
	}

	@Override
	public String toString() {
		return (value == null ? "?" : value) + " (" + modifications + " mods)";
	}
}
