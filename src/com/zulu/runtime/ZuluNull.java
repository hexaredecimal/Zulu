package com.zulu.runtime;

import com.zulu.utils.ZuluException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ZuluNull implements ZuluValue {

	public ZuluNull() {
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "can't cast NULL to NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "can't cast NULL to NUMBER");
	}

	@Override
	public Object raw() {
		return null;
	}

	@Override
	public String toString() {
		return "#Null<" + hashCode() + ">";
	}
}
