package com.brenttheisen.wordindex.om;


public class Url 
{
	private long id;
	private String url;
	private int totalWords;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getTotalWords() {
		return totalWords;
	}
	public void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String siteUrl) {
		this.url = siteUrl;
	}

}
