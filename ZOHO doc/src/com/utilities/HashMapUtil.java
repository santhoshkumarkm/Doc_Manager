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
	File trieFile = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/TrieFile.txt");
	Trie trie;

	public void setTrie() {
//		System.out.println("constructor");
		if (trieFile.exists()) {
			try {
				trie = (Trie) Utilities.readFile(trieFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			trie = new Trie();
			File initialFile = new File(
					"/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/all-words.txt");
			try {
				String allWords = Utilities.stringBuilder(new BufferedReader(new FileReader(initialFile)));
				System.out.println("dictionary");
				for (String word : allWords.split("\n")) {
					trie.insert(word, null);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			saveTrie();
		}
	}

	public void addWords(String filePath, String fileContent) {
		long fileId = ClientsInfoDao.getFileId(filePath);
		String[] words = fileContent.split("\\W+");
		for (int i = 0; i < words.length; i++) {
			addHashMapEntry(i, words[i].trim(), (int) fileId);
		}
		saveTrie();
	}

	private void addHashMapEntry(int position, String word, int fileId) {
		WordUtil wordUtil;
		if ((wordUtil = trie.getWordUtil(word)) != null) {
				if (wordUtil.getInfoMap().containsKey(fileId)) {
					wordUtil.getInfoMap().get(fileId).add(position);
				} else {
					LinkedList<Integer> list = new LinkedList<Integer>();
					list.add(position);
					wordUtil.getInfoMap().put(fileId, list);
				}
		} else {
			wordUtil = new WordUtil();
			LinkedList<Integer> list = new LinkedList<Integer>();
			list.add(position);
			wordUtil.getInfoMap().put(fileId, list);
		}
		trie.insert(word, wordUtil);
	}

	LinkedHashMap<Integer, ArrayList<Integer>> fileAndPosition;
//	LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> wordsDeatilMap = new LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>>();

	public LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> findWord(String[] words) {
//		System.out.println("Trie: " + trie);
		LinkedList<String> foundWords = trie.searchPrefix(words[words.length - 1]);
		LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> map = new LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>>();
		String sentence = "";
		for (int i = 0; i < words.length - 1; i++) {
			sentence += words[i] + " ";
		}
//		System.out.println("found :" + foundWords);
		if (foundWords == null) {
			return null;
		}
		for (String word : foundWords) {
//			System.out.println("word: " + word);
			words[words.length - 1] = word;
			fileAndPosition = new LinkedHashMap<Integer, ArrayList<Integer>>();
			if (findMultiWordsImpl(false, words, 0, new ArrayList<Integer>(), new ArrayList<Integer>())) {
				sentence += word;
				map.put(sentence, fileAndPosition);
				sentence = sentence.substring(0, sentence.length() - word.length());
			}
		}
		return map;
	}

	public LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> editDistance(String[] words) {
//		System.out.println("Trie: " + trie);
		LinkedList<String> foundWords = trie.editDistance(words[words.length - 1]);
//		System.out.println("found :" + foundWords);
		LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> map = new LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>>();
		String sentence = "";
		if (foundWords == null) {
			return null;
		}
		for (int i = 0; i < words.length - 1; i++) {
			sentence += words[i] + " ";
		}
		for (String word : foundWords) {
//			System.out.println("word: " + word);
			words[words.length - 1] = word;
			fileAndPosition = new LinkedHashMap<Integer, ArrayList<Integer>>();
			if (findMultiWordsImpl(false, words, 0, new ArrayList<Integer>(), new ArrayList<Integer>())) {
				sentence += word;
				map.put(sentence, fileAndPosition);
				sentence = sentence.substring(0, sentence.length() - word.length());
			}
		}
		return map;
	}

	private boolean findMultiWordsImpl(boolean flag, String[] words, int index, ArrayList<Integer> tempPositions,
			ArrayList<Integer> tempFiles) {
		WordUtil wordUtil = trie.getWordUtil(words[index]);
//		System.out.println("word: " + words[index] + " word util: " + wordUtil);
		if (wordUtil == null) {
			return false;
		}
		LinkedHashMap<Integer, ArrayList<Integer>> fileAndPositionCopy = null;
		if (index == 0) {
			fileAndPositionCopy = new LinkedHashMap<Integer, ArrayList<Integer>>();
			for (Map.Entry<Integer, LinkedList<Integer>> entry : wordUtil.getInfoMap().entrySet()) {
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.addAll(entry.getValue());
				fileAndPositionCopy.put(entry.getKey(), tempList);
			}
		} else {
			fileAndPositionCopy = new LinkedHashMap<Integer, ArrayList<Integer>>();
//			fileAndPositionCopy = (LinkedHashMap<Integer, ArrayList<Integer>>) fileAndPosition.clone();
//			fileAndPosition.clear();
			for (Map.Entry<Integer, ArrayList<Integer>> entry : fileAndPosition.entrySet()) {
				tempPositions.clear();
				tempFiles.clear();

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
						fileAndPositionCopy.put(entry.getKey(), tempPosListConfirm);
					}
				}
			}
		}
		if (fileAndPositionCopy.size() > 0 && index + 1 != words.length) {
				fileAndPosition.clear();
				fileAndPosition = fileAndPositionCopy;
			flag = findMultiWordsImpl(flag, words, index + 1, tempPositions, tempFiles);
		} else if (fileAndPositionCopy.size() > 0 && index + 1 == words.length) {
			fileAndPosition.clear();
			fileAndPosition = fileAndPositionCopy;
			return true;
		}
		return flag;
	}

	public void removeWords(long fileId, LinkedHashMap<Integer, String> words) {
//		System.out.println("Delete words: " + fileId + words);
		for (Map.Entry<Integer, String> entry : words.entrySet()) {
			trie.remove(entry.getValue(), (int) fileId, entry.getKey());
		}
		saveTrie();
	}

	public void editWords(long fileId, LinkedHashMap<Integer, String> words) {
//		System.out.println("Edit words: " + fileId + words);
		for (Map.Entry<Integer, String> entry : words.entrySet()) {
			addHashMapEntry(entry.getKey(), entry.getValue(), (int) fileId);
		}
		saveTrie();
	}

	void saveTrie() {
		Utilities.writeFile(trieFile, trie);
	}

}
