/*
 * SearchTest.java Aug 28, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service.impl;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Level;
import org.hibernate.stat.Statistics;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.AbstractSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.model.*;
import com.uralian.cgiats.service.CandidateService;

/**
 * @author Vlad Orzhekhovskiy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/analysis-context.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class SearchTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private AbstractSessionFactoryBean sessionFactory;

	@BeforeClass
	public static void setUp()
	{
		disableLogging();
	}

	@After
	public void tearDown()
	{
		Statistics stats = sessionFactory.getObject().getStatistics();
		stats.logSummary();
	}

	@Test
	public void searchCandidatesByFields()
	{
		log.info("-- Testing findCandidatesCount() with fields query");
		CandidateSearchDto criteria = new CandidateSearchDto();
		criteria.setCity("Dayton");
		criteria.setState("FL");
		int count = candidateService.findCandidatesCount(criteria);
		log.info(count + " candidates found");

		log.info("-- Testing findCandidates() with fields query");
		criteria = new CandidateSearchDto();
		criteria.setCity("Dayton");
		criteria.setState("FL");
		criteria.setOrderByColumn(OrderByColumn.LOCATION);
		criteria.setMaxResults(50);
		criteria.setStartPosition(100);
		List<Candidate> list = (List<Candidate>) candidateService.findCandidates(criteria);
		log.info(list.size() + " candidates retrieved");
	}

	@Test
	public void searchCandidatesByFieldsAndKeywordAny()
	{
		log.info("-- Testing findCandidatesCount() with keywords/ANY and fields query");
		CandidateSearchDto criteria = new CandidateSearchDto();
		criteria.setState("TN");
//		criteria.setKeywords("jar", "code", "update");
//		criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ANY);
		int count = candidateService.findCandidatesCount(criteria);
		log.info(count + " candidates found");
		
		log.info("-- Testing findCandidates() with keywords/ANY and fields query");
		criteria = new CandidateSearchDto();
		criteria.setState("TN");
//		criteria.setKeywords("jar", "code", "update");
//		criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ANY);
		criteria.setOrderByColumn(OrderByColumn.LASTNAME);
		criteria.setMaxResults(50);
		criteria.setStartPosition(100);
		List<Candidate> list = (List<Candidate>) candidateService.findCandidates(criteria);
		log.info(list.size() + " candidates retrieved");
	}
	
	@Test
	public void searchCandidatesByFieldAndKeywordALL()
	{
		log.info("-- Testing findCandidatesCount() with keywords/ALL and fields query");
		CandidateSearchDto criteria = new CandidateSearchDto();
		criteria.setState("GA");
//		criteria.setKeywords("company", "efficient", "help");
//		criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ALL);
		int count = candidateService.findCandidatesCount(criteria);
		log.info(count + " candidates found");
		
		log.info("-- Testing findCandidates() with keywords/ANY and fields query");
		criteria = new CandidateSearchDto();
		criteria.setState("GA");
//		criteria.setKeywords("company", "efficient", "help");
//		criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ALL);
		criteria.setOrderByColumn(OrderByColumn.LASTNAME);
		criteria.setMaxResults(50);
		criteria.setStartPosition(100);
		List<Candidate> list = (List<Candidate>) candidateService.findCandidates(criteria);
		log.info(list.size() + " candidates retrieved");
	}

	/**
	 */
	public static void disableLogging()
	{
		getLogger("org.hibernate").setLevel(Level.ERROR);
		getLogger("org.springframework").setLevel(Level.ERROR);
		getLogger("org.springframework.orm").setLevel(Level.ERROR);
		getLogger(CandidateServiceImpl.class).setLevel(Level.ERROR);
		getLogger(CandidateDaoImpl.class).setLevel(Level.ERROR);
	}
}