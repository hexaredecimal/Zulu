/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.runtime;

import com.erlava.utils.BarleyException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import xmlparser.model.XmlElement;

/**
 *
 * @author hexaredecimal
 */
public class BarleyXML implements BarleyValue, Serializable {

	private XmlElement element;

	public BarleyXML(XmlElement element) {
		this.element = element;
	}

	public BarleyValue getNodeByTagName(String tag) {
		List<XmlElement> elements = this.element.getElementsByTagName(tag);
		LinkedList<BarleyValue> obj = new LinkedList<>(); 
		for (XmlElement e: elements) {
			BarleyXML xml = new BarleyXML(e);
			obj.add(xml);
		}
		return new BarleyList(obj);
	}

	public BarleyValue getNodeByTagId(String id) {
		List<XmlElement> elements = this.element.children;
		LinkedList<BarleyValue> obj = new LinkedList<>(); 
		for (XmlElement e: elements) {
			if (e.attributes.containsKey("id")) {
				String val = e.attributes.get("id");
				if (id.equals(val)) {
					BarleyXML xml = new BarleyXML(e);
					obj.add(xml);
				}
			}
		}
		return new BarleyList(obj);
	}
	
	@Override
	public BigInteger asInteger() {
		throw new BarleyException("BadArithmetic", "Cannot cast XML to a NUMBER");
	}

	@Override
	public BigDecimal asFloat() {
		throw new BarleyException("BadArithmetic", "Cannot cast XML to a NUMBER");
	}

	@Override
	public Object raw() {
		return this.element;
	}
}
