package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.ZuluException;
import com.zulu.utils.Function;
import com.zulu.utils.FunctionState;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class ZuluFunction implements ZuluValue, Function, FunctionState {

	private static final long serialVersionUID = 1L;
	private Function function;
	public static ZuluFunction EMPTY = new ZuluFunction((args) -> new ZuluNumber(0));

	public ZuluFunction(Function function) {
		this.function = function;
		Storage.segment(this);
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "Cannot cast FUNCTION to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "Cannot cast FUNCTION to a NUMBER");
	}

	@Override
	public Object raw() {
		return function;
	}

	@Override
	public ZuluValue execute(ZuluValue... args) {
		return function.execute(args);
	}

	@Override
	public boolean isLambda() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ZuluFunction that = (ZuluFunction) o;
		return Objects.equals(function, that.function);
	}

	@Override
	public int hashCode() {
		return Objects.hash(function);
	}

	@Override
	public String toString() {
		return function.toString();
	}
}
