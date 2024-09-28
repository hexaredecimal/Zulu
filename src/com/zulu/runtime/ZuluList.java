package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.ZuluException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class ZuluList implements ZuluValue {

	private static final long serialVersionUID = 1L;
	private LinkedList<ZuluValue> list;

	public ZuluList(LinkedList<ZuluValue> list) {
		this.list = list;
		Storage.segment(this);
	}

	public ZuluList(ZuluValue... values) {
		List<ZuluValue> vals = List.of(values);
		LinkedList<ZuluValue> res = new LinkedList<>();
		for (ZuluValue val : vals) {
			res.add(val);
		}
		this.list = res;
	}

	public ZuluList(int size) {
		LinkedList<ZuluValue> result = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			result.add(null);
		}
		this.list = result;
	}

	public void set(int index, ZuluValue value) {
		list.set(index, value);
	}

	public LinkedList<ZuluValue> getList() {
		return list;
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "Cannot cast LIST to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "Cannot cast LIST to a NUMBER");
	}

	@Override
	public Object raw() {
		return list;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int size = list.size(); 
		for (int i = 0; i < size; i++) {
			var val = list.get(i);
			sb.append(i < size - 1 ? val + ", " : val.toString());
		}
		sb.append("]");
		return sb.toString();
	}
}
