package com.brenttheisen.wordindex.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.brenttheisen.wordindex.dao.DaoException;
import com.brenttheisen.wordindex.dao.WordDao;
import com.brenttheisen.wordindex.om.Word;

public class WordDaoImpl implements WordDao 
{
	private Connection conn;

	public WordDaoImpl(Connection conn)
	{
		this.conn = conn;
	}
	
	public void save(Word word) throws DaoException 
	{
		if(word.getId() == 0)
		{
			insert(word);
		}
		else
		{
			// Update not implemented
		}
	}

	private void insert(Word word) throws DaoException
	{
		PreparedStatement pstmt = null;
		try 
		{
			pstmt = conn.prepareStatement("INSERT INTO word (url_id, word, occurrence) VALUES (?, ?, ?)");
		}
		catch(SQLException e) 
		{
			throw new DaoException(e);
		}
		
		try
		{
			pstmt.setLong(1, word.getUrlId());
			pstmt.setString(2, word.getWord());
			pstmt.setLong(3, word.getOccurrences());
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
				conn.prepareStatement("SELECT last_insert_id() AS word_id");
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
				long wordId = rs.getLong("word_id");
				word.setId(wordId);
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

}
