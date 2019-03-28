package com.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Trie implements Serializable {
	public static final long serialVersionUID = 20003L;
	private TrieNode root = new TrieNode(null);

	static class TrieNode implements Serializable {
		Character c;
		ArrayList<TrieNode> nodeArray;
		WordUtil wordDetail;

		TrieNode(Character character) {
			c = character;
			nodeArray = new ArrayList<TrieNode>();
			wordDetail = null;
		}

		public WordUtil getWordDetail() {
			return wordDetail;
		}

		public void setWordDetail(WordUtil wordDetail) {
			this.wordDetail = wordDetail;
		}

	};

	public void insert(String key, WordUtil wordDetail) {
		key = key.toLowerCase();
		int length = key.length();
		char letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			boolean contains = false;
			for (TrieNode node : trieNode.nodeArray) {
				if (node.c.equals(letter)) {
					contains = true;
					trieNode = node;
					break;
				}
			}
			if (!contains) {
				TrieNode tempNode = new TrieNode(letter);
				trieNode.nodeArray.add(tempNode);
				trieNode = tempNode;
			}
		}
		trieNode.wordDetail = wordDetail;
	}

	public LinkedList<String> editDistance(String key) {
		key = key.toLowerCase();
		Character first = key.charAt(0);
		TrieNode trieNode = root;
		boolean contains = false;
		for (TrieNode node : trieNode.nodeArray) {
			if (node.c.equals(first)) {
				trieNode = node;
				contains = true;
				break;
			}
		}
		if (!contains)
			return null;
		LinkedList<String> pairs = new LinkedList<String>();
		if (trieNode.wordDetail != null) {
			pairs.add(key);
		}
//		for (int i = 0; i < trieNode.nodeArray.size(); i++) {
			
//			System.out.println(
//					getSuggestionsMax(new LinkedList<String>(), String.valueOf(first), trieNode, key.length() + 2));
			pairs.addAll(
					getSuggestionsMax(new LinkedList<String>(), String.valueOf(first), trieNode, key.length() + 2));
//		}
		LinkedList<String> foundWords = new LinkedList<String>();
		for (String predictedWord : pairs) {
			if (Utilities.editDistance(key, predictedWord, key.length(), predictedWord.length()) <= 3) {
				foundWords.add(predictedWord);
			}
		}
		return foundWords;
	}

	public LinkedList<String> searchPrefix(String key) {
		key = key.toLowerCase();
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			boolean contains = false;
			for (TrieNode node : trieNode.nodeArray) {
				if (node.c.equals(letter)) {
					contains = true;
					trieNode = node;
					break;
				}
			}
			if (!contains)
				return null;
		}
		LinkedList<String> pairs = new LinkedList<String>();
		if (trieNode.wordDetail != null) {
			pairs.add(key);
		}
//		for (int i = 0; i < trieNode.nodeArray.size(); i++) {
			pairs.addAll(getSuggestions(new LinkedList<String>(), key, trieNode));
//		}
		return pairs;
	}

	private LinkedList<String> getSuggestions(LinkedList<String> pairs, String word, TrieNode trieNode) {
		if (trieNode == null) {
			return null;
		}
		if (trieNode.wordDetail != null) {
			pairs.add(word);

		}
		for (int i = 0; i < trieNode.nodeArray.size(); i++) {
			word += trieNode.nodeArray.get(i).c;
			pairs = getSuggestions(pairs, word, trieNode.nodeArray.get(i));
			word = word.substring(0, word.length() - 1);
		}
		return pairs;
	}

	private LinkedList<String> getSuggestionsMax(LinkedList<String> pairs, String word, TrieNode trieNode, int max) {
		if (max == 0) {
			return pairs;
		}
		if (trieNode == null) {
			return null;
		}
		if (trieNode.wordDetail != null) {
			pairs.add(word);

		}
		for (int i = 0; i < trieNode.nodeArray.size(); i++) {
			word += trieNode.nodeArray.get(i).c;
			pairs = getSuggestionsMax(pairs, word, trieNode.nodeArray.get(i), max - 1);
			word = word.substring(0, word.length() - 1);
		}
		return pairs;
	}

	public static void main(String[] args) {
		Trie trie = new Trie();
		LinkedList<Integer> positions = new LinkedList<Integer>();
		positions.add(1);
		positions.add(2);
		positions.add(3);
		positions.add(4);
		Map<Integer, LinkedList<Integer>> infoMap = new LinkedHashMap<Integer, LinkedList<Integer>>();
		infoMap.put(1, positions);
		WordUtil wordUtil = new WordUtil();
		wordUtil.setInfoMap(infoMap);

		LinkedList<Integer> positions2 = new LinkedList<Integer>();
		positions2.add(1);
		positions2.add(2);
		Map<Integer, LinkedList<Integer>> infoMap2 = new LinkedHashMap<Integer, LinkedList<Integer>>();
		infoMap2.put(1, positions2);
		WordUtil wordUtil2 = new WordUtil();
		wordUtil2.setInfoMap(infoMap2);

		LinkedList<Integer> positions3 = new LinkedList<Integer>();
		positions3.add(1);
		positions3.add(2);
		positions3.add(3);
		positions3.add(4);
		Map<Integer, LinkedList<Integer>> infoMap3 = new LinkedHashMap<Integer, LinkedList<Integer>>();
		infoMap3.put(1, positions3);
		WordUtil wordUtil3 = new WordUtil();
		wordUtil3.setInfoMap(infoMap3);

		trie.insert("newfile1", wordUtil2);
//		trie.remove("newer", 1, 1);
//		trie.remove("newest", 1, 2);
		System.out.println(trie.editDistance("newfile2"));
	}

	public WordUtil getWordUtil(String key) {
		key = key.toLowerCase();
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			boolean contains = false;
			for (TrieNode node : trieNode.nodeArray) {
				if (node.c.equals(letter)) {
					contains = true;
					trieNode = node;
					break;
				}
			}
			if (!contains) {
				return null;
			}
		}
		if (trieNode.wordDetail == null) {
			return null;
		}
		return trieNode.wordDetail;
	}

	public boolean remove(String key, int fileId, int position) {
//		System.out.println("remove: " + key + fileId + position);
		key = key.toLowerCase();
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			boolean contains = false;
			for (TrieNode node : trieNode.nodeArray) {
				if (node.c.equals(letter)) {
					contains = true;
					trieNode = node;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}
		if (trieNode.wordDetail == null) {
			return false;
		}
		if (trieNode.wordDetail.getInfoMap().containsKey(fileId)) {
			trieNode.wordDetail.getInfoMap().get(fileId).remove(new Integer(position));
			if (trieNode.wordDetail.getInfoMap().get(fileId).size() == 0) {
				trieNode.wordDetail.getInfoMap().remove(new Integer(fileId));
			}
			if (trieNode.wordDetail.getInfoMap().size() == 0) {
				trieNode.setWordDetail(null);
			}
			return true;
		}
		return false;
	}

}