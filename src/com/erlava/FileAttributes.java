/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erlava;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hexaredecimal
 */
public class FileAttributes implements Serializable {
	private static final long serialVersionUID = 1L;
	private File file;
	private long lastFileSize;

	public FileAttributes(String filePath) {
		this.file = new File(filePath);
		this.lastFileSize = file.length();
	}

	public String getPath() { return this.file.getPath(); }
	
	public boolean equals(FileAttributes other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public String toString() {
		return "FileAttr { "
			.concat(this.file.getName())
			.concat(", ")
			.concat("" + this.lastFileSize)
			.concat(" }\n"); 
	}
	
	public FileAttributes loadAttributes() {
		String path = ".pkg/attr.ssh";
		FileAttributes att = null;
		try (FileInputStream fileIn = new FileInputStream(path); ObjectInputStream in = new ObjectInputStream(fileIn)) {
			att = (FileAttributes) in.readObject();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Person class not found");
			c.printStackTrace();
		}
		return att;
	}

public void saveAttributes() {
		try (FileOutputStream out = new FileOutputStream(".pkg/attr.ssh")) {
			ObjectOutputStream wr = new ObjectOutputStream(out);
			wr.writeObject(this);
			wr.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(FileAttributes.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(FileAttributes.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean hasFileChanged() {
		long currentFileSize = file.length();
		if (currentFileSize != lastFileSize) {
			lastFileSize = currentFileSize;
			return true;
		}
		return false;
	}
}
