package com.brenttheisen.wordindex.om;

public class Word 
{
	private long id;
	private long urlId;
	private String word;
	private long occurrences;
	
	public long getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(long occurrences) {
		this.occurrences = occurrences;
	}
	public long getUrlId() {
		return urlId;
	}
	public void setUrlId(long urlId) {
		this.urlId = urlId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}

}
