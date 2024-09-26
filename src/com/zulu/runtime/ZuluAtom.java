package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.ZuluException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class ZuluAtom implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private String atom;

	public ZuluAtom(String atom) {
		this.atom = atom;
		Storage.segment(this);
	}

	public ZuluAtom(int pos) {
		this(AtomTable.get(pos));
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "Cannot cast ATOM to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "Cannot cast ATOM to a NUMBER");
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
		ZuluAtom that = (ZuluAtom) o;
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
