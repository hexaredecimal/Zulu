package com.zulu.runtime;

import com.zulu.memory.Storage;
import com.zulu.utils.BarleyException;

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
		throw new BarleyException("BadArithmetic", "Cannot cast LIST to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast LIST to a NUMBER");
	}

	@Override
	public Object raw() {
		return list.toArray();
	}

	@Override
	public String toString() {
		if (list.size() >= 10) {
			List<ZuluValue> rest = list.subList(1, 10);
			StringBuilder buffer = new StringBuilder();
			buffer.append("[");
			int i = 1;
			for (ZuluValue val : rest) {
				buffer.append(i == 10 ? val.toString() : val + ", ");
				i++;
			}
			buffer.append("...");
			buffer.append("]");
			return buffer.toString();
		} else {
			return list.toString();
		}
	}
}
