package com.brenttheisen.wordindex.test.util;

import java.util.Enumeration;

import com.brenttheisen.wordindex.util.WordStringTokenizer;

import junit.framework.TestCase;

public class WordStringTokenizerTest extends TestCase 
{
	public void testArbitraryString() throws Throwable
	{
		String[] words = new String[] {
			"this",
			"is",
			"a",
			"test"
		};
		Enumeration wordsEnum = 
			new  WordStringTokenizer(" - this, is a test $@1.00");
		
		int i = 0;
		while(wordsEnum.hasMoreElements() && i < words.length)
		{
			String tokenizerWord = (String) wordsEnum.nextElement();
			assertEquals(tokenizerWord, words[i]);
			i++;
		}
		
		assertEquals(words.length, i);
	}
}
