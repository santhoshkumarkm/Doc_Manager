package com.utilities;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class HashMapObject implements Serializable {
	public static final long serialVersionUID = 10001L;
	LinkedHashMap<String, WordUtil> hashMap;

	LinkedHashMap<String, WordUtil> getHashMap() {
		return hashMap;
	}

	public void setHashMap(LinkedHashMap<String, WordUtil> hashMap) {
		this.hashMap = hashMap;
	}

}
