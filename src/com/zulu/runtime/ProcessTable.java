package com.zulu.runtime;

import com.zulu.utils.AST;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessTable {

	public static HashMap<ZuluPID, ZuluValue> storage = new HashMap<>();
	public static HashMap<ZuluPID, AST> receives = new HashMap<>();
	public static ArrayList<ZuluPID> links;

	public static void put(ZuluPID pid, ZuluValue val) {
		storage.put(pid, val);
	}

	public static void put(ZuluPID pid) {
		put(pid, new ZuluNumber(0));
	}

	public static ZuluValue get(ZuluPID pid) {
		ZuluValue result = storage.get(pid);
		return result;
	}

	public static void dump() {
		System.out.println(storage);
		System.out.println(receives);
	}

}
