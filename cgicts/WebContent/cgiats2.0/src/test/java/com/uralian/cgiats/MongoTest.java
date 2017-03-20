/*
 * MongoTest.java May 24, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats;

import java.io.*;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.*;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;

/**
 * @author Vlad Orzhekhovskiy
 */
public class MongoTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	// @Test
	public void testConnection() throws UnknownHostException, MongoException
	{
		Mongo mongo = new Mongo("localhost");
		mongo.getDB("cgiats");
		mongo.close();
	}

	/**
	 * @throws MongoException
	 * @throws IOException
	 */
	// @Test
	public void insertResume() throws MongoException, IOException
	{
		Candidate candidate = new Candidate();
		candidate.setEmail("john@doe.com");
		candidate.setFirstName("John");
		candidate.setLastName("Doe");
		candidate.setPhone("505-123-4444");
		candidate.getAddress().setState("CO");
		candidate.getAddress().setZipcode("80525");
		candidate.setTitle("Developer");

		byte[] document = loadMsWordFile("/docs/ResumeTest.doc");
		candidate.setDocument(document, ContentType.MS_WORD);
		candidate.parseDocument();

		Mongo mongo = new Mongo("localhost");
		DB db = mongo.getDB("cgiats");
		DBCollection coll = db.getCollection("resume");

		DBObject resume = pojoToMongo(candidate);
		coll.insert(resume);

		mongo.close();
	}

	/**
	 * @throws MongoException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	// @Test
	public void insertResumeBatch() throws UnknownHostException, MongoException,
	    IOException
	{
		// retrieve files
		File folder = new File("/Users/vladorz/Downloads/archive");
		File[] files = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".doc");
			}
		});

		// open connection to DB
		Mongo mongo = new Mongo("localhost");
		DB db = mongo.getDB("cgiats");
		DBCollection coll = db.getCollection("resume");

		// insert resumes
		long start = System.currentTimeMillis();
		int count = 0;
		PrintStream nps = new PrintStream(new FileOutputStream("/dev/null"));
		System.setErr(nps);
		for (int i = 0; i < 20; i++)
		{
			for (File file : files)
			{
				try
				{
					byte[] document = loadMsWordFile(file);

					Candidate candidate = new Candidate();
					candidate.setLastName("Unknown");
					candidate.setDocument(document, ContentType.MS_WORD);
					candidate.parseDocument();

					DBObject resume = pojoToMongo(candidate);
					coll.insert(resume);

					count++;
					if (count % 500 == 0)
						System.out.println(count + " resumes processed");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();

		String pattern = "{0,number,integer} resumes processed in {1,number,integer} sec.";
		System.out.println(MessageFormat.format(pattern, count,
		    (end - start) / 1000));

		mongo.close();
	}

	// @Test
	public void retrieveResume() throws IOException, MongoException
	{
		Mongo mongo = new Mongo("localhost");
		DB db = mongo.getDB("cgiats");
		DBCollection coll = db.getCollection("resume");

		// create indexes for the selected keywords
		coll.ensureIndex("keywords.oracle");
		coll.ensureIndex("keywords.java");

		// run AND query on indexed fields: JAVA and ORACLE
		BasicDBObject query = new BasicDBObject();
		query.put("keywords.java", new BasicDBObject("$gte", 1));
		query.put("keywords.oracle", new BasicDBObject("$gte", 1));
		testQuery("AND, INDEXED", coll, query);

		// run OR query on indexed fields: JAVA and ORACLE
		query = new BasicDBObject();
		BasicDBList orList = new BasicDBList();
		orList
		    .add(new BasicDBObject("keywords.java", new BasicDBObject("$gte", 1)));
		orList.add(new BasicDBObject("keywords.oracle",
		    new BasicDBObject("$gte", 1)));
		testQuery("OR, INDEXED", coll, query);

		// run AND query on non-indexed fields: JAVA and ORACLE
		query = new BasicDBObject();
		query.put("keywords.mysql", new BasicDBObject("$gte", 1));
		query.put("keywords.application", new BasicDBObject("$gte", 1));
		testQuery("AND, NON-INDEXED", coll, query);

		// run OR query on indexed fields: JAVA and ORACLE
		query = new BasicDBObject();
		orList = new BasicDBList();
		orList
		    .add(new BasicDBObject("keywords.mysql", new BasicDBObject("$gte", 1)));
		orList.add(new BasicDBObject("keywords.application", new BasicDBObject(
		    "$gte", 1)));
		testQuery("OR, NON-INDEXED", coll, query);
	}

	/**
	 * @param coll
	 * @param query
	 */
	protected void testQuery(String name, DBCollection coll, DBObject query)
	{
		long start = System.currentTimeMillis();
		int count = coll.find(query).count();
		long end = System.currentTimeMillis();
		System.out.println(name + ": count retrieved in " + (end - start) + " ms: "
		    + count);

		start = System.currentTimeMillis();
		int size = coll.find(query).skip(count * 3 / 4).limit(50).toArray().size();
		end = System.currentTimeMillis();
		System.out.println(name + ": 50 records retrieved in " + (end - start)
		    + " ms: " + size);
	}

	/**
	 * @param candidate
	 * @return
	 */
	protected static DBObject pojoToMongo(Candidate candidate)
	{
		BasicDBObject resume = new BasicDBObject();
		resume.put("email", candidate.getEmail());
		resume.put("firstName", candidate.getFirstName());
		resume.put("lastName", candidate.getLastName());
		resume.put("phone", candidate.getPhone());
		resume.put("title", candidate.getTitle());

		BasicDBObject address = new BasicDBObject();
		address.put("state", candidate.getAddress().getState());
		address.put("zip", candidate.getAddress().getZipcode());
		resume.put("address", address);

		resume.put("document", candidate.getDocument());

		return resume;
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	protected static byte[] loadMsWordFile(String resource) throws IOException
	{
		InputStream is = MongoTest.class.getResourceAsStream(resource);
		byte[] document = IOUtils.toByteArray(is);

		return document;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected static byte[] loadMsWordFile(File file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		byte[] document = IOUtils.toByteArray(fis);
		fis.close();

		return document;
	}
}