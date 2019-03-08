package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.dao.ClientsInfoDao;

public class HashMapUtil {
	HashMapObject hashMapSavedObject;
	LinkedHashMap<String, WordUtil> hashMap;
	File hashMapFile;
	String filePath, fileContent;

	public HashMapUtil() {
		hashMapFile = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/HashMap.txt");
		if (hashMapFile.exists()) {
			try {
				hashMapSavedObject = (HashMapObject) Utilities.readFile(hashMapFile);
				hashMap = hashMapSavedObject.getHashMap();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			hashMapSavedObject = new HashMapObject();
			hashMap = new LinkedHashMap<String, WordUtil>();
		}
	}

	public void addWords(String filePath, String fileContent) {
		long fileId = ClientsInfoDao.getFileId(filePath);
		String[] words = fileContent.split("\\W+");
		for (int i = 0; i < words.length; i++) {
			addHashMapEntry(i, words[i].replace(".", "").trim(), (int) fileId);
		}
	}

	private void addHashMapEntry(int position, String word, int fileId) {
		System.out.println(word);
		WordUtil wordUtil = new WordUtil();
		if (hashMap.containsKey(word)) {
			wordUtil = hashMap.get(word);
			for (int i = 0; i < wordUtil.getInfoMap().size(); i++) {
				if (wordUtil.getInfoMap().containsKey(fileId)) {
					if (!wordUtil.getInfoMap().get(fileId).contains(position)) {
						wordUtil.getInfoMap().get(fileId).add(position);
					}
				} else {
					LinkedList<Integer> list = new LinkedList<Integer>();
					list.add(position);
					wordUtil.getInfoMap().put(fileId, list);
				}
			}
		} else {
			LinkedList<Integer> list = new LinkedList<Integer>();
			list.add(position);
			wordUtil.getInfoMap().put(fileId, list);
		}
		hashMap.put(word, wordUtil);
		saveHashMap();
	}

	LinkedHashMap<Integer, ArrayList<Integer>> fileAndPosition = new LinkedHashMap<Integer, ArrayList<Integer>>();

	public LinkedHashMap<Integer, ArrayList<Integer>> findWord(String[] words) {
		if (findMultiWordsImpl(false, words, 0, new ArrayList<Integer>(), new ArrayList<Integer>())) {
			return (fileAndPosition);
		}
		return null;
	}

	private boolean findMultiWordsImpl(boolean flag, String[] words, int index, ArrayList<Integer> tempPositions,
			ArrayList<Integer> tempFiles) {
		WordUtil wordUtil = hashMap.get(words[index]);
		if (wordUtil == null) {
			return false;
		}
		if (index == 0) {
			for (Map.Entry<Integer, LinkedList<Integer>> entry : wordUtil.getInfoMap().entrySet()) {
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.addAll(entry.getValue());
				fileAndPosition.put(entry.getKey(), tempList);
			}
		} else {
			LinkedHashMap<Integer, ArrayList<Integer>> fileAndPositionCopy = fileAndPosition;
			for (Map.Entry<Integer, ArrayList<Integer>> entry : fileAndPositionCopy.entrySet()) {
				tempPositions.clear();
				tempFiles.clear();
				fileAndPosition.clear();
				if (wordUtil.getInfoMap().get(entry.getKey()) != null) {
					ArrayList<Integer> tempPosListConfirm = new ArrayList<Integer>();
					ArrayList<Integer> tempPosList = entry.getValue();
					for (int i = 0; i < tempPosList.size(); i++) {
						int val = tempPosList.get(i);
						if (wordUtil.getInfoMap().get(entry.getKey()).contains(val + 1)) {
							tempPosListConfirm.add(val + 1);
						}
					}
					if (tempPosListConfirm.size() > 0) {
						fileAndPosition.put(entry.getKey(), tempPosListConfirm);
					}
				}
			}
		}
		if (fileAndPosition.size() > 0 && index + 1 != words.length) {
			flag = findMultiWordsImpl(flag, words, index + 1, tempPositions, tempFiles);
		} else if (fileAndPosition.size() > 0 && index + 1 == words.length)
			return true;
		return flag;
	}

	private void saveHashMap() {
		hashMapSavedObject.setHashMap(hashMap);
		Utilities.writeFile(hashMapFile, hashMapSavedObject);
	}

	public void editWords(String filePath, String fileContent) {
		long fileId = ClientsInfoDao.getFileId(filePath);
		System.out.println(fileId);
		String[] curWords = fileContent.split("\\W+");
		removeAllForFileId(fileId);
		addWords(filePath, fileContent);
	}

	private void removeAllForFileId(long fileId) {
		
	}

}
