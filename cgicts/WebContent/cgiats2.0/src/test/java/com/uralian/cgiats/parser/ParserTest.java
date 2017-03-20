/*
 * ParserTest.java Mar 1, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.model.ContentType;

/**
 * @author Vlad Orzhekhovskiy
 */
public class ParserTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 */
	@Test
	public void testParser()
	{
		log.info("Testing parser factory");

		String s = ".net j2ee: 123main java2html, j2ee";
		InputStream is = new ByteArrayInputStream(s.getBytes());

		try
		{
			PlainTextParser parser = new PlainTextParser();
			Map<String, Integer> map = parser.extractKeywords(is);

			Integer j2eeCount = map.get("j2ee");
			assertNotNull(j2eeCount);
			assertEquals(2, j2eeCount.intValue());

			Integer java2htmlCount = map.get("java2html");
			assertNotNull(java2htmlCount);
			assertEquals(1, java2htmlCount.intValue());

		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 */
	@Test
	public void testParserFactory()
	{
		log.info("Testing parser factory");

		TextParserFactory tpf = TextParserFactory.getInstance();
		assertNotNull(tpf);

		try
		{
			for (ContentType cType : ContentType.values())
			{
				TextParser tp = tpf.getParser(cType);
				assertNotNull(tp);
			}
		}
		catch (IllegalArgumentException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 */
	@Test
	public void testPlainTextParser()
	{
		log.info("Testing plain text parser");

		TextParserFactory tpf = TextParserFactory.getInstance();
		TextParser tp = tpf.getParser(ContentType.PLAIN);

		try
		{
			InputStream is = getClass().getResourceAsStream("/docs/ResumeTest.txt");
			Map<String, Integer> keywords = tp.extractKeywords(is);
			assertNotNull(keywords);

			Integer javaCount = keywords.get("java");
			assertNotNull(javaCount);
			assertTrue(javaCount > 0);

			Integer softCount = keywords.get("software");
			assertNotNull(softCount);
			assertTrue(softCount > 1);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 */
	@Test
	public void testMsWordTextParser()
	{
		log.info("Testing MS Word text parser");

		TextParserFactory tpf = TextParserFactory.getInstance();
		TextParser tp = tpf.getParser(ContentType.MS_WORD);

		try
		{
			InputStream is = getClass().getResourceAsStream("/docs/ResumeTest.doc");
			Map<String, Integer> keywords = tp.extractKeywords(is);
			assertNotNull(keywords);

			Integer javaCount = keywords.get("java");
			assertNotNull(javaCount);
			assertTrue(javaCount > 0);

			Integer softCount = keywords.get("software");
			assertNotNull(softCount);
			assertTrue(softCount > 1);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 */
	@Test
	public void testHTMLTextParser()
	{
		log.info("Testing HTML text parser");

		TextParserFactory tpf = TextParserFactory.getInstance();
		TextParser tp = tpf.getParser(ContentType.HTML);

		try
		{
			InputStream is = getClass().getResourceAsStream(
			    "/docs/TicketVueloChris.htm");
			Map<String, Integer> keywords = tp.extractKeywords(is);
			assertNotNull(keywords);

			Integer javaCount = keywords.get("trip");
			assertNotNull(javaCount);
			assertTrue(javaCount > 0);

			Integer softCount = keywords.get("ticket");
			assertNotNull(softCount);
			assertTrue(softCount > 1);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 */
	@Test
	public void testDocXTextParser()
	{
		log.info("Testing MS Word X text parser");

		TextParserFactory tpf = TextParserFactory.getInstance();
		TextParser tp = tpf.getParser(ContentType.DOCX);

		try
		{
			InputStream is = getClass().getResourceAsStream("/docs/resumeDocx.docx");
			Map<String, Integer> keywords = tp.extractKeywords(is);
			assertNotNull(keywords);

			Integer javaCount = keywords.get("java");
			assertNotNull(javaCount);
			assertTrue(javaCount > 0);

			Integer softCount = keywords.get("software");
			assertNotNull(softCount);
			assertTrue(softCount > 1);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}
}