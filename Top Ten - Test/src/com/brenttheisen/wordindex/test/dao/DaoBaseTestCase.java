package com.brenttheisen.wordindex.test.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

public abstract class DaoBaseTestCase extends TestCase 
{
	private static final String JDBC_URL = "jdbc:mysql://localhost/top_ten?user=top_ten&password=whatever";
	private static final String JDBC_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	
	protected Connection conn;

	@Override
	protected void setUp() throws Exception 
	{
		Class.forName(JDBC_DRIVER_CLASS_NAME);
		
		conn = DriverManager.getConnection(JDBC_URL);
	}

	@Override
	protected void tearDown() throws Exception 
	{
		if(conn != null)
		{
			conn.close();
		}
	}
	

}
