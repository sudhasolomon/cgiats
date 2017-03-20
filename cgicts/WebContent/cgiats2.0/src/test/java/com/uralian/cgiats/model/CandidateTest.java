/*
 * CandidateTest.java Mar 14, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vlad Orzhekhovskiy
 */
public class CandidateTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void testFieldsOnly()
	{
		log.info("Testing fields only");

		Candidate c = new Candidate();
		c.setFirstName("John");
		c.setLastName("Developer");
		c.setTitle("Software Developer");
		c.parseDocument();
		//
		// assertTrue(c.getKeywords().containsKey("john"));
		// assertTrue(c.getKeywords().containsKey("developer"));
		// assertTrue(c.getKeywords().containsKey("software"));
		//
		// assertEquals(1, c.getKeyCount("john"));
		// assertEquals(2, c.getKeyCount("developer"));
		// assertEquals(1, c.getKeyCount("software"));
	}

	@Test
	public void testDocumentOnly()
	{
		log.info("Testing document only");

		String document = "Solid experience in Java and Java-related stuff; also web design.";

		Candidate c = new Candidate();
		c.setDocument(document.getBytes(), ContentType.PLAIN);
		c.parseDocument();
		//
		// assertTrue(c.getKeywords().containsKey("solid"));
		// assertTrue(c.getKeywords().containsKey("stuff"));
		// assertTrue(c.getKeywords().containsKey("design"));
		//
		// assertEquals(1, c.getKeyCount("web"));
		// assertEquals(2, c.getKeyCount("java"));
	}

	@Test
	public void testDocumentAndFields()
	{
		log.info("Testing document and fields");

		String document = "Solid experience in Java and Java-related stuff; also web design.";

		Candidate c = new Candidate();
		c.setFirstName("John");
		c.setLastName("Developer");
		c.setTitle("Java Developer");
		c.setDocument(document.getBytes(), ContentType.PLAIN);
		c.parseDocument();

		// assertTrue(c.getKeywords().containsKey("solid"));
		// assertTrue(c.getKeywords().containsKey("stuff"));
		// assertTrue(c.getKeywords().containsKey("design"));
		// assertTrue(c.getKeywords().containsKey("john"));
		//
		// assertEquals(1, c.getKeyCount("web"));
		// assertEquals(3, c.getKeyCount("java"));
		// assertEquals(2, c.getKeyCount("developer"));
	}
}
