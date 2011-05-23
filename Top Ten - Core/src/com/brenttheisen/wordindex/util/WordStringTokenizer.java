package com.brenttheisen.wordindex.util;

import java.util.Enumeration;

public class WordStringTokenizer implements Enumeration 
{
	private String string;
	private int offset = 0;
	
	public WordStringTokenizer(String string)
	{
		this.string = string;
	}

	public boolean hasMoreElements() 
	{
		if(isEos())
		{
			return false;
		}

		while(offset < string.length())
		{
			char c = string.charAt(offset); 
			if(isCharPartOfWord(c))
			{
				return true;
			}
			offset++;
		}

		return false;
	}

	public Object nextElement() 
	{
		if(isEos())
		{
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		while(offset < string.length())
		{
			char c = string.charAt(offset);
			offset++;
			if(isCharPartOfWord(c))
			{
				sb.append(c);
			}
			else if(sb.length() > 0)
			{
				break;
			}
		}

		if(sb.length() == 0)
		{
			// Someone forgot to call hasMoreElements()
			return null;
		}
		
		return sb.toString();
	}
	
	/**
	 * @return true if there is a next word
	 */
	private boolean isEos()
	{
		return offset + 1 >= string.length();
	}
	
	private boolean isCharPartOfWord(char c)
	{
		if((c >= 'a' && c <= 'z') ||
				c >= 'A' && c <='Z')
		{
			return true;
		}
		
		return false;
	}
}
