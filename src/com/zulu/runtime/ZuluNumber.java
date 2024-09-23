package com.zulu.runtime;

import com.zulu.memory.Storage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class ZuluNumber implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private BigDecimal number;

	public static ZuluNumber fromBoolean(boolean bool) {
		return new ZuluNumber(bool ? 1 : 0);
	}

	public static ZuluNumber of(Number val) {
		return of(val.doubleValue());
	}

	public static ZuluNumber of(double val) {
		return new ZuluNumber(val);
	}

	public ZuluNumber(BigDecimal number) {
		this.number = number;
		Storage.segment(this);
	}

	public ZuluNumber(double v) {
		this(BigDecimal.valueOf(v));
	}

	@Override
	public BigInteger asInteger() {
		return number.toBigInteger();
	}

	@Override
	public BigDecimal asFloat() {
		return number;
	}

	@Override
	public Object raw() {
		if (this.toString().contains(".")) {
			return number.doubleValue();
		} else {
			return number.intValue();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ZuluNumber that = (ZuluNumber) o;
		return Objects.equals(number, that.number);
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public String toString() {
		return number.remainder(BigDecimal.valueOf(1)).equals(BigDecimal.valueOf(0.0)) ? String.valueOf(number.longValue()) : number.toString();
	}
}
