package com.zulu.runtime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface ZuluValue extends Serializable {

	BigInteger asInteger();

	BigDecimal asFloat();

	Object raw();
}
