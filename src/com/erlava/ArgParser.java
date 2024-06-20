/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author hexaredecimal
 */

public class ArgParser {
	public ArrayList<String> arr;
		
	public ArgParser(String[] args) {
		arr = new ArrayList<>(); 
		for (String arg: args) {
			arr.add(arg);
		}
	}

	public String get() {
		if (!arr.isEmpty())
			return arr.remove(0);
		return null;
	} 
}
