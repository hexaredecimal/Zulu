package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.BarleyException;
import com.zulu.utils.Pointers;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ZuluPointer implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private ZuluValue stored;
	private String pointer;

	public ZuluPointer(ZuluValue execute) {
		this.stored = execute;
		this.pointer = Integer.toHexString(Modules.getRandomNumber(0, 100000000));
		Pointers.put(this.toString(), stored);
		Storage.segment(this);
	}

	public ZuluPointer() {
		this(new ZuluNumber(0));
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

	public ZuluValue getStored() {
		return stored;
	}

	public void setStored(ZuluValue stored) {
		this.stored = stored;
	}

	@Override
	public String toString() {
		return pointer;
	}
}
