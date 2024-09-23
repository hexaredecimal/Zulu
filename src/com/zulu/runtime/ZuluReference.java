package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.BarleyException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ZuluReference implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private Object ref;

	public ZuluReference(Object ref) {
		this.ref = ref;
		Storage.segment(this);
	}

	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "Cannot cast REFERENCE to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast REFERENCE to a NUMBER");
	}

	@Override
	public Object raw() {
		return ref;
	}

	public Object getRef() {
		return ref;
	}

	@Override
	public String toString() {
		return "#Reference<" + hashCode() + ">";
	}
}
