/*
 * TextParser.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface TextParser
{
	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public Map<String, Integer> extractKeywords(InputStream is)
	    throws IOException;

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String parseText(InputStream is) throws IOException;
}