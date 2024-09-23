package com.zulu.memory;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluClosure;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.ZuluFunction;
import com.zulu.runtime.ZuluPointer;
import com.zulu.runtime.ZuluReference;
import com.zulu.reflection.Reflection;
import com.zulu.runtime.ZuluValue;

public class StorageUtils {

	public static short size(ZuluValue value) {
		if (value instanceof ZuluNumber) {
			return 12;
		} else if (value instanceof ZuluString) {
			return 24;
		} else if (value instanceof ZuluPointer) {
			return 8;
		} else if (value instanceof ZuluClosure) {
			return 512;
		} else if (value instanceof Allocation p) {
			return (short) p.getAllocated();
		} else if (value instanceof ZuluList) {
			return 24;
		} else if (value instanceof ZuluFunction) {
			return 48;
		} else if (value instanceof ZuluAtom) {
			return 8;
		} else if (value instanceof ZuluReference) {
			return 128;
		} else if (value instanceof Reflection.ObjectValue) {
			return 328;
		}

		return 24;
	}
}
