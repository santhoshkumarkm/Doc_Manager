package com.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Trie implements Serializable {
	public static final long serialVersionUID = 20003L;
	private TrieNode root = new TrieNode();

	static class TrieNode implements Serializable {
		public static final long serialVersionUID = 20004L;
		ArrayList<Character> charArray;
		ArrayList<TrieNode> nodeArray;
		WordUtil wordDetail;

		TrieNode() {
			charArray = new ArrayList<Character>();
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
		int length = key.length();
		char letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			if (!trieNode.charArray.contains(letter)) {
				trieNode.charArray.add(letter);
				trieNode.nodeArray.add(new TrieNode());
			}
			trieNode = trieNode.nodeArray.get(trieNode.charArray.indexOf(letter));
		}
		trieNode.wordDetail = wordDetail;
	}

	public LinkedList<WordDetailPair> searchPrefix(String key) {
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			if (trieNode.charArray.indexOf(letter) == -1)
				return null;
			trieNode = trieNode.nodeArray.get(trieNode.charArray.indexOf(letter));
		}
		if (trieNode == null) {
			return null;
		}
		LinkedList<WordDetailPair> pairs = new LinkedList<WordDetailPair>();
		if (trieNode.wordDetail != null) {
			pairs.add(new WordDetailPair(key, trieNode.wordDetail));
			return pairs;
		}
		for (int i = 0; i < trieNode.charArray.size(); i++) {
			pairs = getSuggestions(new LinkedList<WordDetailPair>(), key, trieNode);
		}
		return pairs;
	}

	private LinkedList<WordDetailPair> getSuggestions(LinkedList<WordDetailPair> pairs, String word,
			TrieNode trieNode) {
		if (trieNode == null) {
			return null;
		}
		if (trieNode.wordDetail != null) {
			pairs.add(new WordDetailPair(word, trieNode.wordDetail));

		}
		for (int i = 0; i < trieNode.charArray.size(); i++) {
			word += trieNode.charArray.get(i);
			pairs = getSuggestions(pairs, word, trieNode.nodeArray.get(i));
		}
		return pairs;
	}

	public LinkedList<WordDetailPair> print(LinkedList<WordDetailPair> pairs, String word, TrieNode trieNode) {
		if (trieNode == null) {
			return null;
		}
		if (trieNode.wordDetail != null) {
			pairs.add(new WordDetailPair(word, trieNode.wordDetail));

		}
		for (int i = 0; i < trieNode.charArray.size(); i++) {
			word += trieNode.charArray.get(i);
			pairs = print(pairs, word, trieNode.nodeArray.get(i));
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
		positions2.add(9);
		positions2.add(14);
		Map<Integer, LinkedList<Integer>> infoMap2 = new LinkedHashMap<Integer, LinkedList<Integer>>();
		infoMap2.put(3, positions2);
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

		trie.insert("word1", wordUtil);
		trie.insert("new2", wordUtil2);
		trie.insert("wordrd1", wordUtil3);
		trie.insert("word111", wordUtil2);
		trie.remove("word1", 1, 2);
		System.out.println(trie.searchPrefix("new"));
	}

	public WordUtil getWordUtil(String key) {
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			if (trieNode.charArray.indexOf(letter) == -1)
				return null;
			trieNode = trieNode.nodeArray.get(trieNode.charArray.indexOf(letter));
		}
		if (trieNode == null || trieNode.wordDetail == null) {
			return null;
		}
		return trieNode.wordDetail;
	}

	public boolean remove(String key, int fileId, int position) {
		int length = key.length();
		Character letter;
		TrieNode trieNode = root;
		for (int level = 0; level < length; level++) {
			letter = key.charAt(level);
			if (trieNode.charArray.indexOf(letter) == -1)
				return false;
			trieNode = trieNode.nodeArray.get(trieNode.charArray.indexOf(letter));
		}
		if (trieNode == null || trieNode.wordDetail == null) {
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