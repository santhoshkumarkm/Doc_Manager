package com.utilities;

public class WordDetailPair {
	private String word;
	private WordUtil detail = new WordUtil();

	public WordDetailPair(String word, WordUtil detail) {
		super();
		this.word = word;
		this.detail = detail;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public WordUtil getDetail() {
		return detail;
	}

	public void setDetail(WordUtil detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "WordDetailPair [word=" + word + ", detail=" + detail + "]\n";
	}
}
