package com.brenttheisen.wordindex.dao;

public class DaoException extends Exception 
{
	// JDK 1.5 compiler gives warning if this isn't there
	private static final long serialVersionUID = 1L;

	public DaoException(String message)
	{
		super(message);
	}
	
	public DaoException(Throwable cause)
	{
		super(cause);
	}
	
	public DaoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
