package com.erlava.runtime;

import com.erlava.memory.Storage;
import com.erlava.utils.BarleyException;
import com.erlava.utils.Pointers;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BarleyPointer implements BarleyValue {

	private static final long serialVersionUID = 1L;
	private BarleyValue stored;
	private String pointer;

	public BarleyPointer(BarleyValue execute) {
		this.stored = execute;
		this.pointer = Integer.toHexString(Modules.getRandomNumber(0, 100000000));
		Pointers.put(this.toString(), stored);
		Storage.segment(this);
	}

	public BarleyPointer() {
		this(new BarleyNumber(0));
	}

	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "can't cast POINTER to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "can't cast POINTER to a NUMBER");
	}

	@Override
	public Object raw() {
		return stored;
	}

	public BarleyValue getStored() {
		return stored;
	}

	public void setStored(BarleyValue stored) {
		this.stored = stored;
	}

	@Override
	public String toString() {
		return pointer;
	}
}
