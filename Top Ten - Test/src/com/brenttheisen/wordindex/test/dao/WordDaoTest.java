package com.brenttheisen.wordindex.test.dao;

import com.brenttheisen.wordindex.dao.DaoException;
import com.brenttheisen.wordindex.dao.WordDao;
import com.brenttheisen.wordindex.dao.impl.WordDaoImpl;
import com.brenttheisen.wordindex.om.Word;

public class WordDaoTest extends DaoBaseTestCase 
{
	private WordDao wordDao;

	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		wordDao = new WordDaoImpl(conn);
	}


	/**
	 * Tests referential integrity violation.
	 */
	public void testForeignKeyViolation()
	{
		Word word = new Word();
		word.setOccurrences(303);
		word.setWord("hire");
		
		// A made up foreign key..
		word.setUrlId(303);
		
		DaoException daoEx = null;
		try
		{
			wordDao.save(word);
		}
		catch(DaoException e) 
		{
			daoEx = e;
		}
		assertNotNull(daoEx);
	}
}
