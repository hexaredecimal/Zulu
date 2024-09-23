package com.zulu.memory;

import com.zulu.utils.BarleyException;
import com.zulu.runtime.ZuluValue;

public class Storage {

	private static long left = 2080000000;

	public static void free(ZuluValue obj) {
		left += StorageUtils.size(obj);
	}

	public static void segment(ZuluValue obj) {
		left -= StorageUtils.size(obj);
		if (left <= 0) {
			throw new BarleyException("SegmentationFault", "segmentation fault, last allocation: '#Allocation<" + obj + ":" + StorageUtils.size(obj) + ">'");
		}
	}

	public static void segment(int obj) {
		left -= obj;
	}

	public static void reset() {
		left = 2080000000;
	}

	public static long left() {
		return left;
	}

}
