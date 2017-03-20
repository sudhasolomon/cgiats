/*
 * LuceneTest.java Jan 29, 2013
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats;

import static org.apache.log4j.Logger.getLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Level;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.*;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.parser.TextParser;
import com.uralian.cgiats.parser.TextParserFactory;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;
import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/analysis-context.xml")
// @Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class LuceneTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CandidateService candidateService;

	@Before
	public void setUp()
	{
		disableLogging();
	}

	// @Test
	public void parseDocuments()
	{
		CandidateSearchDto criteria = new CandidateSearchDto();
		List<Candidate> candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		log.info(candidates.size() + " candidates retrieved");

		int counter = 0;

		List<Integer> errorIds = new ArrayList<Integer>();

		TextParserFactory tpf = TextParserFactory.getInstance();
		for (Candidate candidate : candidates)
		{
			int id = candidate.getId();

			try
			{
				candidate = candidateService.getCandidate(id, true, false);
				byte[] doc = candidate.getDocument();

				ContentType docType = candidate.getDocumentType();
				if (docType == null)
					continue;

				TextParser parser = tpf.getParser(docType);

				ByteArrayInputStream bais = new ByteArrayInputStream(doc);
				String parsedText = parser.parseText(bais);
				candidate.getResume().setParsedText(parsedText);

				candidateService.updateCandidate(candidate);

				counter++;

				if (counter % 100 == 0)
					log.info(counter + " candidates processed");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				errorIds.add(id);
			}
		}

		if (Utils.isEmpty(errorIds))
			System.out.println(counter + " candidates successfully processed");
		else
		{
			System.out.println(counter + " candidates processed, " + errorIds.size()
			    + " error(s) encountered. Errored IDs:");
			for (Integer id : errorIds)
				System.out.println(id);
		}
	}

	// @Test
	// @Transactional
	public void reindex() throws InterruptedException, ExecutionException
	{
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		MassIndexer indexer = fullTextSession.createIndexer();
		MassIndexerProgressMonitor monitor = new MassIndexerProgressMonitor()
		{
			private int loaded = 0;
			private int built = 0;
			private long increment = 0;
			private int added = 0;

			@Override
			public void indexingCompleted()
			{
				System.out.println("COMPLETED");
			}

			@Override
			public void entitiesLoaded(int size)
			{
				this.loaded += size;
				System.out.println("Loaded " + loaded + " entities");
			}

			@Override
			public void documentsBuilt(int number)
			{
				this.built += number;
				System.out.println("Built " + built + " documents");
			}

			@Override
			public void documentsAdded(long increment)
			{
				this.increment += increment;
				System.out.println("Added" + this.increment + " documents");
			}

			@Override
			public void addToTotalCount(long count)
			{
				this.added += count;
				System.out.println("AddedToTotal " + added);
			}
		};

		indexer.progressMonitor(monitor);
		indexer.startAndWait();

		System.out.println("Resume data re-indexed");
	}

	// @Test
	// @Transactional()
	public void searchHibernate()
	{
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder()
		    .forEntity(Candidate.class).get();
		Query query = qb.keyword().onFields("firstName", "lastName")
		    .matching("John").createQuery();
		org.hibernate.Query q = fullTextSession.createFullTextQuery(query,
		    Candidate.class);

		List<?> result = q.list();
		System.out.println(result.size() + " item(s) found");
	}

	@Test
	@Transactional()
	public void searchLucene() throws ParseException
	{
		String searchString = "java* AND oracle";
		QueryParser parser = new QueryParser(Version.LUCENE_31, "resume",
		    new StandardAnalyzer(Version.LUCENE_31));
		org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);

		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		FullTextQuery hibernateQuery = fullTextSession.createFullTextQuery(
		    luceneQuery, Candidate.class);

		List<?> result = hibernateQuery.list();
		System.out.println(result.size() + " item(s) found");
	}

	/**
	 */
	public void disableLogging()
	{
		getLogger("org.hibernate").setLevel(Level.ERROR);
		getLogger("org.springframework").setLevel(Level.ERROR);
		getLogger("org.springframework.orm").setLevel(Level.ERROR);
		getLogger(CandidateServiceImpl.class).setLevel(Level.WARN);
		getLogger(CandidateDaoImpl.class).setLevel(Level.WARN);
		System.setErr(new PrintStream(new OutputStream()
		{
			@Override
			public void write(int b) throws IOException
			{
			}
		}));
	}
}
