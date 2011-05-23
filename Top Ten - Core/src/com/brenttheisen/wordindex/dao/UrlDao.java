package com.brenttheisen.wordindex.dao;

import java.util.List;

import com.brenttheisen.wordindex.om.Url;
import com.brenttheisen.wordindex.om.Word;

public interface UrlDao 
{
	void save(Url siteUrl) throws DaoException;
	
	List<Word> getTopTenWords(Url url) throws DaoException;
}
