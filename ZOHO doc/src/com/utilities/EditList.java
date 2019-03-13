package com.utilities;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditList implements Serializable {
	private static final long serialVersionUID = 1007733L;

	Map<String,LinkedHashMap<Integer, String>[]> fileNames = new LinkedHashMap<String,LinkedHashMap<Integer, String>[]>();

	public void addFileName(String name , LinkedHashMap<Integer, String>[] twoLists) {
		fileNames.put(name, twoLists);
	}

	public Map<String, LinkedHashMap<Integer, String>[]> getFileNames() {
		return fileNames;
	}
}