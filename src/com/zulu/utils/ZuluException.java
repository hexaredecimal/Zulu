package com.zulu.utils;

public class ZuluException extends RuntimeException {

	private String type;
	private String text;

	public ZuluException(String type, String text) {
		super();
		this.type = type;
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
