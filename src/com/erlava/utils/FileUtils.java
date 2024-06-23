/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.utils;

import java.util.ArrayList;

/**
 *
 * @author hexaredecimal
 */
public class FileUtils {
	
	public static String getExtension(String path) {
		ArrayList<Character> ext = new ArrayList<>();
			for (int i = path.length() -1; i >= 0 && path.charAt(i) != '.'; i--) 
				ext.add(path.charAt(i));

		String e = ""; 
		for (int i = ext.size() - 1; i >= 0; i--)
			e += ext.get(i);
		
		return e;
	}

	public static String expectExtention(String path, String ext) {
			String extension = FileUtils.getExtension(path); 
			if (!extension.equals(ext)) {
				System.out.println("Invalid file provided, expecting an `" + ext + "` source file but found a `" + extension + "` file ");
				System.exit(1);
			}
			return extension;
	}
}
