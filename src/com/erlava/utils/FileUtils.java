/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hexaredecimal
 */
public class FileUtils {

	public static String getExtension(String path) {
		ArrayList<Character> ext = new ArrayList<>();
		for (int i = path.length() - 1; i >= 0 && path.charAt(i) != '.'; i--) {
			ext.add(path.charAt(i));
		}

		String e = "";
		for (int i = ext.size() - 1; i >= 0; i--) {
			e += ext.get(i);
		}

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

	public static HashMap<String, String> getFilesWithExtension(String path, String ext) {
		HashMap<String, String> filePaths = new HashMap<>();
		searchFiles(new File(path), ext, filePaths);
		return filePaths;
	}

	private static void searchFiles(File directory, String extension, HashMap<String, String> filePaths) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						searchFiles(file, extension, filePaths);
					} else if (file.getName().endsWith(extension)) {
						filePaths.put(file.getName(), file.getAbsolutePath());
					}
				}
			}
		}
	}
}
