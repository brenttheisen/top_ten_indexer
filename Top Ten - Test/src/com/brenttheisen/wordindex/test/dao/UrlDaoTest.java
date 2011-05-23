package com.brenttheisen.wordindex.test.dao;

import com.brenttheisen.wordindex.dao.UrlDao;
import com.brenttheisen.wordindex.dao.impl.UrlDaoImpl;
import com.brenttheisen.wordindex.om.Url;

public class UrlDaoTest extends DaoBaseTestCase 
{
	private UrlDao urlDao;

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		urlDao = new UrlDaoImpl(conn);
	}
	
	public void testSaveTransient() throws Throwable
	{
		Url url = new Url();
		url.setTotalWords(303);
		url.setUrl("http://dummyurl.com");
		urlDao.save(url);
		assertTrue(url.getId() > 0);
	}

}
