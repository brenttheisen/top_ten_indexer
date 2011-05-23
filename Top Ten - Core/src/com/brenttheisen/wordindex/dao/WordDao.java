package com.brenttheisen.wordindex.dao;

import com.brenttheisen.wordindex.om.Word;

public interface WordDao 
{
	void save(Word word)
		throws DaoException;
}
