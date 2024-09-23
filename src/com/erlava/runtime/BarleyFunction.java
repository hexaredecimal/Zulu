package com.erlava.runtime;

import com.erlava.memory.Storage;
import com.erlava.utils.BarleyException;
import com.erlava.utils.Function;
import com.erlava.utils.FunctionState;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class BarleyFunction implements BarleyValue, Function, FunctionState {

	private static final long serialVersionUID = 1L;
	private Function function;
	public static BarleyFunction EMPTY = new BarleyFunction((args) -> new BarleyNumber(0));

	public BarleyFunction(Function function) {
		this.function = function;
		Storage.segment(this);
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "Cannot cast FUNCTION to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast FUNCTION to a NUMBER");
	}

	@Override
	public Object raw() {
		return function;
	}

	@Override
	public BarleyValue execute(BarleyValue... args) {
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
		BarleyFunction that = (BarleyFunction) o;
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
