package com.brenttheisen.wordindex.util;

import java.util.HashMap;
import java.util.Map;

import org.htmlparser.beans.StringBean;

public class HtmlPageWordIndexer 
{
	private boolean caseSensitive;
	
	public HtmlPageWordIndexer(boolean caseSensitive)
	{
		this.caseSensitive = caseSensitive;
	}

	public Map<String, Long> indexPage(String url)
	{
		Map<String, Long> index = new HashMap<String, Long>();
		StringBean sb = new StringBean();

		sb.setCollapse(true);
		sb.setLinks(false);
		sb.setReplaceNonBreakingSpaces(true);

		// setURL must be called last otherwise the text will 
		// be "reacquired"
		sb.setURL(url);
		
		String s = sb.getStrings();
		WordStringTokenizer tokenizer = new WordStringTokenizer(s);
		while(tokenizer.hasMoreElements())
		{
			String word = (String) tokenizer.nextElement();
			addWordOccurence(index, word);
		}
		
		return index;
	}
	
	private long addWordOccurence(Map<String, Long> index, String word)
	{
		String indexKey = generateIndexKey(word);
		
		Long wordCount = index.get(indexKey);
		if(wordCount == null)
		{
			wordCount = new Long(1);
		}
		else
		{
			wordCount = new Long(wordCount.longValue() + 1);
		}
		index.put(indexKey, wordCount);
		
		return wordCount.longValue();
	}
	
	private String generateIndexKey(String word)
	{
		if(!caseSensitive)
		{
			word = word.toLowerCase();
		}

		return word;
	}
	
}
