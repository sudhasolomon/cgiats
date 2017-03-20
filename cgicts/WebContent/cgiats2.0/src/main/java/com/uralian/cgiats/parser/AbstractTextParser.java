/*
 * AbstractTextParser.java Mar 1, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vlad Orzhekhovskiy
 */
public abstract class AbstractTextParser implements TextParser
{
	protected final Pattern pattern = Pattern.compile("[A-Za-z]+[A-Za-z0-9]*");

	/**
	 * @param text
	 * @param keywords
	 */
	protected void parseTextAddKeywords(String text, Map<String, Integer> keywords)
	{
		Matcher matcher = pattern.matcher(text);
		while (matcher.find())
		{
			String token = matcher.group().toLowerCase();
			Integer oldCount = keywords.get(token);
			keywords.put(token, oldCount != null ? oldCount + 1 : 1);
		}
	}
}
