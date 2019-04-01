package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {
	static String defaultLocation = "/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/";
	File file;

	public FileOperations(String location) {
		file = new File(defaultLocation + location);
	}
	
	public void create() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String text) {
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(text);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void makeDirectory() {
		file.mkdir();
	}
	
	public void delete() {
		recursiveDelete(file);
	}

	private static void recursiveDelete(File file) {
		if (!file.exists())
			return;

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				recursiveDelete(f);
			}
		}
		file.delete();
	}

	public boolean exists() {
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public String read() {
		String readText = null;
		try {
			readText = Utilities.stringBuilder(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return readText;
	}
}
