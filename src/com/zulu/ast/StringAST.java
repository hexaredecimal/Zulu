package com.zulu.ast;

import com.zulu.optimizations.Optimization;
import com.zulu.runtime.ZuluString;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;
import com.zulu.utils.Handler;
import com.zulu.runtime.ZuluValue;

public class StringAST implements AST {

	private static final long serialVersionUID = 1L;
	//private final String current;
	private StringBuilder result;
	//private final int line;
	private int length;
	private int pos;
	private String str;

	public StringAST(String str, int line, String current, int pos) {
		this.str = str;
		//this.line = line;
		//this.current = current;
		this.pos = pos;
	}

	@Override
	public ZuluValue execute() {
		this.pos = 0;
		this.result = new StringBuilder();
		this.length = str.length();
		try {
			lex();
		} catch (OutOfMemoryError ex) {
			this.result = new StringBuilder(str);
		}
		return new ZuluString(result.toString());
	}

	private void lex() {
		while (pos < length) {
			char c = peek(0);
			if (c == '#') {
				c = next();
				interpolate();
			} else if (c == '\\') {
				c = next();
				result.append(c);
			} else {
				result.append(c);
				next();
			}
		}
	}

	private void interpolate() {
		char c = next();
		StringBuilder buffer = new StringBuilder();
		while (c != '}') {
			buffer.append(c);
			c = next();
		}
		next();
		result.append(Handler.evalAST(buffer + "."));
	}

	private char peek(int relativePos) {
		int p = relativePos + pos;
		if (p >= length) {
			return '\0';
		}
		return str.charAt(p);
	}

	private char next() {
		pos++;
		return peek(0);
	}

	@Override
	public void visit(Optimization optimization) {
		try {
			lex();
		} catch (ZuluException ex) {
			// skip
			;
		}
	}

	@Override
	public String toString() {
		return str;
	}
}
