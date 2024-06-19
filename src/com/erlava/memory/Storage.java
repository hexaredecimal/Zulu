package com.erlava.memory;

import com.erlava.runtime.BarleyValue;
import com.erlava.utils.BarleyException;

public class Storage {

    private static long left = 2080000000;

    public static void free(BarleyValue obj) {
        left += StorageUtils.size(obj);
    }

    public static void segment(BarleyValue obj) {
        left -= StorageUtils.size(obj);
        if (left <= 0) throw new BarleyException("SegmentationFault", "segmentation fault, last allocation: '#Allocation<" + obj + ":" + StorageUtils.size(obj) + ">'");
    }

    public static void segment(int obj) {
        left -= obj;
    }

    public static void reset() { left = 2080000000; }

    public static long left() {
        return left;
    }

}
