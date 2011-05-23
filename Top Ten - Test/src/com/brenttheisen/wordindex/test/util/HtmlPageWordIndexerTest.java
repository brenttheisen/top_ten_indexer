package com.brenttheisen.wordindex.test.util;

import java.util.Map;

import com.brenttheisen.wordindex.util.HtmlPageWordIndexer;

import junit.framework.TestCase;

public class HtmlPageWordIndexerTest extends TestCase
{
	/**
	 * Spot checks case sensitive words on the Yahoo web page.
	 * @throws Throwable
	 */
	public void testSpotCheckWithCaseSensitive() throws Throwable
	{
		HtmlPageWordIndexer indexer = new HtmlPageWordIndexer(true);
		Map<String, Long> index = 
			indexer.indexPage("http://www.w3.org/Protocols/rfc2616/rfc2616.html");
		
		assertEquals(5, index.get("protocol").longValue());
		assertEquals(5, index.get("Protocol").longValue());
		assertEquals(7, index.get("Internet").longValue());
	}
	
	/**
	 * Spot checks case sensitive words on the Yahoo web page.
	 * @throws Throwable
	 */
	public void testSpotCheckWithCaseInsensitive() throws Throwable
	{
		HtmlPageWordIndexer indexer = new HtmlPageWordIndexer(false);
		Map<String, Long> index = 
			indexer.indexPage("http://www.w3.org/Protocols/rfc2616/rfc2616.html");
		assertEquals(10, index.get("protocol").longValue());
		assertEquals(7, index.get("internet").longValue());
	}
}
