/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

	public BarleyXML(Element element) {
		this.element = element;
	}


	public String prettyPrint() {
		try {
			// Create a TransformerFactory
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			// Create a Transformer
			Transformer transformer = transformerFactory.newTransformer();

			// Set output properties to pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			// Create a StringWriter to capture the output
			StringWriter writer = new StringWriter();

			// Transform the document to a string
			transformer.transform(new DOMSource(element), new StreamResult(writer));

			// Print the pretty-printed XML string
			String prettyPrintedXml = writer.toString();
			return prettyPrintedXml; 
		} catch (TransformerConfigurationException ex) {
			Logger.getLogger(BarleyXML.class.getName()).log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			Logger.getLogger(BarleyXML.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null; 
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
