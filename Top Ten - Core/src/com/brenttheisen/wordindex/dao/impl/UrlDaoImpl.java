package com.brenttheisen.wordindex.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.brenttheisen.wordindex.dao.DaoException;
import com.brenttheisen.wordindex.dao.UrlDao;
import com.brenttheisen.wordindex.om.Url;
import com.brenttheisen.wordindex.om.Word;

public class UrlDaoImpl implements UrlDao 
{
	private Connection conn;
	
	// This is soft of a hack.  Need for the WordDaoTest to 
	// get a persisted Url.
	private static Url persistedUrl;
	
	public UrlDaoImpl(Connection conn)
	{
		this.conn = conn;
	}

	public void save(Url siteUrl) throws DaoException
	{
		if(siteUrl.getId() == 0)
		{
			insert(siteUrl);
		}
		else
		{
			// Update not supported yet
		}
	}
	
	private void insert(Url siteUrl) throws DaoException
	{
		PreparedStatement pstmt;
		try 
		{
			pstmt = conn.prepareStatement("INSERT INTO url (url, word_count) VALUES (?, ?)");
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}
		try
		{
			pstmt.setString(1, siteUrl.getUrl());
			pstmt.setInt(2, siteUrl.getTotalWords());
			pstmt.execute();
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}
		finally
		{
			try
			{
				pstmt.close();
			}
			catch(SQLException e) 
			{
				// Log to debug
			}
		}
		
		try 
		{
			pstmt = 
				conn.prepareStatement("SELECT last_insert_id() AS url_id");
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}

		ResultSet rs = null;
		try
		{
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				long urlId = rs.getLong("url_id");
				siteUrl.setId(urlId);
			}
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}
		finally
		{
			if(rs != null)
			{
				try 
				{
					rs.close();
				}
				catch(SQLException e) 
				{
					// Log to debug
				}
			}
			
			try 
			{
				pstmt.close();
			}
			catch(SQLException e) 
			{
				// Log to debug
			}
		}
	}

	public static Url getPersistedUrl() 
	{
		return persistedUrl;
	}

	public List<Word> getTopTenWords(Url url) 
		throws DaoException
	{
		List<Word> topTenWords = new ArrayList<Word>(10);
		
		PreparedStatement pstmt = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM word ORDER BY occurrence DESC LIMIT 10");
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}

		ResultSet rs = null;
		try
		{
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				Word word = new Word();
				word.setId(rs.getLong("id"));
				word.setWord(rs.getString("word"));
				word.setOccurrences(rs.getLong("occurrence"));
				word.setUrlId(rs.getLong("url_id"));
				topTenWords.add(word);
			}
		}
		catch(SQLException e)
		{
			throw new DaoException(e);
		}
		finally
		{
			if(rs != null)
			{
				try 
				{
					rs.close();
				}
				catch(SQLException e) 
				{
					throw new DaoException(e);
				}
			}
			
			try 
			{
				pstmt.close();
			}
			catch(SQLException e) 
			{
				throw new DaoException(e);
			}
		}
		
		return topTenWords;
	}

	
}
