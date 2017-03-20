/*
 * TextParserFactory.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.model.ContentType;

/**
 * @author Vlad Orzhekhovskiy
 */
public class TextParserFactory
{
	private static TextParserFactory instance = null;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final Map<ContentType, TextParser> parsers;

	/**
	 * @return
	 */
	public static synchronized TextParserFactory getInstance()
	{
		if (instance == null)
			instance = new TextParserFactory();
		return instance;
	}

	/**
	 */
	private TextParserFactory()
	{
		parsers = new HashMap<ContentType, TextParser>();
		parsers.put(ContentType.PLAIN, new PlainTextParser());
		parsers.put(ContentType.MS_WORD, new MsWordTextParser());
		parsers.put(ContentType.HTML, new HtmlTextParser());
		parsers.put(ContentType.DOCX, new MsWordXTextParser());
		parsers.put(ContentType.PDF, new PdfTextParser());
		parsers.put(ContentType.RTF, new RtfTextParser());
		log.info("Text parsers initialized");
	}

	/**
	 * @param contentType
	 * @return
	 * @throws IllegalArgumentException
	 */
	public TextParser getParser(ContentType contentType)
	    throws IllegalArgumentException
	{
		TextParser parser = parsers.get(contentType);
		if (parser == null)
		{
			throw new IllegalArgumentException("No parser found for type: "
			    + contentType);
		}

		return parser;
	}
}