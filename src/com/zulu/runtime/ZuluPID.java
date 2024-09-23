package com.zulu.runtime;

import com.zulu.utils.BarleyException;
import com.zulu.utils.PidValues;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class ZuluPID implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private PidValues id;

	public ZuluPID(PidValues id) {
		this.id = id;
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
		return id;
	}

	public PidValues getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ZuluPID barleyPID = (ZuluPID) o;
		return Objects.equals(id, barleyPID.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "#PID<" + id + ">";
	}
}
