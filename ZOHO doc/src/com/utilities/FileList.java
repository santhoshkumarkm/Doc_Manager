package com.utilities;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileList implements Serializable {
	private static final long serialVersionUID = 100003L;

	Map<String,String> fileNames = new LinkedHashMap<String,String>();

	public void addFileName(String name , String mode) {
		fileNames.put(name, mode);
	}

	public Map<String, String> getFileNames() {
		return fileNames;
	}
}