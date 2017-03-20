/*
 * PlainTextParser.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

/**
 * @author Vlad Orzhekhovskiy
 */
public class PlainTextParser extends AbstractTextParser
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.parser.TextParser#extractKeywords(java.io.InputStream)
	 */
	@Override
	public Map<String, Integer> extractKeywords(InputStream is)
	    throws IOException
	{
		Map<String, Integer> keywords = new HashMap<String, Integer>();

		Scanner scanner = new Scanner(is);
		while (scanner.hasNextLine())
		{
			String text = scanner.nextLine();
			parseTextAddKeywords(text, keywords);
		}

		return keywords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.parser.TextParser#parseText(java.io.InputStream)
	 */
	@Override
	public String parseText(InputStream is) throws IOException
	{
		return IOUtils.toString(is);
	}
}