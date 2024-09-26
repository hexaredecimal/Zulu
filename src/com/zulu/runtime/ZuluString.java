package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.ZuluException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class ZuluString implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private byte[] string;

	public ZuluString(byte[] string) {
		this.string = string;
		Storage.segment(this);
	}

	public ZuluString(String s) {
		this(s.getBytes());
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "Cannot cast STRING to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "Cannot cast STRING to a NUMBER");
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
		ZuluString that = (ZuluString) o;
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
