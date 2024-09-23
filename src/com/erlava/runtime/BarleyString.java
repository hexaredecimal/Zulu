package com.erlava.runtime;

import com.erlava.memory.Storage;
import com.erlava.utils.BarleyException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class BarleyString implements BarleyValue {

	private static final long serialVersionUID = 1L;
	private byte[] string;

	public BarleyString(byte[] string) {
		this.string = string;
		Storage.segment(this);
	}

	public BarleyString(String s) {
		this(s.getBytes());
	}

	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "Cannot cast STRING to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast STRING to a NUMBER");
	}

	@Override
	public Object raw() {
		return toString();
	}

	public byte[] getString() {
		return string;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BarleyString that = (BarleyString) o;
		return Arrays.equals(string, that.string);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(string);
	}

	@Override
	public String toString() {
		return new String(string).intern();
	}
}
