package com.zulu.memory;

import com.zulu.runtime.ZuluList;
import com.zulu.utils.ZuluException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import com.zulu.runtime.ZuluValue;

public class Allocation implements ZuluValue {

	private LinkedList<ZuluValue> list;
	private int allocated, defaultAlloc;

	public Allocation(LinkedList<ZuluValue> list, int allocated) {
		this.list = list;
		Storage.segment(allocated);
		this.allocated = allocated;
		this.defaultAlloc = allocated;
	}

	public Allocation(int allocated, ZuluValue... values) {
		this.list = new LinkedList<>(List.of(values));
		this.allocated = allocated;
		this.defaultAlloc = allocated;
	}

	public Allocation(int size) {
		this.list = new LinkedList<>();
		this.allocated = size;
		this.defaultAlloc = size;
	}

	public ZuluValue toList() {
		return new ZuluList(list);
	}

	public int getAllocated() {
		return allocated;
	}

	public int getDefaultAlloc() {
		return defaultAlloc;
	}

	public void segment(ZuluValue obj) {
		allocated -= StorageUtils.size(obj);
	}

	public void clear() {
		allocated = defaultAlloc;
		list.clear();
	}

	public void setList(LinkedList<ZuluValue> list) {
		this.list = list;
	}

	public void setAllocated(int allocated) {
		this.allocated = allocated;
	}

	public void setDefaultAlloc(int defaultAlloc) {
		this.defaultAlloc = defaultAlloc;
	}

	@Override
	public String toString() {
		return "#Allocation<" + hashCode() + ">";
	}

	@Override
	public BigInteger asInteger() {
		throw new ZuluException("BadArithmetic", "can't cast ALLOCATION to NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new ZuluException("BadArithmetic", "can't cast ALLOCATION to NUMBER");
	}

	public LinkedList<ZuluValue> getList() {
		return list;
	}

	@Override
	public Object raw() {
		return list;
	}
}
