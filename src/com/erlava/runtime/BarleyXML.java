package com.erlava.runtime;

import com.erlava.utils.BarleyException;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlparser.model.XmlElement;

/**
 *
 * @author hexaredecimal
 */
public class BarleyXML implements BarleyValue, Serializable {
	private Element element;
	private String formatted;
		
	public BarleyXML(Element element) {
		this.element = element;
	}

	public void setFormatted(String fmt) { this.formatted = fmt; }

	public String getFormatted() {
		return formatted;
	}
	
	public BarleyValue getNodeByTagName(String tag) {
		NodeList elements = this.element.getElementsByTagName(tag);
		LinkedList<BarleyValue> obj = new LinkedList<>(); 
		for (int i = 0; i < elements.getLength(); i++) {
			Node n = elements.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				obj.push(new BarleyXML((Element) n));
			}
		}
		return new BarleyList(obj);
	}

	public BarleyValue getNodeByTagId(String id) {
		return new BarleyAtom("error");
	}

	@Override
	public String toString() {
		return this.formatted;
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
