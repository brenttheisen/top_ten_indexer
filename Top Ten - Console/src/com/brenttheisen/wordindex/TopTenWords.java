package com.brenttheisen.wordindex;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brenttheisen.wordindex.dao.DaoException;
import com.brenttheisen.wordindex.dao.UrlDao;
import com.brenttheisen.wordindex.dao.WordDao;
import com.brenttheisen.wordindex.dao.impl.UrlDaoImpl;
import com.brenttheisen.wordindex.dao.impl.WordDaoImpl;
import com.brenttheisen.wordindex.om.Url;
import com.brenttheisen.wordindex.om.Word;
import com.brenttheisen.wordindex.util.HtmlPageWordIndexer;

public class TopTenWords 
{
	private static final String JDBC_URL = "jdbc:mysql://localhost/top_ten?user=top_ten&password=whatever";
	private static final String JDBC_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

	private static final long JOIN_DURATION_MILLIS = 200;
	
	private String url;
	private boolean caseSensitive;
	private int wordPersisterThreadCount;
	private List<WordPersisterThread> wordPersisterThreads;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		long startMillis = System.currentTimeMillis();
		
		
		
		new TopTenWords().run(args);
		
		long stopMillis = System.currentTimeMillis();
		printTimingSummary(startMillis, stopMillis);
	}
	
	/**
	 * Non-static run method just so everything doesn't have to be.
	 * Force of habit.
	 * @param args
	 */
	private void run(String[] args) 
	{
		if(!processArgs(args))
		{
			printUsage();
			return;
		}
		
		HtmlPageWordIndexer indexer = new HtmlPageWordIndexer(caseSensitive);
		Map<String, Long> index = indexer.indexPage(url);

		Url siteUrl = new Url();
		siteUrl.setUrl(url);
		siteUrl.setTotalWords(index.size());

		Connection conn = getConnection();
		if(conn == null)
		{
			return;
		}

		try
		{
			UrlDao urlDao = new UrlDaoImpl(conn);
			if(!persistUrl(siteUrl, urlDao))
			{
				return;
			}

			runWordPersisterThreads(index, siteUrl);
			
			List<Word> topTenWords = findTopTenWords(siteUrl, urlDao);
			if(topTenWords == null)
			{
				return;
			}
			printTopTenWordSummary(siteUrl, topTenWords);
		}
		finally
		{
			try 
			{
				conn.close();
			}
			catch(SQLException e) 
			{
				// Log to debug
			}
		}

	}
	
	private void printTopTenWordSummary(Url siteUrl, List<Word> topTenWords) 
	{
		System.out.println("Top Words Statistics");
		System.out.println("                   URL:  " + siteUrl.getUrl());
		System.out.println("    Total Unique Words:  " + siteUrl.getTotalWords());
		System.out.print("        Case Sensitive:  ");
		if(caseSensitive)
		{
			System.out.println("yes");
		}
		else
		{
			System.out.println("no");
		}
		System.out.print("           Top Ten Words:  ");
		Iterator<Word> topTenWordsIt = topTenWords.iterator();
		boolean pad = false;
		while(topTenWordsIt.hasNext())
		{
			if(pad)
			{
				System.out.print("                           ");
			}
			Word word = topTenWordsIt.next();
			System.out.print(word.getWord());
			System.out.print(" (");
			System.out.print(word.getOccurrences());
			System.out.print(" occurrences, ");
			System.out.print((float) word.getOccurrences() / (float) siteUrl.getTotalWords() * (float) 100);
			System.out.println("% of total)");
			pad = true;
		}
	}

	private Connection getConnection()
	{
		try 
		{
			Class.forName(JDBC_DRIVER_CLASS_NAME);
		}
		catch (ClassNotFoundException e) 
		{
			System.err.println("Exception loading JDBC driver class " + JDBC_DRIVER_CLASS_NAME);
			e.printStackTrace();
			return null;
		}
		Connection conn = null;
		try 
		{
			conn = DriverManager.getConnection(JDBC_URL);
		}
		catch(SQLException e) 
		{
			System.err.println("SQLException getting Connection");
			e.printStackTrace();
			return null;
		}
		
		return conn;
	}
	
	private List<Word> findTopTenWords(Url url, UrlDao urlDao)
	{
		try 
		{
			return urlDao.getTopTenWords(url);
		}
		catch(DaoException e) 
		{
			System.err.println("Exception getting top ten words");
			e.printStackTrace();
			return null;
		}
	}
	
	private void runWordPersisterThreads(Map<String, Long> index, Url siteUrl) 
	{
		// Start some threads to persist word records
		wordPersisterThreads = new ArrayList<WordPersisterThread>(index.size());
		WordDelegator delegator = new WordDelegator(index, siteUrl);
		for(int i = 0; i < wordPersisterThreadCount; i++)
		{
			WordPersisterThread thread = new WordPersisterThread(JDBC_URL, delegator, i + 1);
			wordPersisterThreads.add(thread);
			thread.start();
		}
		
		// Now that they are running, wait until they are all finished
		boolean stillRunning = true;
		while(stillRunning)
		{
			stillRunning = false;
			Iterator<WordPersisterThread> threadIt = wordPersisterThreads.iterator();
			WordPersisterThread thread = threadIt.next();
			if(thread.isAlive())
			{
				stillRunning = true;
				try
				{
					thread.join(JOIN_DURATION_MILLIS);
				}
				catch(InterruptedException e) 
				{
					// Can be ignored
				}
			}
		}
	}

	private boolean persistUrl(Url siteUrl, UrlDao urlDao) 
	{		
		try
		{
			urlDao.save(siteUrl);
		}
		catch(DaoException e) 
		{
			System.err.println("Exception saving Url");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Validates and parses command line arguments.
	 * @param args
	 * @return true if command line arguments, false if otherwise
	 */
	private boolean processArgs(String[] args)
	{
		if(args.length != 3)
		{
			return false;
		}
		
		url = args[0];
		if(args[1].equalsIgnoreCase("Y"))
		{
			caseSensitive = true;
		}
		else if(args[1].equalsIgnoreCase("N"))
		{
			caseSensitive = false;
		}
		else
		{
			return false;
		}
		
		try
		{
			wordPersisterThreadCount = Integer.parseInt(args[2]);
		}
		catch(NumberFormatException e) 
		{
			return false;
		}
		
		return true;
	}
	
	private static void printTimingSummary(long startMillis, long stopMillis)
	{
		System.out.print("Processing time: ");
		System.out.print(stopMillis - startMillis);
		System.out.print("ms\n");
	}
	
	private void printUsage()
	{
		PrintStream out = System.out;
		out.println("java <JVM ARGS> <url> <username> <description> <caseSensitive>");
		out.println("  JVM ARGS       Java virtual machine specific arguments");
		out.println("  url            URL of the site to index");
		out.println("  username       A name (any name) for you");
		out.println("  description    A description (any description)");
		out.println("  caseSensitive  Y to turn on case sensitivy, N otherwise");
	}
	
	private class WordPersisterThread extends Thread
	{
		private String jdbcUrl;
		private WordDelegator delegator;
		
		public WordPersisterThread(String jdbcUrl, WordDelegator delegator, int threadId)
		{
			super("Word Persister Thread #" + String.valueOf(threadId));
			this.jdbcUrl = jdbcUrl;
			this.delegator = delegator;
		}

		@Override
		public void run() 
		{
			Connection conn = null;
			try 
			{
				conn = DriverManager.getConnection(jdbcUrl);
			}
			catch (SQLException e) 
			{
				System.err.println("SQLException getting Connection in thread: " + getName());
				e.printStackTrace();
				return;
			}

			try
			{
				WordDao wordDao = new WordDaoImpl(conn);
				Word word = null;
				while((word = delegator.getNextWord()) != null)
				{
					try 
					{
						wordDao.save(word);
					}
					catch(DaoException e) 
					{
						System.err.println("DaoException saving word in thread " + getName());
						e.printStackTrace();
					}
				}
			}
			finally
			{
				try 
				{
					conn.close();
				}
				catch(SQLException e) 
				{
					// Log this in debug
				}
			}
		}
	}
	
	/**
	 * Intended to guaranty thread safety when getting
	 * Words to save.
	 * @author Brent Theisen
	 */
	private class WordDelegator
	{
		private Map<String, Long> index;
		private Iterator<String> indexIt;
		private Url url;

		public WordDelegator(Map<String, Long> index, Url url)
		{
			this.index = index;
			this.indexIt = index.keySet().iterator();
			this.url = url;
		}
		
		public Word getNextWord()
		{
			String word = null;
			Long occurrences = null;
			synchronized(this) 
			{
				if(indexIt.hasNext())
				{
					word = indexIt.next();
					occurrences = index.get(word);
				}
				else
				{
					return null;
				}
			}
			
			Word wordObj = new Word();
			wordObj.setWord(word);
			wordObj.setOccurrences(occurrences.longValue());
			wordObj.setUrlId(url.getId());
			
			return wordObj;
		}
	}
}
