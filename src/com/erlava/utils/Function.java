package com.erlava.utils;

import com.erlava.runtime.BarleyValue;

import java.io.Serializable;

public interface Function extends Serializable {

	BarleyValue execute(BarleyValue... args);
}
