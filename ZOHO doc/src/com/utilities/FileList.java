package com.utilities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class FileList implements Serializable {
	private static final long serialVersionUID = 10044003L;

	Set<String> fileNames = new LinkedHashSet<String>();

	public void addFileName(String name) {
		fileNames.add(name);
	}

	public Set<String> getFileNames() {
		return fileNames;
	}

}
