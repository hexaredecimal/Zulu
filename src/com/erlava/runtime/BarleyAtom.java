package com.erlava.runtime;

import com.erlava.memory.Storage;
import com.erlava.utils.BarleyException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class BarleyAtom implements BarleyValue {

	private static final long serialVersionUID = 1L;
	private String atom;

	public BarleyAtom(String atom) {
		this.atom = atom;
		Storage.segment(this);
	}

	public BarleyAtom(int pos) {
		this(AtomTable.get(pos));
	}

	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "Cannot cast ATOM to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast ATOM to a NUMBER");
	}

	@Override
	public Object raw() {
		return atom;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BarleyAtom that = (BarleyAtom) o;
		return Objects.equals(atom, that.atom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(atom);
	}

	@Override
	public String toString() {
		return atom;
	}
}
