/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.ast;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyAtom;
import com.erlava.runtime.BarleyReference;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.BarleyXML;
import com.erlava.utils.AST;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xmlparser.XmlParser;

/**
 *
 * @author hexaredecimal
 */
public class XMLAST implements AST, Serializable {

	private static final long serialVersionUID = 1L;
	ArrayList<AST> nodes;

	public XMLAST(ArrayList<AST> nodes) {
		this.nodes = nodes;
	}

	private BarleyValue parserXMLString(String xml) {
		XmlParser parser = new XmlParser();
		// Create a DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Create a DocumentBuilder
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			// Parse the XML string into a Document
			Document document = builder.parse(new InputSource(new StringReader(xml)));

			// Normalize the document
			document.getDocumentElement().normalize();
			Element e = document.getDocumentElement();
			return new BarleyReference(new BarleyXML(e));
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(XMLAST.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SAXException ex) {
			Logger.getLogger(XMLAST.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(XMLAST.class.getName()).log(Level.SEVERE, null, ex);
		}

		return new BarleyAtom("error");
	}

	@Override
	public BarleyValue execute() {
		String txt = "";
		for (AST node : nodes) {
			if (node instanceof StringAST) {
				BarleyString s = (BarleyString) node.execute();
				for (byte b : s.getString()) {
					txt += (char) b;
				}
			} else if (node instanceof BarleyReference) {
				BarleyReference ref = (BarleyReference) node; 
				Object reff = ref.raw(); 
				if (reff instanceof BarleyXML) {
					BarleyXML x = (BarleyXML) reff;
					txt += x.prettyPrint();
				} else txt += ref.toString();
			} else {
				txt += node.execute().toString();
			}
		}

		return parserXMLString(txt);
	}

	@Override
	public void visit(Optimization optimization) {
	}
}
