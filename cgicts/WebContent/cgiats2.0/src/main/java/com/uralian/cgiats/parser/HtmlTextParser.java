/*
 * HtmlTextParser.java Jun 11, 2012
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
import org.jsoup.Jsoup;

/**
 * @author Vlad Orzhekhovskiy
 */
public class HtmlTextParser extends AbstractTextParser {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.parser.TextParser#extractKeywords(java.io.InputStream)
	 */
	@Override
	public Map<String, Integer> extractKeywords(InputStream is) throws IOException {

		String parsedText = Jsoup.parse(is, null, "/").text();
		Map<String, Integer> keywords = new HashMap<String, Integer>();

		Scanner scanner = new Scanner(parsedText);
		while (scanner.hasNextLine()) {
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
	public String parseText(InputStream is) throws IOException {
		String parsedText = IOUtils.toString(is);
		if (parsedText != null) {
			parsedText = parsedText.replace("BACKGROUND: yellow;", "").replace("background-color:#FFFF00", "");
		}
		return parsedText;
	}
}