package com.zulu.runtime;

import com.zulu.memory.Allocation;
import com.zulu.memory.Storage;
import com.zulu.memory.StorageUtils;
import com.zulu.utils.Arguments;
import com.zulu.utils.ZuluException;
import com.zulu.utils.Function;
import com.zulu.utils.Pointers;

import java.util.HashMap;

public class Externals {

	private static HashMap<String, Function> exts = new HashMap<>();

	static {
		Externals.put("free", args -> {
			Arguments.check(1, args.length);
			Storage.free(args[0]);
			Pointers.remove(args[0].toString());
			return new ZuluAtom("ok");
		});
		Externals.put("alloc", args -> {
			Arguments.check(1, args.length);
			ZuluPointer ptr = new ZuluPointer();
			Pointers.put(ptr.toString(), new Allocation(args[0].asInteger().intValue()));
			Storage.segment(args[0].asInteger().intValue());
			return ptr;
		});
		Externals.put("altlst", args -> {
			Arguments.check(1, args.length);
			ZuluPointer ptr = (ZuluPointer) args[0];
			Allocation alc = (Allocation) Pointers.get(ptr.toString());
			return alc.toList();
		});
		Externals.put("alinst", args -> {
			Arguments.check(2, args.length);
			ZuluPointer ptr = (ZuluPointer) args[0];
			Allocation alc = (Allocation) Pointers.get(ptr.toString());
			alc.segment(args[1]);
			//System.out.println("alloc: " + alc.getAllocated() + "\ndefalloc: " + alc.getDefaultAlloc());
			if (alc.getAllocated() < 0) {
				throw new ZuluException("SegmentationFault", "segmentation fault");
			}
			alc.getList().add(args[1]);
			Pointers.put(ptr.toString(), alc);
			return alc;
		});
		Externals.put("alcpy", args -> {
			Arguments.check(1, args.length);
			ZuluPointer ptr = new ZuluPointer();
			Allocation old = (Allocation) Pointers.get(args[0].toString());
			Allocation alc = new Allocation(old.getDefaultAlloc());
			alc.setAllocated(old.getAllocated());
			alc.setDefaultAlloc(old.getDefaultAlloc());
			alc.setList(old.getList());
			Pointers.put(ptr.toString(), alc);
			return ptr;
		});

		Externals.put("alclr", args -> {
			Arguments.check(1, args.length);
			ZuluPointer ptr = (ZuluPointer) args[0];
			Allocation alc = (Allocation) Pointers.get(ptr.toString());
			alc.clear();
			return alc;
		});
		Externals.put("alcmp", args -> {
			Arguments.check(2, args.length);
			ZuluPointer ptr = (ZuluPointer) args[0];
			Allocation alc = (Allocation) Pointers.get(ptr.toString());
			ZuluPointer ptr2 = (ZuluPointer) args[1];
			Allocation alc2 = (Allocation) Pointers.get(ptr2.toString());
			if (alc.getDefaultAlloc() != alc2.getDefaultAlloc()) {
				return new ZuluAtom("false");
			}
			if (alc.getAllocated() != alc2.getAllocated()) {
				return new ZuluAtom("false");
			}
			if (!alc.getList().equals(alc2.getList())) {
				return new ZuluAtom("false");
			}
			return new ZuluAtom("true");
		});
		Externals.put("realloc", args -> {
			Arguments.check(1, args.length);
			ZuluPointer ptr = (ZuluPointer) args[0];
			Allocation alc = (Allocation) Pointers.get(ptr.toString());
			ZuluPointer newPtr = new ZuluPointer();
			Pointers.put(newPtr.toString(), alc);
			return newPtr;
		});
		Externals.put("nullptr", args -> {
			Arguments.check(0, args.length);
			ZuluPointer ptr = new ZuluPointer();
			Pointers.put(ptr.toString(), new ZuluNull());
			return ptr;
		});
		Externals.put("sizeof", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(StorageUtils.size(args[0]));
		});
		Externals.put("alszs", args -> new ZuluNumber(Storage.left()));
	}

	public static Function get(Object key) {
		return exts.get(key);
	}

	public static Function put(String key, Function value) {
		return exts.put(key, value);
	}

	public static boolean containsKey(Object key) {
		return exts.containsKey(key);
	}

	public static void clear() {
		exts.clear();
	}
}
